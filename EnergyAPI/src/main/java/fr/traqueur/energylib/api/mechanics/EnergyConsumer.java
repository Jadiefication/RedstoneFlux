package fr.traqueur.energylib.api.mechanics;

/**
 * This interface is used to represent an energy consumer.
 * An energy consumer is an object that can receive energy.
 * It can also consume energy.
 */
public interface EnergyConsumer  extends EnergyMechanic {

    /**
     * This method is used to know if the energy consumer is enable.
     * @return true if the energy consumer is enable, false otherwise.
     */
    boolean isEnable();

    /**
     * This method is used to enable or disable the energy consumer.
     * @param enable true to enable the energy consumer, false to disable it.
     */
    void setEnable(boolean enable);

    /**
     * This method is used to get the energy demand of the energy consumer.
     * The energy demand is the amount of energy that the energy consumer needs to work.
     * @return the energy demand of the energy consumer.
     */
    double getEnergyDemand();

    /**
     * This method is used to receive energy.
     * @param energyToGive the amount of energy to give to the energy consumer.
     */
    void receiveEnergy(double energyToGive);

    /**
     * This method is used to consume energy and make something with it.
     */
    void consumeEnergy();
}
