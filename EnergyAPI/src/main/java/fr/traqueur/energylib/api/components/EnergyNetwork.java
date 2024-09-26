package fr.traqueur.energylib.api.components;

import com.google.common.util.concurrent.AtomicDouble;
import fr.traqueur.energylib.api.EnergyAPI;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import fr.traqueur.energylib.api.mechanics.EnergyConsumer;
import fr.traqueur.energylib.api.mechanics.EnergyProducer;
import fr.traqueur.energylib.api.mechanics.EnergyStorage;
import fr.traqueur.energylib.api.types.EnergyType;
import fr.traqueur.energylib.api.types.MechanicType;
import fr.traqueur.energylib.api.types.MechanicTypes;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EnergyNetwork {

    private final EnergyAPI api;
    private final Map<Location, EnergyComponent<?>> components;

    public EnergyNetwork(EnergyAPI api, EnergyComponent<?> component, Location location) {
        this.api = api;
        this.components = new ConcurrentHashMap<>();
        this.components.put(location,component);
    }

    public EnergyNetwork(EnergyAPI api) {
        this.api = api;
        this.components = new ConcurrentHashMap<>();
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
        Map<Location, EnergyComponent<?>> consumers = getComponentByType(MechanicTypes.CONSUMER);
        Map<Location, EnergyComponent<?>> producers = getComponentByType(MechanicTypes.PRODUCER);

        producers.forEach((location, producer) -> ((EnergyProducer) producer.getMechanic()).produce(location));

        for (EnergyComponent<?> consumerComponent : consumers.values()) {
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
                System.out.println("Le consommateur " + consumerComponent +
                        " n'a pas reçu suffisamment d'énergie.");
            }
        }

        for (EnergyComponent<?> producerComponent : producers.values()) {
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

            if (excessEnergy > 0) {
                System.out.println("L'énergie excédentaire du producteur " + producerComponent + " est perdue.");
            }
        }
    }

    public boolean isEmpty() {
        return this.components.isEmpty();
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

    /**
     * Récupère les composants connectés directement ou reliés par des transporteurs.
     * @param component Le composant dont on cherche les connexions.
     * @param type Le type de composant à rechercher (PRODUCER, STORAGE, etc.).
     * @return Liste des composants reliés directement ou via des transporteurs.
     */
    private List<EnergyComponent<?>> getConnectedComponents(EnergyComponent<?> component, MechanicType type) {
        Set<EnergyComponent<?>> visited = new HashSet<>();
        List<EnergyComponent<?>> connectedComponents = new ArrayList<>();
        dfsConnectedComponents(component, visited, connectedComponents, type);
        return connectedComponents;
    }

    /**
     * Effectue une recherche DFS pour trouver les composants connectés (directement ou via des transporteurs).
     * @param component Le composant actuel de la recherche.
     * @param visited Liste des composants déjà visités pour éviter les boucles.
     * @param connectedComponents Liste des composants reliés trouvés.
     * @param type Le type de composant recherché (PRODUCER, STORAGE, etc.).
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
            if (!visited.contains(neighbor) && (MechanicTypes.TRANSPORTER.isInstance(neighbor) || type.isInstance(neighbor))) {
                dfsConnectedComponents(neighbor, visited, connectedComponents, type);
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
