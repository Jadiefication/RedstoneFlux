package fr.traqueur.energylib.api.mechanics;

import org.bukkit.event.player.PlayerInteractEvent;

/**
 * This interface represents a mechanic that can be interacted with.
 */
public interface InteractableMechanic {

    /**
     * Called when the mechanic is right-clicked.
     * @param event the event
     */
    void onRightClick(PlayerInteractEvent event);

    /**
     * Called when the mechanic is left-clicked.
     * @param event the event
     */
    void onLeftClick(PlayerInteractEvent event);

}
