package fr.traqueur.testplugin.tests

import fr.traqueur.energylib.api.mechanics.EnergyConsumer

class BlockConsumer : EnergyConsumer {
    override var isEnable: Boolean = false
    private var energy = 0.0

    override val energyDemand: Double
        get() = 1000.0

    override fun receiveEnergy(energyToGive: Double) {
        this.energy += energyToGive
        if (this.energy >= this.energyDemand) {
            this.consumeEnergy()
        }
    }

    override fun consumeEnergy() {
        this.energy -= this.energyDemand
        println("Consuming 1000 energy, remaining: " + this.energy)
    }
}
