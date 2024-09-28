package fr.traqueur.energylib.api.components;

import fr.traqueur.energylib.api.EnergyAPI;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import fr.traqueur.energylib.api.mechanics.EnergyConsumer;
import fr.traqueur.energylib.api.mechanics.EnergyProducer;
import fr.traqueur.energylib.api.mechanics.EnergyStorage;
import fr.traqueur.energylib.api.types.EnergyType;
import fr.traqueur.energylib.api.types.MechanicType;
import fr.traqueur.energylib.api.types.MechanicTypes;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EnergyNetwork {

    private final EnergyAPI api;
    private final Map<Location, EnergyComponent<?>> components;
    private boolean enable;

    public EnergyNetwork(EnergyAPI api, EnergyComponent<?> component, Location location) {
        this.api = api;
        this.components = new ConcurrentHashMap<>();
        this.components.put(location,component);
        this.enable = true;
    }

    public EnergyNetwork(EnergyAPI api) {
        this.api = api;
        this.components = new ConcurrentHashMap<>();
    }

    public boolean isEnable() {
        return this.enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void addComponent(EnergyComponent<?> component, Location location) throws SameEnergyTypeException {
        for (Map.Entry<Location, EnergyComponent<?>> entry : this.components.entrySet().stream()
                .filter(entry -> entry.getKey().distance(location) == 1).toList()) {
            entry.getValue().connect(component);
        }
        this.components.put(location,component);
    }

    public void removeComponent(Location location) {
        this.components.entrySet().stream().filter(entry -> entry.getKey().distance(location) == 1).forEach(entry -> {
            entry.getValue().disconnect(this.components.get(location));
        });
        this.components.remove(location);
    }

    public boolean contains(Location location) {
        return this.components.containsKey(location);
    }

    public void mergeWith(EnergyNetwork network) {
        this.components.putAll(network.components);
    }

    public void update() {
        this.handleProduction().thenAccept((t) -> {
            this.handleConsumers().thenAccept((t1) -> {
                this.handleExcess();
            });
        });
    }

    private CompletableFuture<Void> handleProduction() {
        Map<Location, EnergyComponent<?>> producers = this.getComponentByType(MechanicTypes.PRODUCER);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        producers.forEach((location, producer) -> {
            var future =this.api.getScheduler().runAtLocation(location, (t) -> {
                ((EnergyProducer) producer.getMechanic()).produce(location);
            });
            futures.add(future);
        });
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private void handleExcess() {
        Map<Location, EnergyComponent<?>> producers = getComponentByType(MechanicTypes.PRODUCER);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        producers.forEach((location, producerComponent) -> {
            var future = this.api.getScheduler().runAtLocation(location, (t) -> {
                EnergyProducer producer = (EnergyProducer) producerComponent.getMechanic();
                double excessEnergy = producer.getExcessEnergy();

                if (excessEnergy > 0) {
                    List<EnergyComponent<?>> connectedStorages =
                            getConnectedComponents(producerComponent, MechanicTypes.STORAGE);

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

    private CompletableFuture<Void> handleConsumers() {
        Map<Location, EnergyComponent<?>> consumers = this.getComponentByType(MechanicTypes.CONSUMER);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        consumers.forEach((location, consumerComponent) -> {
            var future = this.api.getScheduler().runAtLocation(location, (t) -> {
                EnergyConsumer consumer = (EnergyConsumer) consumerComponent.getMechanic();
                double requiredEnergy = consumer.getEnergyDemand();
                double providedEnergy = 0;

                List<EnergyComponent<?>> connectedProducers =
                        getConnectedComponents(consumerComponent, MechanicTypes.PRODUCER);

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
                            getConnectedComponents(consumerComponent, MechanicTypes.STORAGE);

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
                    if(api.isDebug()) {
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

    public boolean isEmpty() {
        return this.components.isEmpty();
    }

    public boolean isInChunk(Chunk chunk) {
        return this.components.keySet()
                .stream()
                .anyMatch(location -> this.isSameChunk(chunk, location.getChunk()));
    }

    private boolean isSameChunk(Chunk chunk, Chunk chunk1) {
        return chunk.getX() == chunk1.getX() && chunk.getZ() == chunk1.getZ()
                && chunk.getWorld().getName().equals(chunk1.getWorld().getName());
    }

    public Map<Location, EnergyComponent<?>> getComponents() {
        return this.components;
    }

    public EnergyType getEnergyType() {
        return this.getRoot().getEnergyType();
    }

    private EnergyComponent<?> getRoot() {
        return this.components.values().iterator().next();
    }

    private List<EnergyComponent<?>> getConnectedComponents(EnergyComponent<?> component, MechanicType type) {
        Set<EnergyComponent<?>> visited = new HashSet<>();
        List<EnergyComponent<?>> connectedComponents = new ArrayList<>();
        this.dfsConnectedComponents(component, visited, connectedComponents, type);
        return connectedComponents;
    }

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
            if (!visited.contains(neighbor) && (MechanicTypes.TRANSPORTER.isInstance(neighbor) || type.isInstance(neighbor))) {
                this.dfsConnectedComponents(neighbor, visited, connectedComponents, type);
            }
        }
    }

    private Map<Location, EnergyComponent<?>> getComponentByType(MechanicType type) {
        return this.components.entrySet()
                .stream()
                .filter(entry -> type.getClazz().isAssignableFrom(entry.getValue().getMechanic().getClass()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
