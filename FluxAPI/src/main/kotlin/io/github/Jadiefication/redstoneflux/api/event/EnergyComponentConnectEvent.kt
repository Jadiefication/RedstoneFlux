package io.github.Jadiefication.redstoneflux.api.event

import io.github.Jadiefication.redstoneflux.api.components.EnergyComponent
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Event which happens when a component connects to another.
 * @param component1 the component connecting.
 * @param component2 the component which is being connected to.
 */
class EnergyComponentConnectEvent(
    val component1: EnergyComponent<*>,
    val component2: EnergyComponent<*>
) : Event(true) {

    override fun getHandlers(): HandlerList {
        return HandlerList()
    }
}