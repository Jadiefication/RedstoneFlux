package fr.traqueur.energylib.api.mechanics;

import org.bukkit.Location;

/**
 * An energy producer is a mechanic that can produce energy.
 */
public interface EnergyProducer extends EnergyMechanic {

    /**
     * Get the maximum rate of energy that can be produced.
     * @return the maximum rate of energy that can be produced
     */
    double getMaxRate();

    /**
     * Get the rate of energy that is currently being produced.
     * @return the rate of energy that is currently being produced
     */
    double getRate();

    /**
     * Check if this energy producer can produce energy at the given location.
     * @param location the location to check
     * @return true if this energy producer can produce energy at the given location, false otherwise
     */
    boolean canProduce(Location location);

    /**
     * Produce energy at the given location.
     * @param location the location to produce energy at
     */
    void produce(Location location);

    /**
     * Extract energy from this energy producer.
     * @param v the amount of energy to extract
     * @return the amount of energy that was actually extracted
     */
    double extractEnergy(double v);

    /**
     * Get the amount of excess energy that is currently stored in this energy producer.
     * @return the amount of excess energy that is currently stored in this energy producer
     */
    double getExcessEnergy();

}
