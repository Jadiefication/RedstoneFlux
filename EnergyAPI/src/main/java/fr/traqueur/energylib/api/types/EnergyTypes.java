package fr.traqueur.energylib.api.types;

/**
 * Represents an energy type.
 */
public enum EnergyTypes implements EnergyType {

    /**
     * Represents the energy type of the United States.
     */
    RF,

    /**
     * Represents the energy type of the European Union.
     */
    EU,
    ;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.name();
    }
}
