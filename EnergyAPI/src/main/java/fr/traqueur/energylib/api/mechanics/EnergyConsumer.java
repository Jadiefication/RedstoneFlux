package fr.traqueur.energylib.api.mechanics;

public interface EnergyConsumer  extends EnergyMechanic {

    boolean isEnable();

    void setEnable(boolean enable);

    double getEnergyDemand();

    void receiveEnergy(double energyToGive);

    void consumeEnergy();
}
