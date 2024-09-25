package fr.traqueur.energylib.api.mechanics;

import org.bukkit.Location;

public interface EnergyProducer extends EnergyMechanic {

    double getMaxRate();

    double getRate();

    void setMaxRate(double rate);

    boolean canProduce(Location location);

    double produce(Location location);

    default double handle(Location location) {
        return this.produce(location);
    }

}
