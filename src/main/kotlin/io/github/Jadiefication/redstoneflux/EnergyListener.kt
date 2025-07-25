package io.github.Jadiefication.redstoneflux

import io.github.Jadiefication.redstoneflux.api.EnergyAPI
import io.github.Jadiefication.redstoneflux.api.EnergyManager
import io.github.Jadiefication.redstoneflux.api.exceptions.SameEnergyTypeException
import io.github.Jadiefication.redstoneflux.api.mechanics.InteractableMechanic
import kotlinx.coroutines.launch
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.world.ChunkLoadEvent

/**
 * This class is a listener for the energy system.
 * It allows to load and unload energy networks in chunks and to place and break energy components in the world.
 */
class EnergyListener(
    /**
     * The energy API
     */
    private val api: EnergyAPI
) : Listener {

    /**
     * The energy manager
     */
    private val energyManager: EnergyManager = api.manager!!

    /**
     * Load energy networks in a chunk
     *
     * @param event the event
     */
    @EventHandler
    fun onChunkLoad(event: ChunkLoadEvent) {
        this.energyManager.loadNetworks(event.getChunk())
    }

    /**
     * Place an energy component in the world
     *
     * @param event the event
     */
    @EventHandler
    fun onEnergyComponentPlace(event: BlockPlaceEvent) {
        val item = event.getItemInHand()
        if (!this.energyManager.isComponent(item)) {
            return
        }
        val location = event.blockPlaced.location
        this.api.scheduler?.runAtLocation(location) { t ->
            val component = this.energyManager.createComponent(item)
            try {
                this.energyManager.placeComponent(component, location)
            } catch (ignored: SameEnergyTypeException) {
            }
        }
    }

    /**
     * Break an energy component in the world
     *
     * @param event the event
     */
    @EventHandler
    fun onEnergyComponentBreak(event: BlockBreakEvent) {
        val location = event.getBlock().location
        if (!this.energyManager.isBlockComponent(location)) {
            return
        }
        event.isCancelled = true
        this.energyManager.breakComponent(event.player, location)
    }

    /**
     * Interact with an energy component in the world
     *
     * @param event the event
     */
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val block = event.clickedBlock
        if (block == null) {
            return
        }

        val location = block.location
        val optComponent = this.energyManager.getComponentFromBlock(location)

        if (optComponent!!.isEmpty) {
            return
        }

        val component = optComponent.get()
        if (component.mechanic !is InteractableMechanic) {
            return
        }
        val interactableMechanic = component.mechanic as InteractableMechanic

        when (event.action) {
            Action.RIGHT_CLICK_BLOCK -> {
                interactableMechanic.onRightClick(event)
            }

            Action.LEFT_CLICK_BLOCK -> {
                interactableMechanic.onLeftClick(event)
            }

            Action.PHYSICAL -> {
                interactableMechanic.onPhysicalInteraction(event)
            }

            else -> {}
        }
    }
}
