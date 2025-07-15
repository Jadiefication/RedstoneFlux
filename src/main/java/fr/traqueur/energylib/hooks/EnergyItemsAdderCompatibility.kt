/*package fr.traqueur.energylib.hooks

import fr.traqueur.energylib.api.EnergyAPI
import fr.traqueur.energylib.api.EnergyManager
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

/**
 * This class is a compatibility for ItemsAdder plugin.
 * It allows to place and break energy components from ItemsAdder.
 */
class EnergyItemsAdderCompatibility(
    /**
     * The EnergyAPI instance.
     */
    private val api: EnergyAPI
) : Listener {

    /**
     * The EnergyManager instance.
     */
    private val energyManager: EnergyManager = api.manager!!

    /**
     * Handle the place of a custom block.
     * @param event The event.
     */
    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        val item = event.getItemInHand().clone()
        if (!energyManager.isComponent(item)) {
            return
        }
        val location = event.getBlock().location
        api.scheduler?.runAtLocation(location, { t ->
            val component = energyManager.createComponent(item)
            try {
                energyManager.placeComponent(component, location)
            } catch (ignored: SameEnergyTypeException) {
            }
        })
    }

    /**
     * Handle the break of a custom block.
     * @param event The event.
     */
    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        val location = event.getBlock().location
        if (!energyManager.isBlockComponent(location)) {
            return
        }
        event.isCancelled = true
        api.scheduler?.runAtLocation(location, { t -> energyManager.breakComponent(event.getPlayer(), location) })
    }
}/

 */
