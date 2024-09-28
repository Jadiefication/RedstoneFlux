package fr.traqueur.energylib.api.components;

import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import fr.traqueur.energylib.api.mechanics.EnergyMechanic;
import fr.traqueur.energylib.api.types.EnergyType;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a component that can be connected to other components.
 * @param <T> The mechanic that this component uses.
 */
public class EnergyComponent<T extends EnergyMechanic> {

    /**
     * The type of energy that this component uses.
     */
    private final EnergyType energyType;

    /**
     * The mechanic that this component uses.
     */
    private final T mechanic;

    /**
     * The components that this component is connected to.
     */
    private final Set<EnergyComponent<?>> connectedComponents;

    /**
     * Creates a new energy component.
     * @param energyType The type of energy that this component uses.
     * @param mechanic The mechanic that this component uses.
     */
    public EnergyComponent(EnergyType energyType, T mechanic) {
        this.mechanic = mechanic;
        this.connectedComponents = new HashSet<>();
        this.energyType = energyType;
    }

    /**
     * Connects this component to another component.
     * @param component The component to connect to.
     * @throws SameEnergyTypeException If the component uses not the same energy type as this component.
     */
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

    /**
     * Disconnects this component from another component.
     * @param component The component to disconnect from.
     */
    public void disconnect(EnergyComponent<?> component) {
        if(!this.connectedComponents.contains(component)) {
            return;
        }
        this.connectedComponents.remove(component);
        component.disconnect(this);
    }

    /**
     * Get the components that this component is connected to.
     */
    public Set<EnergyComponent<?>> getConnectedComponents() {
        return this.connectedComponents;
    }

    /**
     * Get the type of energy that this component uses.
     */
    public EnergyType getEnergyType() {
        return this.energyType;
    }

    /**
     * Get the mechanic that this component uses.
     */
    public T getMechanic() {
        return mechanic;
    }
}
