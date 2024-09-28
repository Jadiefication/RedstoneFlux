package fr.traqueur.energylib.hooks;

import fr.traqueur.energylib.EnergyLib;
import fr.traqueur.energylib.api.EnergyAPI;
import fr.traqueur.energylib.api.EnergyManager;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockBreakEvent;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockPlaceEvent;
import io.th0rgal.oraxen.compatibilities.CompatibilityProvider;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

/**
 * This class is a compatibility provider for Oraxen.
 * It allows to place and break energy components in the world.
 */
public class EnergyOraxenCompatibility extends CompatibilityProvider<EnergyLib> {


    /**
     * Handle the event when a player places a custom not block from oraxen
     * @param event the event
     */
    @EventHandler
    public void onPlace(OraxenNoteBlockPlaceEvent event) {
        EnergyAPI api = (EnergyLib) this.getPlugin();
        EnergyManager energyManager = api.getManager();
        ItemStack item = event.getItemInHand();
        if (!energyManager.isComponent(item)) {
            return;
        }
        Location location = event.getBlock().getLocation();
        api.getScheduler().runAsync((t) -> {
            var component = energyManager.createComponent(item);
            try {
                energyManager.placeComponent(component, location);
            } catch (SameEnergyTypeException ignored) {}
        });
    }

    /**
     * Handle the event when a player breaks a custom not block from oraxen
     * @param event the event
     */
    @EventHandler
    public void onBreak(OraxenNoteBlockBreakEvent event) {
        EnergyAPI api = (EnergyLib) this.getPlugin();
        EnergyManager energyManager = api.getManager();
        Location location = event.getBlock().getLocation();
        if (!energyManager.isBlockComponent(location)) {
            return;
        }
        api.getScheduler().runAsync((t) -> energyManager.breakComponent(location));
    }

}
