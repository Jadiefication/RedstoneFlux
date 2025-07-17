package io.github.Jadiefication.redstoneflux.api.components

import io.github.Jadiefication.redstoneflux.api.event.EnergyComponentConnectEvent
import io.github.Jadiefication.redstoneflux.api.event.EnergyComponentDisconnectEvent
import io.github.Jadiefication.redstoneflux.api.exceptions.SameEnergyTypeException
import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyMechanic
import io.github.Jadiefication.redstoneflux.api.types.EnergyType
import org.bukkit.Bukkit

/**
 * Represents a component that can be connected to other components.
 * @param <T> The mechanic that this component uses.
</T> */
open class EnergyComponent<T : EnergyMechanic?>(
    /**
     * The type of energy that this component uses.
     */
    var energyType: EnergyType?,
    /**
     * The mechanic that this component uses.
     */
    var mechanic: T?
) {
    /**
     * The components that this component is connected to.
     */
    internal val connectedComponents: MutableSet<EnergyComponent<*>?> = HashSet<EnergyComponent<*>?>()

    /**
     * Connects this component to another component.
     * @param component The component to connect to.
     * @throws SameEnergyTypeException If the component uses not the same energy type as this component.
     */
    @Throws(SameEnergyTypeException::class)
    internal fun connect(component: EnergyComponent<*>) {
        if (this.energyType !== component.energyType) {
            throw SameEnergyTypeException()
        }
        if (this.connectedComponents.contains(component)) {
            return
        }
        val componentConnectEvent = EnergyComponentConnectEvent(component, this)
        Bukkit.getServer().pluginManager.callEvent(componentConnectEvent)
        this.connectedComponents.add(component)
        component.connect(this)
    }

    /**
     * Disconnects this component from another component.
     * @param component The component to disconnect from.
     */
    internal fun disconnect(component: EnergyComponent<*>) {
        if (!this.connectedComponents.contains(component)) {
            return
        }
        val componentDisconnectEvent = EnergyComponentDisconnectEvent(component, this)
        Bukkit.getServer().pluginManager.callEvent(componentDisconnectEvent)
        this.connectedComponents.remove(component)
        component.disconnect(this)
    }
}
