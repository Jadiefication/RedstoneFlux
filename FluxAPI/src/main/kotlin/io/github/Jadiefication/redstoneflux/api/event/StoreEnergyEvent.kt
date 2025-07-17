package io.github.Jadiefication.redstoneflux.api.event

import io.github.Jadiefication.redstoneflux.api.components.EnergyComponent
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Event which happens when a machine stores energy in another.
 * @param amount the energy produced by the machine.
 * @param target the machine storing the energy.
 * @param producer the machine which produced said energy.
 */
class StoreEnergyEvent(
    val amount: Double,
    val target: EnergyComponent<*>,
    val producer: EnergyComponent<*>
) : Event(true) {

    override fun getHandlers(): HandlerList {
        return HandlerList()
    }
}