package fr.traqueur.energylib.api.components;

import com.google.common.util.concurrent.AtomicDouble;
import fr.traqueur.energylib.api.EnergyAPI;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import fr.traqueur.energylib.api.mechanics.EnergyConsumer;
import fr.traqueur.energylib.api.mechanics.EnergyStorage;
import fr.traqueur.energylib.api.types.EnergyType;
import fr.traqueur.energylib.api.types.MechanicType;
import fr.traqueur.energylib.api.types.MechanicTypes;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public boolean contains(Location neibhor) {
        return this.components.containsKey(neibhor);
    }

    public void mergeWith(EnergyNetwork network) {
        this.components.putAll(network.components);
    }

    public void update() {
        double produced = this.handleMechanic(MechanicTypes.PRODUCER);
        double canTransfer = this.handleMechanic(MechanicTypes.TRANSPORTER);
        double canStock = this.handleMechanic(MechanicTypes.STORAGE);
        double mustHave = this.handleMechanic(MechanicTypes.CONSUMER);

        if (produced >= mustHave) {
            distributeEnergyToConsumers(mustHave);

            double excess = produced - mustHave;

            if (excess > 0 && canStock > 0) {
                double energyToStore = Math.min(excess, canStock);
                double wasted = storeEnergy(energyToStore);
                excess -= energyToStore;
                excess += wasted;
            }

            if (excess > 0) {
                handleWaste(excess);
            }

        } else {
            double totalAvailableEnergy = produced;
            double deficit = mustHave - totalAvailableEnergy;

            if (deficit > 0) {
                double lake = consumeStoredEnergy(deficit);
                totalAvailableEnergy += deficit;
                totalAvailableEnergy -= lake;
                deficit = mustHave - totalAvailableEnergy;
            }

            if (totalAvailableEnergy >= mustHave) {
                distributeEnergyToConsumers(mustHave);
            } else {
                distributeEnergyToConsumers(totalAvailableEnergy);
                handleShortage(deficit);
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

    private void distributeEnergyToConsumers(double availableEnergy) {
        double totalDemand = this.handleMechanic(MechanicTypes.CONSUMER);
        if (totalDemand == 0) return;

        for (EnergyComponent<?> component : getComponents().values()) {
            if (component.getMechanic() instanceof EnergyConsumer consumer) {
                double energyNeeded = consumer.getEnergyDemand();
                double energyToGive = (energyNeeded / totalDemand) * availableEnergy;
                consumer.receiveEnergy(energyToGive);
            }
        }
    }

    private double storeEnergy(double energyToStore) {

        for (EnergyComponent<?> component : getComponents().values()) {
            if (component.getMechanic() instanceof EnergyStorage storage) {
                double availableCapacity = storage.getAvailableCapacity();
                double energyStored = Math.min(availableCapacity, energyToStore);

                double wasted = storage.storeEnergy(energyStored);

                energyToStore -= energyStored;
                energyToStore += wasted;

                if (energyToStore <= 0) {
                    break;
                }
            }
        }
        return energyToStore;
    }

    private double consumeStoredEnergy(double energyToRetrieve) {
        for (EnergyComponent<?> component : getComponents().values()) {
            if (component.getMechanic() instanceof EnergyStorage storage) {
                double energyAvailable = storage.getStoredEnergy();
                double energyTaken = Math.min(energyAvailable, energyToRetrieve);

                storage.consumeEnergy(energyTaken);

                energyToRetrieve -= energyTaken;

                if (energyToRetrieve <= 0) {
                    break;
                }
            }
        }
        return energyToRetrieve;
    }

    private void handleShortage(double energyDeficit) {
        System.out.println("Energy shortage of " + energyDeficit + " units. Consumers may not work properly.");
    }

    private void handleWaste(double excessEnergy) {
        System.out.println("Excess energy of " + excessEnergy + " units has been wasted.");
    }


    private double handleMechanic(MechanicType type) {
        AtomicDouble energy = new AtomicDouble(0);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        this.getComponentByType(type).forEach((location,component) ->
                futures.add(this.api.getScheduler().runAtLocation(location, (task) -> energy.getAndAdd(component.update(location))))
        );
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return energy.get();
    }

    private Map<Location, EnergyComponent<?>> getComponentByType(MechanicType type) {
        return this.components.entrySet()
                .stream()
                .filter(entry -> type.getClazz().isAssignableFrom(entry.getValue().getMechanic().getClass()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
