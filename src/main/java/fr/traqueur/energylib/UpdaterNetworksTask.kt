package fr.traqueur.energylib

import fr.traqueur.energylib.api.EnergyManager

class UpdaterNetworksTask(private val manager: EnergyManager) : Runnable {
    override fun run() {
        manager.networks!!.forEach({ energyNetwork ->
            if (energyNetwork!!.chunk?.isLoaded == true) {
                energyNetwork.update()
            }
        })
    }
}
