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
open class EnergyComponent<T : EnergyMechanic>(
    /**
     * The type of energy that this component uses.
     */
    var energyType: EnergyType?,
    /**
     * The mechanic that this component uses.
     */
    var mechanic: T?
) : BaseComponent<EnergyComponent<*>>() {
    /**
     * The components that this component is connected to.
     */
    override val connectedComponents: MutableSet<EnergyComponent<*>> = HashSet<EnergyComponent<*>>()

    override fun connectionFunction(component: EnergyComponent<*>) {
        val componentConnectEvent = EnergyComponentConnectEvent(component, this)
        Bukkit.getServer().pluginManager.callEvent(componentConnectEvent)
    }

    override fun disconnectionFunction(component: EnergyComponent<*>) {
        val componentDisconnectEvent = EnergyComponentDisconnectEvent(component, this)
        Bukkit.getServer().pluginManager.callEvent(componentDisconnectEvent)
    }

    override fun checker(component: EnergyComponent<*>): Boolean {
        if (this.energyType !== component.energyType) {
            throw SameEnergyTypeException()
        } else {
            return true
        }
    }
}
