package fr.traqueur.energylib.api.types;

import fr.traqueur.energylib.api.components.EnergyComponent;
import fr.traqueur.energylib.api.mechanics.*;

/**
 * The type of a mechanic.
 */
public enum MechanicType {

    /**
     * Represents an energy producer.
     */
    PRODUCER(EnergyProducer.class),

    /**
     * Represents an energy consumer.
     */
    CONSUMER(EnergyConsumer.class),

    /**
     * Represents an energy storage.
     */
    STORAGE(EnergyStorage.class),

    /**
     * Represents an energy transporter.
     */
    TRANSPORTER(EnergyTransporter.class);

    /**
     * The class of the mechanic.
     */
    private final Class<? extends EnergyMechanic> clazz;

    /**
     * Constructor of MechanicType.
     * @param clazz the class of the mechanic.
     */
    MechanicType(Class<? extends EnergyMechanic> clazz) {
        this.clazz = clazz;
    }

    public static MechanicType fromComponent(EnergyComponent<?> component) {
        for (MechanicType type : values()) {
            if (type.isInstance(component)) {
                return type;
            }
        }
        throw new IllegalArgumentException("The component is not an instance of any mechanic type.");
    }

    /**
     * Get the class of the mechanic.
     * @return the class of the mechanic.
     */
    public Class<? extends EnergyMechanic> getClazz() {
        return clazz;
    }

    /**
     * Check if the mechanic is an instance of the given component.
     * @param component the component to check.
     * @return true if the mechanic is an instance of the given component, false otherwise.
     */
    public boolean isInstance(EnergyComponent<?> component) {
        return this.clazz.isInstance(component.getMechanic());
    }
}
