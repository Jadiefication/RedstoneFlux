package fr.traqueur.energylib.tests;

import fr.traqueur.energylib.api.mechanics.EnergyConsumer;

public class BlockConsumer implements EnergyConsumer {

    @Override
    public void handle() {
        System.out.println("BlockConsumer handle");
    }
}
