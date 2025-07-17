package io.github.Jadiefication.redstoneflux.api.mechanics

import org.bukkit.event.player.PlayerInteractEvent

/**
 * This interface represents a mechanic that can be interacted with.
 */
interface InteractableMechanic {
    /**
     * Called when the mechanic is right-clicked.
     * @param event the event
     */
    fun onRightClick(event: PlayerInteractEvent?)

    /**
     * Called when the mechanic is left-clicked.
     * @param event the event
     */
    fun onLeftClick(event: PlayerInteractEvent?)

    /**
     * Called when the mechanic is for example:
     *      <ul>
     *      <li>Jumping on soil
     *      <li>Standing on pressure plate
     *      <li>Triggering redstone ore
     *      <li>Triggering tripwire
     *      </ul>
     * @param event the event
     */
    fun onPhysicalInteraction(event: PlayerInteractEvent?)
}
