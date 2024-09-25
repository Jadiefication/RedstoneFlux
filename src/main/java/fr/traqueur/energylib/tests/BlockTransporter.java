package fr.traqueur.energylib.tests;

import fr.traqueur.energylib.api.mechanics.EnergyConsumer;
import fr.traqueur.energylib.api.mechanics.EnergyTransporter;

public class BlockTransporter implements EnergyTransporter {

    @Override
    public void handle() {
        System.out.println("BlockTransporter handle");
    }
}
