/*package fr.traqueur.energylib.hooks

import fr.traqueur.energylib.EnergyLib
import fr.traqueur.energylib.api.EnergyAPI
import fr.traqueur.energylib.api.EnergyManager
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockBreakEvent
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockPlaceEvent
import io.th0rgal.oraxen.compatibilities.CompatibilityProvider
import org.bukkit.event.EventHandler

/**
 * This class is a compatibility provider for Oraxen.
 * It allows to place and break energy components in the world.
 */
class EnergyNexoCompatibility : CompatibilityProvider<EnergyLib?>() {
    /**
 * Handle the event when a player places a custom not block from oraxen
 * @param event the event
 */
    @EventHandler
    fun onPlace(event: OraxenNoteBlockPlaceEvent) {
        val api: EnergyAPI = this.getPlugin() as EnergyLib
        val energyManager: EnergyManager = api.manager!!
        val item = event.getItemInHand().clone()
        if (!energyManager.isComponent(item)) {
            return
        }
        val location = event.getBlock().getLocation()
        api.scheduler.runAtLocation(location, { t ->
            val component = energyManager.createComponent(item)
            try {
                energyManager.placeComponent(component, location)
            } catch (ignored: SameEnergyTypeException) {
            }
        })
    }

    /**
 * Handle the event when a player breaks a custom not block from oraxen
 * @param event the event
 */
    @EventHandler
    fun onBreak(event: OraxenNoteBlockBreakEvent) {
        val api: EnergyAPI = this.getPlugin() as EnergyLib
        val energyManager: EnergyManager = api.manager!!
        val location = event.getBlock().getLocation()
        if (!energyManager.isBlockComponent(location)) {
            return
        }
        event.setCancelled(true)
        api.scheduler.runAtLocation(location, { t -> energyManager.breakComponent(event.getPlayer(), location) })
    }
}*/
