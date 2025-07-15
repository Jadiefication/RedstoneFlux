package fr.traqueur.testplugin.tests

import fr.traqueur.energylib.api.mechanics.EnergyStorage
import kotlin.math.min

class BlockStorage : EnergyStorage {
    override val maximumCapacity: Double = 10000.0
    override var storedEnergy: Double = 0.0
        private set

    override fun storeEnergy(energyStored: Double): Double {
        val energy = min(this.availableCapacity, energyStored)
        this.storedEnergy += energy
        println("BlockStorage stored " + energyStored + " energy. Total: " + this.storedEnergy)
        return energy
    }

    override fun consumeEnergy(energyTaken: Double): Double {
        val energy = min(this.storedEnergy, energyTaken)
        this.storedEnergy -= energy
        println("BlockStorage consumed " + energy + " energy. Total: " + this.storedEnergy)
        return energy
    }
}
