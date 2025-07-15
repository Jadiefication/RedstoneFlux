package fr.traqueur.energylib.api.types

import fr.traqueur.energylib.api.components.EnergyComponent
import fr.traqueur.energylib.api.mechanics.*

/**
 * The type of a mechanic.
 */
enum class MechanicType
/**
 * Constructor of MechanicType.
 * @param clazz the class of the mechanic.
 */(
    /**
     * The class of the mechanic.
     */
    val clazz: Class<out EnergyMechanic?>
) {
    /**
     * Represents an energy producer.
     */
    PRODUCER(EnergyProducer::class.java),

    /**
     * Represents an energy consumer.
     */
    CONSUMER(EnergyConsumer::class.java),

    /**
     * Represents an energy storage.
     */
    STORAGE(EnergyStorage::class.java),

    /**
     * Represents an energy transporter.
     */
    TRANSPORTER(EnergyTransporter::class.java);

    /**
     * Get the class of the mechanic.
     * @return the class of the mechanic.
     */

    /**
     * Check if the mechanic is an instance of the given component.
     * @param component the component to check.
     * @return true if the mechanic is an instance of the given component, false otherwise.
     */
    fun isInstance(component: EnergyComponent<*>): Boolean {
        return this.clazz.isInstance(component.mechanic)
    }

    companion object {
        fun fromComponent(component: EnergyComponent<*>): MechanicType {
            for (type in entries) {
                if (type.isInstance(component)) {
                    return type
                }
            }
            throw IllegalArgumentException("The component is not an instance of any mechanic type.")
        }
    }
}
