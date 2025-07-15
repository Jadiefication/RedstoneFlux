package io.github.Jadiefication.redstoneflux.api.mechanics

import org.bukkit.Location

/**
 * An energy producer is a mechanic that can produce energy.
 */
interface EnergyProducer : EnergyMechanic {
    /**
     * Get the maximum rate of energy that can be produced.
     * @return the maximum rate of energy that can be produced
     */
    val maxRate: Double

    /**
     * Get the rate of energy that is currently being produced.
     * @return the rate of energy that is currently being produced
     */
    val rate: Double

    /**
     * Check if this energy producer can produce energy at the given location.
     * @param location the location to check
     * @return true if this energy producer can produce energy at the given location, false otherwise
     */
    fun canProduce(location: Location?): Boolean

    /**
     * Produce energy at the given location.
     * @param location the location to produce energy at
     */
    fun produce(location: Location?)

    /**
     * Extract energy from this energy producer.
     * @param v the amount of energy to extract
     * @return the amount of energy that was actually extracted
     */
    fun extractEnergy(v: Double): Double

    /**
     * Get the amount of excess energy that is currently stored in this energy producer.
     * @return the amount of excess energy that is currently stored in this energy producer
     */
    val excessEnergy: Double
}
