package fr.traqueur.energylib.api;

import com.tcoded.folialib.impl.PlatformScheduler;

/**
 * The main class of the API.
 */
public interface EnergyAPI {

    /**
     * Get the instance of the manager.
     * @return the instance of the manager.
     */
    EnergyManager getManager();

    /**
     * Get the instance of the scheduler.
     * @return the instance of the scheduler.
     */
    PlatformScheduler getScheduler();

    /**
     * Get if the API is in debug mode.
     * @return true if the API is in debug mode.
     */
    boolean isDebug();

    /**
     * Set the debug mode of the API.
     * @param debug the new debug mode.
     */
    void setDebug(boolean debug);
}
