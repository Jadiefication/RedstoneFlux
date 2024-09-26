package fr.traqueur.energylib.api.components;

import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import fr.traqueur.energylib.api.mechanics.EnergyMechanic;
import fr.traqueur.energylib.api.types.EnergyType;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class EnergyComponent<T extends EnergyMechanic> {

    private final EnergyType energyType;
    private final T mechanic;
    private final Set<EnergyComponent<?>> connectedComponents;

    public EnergyComponent(EnergyType energyType, T mechanic) {
        this.mechanic = mechanic;
        this.connectedComponents = new HashSet<>();
        this.energyType = energyType;
    }

    public void connect(EnergyComponent<?> component) throws SameEnergyTypeException {
        if(this.energyType != component.getEnergyType()) {
            throw new SameEnergyTypeException();
        }
        if(this.connectedComponents.contains(component)) {
            return;
        }
        this.connectedComponents.add(component);
        component.connect(this);
    }

    public void disconnect(EnergyComponent<?> component) {
        if(!this.connectedComponents.contains(component)) {
            return;
        }
        this.connectedComponents.remove(component);
        component.disconnect(this);
    }

    public Set<EnergyComponent<?>> getConnectedComponents() {
        return this.connectedComponents;
    }

    public EnergyType getEnergyType() {
        return this.energyType;
    }

    public T getMechanic() {
        return mechanic;
    }
}
