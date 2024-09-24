package fr.traqueur.energylib.api.components;

import fr.traqueur.energylib.api.EnergyAPI;
import fr.traqueur.energylib.api.EnergyType;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import org.bukkit.Location;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnergyNetwork {

    private final EnergyAPI api;
    private final Map<EnergyComponent, Location> components;

    public EnergyNetwork(EnergyAPI api, EnergyComponent component, Location location) {
        this.api = api;
        this.components = new ConcurrentHashMap<>();
        this.components.put(component, location);
    }

    public void addComponent(EnergyComponent component, Location location) throws SameEnergyTypeException {
        for (Map.Entry<EnergyComponent, Location> energyComponentLocationEntry : this.components.entrySet()) {
            EnergyComponent energyComponent = energyComponentLocationEntry.getKey();
            Location componentLocation = energyComponentLocationEntry.getValue();
            if (componentLocation.distance(location) == 1) {
                energyComponent.connect(component);
                component.connect(energyComponent);
            }
        }
        this.components.put(component, location);
    }

    public boolean contains(Location neibhor) {
        return this.components.containsValue(neibhor);
    }

    public void mergeWith(EnergyNetwork network) {
        this.components.putAll(network.components);
    }

    public EnergyType getEnergyType() {
        return this.getRoot().getEnergyType();
    }

    private EnergyComponent getRoot() {
        return this.components.keySet().iterator().next();
    }

    public void update() {
        this.components.forEach((component, location) -> {
            this.api.getScheduler().runAtLocation(location, (task) -> component.update());
        });
    }
}
