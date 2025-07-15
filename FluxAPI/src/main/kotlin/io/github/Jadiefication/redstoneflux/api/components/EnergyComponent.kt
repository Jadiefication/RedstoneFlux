package io.github.Jadiefication.redstoneflux.api.components

import io.github.Jadiefication.redstoneflux.api.exceptions.SameEnergyTypeException
import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyMechanic
import io.github.Jadiefication.redstoneflux.api.types.EnergyType

/**
 * Represents a component that can be connected to other components.
 * @param <T> The mechanic that this component uses.
</T> */
class EnergyComponent<T : EnergyMechanic?>(
    energyType: EnergyType?,
    /**
     * The mechanic that this component uses.
     */
    val mechanic: T?
) {
    /**
     * Get the type of energy that this component uses.
     */
    /**
     * The type of energy that this component uses.
     */
    val energyType: EnergyType?

    /**
     * Get the mechanic that this component uses.
     */

    /**
     * Get the components that this component is connected to.
     */
    /**
     * The components that this component is connected to.
     */
    val connectedComponents: MutableSet<EnergyComponent<*>?>

    /**
     * Creates a new energy component.
     * @param energyType The type of energy that this component uses.
     * @param mechanic The mechanic that this component uses.
     */
    init {
        this.connectedComponents = HashSet<EnergyComponent<*>?>()
        this.energyType = energyType
    }

    /**
     * Connects this component to another component.
     * @param component The component to connect to.
     * @throws SameEnergyTypeException If the component uses not the same energy type as this component.
     */
    @Throws(SameEnergyTypeException::class)
    fun connect(component: EnergyComponent<*>) {
        if (this.energyType !== component.energyType) {
            throw SameEnergyTypeException()
        }
        if (this.connectedComponents.contains(component)) {
            return
        }
        this.connectedComponents.add(component)
        component.connect(this)
    }

    /**
     * Disconnects this component from another component.
     * @param component The component to disconnect from.
     */
    fun disconnect(component: EnergyComponent<*>) {
        if (!this.connectedComponents.contains(component)) {
            return
        }
        this.connectedComponents.remove(component)
        component.disconnect(this)
    }
}
