package io.github.Jadiefication.redstoneflux.api

import com.tcoded.folialib.impl.PlatformScheduler
import kotlinx.coroutines.CoroutineScope

/**
 * The main class of the API.
 */
interface EnergyAPI {
    /**
     * Get the instance of the manager.
     * @return the instance of the manager.
     */
    val managers: MutableSet<Manager<*>>

    /**
     * Get the coroutine scope.
     * @return the coroutine scope.
     */
    val scope: CoroutineScope

    /**
     * Get the instance of the scheduler.
     * @return the instance of the scheduler.
     */
    val scheduler: PlatformScheduler?

    /**
     * Get if the API is in debug mode.
     * @return true if the API is in debug mode.
     */

    /**
     * Set the debug mode of the API.
     * @param debug the new debug mode.
     */
    var isDebug: Boolean

    fun addManager(manager: Manager<*>) {
        managers.add(manager)
    }
}
