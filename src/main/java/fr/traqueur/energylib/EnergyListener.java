package fr.traqueur.energylib;

import fr.traqueur.energylib.api.EnergyAPI;
import fr.traqueur.energylib.api.EnergyManager;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This class is a listener for the energy system.
 * It allows to load and unload energy networks in chunks and to place and break energy components in the world.
 */
public class EnergyListener implements Listener {

    /**
     * The energy API
     */
    private final EnergyAPI api;

    /**
     * The energy manager
     */
    private final EnergyManager energyManager;

    /**
     * Create a new energy listener
     * @param api the energy API
     */
    public EnergyListener(EnergyAPI api) {
        this.api = api;
        this.energyManager = api.getManager();
    }

    /**
     * Load the energy networks in a chunk when it is loaded
     * @param event the event
     */
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        this.energyManager.loadNetworks(chunk);
        this.energyManager.enableInChunk(chunk);
    }

    /**
     * Unload the energy networks in a chunk when it is unloaded
     * @param event the event
     */
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        this.energyManager.disableInChunk(chunk);
    }

    /**
     * Place an energy component in the world
     * @param event the event
     */
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

    /**
     * Break an energy component in the world
     * @param event the event
     */
    @EventHandler
    public void onEnergyComponentBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        if (!this.energyManager.isBlockComponent(location)) {
            return;
        }
        this.api.getScheduler().runAsync((t) -> this.energyManager.breakComponent(location));
    }

}
