package fr.traqueur.testplugin.tests;

import fr.traqueur.energylib.api.mechanics.EnergyStorage;

public class BlockStorage implements EnergyStorage {

    private final double maxCapacity = 10000;
    private double capacity;

    @Override
    public double getAvailableCapacity() {
        return this.maxCapacity - this.capacity;
    }

    @Override
    public double storeEnergy(double energyStored) {
        double energy = Math.min(this.getAvailableCapacity(), energyStored);
        this.capacity += energy;
        System.out.println("BlockStorage stored " + energyStored + " energy. Total: " + this.capacity);
        return energy;
    }

    @Override
    public double getStoredEnergy() {
        return this.capacity;
    }

    @Override
    public double consumeEnergy(double energyTaken) {
        double energy = Math.min(this.capacity, energyTaken);
        this.capacity -= energy;
        System.out.println("BlockStorage consumed " + energy + " energy. Total: " + this.capacity);
        return energy;
    }
}
