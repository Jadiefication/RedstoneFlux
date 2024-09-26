package fr.traqueur.energylib.tests;

import fr.traqueur.energylib.api.mechanics.EnergyConsumer;

public class BlockConsumer implements EnergyConsumer {

    private boolean enable = false;
    private double energy = 0;

    @Override
    public boolean isEnable() {
        return this.enable;
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

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
