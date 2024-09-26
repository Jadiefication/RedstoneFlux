package fr.traqueur.energylib.api.mechanics;

import org.bukkit.Location;

public interface EnergyConsumer  extends EnergyMechanic {

    double getEnergyDemand();

    void receiveEnergy(double energyToGive);

    void consumeEnergy();
}
