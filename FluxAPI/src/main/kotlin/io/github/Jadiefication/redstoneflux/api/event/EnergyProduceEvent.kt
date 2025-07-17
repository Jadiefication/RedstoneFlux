package io.github.Jadiefication.redstoneflux.api.event

import io.github.Jadiefication.redstoneflux.api.components.EnergyComponent
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Event which happens when a machine produces energy.
 * @param energyProduced the energy produced by the machine.
 * @param producer the machine which produced said energy.
 */
class EnergyProduceEvent(
    val energyProduced: Double,
    val producer: EnergyComponent<*>
) : Event(true) {

    override fun getHandlers(): HandlerList {
        return HandlerList()
    }
}