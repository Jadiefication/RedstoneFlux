package io.github.Jadiefication.redstoneflux.api.event

import io.github.Jadiefication.redstoneflux.api.components.EnergyComponent
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Event which happens when a machine doesn't have enough energy.
 * @param requiredEnergy the energy amount required.
 * @param givenEnergy the energy amount said machine needs.
 * @param consumer the machine consuming said energy.
 */
class NotEnoughEnergyEvent(
    val requiredEnergy: Double,
    val givenEnergy: Double,
    val consumer: EnergyComponent<*>
) : Event(true) {

    override fun getHandlers(): HandlerList {
        return HandlerList()
    }
}