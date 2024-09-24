package fr.traqueur.energylib;

import fr.traqueur.energylib.api.EnergyManager;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class EnergyListener implements Listener {

    private final EnergyManager energyManager;

    public EnergyListener(EnergyManager energyManager) {
        this.energyManager = energyManager;
    }

    @EventHandler
    public void onEnergyComponentPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (!this.energyManager.isComponent(item)) {
            return;
        }
        Location location = event.getBlockPlaced().getLocation();
        var component = this.energyManager.createComponent(item, location);
        try {
            this.energyManager.placeComponent(component, location);
        } catch (SameEnergyTypeException ignored) {}
    }

    @EventHandler
    public void onEnergyComponentBreak(BlockBreakEvent event) {

    }

}
