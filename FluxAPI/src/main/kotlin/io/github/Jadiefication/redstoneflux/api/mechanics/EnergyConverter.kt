package io.github.Jadiefication.redstoneflux.api.mechanics

import io.github.Jadiefication.redstoneflux.api.types.EnergyType

/**
 * This interface is used to represent an energy converter.
 * An energy converter is an object that can convert energy from one type to another.
 * It can also consume energy.
 */
interface EnergyConverter : EnergyMechanic {

    /**
     * The type of energy the converted accepts, null if accepts all types.
     * @return The type of energy the converted accepts, null if accepts all types.
     */
    val acceptedType: EnergyType?

    /**
     * The type of energy the converted outputs.
     * @return The type of energy the converted outputs.
     */
    val outputType: EnergyType

    /**
     * Consumes the given amount of energy.
     * @param energyTaken the amount of energy to consume
     * @return the amount of energy that could be consumed
     */
    fun consumeEnergy(energyTaken: Double): Double
}