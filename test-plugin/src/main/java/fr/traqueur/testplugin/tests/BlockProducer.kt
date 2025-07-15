package fr.traqueur.testplugin.tests

import fr.traqueur.energylib.api.mechanics.EnergyProducer
import fr.traqueur.energylib.api.mechanics.InteractableMechanic
import fr.traqueur.testplugin.TestPlugin.Companion.plugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.event.player.PlayerInteractEvent
import kotlin.math.min

class BlockProducer : EnergyProducer, InteractableMechanic {
    override val maxRate: Double = 2000.0
    private var age = 0
    private var producedEnergy = 0.0

    override val rate: Double
        get() {
            if (age < 1000) {
                return maxRate
            } else if (age > 1000 && age < 2000) {
                return 0.5 * maxRate
            } else {
                return 0.05 * maxRate
            }
        }

    override fun canProduce(location: Location?): Boolean {
        return location?.block?.getRelative(BlockFace.UP)?.lightFromSky?.toInt() == 15
    }

    override fun produce(location: Location?) {
        if (this.canProduce(location)) {
            println("Producing " + this.rate + " energy at " + location)
            age++
            producedEnergy = this.rate
        }
    }

    override fun extractEnergy(v: Double): Double {
        val energy = min(v, producedEnergy)
        producedEnergy -= energy
        return energy
    }

    override val excessEnergy: Double
        get() {
            val excess = producedEnergy
            producedEnergy = 0.0
            return excess
        }

    override fun onRightClick(event: PlayerInteractEvent?) {
        plugin.server.broadcastMessage("Right click")
    }

    override fun onLeftClick(event: PlayerInteractEvent?) {
        plugin.server.broadcastMessage("Left click")
    }
}
