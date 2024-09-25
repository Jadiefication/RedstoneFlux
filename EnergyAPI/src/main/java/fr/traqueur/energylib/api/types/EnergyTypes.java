package fr.traqueur.energylib.api.types;

public enum EnergyTypes implements EnergyType {

    RF,
    EU,
    ;

    @Override
    public String getName() {
        return this.name();
    }
}
