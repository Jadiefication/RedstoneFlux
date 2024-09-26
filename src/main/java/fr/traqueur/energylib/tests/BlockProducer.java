package fr.traqueur.energylib.tests;

import fr.traqueur.energylib.api.mechanics.EnergyProducer;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class BlockProducer implements EnergyProducer {

    private double maxRate = 2000;
    private int age = 0;
    private double producedEnergy = 0;

    @Override
    public double getMaxRate() {
        return this.maxRate;
    }

    @Override
    public double getRate() {
        if(age < 1000) {
            return maxRate;
        } else if(age > 1000 && age < 2000) {
            return 0.5 * maxRate;
        } else {
            return 0.05 * maxRate;
        }
    }

    @Override
    public void setMaxRate(double rate) {
        this.maxRate = rate;
    }

    @Override
    public boolean canProduce(Location location) {
        return location.getBlock().getRelative(BlockFace.UP).getLightFromSky() == 15;
    }

    @Override
    public void produce(Location location) {
        if(this.canProduce(location)) {
            System.out.println("Producing " + this.getRate() + " energy at " + location);
            age++;
            producedEnergy = this.getRate();
        }
    }

    @Override
    public double extractEnergy(double v) {
        double energy = Math.min(v, producedEnergy);
        producedEnergy -= energy;
        return energy;
    }

    @Override
    public double getExcessEnergy() {
        double excess = producedEnergy;
        producedEnergy = 0;
        return excess;
    }
}
