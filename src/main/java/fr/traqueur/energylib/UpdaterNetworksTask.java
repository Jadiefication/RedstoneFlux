package fr.traqueur.energylib;

import fr.traqueur.energylib.api.EnergyManager;

public class UpdaterNetworksTask implements Runnable {

    private final EnergyManager manager;

    public UpdaterNetworksTask(EnergyManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        manager.getNetworks().forEach(energyNetwork -> {
            if (energyNetwork.getChunk().isLoaded()) {
                energyNetwork.update();
            }
        });
    }
}
