package fr.traqueur.energylib.api.types;

import fr.traqueur.energylib.api.EnergyType;

public enum EnergyTypes implements EnergyType {

    RF,
    EU,
    ;

    @Override
    public String getName() {
        return this.name();
    }
}
