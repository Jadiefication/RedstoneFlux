package fr.traqueur.energylib;

import fr.traqueur.energylib.api.EnergyAPI;
import fr.traqueur.energylib.api.EnergyManager;
import fr.traqueur.energylib.api.components.EnergyComponent;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import fr.traqueur.energylib.api.mechanics.InteractableMechanic;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
     *
     * @param api the energy API
     */
    public EnergyListener(EnergyAPI api) {
        this.api = api;
        this.energyManager = api.getManager();
    }

    /*

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        this.energyManager.loadNetworks(chunk);
        this.energyManager.enableInChunk(chunk);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        this.energyManager.disableInChunk(chunk);
    }
    */

    /**
     * Place an energy component in the world
     *
     * @param event the event
     */
    @EventHandler
    public void onEnergyComponentPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand().clone();
        if (!this.energyManager.isComponent(item)) {
            return;
        }
        Location location = event.getBlockPlaced().getLocation();
        this.api.getScheduler().runAtLocation(location, (t) -> {
            var component = this.energyManager.createComponent(item);
            try {
                this.energyManager.placeComponent(component, location);
            } catch (SameEnergyTypeException ignored) {
            }
        });
    }

    /**
     * Break an energy component in the world
     *
     * @param event the event
     */
    @EventHandler
    public void onEnergyComponentBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        if (!this.energyManager.isBlockComponent(location)) {
            return;
        }
        event.setCancelled(true);
        this.api.getScheduler().runAtLocation(location, (t) -> this.energyManager.breakComponent(event.getPlayer(), location));
    }

    /**
     * Interact with an energy component in the world
     *
     * @param event the event
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        Location location = block.getLocation();
        var optComponent = this.energyManager.getComponentFromBlock(location);

        if (optComponent.isEmpty()) {
            return;
        }

        EnergyComponent<?> component = optComponent.get();
        if (!(component.getMechanic() instanceof InteractableMechanic interactableMechanic)) {
            return;
        }

        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK -> {
                interactableMechanic.onRightClick(event);
            }
            case LEFT_CLICK_BLOCK -> {
                interactableMechanic.onLeftClick(event);
            }
            default -> {
            }
        }
    }

}
