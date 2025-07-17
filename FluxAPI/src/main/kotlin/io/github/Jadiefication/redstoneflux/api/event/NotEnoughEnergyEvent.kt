package io.github.Jadiefication.redstoneflux.api.event

import io.github.Jadiefication.redstoneflux.api.components.EnergyComponent
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class NotEnoughEnergyEvent(
    val requiredEnergy: Double,
    val givenEnergy: Double,
    val consumer: EnergyComponent<*>
) : Event(true) {

    override fun getHandlers(): HandlerList {
        return HandlerList()
    }
}