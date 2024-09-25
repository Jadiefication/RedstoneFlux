package fr.traqueur.energylib.tests;

import fr.traqueur.energylib.api.mechanics.EnergyStorage;

public class BlockStorage implements EnergyStorage {

    @Override
    public void handle() {
        System.out.println("BlockStorage handle");
    }
}
