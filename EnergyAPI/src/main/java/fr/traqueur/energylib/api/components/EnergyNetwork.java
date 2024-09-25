package fr.traqueur.energylib.api.components;

import fr.traqueur.energylib.api.EnergyAPI;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import fr.traqueur.energylib.api.types.EnergyType;
import org.bukkit.Location;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        this.components.forEach((location,component) -> {
            this.api.getScheduler().runAtLocation(location, (task) -> component.update());
        });
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
}
