package fr.traqueur.energylib.tests;

import fr.traqueur.energylib.api.mechanics.EnergyProducer;

public class BlockProducer implements EnergyProducer {

    @Override
    public void handle() {
        System.out.println("BlockProducer handle");
    }
}
