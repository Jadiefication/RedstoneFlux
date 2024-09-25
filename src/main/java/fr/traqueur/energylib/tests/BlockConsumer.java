package fr.traqueur.energylib.tests;

import fr.traqueur.energylib.api.mechanics.EnergyConsumer;

public class BlockConsumer implements EnergyConsumer {

    private double energy = 0;

    @Override
    public double getEnergyDemand() {
        return 1000;
    }

    @Override
    public void receiveEnergy(double energyToGive) {
        this.energy += energyToGive;
        if (this.energy >= this.getEnergyDemand()) {
            this.consumeEnergy();
        }
    }

    @Override
    public void consumeEnergy() {
        this.energy -= this.getEnergyDemand();
        System.out.println("Consuming 1000 energy, remaining: " + this.energy);
    }
}
