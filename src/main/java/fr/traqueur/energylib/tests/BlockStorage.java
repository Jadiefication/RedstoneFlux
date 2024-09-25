package fr.traqueur.energylib.tests;

import fr.traqueur.energylib.api.mechanics.EnergyStorage;
import org.bukkit.Location;

public class BlockStorage implements EnergyStorage {

    private final double maxCapacity = 10000;
    private double capacity;

    @Override
    public double handle(Location location) {
       return this.maxCapacity;
    }

    @Override
    public double getAvailableCapacity() {
        return this.maxCapacity - this.capacity;
    }

    @Override
    public double storeEnergy(double energyStored) {
        double wasted = Math.max(0, this.capacity + energyStored - this.maxCapacity);
        this.capacity = Math.min(this.maxCapacity, this.capacity + energyStored);
        System.out.println("BlockStorage stored " + energyStored + " energy. Total: " + this.capacity);
        return wasted;
    }

    @Override
    public double getStoredEnergy() {
        return this.capacity;
    }

    @Override
    public void consumeEnergy(double energyTaken) {
        this.capacity -= energyTaken;
        System.out.println("BlockStorage consumed " + energyTaken + " energy. Total: " + this.capacity);
    }
}
