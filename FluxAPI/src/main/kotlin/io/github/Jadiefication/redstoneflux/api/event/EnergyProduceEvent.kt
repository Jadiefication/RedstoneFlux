package io.github.Jadiefication.redstoneflux.api.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Event which happens when a machine produces energy.
 * @param energyProduced the energy produced by the machine.
 */
class EnergyProduceEvent(
    val energyProduced: Double
) : Event(true) {

    override fun getHandlers(): HandlerList {
        return HandlerList()
    }
}