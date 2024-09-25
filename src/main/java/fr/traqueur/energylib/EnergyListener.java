package fr.traqueur.energylib;

import fr.traqueur.energylib.api.EnergyAPI;
import fr.traqueur.energylib.api.EnergyManager;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class EnergyListener implements Listener {

    private final EnergyAPI api;
    private final EnergyManager energyManager;

    public EnergyListener(EnergyAPI api) {
        this.api = api;
        this.energyManager = api.getManager();
    }

    @EventHandler
    public void onEnergyComponentPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (!this.energyManager.isComponent(item)) {
            return;
        }
        Location location = event.getBlockPlaced().getLocation();
        this.api.getScheduler().runAsync((t) -> {
            var component = this.energyManager.createComponent(item);
            try {
                this.energyManager.placeComponent(component, location);
            } catch (SameEnergyTypeException ignored) {}
        });
    }

    @EventHandler
    public void onEnergyComponentBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        if (!this.energyManager.isBlockComponent(location)) {
            return;
        }
        this.api.getScheduler().runAsync((t) -> this.energyManager.breakComponent(location));
    }

}
