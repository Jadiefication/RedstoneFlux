package fr.traqueur.energylib.hooks;

import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import fr.traqueur.energylib.EnergyLib;
import fr.traqueur.energylib.api.EnergyAPI;
import fr.traqueur.energylib.api.EnergyManager;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockBreakEvent;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockPlaceEvent;
import io.th0rgal.oraxen.compatibilities.CompatibilityProvider;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class EnergyItemsAdderCompatibility implements Listener {

    private final EnergyAPI api;
    private final EnergyManager energyManager;

    public EnergyItemsAdderCompatibility(EnergyAPI api) {
        this.api = api;
        this.energyManager = api.getManager();
    }

    @EventHandler
    public void onPlace(CustomBlockPlaceEvent event) {
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

    @EventHandler
    public void onBreak(CustomBlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        if (!energyManager.isBlockComponent(location)) {
            return;
        }
        api.getScheduler().runAsync((t) -> energyManager.breakComponent(location));
    }

}
