package io.github.Jadiefication.redstoneflux

import io.github.Jadiefication.redstoneflux.api.EnergyManager
import io.github.Jadiefication.redstoneflux.api.Manager

@Deprecated(
    message = "Deprecated since 2.0.2, write inline update functions.",
    level = DeprecationLevel.WARNING,
)
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
