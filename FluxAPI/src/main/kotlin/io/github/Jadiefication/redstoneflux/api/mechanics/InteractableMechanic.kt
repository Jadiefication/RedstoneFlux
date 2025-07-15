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
}
