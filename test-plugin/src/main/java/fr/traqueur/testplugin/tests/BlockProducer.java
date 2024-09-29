package fr.traqueur.testplugin.tests;

import fr.traqueur.energylib.api.mechanics.EnergyProducer;
import fr.traqueur.energylib.api.mechanics.InteractableMechanic;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockProducer implements EnergyProducer, InteractableMechanic {

    private final double maxRate = 2000;
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

    @Override
    public void onRightClick(PlayerInteractEvent event) {
        Bukkit.broadcastMessage("Right click");
    }

    @Override
    public void onLeftClick(PlayerInteractEvent event) {
        Bukkit.broadcastMessage("Left click");
    }
}
