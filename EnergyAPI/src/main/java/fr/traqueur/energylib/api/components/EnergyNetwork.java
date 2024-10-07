package fr.traqueur.energylib.api.components;

import fr.traqueur.energylib.api.EnergyAPI;
import fr.traqueur.energylib.api.EnergyManager;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import fr.traqueur.energylib.api.mechanics.EnergyConsumer;
import fr.traqueur.energylib.api.mechanics.EnergyProducer;
import fr.traqueur.energylib.api.mechanics.EnergyStorage;
import fr.traqueur.energylib.api.types.EnergyType;
import fr.traqueur.energylib.api.types.MechanicType;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Represents an energy network.
 */
public class EnergyNetwork {

    /**
     * The API instance.
     */
    private final EnergyAPI api;

    /**
     * The network's unique identifier.
     */
    private final UUID id;

    /**
     * The network's chunk.
     */
    private Chunk chunk;

    /**
     * The network's components.
     */
    private final Map<Location, EnergyComponent<?>> components;

    /**
     * Creates a new energy network.
     *
     * @param api       The API instance.
     * @param component The component to add.
     * @param location  The location of the component.
     */
    public EnergyNetwork(EnergyAPI api, EnergyComponent<?> component, Location location) {
        this(api, UUID.randomUUID());
        this.components.put(location, component);
        this.chunk = location.getChunk();
    }

    /**
     * Creates a new energy network.
     *
     * @param api The API instance.
     * @param id  The network's unique identifier.
     */
    public EnergyNetwork(EnergyAPI api, UUID id) {
        this.api = api;
        this.id = id;
        this.components = new ConcurrentHashMap<>();
    }

    /**
     * Add a component to the network.
     *
     * @param component The component to add.
     * @param location  The location of the component.
     * @throws SameEnergyTypeException If the component is not the same type.
     */
    public void addComponent(EnergyComponent<?> component, Location location) throws SameEnergyTypeException {
        for (Map.Entry<Location, EnergyComponent<?>> entry : this.components.entrySet().stream()
                .filter(entry -> entry.getKey().distance(location) == 1).toList()) {
            entry.getValue().connect(component);
        }
        if (chunk == null) {
            this.chunk = location.getChunk();
        }
        this.components.put(location, component);
    }

    /**
     * Remove a component from the network.
     *
     * @param location The location of the component.
     */
    public void removeComponent(Location location) {
        this.components.entrySet().stream().filter(entry -> entry.getKey().distance(location) == 1).forEach(entry -> {
            entry.getValue().disconnect(this.components.get(location));
        });
        this.components.remove(location);
    }

    /**
     * Get if the network contains a location.
     *
     * @param location The location to check.
     * @return If the network contains the location.
     */
    public boolean contains(Location location) {
        return this.components.containsKey(location);
    }

    /**
     * Merge the network with another network.
     *
     * @param network The network to merge with.
     */
    public void mergeWith(EnergyNetwork network) {
        this.components.putAll(network.components);
    }

    /**
     * Update the network.
     */
    public void update() {
        this.handleProduction().thenAccept((t) -> {
            this.handleConsumers().thenAccept((t1) -> {
                this.handleExcess();
            });
        });
    }

    /**
     * Update the network production asynchronously.
     */
    private CompletableFuture<Void> handleProduction() {
        Map<Location, EnergyComponent<?>> producers = this.getComponentByType(MechanicType.PRODUCER);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        producers.forEach((location, producer) -> {
            var future = this.api.getScheduler().runAtLocation(location, (t) -> {
                ((EnergyProducer) producer.getMechanic()).produce(location);
            });
            futures.add(future);
        });
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    /**
     * Update the network excess asynchronously.
     */
    private void handleExcess() {
        Map<Location, EnergyComponent<?>> producers = getComponentByType(MechanicType.PRODUCER);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        producers.forEach((location, producerComponent) -> {
            var future = this.api.getScheduler().runAtLocation(location, (t) -> {
                EnergyProducer producer = (EnergyProducer) producerComponent.getMechanic();
                double excessEnergy = producer.getExcessEnergy();

                if (excessEnergy > 0) {
                    List<EnergyComponent<?>> connectedStorages =
                            getConnectedComponents(producerComponent, MechanicType.STORAGE);

                    for (EnergyComponent<?> storageComponent : connectedStorages) {
                        EnergyStorage storage = (EnergyStorage) storageComponent.getMechanic();
                        double energyStored = storage.storeEnergy(excessEnergy);
                        excessEnergy -= energyStored;

                        if (excessEnergy <= 0) {
                            break;
                        }
                    }
                }

                if (excessEnergy > 0 && api.isDebug()) {
                    System.out.println("L'énergie excédentaire du producteur " + producerComponent + " est perdue.");
                }
            });
            futures.add(future);
        });
    }

    /**
     * Update the network consumers asynchronously.
     */
    private CompletableFuture<Void> handleConsumers() {
        Map<Location, EnergyComponent<?>> consumers = this.getComponentByType(MechanicType.CONSUMER);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        consumers.forEach((location, consumerComponent) -> {
            var future = this.api.getScheduler().runAtLocation(location, (t) -> {
                EnergyConsumer consumer = (EnergyConsumer) consumerComponent.getMechanic();
                double requiredEnergy = consumer.getEnergyDemand();
                double providedEnergy = 0;

                List<EnergyComponent<?>> connectedProducers =
                        getConnectedComponents(consumerComponent, MechanicType.PRODUCER);

                for (EnergyComponent<?> producerComponent : connectedProducers) {
                    EnergyProducer producer = (EnergyProducer) producerComponent.getMechanic();
                    double energyAvailable = producer.extractEnergy(requiredEnergy);
                    requiredEnergy -= energyAvailable;
                    providedEnergy += energyAvailable;
                    if (requiredEnergy <= 0) {
                        break;
                    }
                }

                if (requiredEnergy > 0) {
                    List<EnergyComponent<?>> connectedStorages =
                            getConnectedComponents(consumerComponent, MechanicType.STORAGE);

                    for (EnergyComponent<?> storageComponent : connectedStorages) {
                        EnergyStorage storage = (EnergyStorage) storageComponent.getMechanic();
                        double energyFromStorage = storage.consumeEnergy(requiredEnergy);
                        requiredEnergy -= energyFromStorage;
                        providedEnergy += energyFromStorage;

                        if (requiredEnergy <= 0) {
                            break;
                        }
                    }
                }

                consumer.receiveEnergy(providedEnergy);
                if (requiredEnergy > 0) {
                    if (api.isDebug()) {
                        System.out.println("Le consommateur " + consumerComponent + " n'a pas reçu assez d'énergie.");
                    }
                    consumer.setEnable(false);
                } else {
                    consumer.setEnable(true);
                }
            });
            futures.add(future);
        });
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    /**
     * Get if the network is empty.
     *
     * @return If the network is empty.
     */
    public boolean isEmpty() {
        return this.components.isEmpty();
    }

    /**
     * Get if the network is in a chunk.
     *
     * @param chunk The chunk to check.
     * @return If the network is in the chunk.
     */
    public boolean isInChunk(Chunk chunk) {
        return this.components.keySet()
                .stream()
                .anyMatch(location -> this.isSameChunk(chunk, location.getChunk()));
    }


    /**
     * Save the network in the chunk.
     */
    public void save() {
        EnergyManager manager = this.api.getManager();
        Chunk chunk = this.getChunk();
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        var gson = manager.getGson();
        String json = gson.toJson(this, EnergyNetwork.class);

        List<String> networks =
                container.getOrDefault(manager.getNetworkKey(), PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING), new ArrayList<>());
        networks = new ArrayList<>(networks);
        networks.removeIf(network -> {
            EnergyNetwork energyNetwork = gson.fromJson(network, EnergyNetwork.class);
            return energyNetwork.getId().equals(this.id);
        });
        networks.add(json);

        container.set(manager.getNetworkKey(), PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING), networks);
    }

    /**
     * Delete the network from the chunk.
     */
    public void delete() {
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        List<String> networks =
                container.getOrDefault(this.api.getManager().getNetworkKey(), PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING), new ArrayList<>());
        networks = new ArrayList<>(networks);
        networks.removeIf(json -> {
            EnergyNetwork network = this.api.getManager().getGson().fromJson(json, EnergyNetwork.class);
            return network.getId().equals(this.id);
        });
        container.set(this.api.getManager().getNetworkKey(), PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING), networks);
    }

    /**
     * Get the network's components.
     *
     * @return The network's components.
     */
    public Map<Location, EnergyComponent<?>> getComponents() {
        return this.components;
    }

    /**
     * Get the network's energy type.
     *
     * @return The network's energy type.
     */
    public EnergyType getEnergyType() {
        return this.getRoot().getEnergyType();
    }

    /**
     * Get the network's unique identifier.
     *
     * @return The network's unique identifier.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Get the chunk.
     *
     * @return The chunk.
     */
    public Chunk getChunk() {
        return this.chunk;
    }

    /**
     * Check if two chunks are the same.
     *
     * @param chunk  The first chunk.
     * @param chunk1 The second chunk.
     * @return If the chunks are the same.
     */
    private boolean isSameChunk(Chunk chunk, Chunk chunk1) {
        return chunk.getX() == chunk1.getX() && chunk.getZ() == chunk1.getZ()
                && chunk.getWorld().getName().equals(chunk1.getWorld().getName());
    }

    /**
     * Get the root component.
     *
     * @return The root component.
     */
    private EnergyComponent<?> getRoot() {
        return this.components.values().iterator().next();
    }

    /**
     * Get the connected components.
     *
     * @param component The component to check.
     * @param type      The type of the component.
     * @return The connected components.
     */
    private List<EnergyComponent<?>> getConnectedComponents(EnergyComponent<?> component, MechanicType type) {
        Set<EnergyComponent<?>> visited = new HashSet<>();
        List<EnergyComponent<?>> connectedComponents = new ArrayList<>();
        this.dfsConnectedComponents(component, visited, connectedComponents, type);
        return connectedComponents;
    }

    /**
     * Depth-first search to get the connected components.
     *
     * @param component           The component to check.
     * @param visited             The visited components.
     * @param connectedComponents The connected components.
     * @param type                The type of the component.
     */
    private void dfsConnectedComponents(EnergyComponent<?> component, Set<EnergyComponent<?>> visited,
                                        List<EnergyComponent<?>> connectedComponents, MechanicType type) {
        if (visited.contains(component)) {
            return;
        }

        visited.add(component);

        if (type.isInstance(component)) {
            connectedComponents.add(component);
        }

        for (EnergyComponent<?> neighbor : component.getConnectedComponents()) {
            if (!visited.contains(neighbor) && (MechanicType.TRANSPORTER.isInstance(neighbor) || type.isInstance(neighbor))) {
                this.dfsConnectedComponents(neighbor, visited, connectedComponents, type);
            }
        }
    }

    /**
     * Get the components by type.
     *
     * @param type The type of the component.
     * @return The components by type.
     */
    private Map<Location, EnergyComponent<?>> getComponentByType(MechanicType type) {
        return this.components.entrySet()
                .stream()
                .filter(entry -> type.getClazz().isAssignableFrom(entry.getValue().getMechanic().getClass()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
