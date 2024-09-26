package fr.traqueur.energylib.api.mechanics;

public interface EnergyStorage  extends EnergyMechanic {
    double getAvailableCapacity();

    double storeEnergy(double energyStored);

    double getStoredEnergy();

    double consumeEnergy(double energyTaken);
}
