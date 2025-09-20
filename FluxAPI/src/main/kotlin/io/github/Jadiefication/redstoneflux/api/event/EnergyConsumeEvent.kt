package io.github.Jadiefication.redstoneflux.api.event

import io.github.Jadiefication.redstoneflux.api.components.EnergyComponent
import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyConsumer
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Event which happens when a machine consumes energy.
 * @param energyConsumed the energy consumed by the machine.
 * @param target the machine whose energy got consumed.
 * @param consumer the machine which consumes the energy.
 */
class EnergyConsumeEvent(
    val energyConsumed: Double,
    val target: EnergyComponent<*>,
    val consumer: EnergyComponent<EnergyConsumer>,
) : Event(true) {
    override fun getHandlers(): HandlerList = HandlerList()
}
