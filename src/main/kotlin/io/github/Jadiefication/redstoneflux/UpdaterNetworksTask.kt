package io.github.Jadiefication.redstoneflux

import io.github.Jadiefication.redstoneflux.api.EnergyManager
import io.github.Jadiefication.redstoneflux.api.Manager

class UpdaterNetworksTask(
    private val manager: Manager<*>,
) {
    suspend fun run() {
        manager.networks.forEach { energyNetwork ->
            if (energyNetwork.chunk.isLoaded) {
                energyNetwork.update()
            }
        }
    }
}
