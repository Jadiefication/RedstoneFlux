package io.github.Jadiefication.redstoneflux.api.mechanics

/**
 * This interface is used to represent an object that can store energy.
 * It is used to represent a battery, a capacitor, a power cell, etc.
 * It is used to store energy and to provide energy to other objects.
 */
interface EnergyStorage : EnergyMechanic {
    /**
     * Returns the maximum amount of energy that can be stored.
     * @return the maximum amount of energy that can be stored
     */
    val maximumCapacity: Double

    val availableCapacity: Double
        /**
         * Returns the amount of energy that can be stored.
         * @return the amount of energy that can be stored
         */
        get() = this.maximumCapacity - this.storedEnergy

    /**
     * Stores the given amount of energy.
     * @param energyStored the amount of energy to store
     * @return the amount of energy that could be stored
     */
    fun storeEnergy(energyStored: Double): Double

    /**
     * Returns the amount of energy stored.
     * @return the amount of energy stored
     */
    val storedEnergy: Double

    /**
     * Consumes the given amount of energy.
     * @param energyTaken the amount of energy to consume
     * @return the amount of energy that could be consumed
     */
    @Deprecated(
        message = "Weird naming, deprecated since 1.3.3",
        replaceWith = ReplaceWith("grabEnergy"),
        level = DeprecationLevel.HIDDEN
    )
    fun consumeEnergy(energyTaken: Double): Double

    /**
     * Attempts to grab the wanted energy from the storage.
     * @param wantedEnergy the amount of energy it wants.
     * @return the amount of energy that could be given.
     */
    fun grabEnergy(wantedEnergy: Double): Double
}
