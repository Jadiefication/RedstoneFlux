package io.github.Jadiefication.redstoneflux.api.mechanics

/**
 * This interface is used to represent an object that can measure energy/other value.
 * It is used to represent a voltmeter, an ammeter, an ohmmeter, etc.
 * It is used to check how much energy/other value is going through other objects.
 */
interface EnergyMeter : EnergyMechanic {
    /**
     * Returns the amount of energy passing through.
     * @return the amount of energy passing through
     */
    val passthroughEnergy: Double

    /**
     * Consumes the given amount of energy.
     * @param energyTaken the amount of energy to consume
     * @return the amount of energy that could be consumed
     */
    fun consumeEnergy(energyTaken: Double): Double
}