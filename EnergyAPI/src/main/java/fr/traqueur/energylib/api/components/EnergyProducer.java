package fr.traqueur.energylib.api.components;

import fr.traqueur.energylib.api.EnergyType;

public abstract class EnergyProducer extends EnergyComponent {

    protected EnergyProducer(EnergyType energyType) {
        super(energyType);
    }
}
