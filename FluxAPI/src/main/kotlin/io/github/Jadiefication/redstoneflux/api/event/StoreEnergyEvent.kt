package io.github.Jadiefication.redstoneflux.api.event

import io.github.Jadiefication.redstoneflux.api.components.EnergyComponent
import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyProducer
import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyStorage
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
    val target: EnergyComponent<EnergyStorage>,
    val producer: EnergyComponent<EnergyProducer>
) : Event(true) {

    override fun getHandlers(): HandlerList {
        return HandlerList()
    }
}