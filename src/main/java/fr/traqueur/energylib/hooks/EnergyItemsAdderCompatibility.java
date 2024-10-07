package fr.traqueur.energylib.hooks;

import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import fr.traqueur.energylib.api.EnergyAPI;
import fr.traqueur.energylib.api.EnergyManager;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * This class is a compatibility for ItemsAdder plugin.
 * It allows to place and break energy components from ItemsAdder.
 */
public class EnergyItemsAdderCompatibility implements Listener {

    /**
     * The EnergyAPI instance.
     */
    private final EnergyAPI api;

    /**
     * The EnergyManager instance.
     */
    private final EnergyManager energyManager;

    /**
     * Constructor.
     * @param api The EnergyAPI instance.
     */
    public EnergyItemsAdderCompatibility(EnergyAPI api) {
        this.api = api;
        this.energyManager = api.getManager();
    }

    /**
     * Handle the place of a custom block.
     * @param event The event.
     */
    @EventHandler
    public void onPlace(CustomBlockPlaceEvent event) {
        ItemStack item = event.getItemInHand().clone();
        if (!energyManager.isComponent(item)) {
            return;
        }
        Location location = event.getBlock().getLocation();
        api.getScheduler().runAtLocation(location, (t) -> {
            var component = energyManager.createComponent(item);
            try {
                energyManager.placeComponent(component, location);
            } catch (SameEnergyTypeException ignored) {}
        });
    }

    /**
     * Handle the break of a custom block.
     * @param event The event.
     */
    @EventHandler
    public void onBreak(CustomBlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        if (!energyManager.isBlockComponent(location)) {
            return;
        }
        event.setCancelled(true);
        api.getScheduler().runAtLocation(location, (t) -> energyManager.breakComponent(event.getPlayer(), location));
    }

}
