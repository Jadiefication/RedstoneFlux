package io.github.Jadiefication.redstoneflux

import io.github.Jadiefication.redstoneflux.api.EnergyManager

class UpdaterNetworksTask(private val manager: EnergyManager) : Runnable {
    override fun run() {
        manager.networks!!.forEach({ energyNetwork ->
            if (energyNetwork!!.chunk?.isLoaded == true) {
                energyNetwork.update()
            }
        })
    }
}
