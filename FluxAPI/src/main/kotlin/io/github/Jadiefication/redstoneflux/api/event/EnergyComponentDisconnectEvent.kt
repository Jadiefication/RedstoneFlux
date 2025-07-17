package io.github.Jadiefication.redstoneflux.api.event

import io.github.Jadiefication.redstoneflux.api.components.EnergyComponent
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Event which happens when a components disconnect to another.
 * @param component1 the component disconnecting.
 * @param component2 the component which is being disconnected from.
 */
class EnergyComponentDisconnectEvent(
    val component1: EnergyComponent<*>,
    val component2: EnergyComponent<*>
) : Event() {

    override fun getHandlers(): HandlerList {
        return HandlerList()
    }
}