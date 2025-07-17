package io.github.Jadiefication.redstoneflux.api.components

import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyMechanic

fun <T : EnergyMechanic> build(builder: EnergyComponent<T>.() -> Unit, component: EnergyComponent<T>? = null): EnergyComponent<T> {
    val theComponent = component ?: EnergyComponent<T>(null, null)
    theComponent.builder()
    return theComponent
}