package fr.traqueur.energylib.api.mechanics;

import org.bukkit.Location;

public interface EnergyProducer extends EnergyMechanic {

    double getMaxRate();

    double getRate();

    void setMaxRate(double rate);

    boolean canProduce(Location location);

    void produce(Location location);

    double extractEnergy(double v);

    double getExcessEnergy();

}
