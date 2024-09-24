package fr.traqueur.energylib.api.components;

import fr.traqueur.energylib.api.EnergyType;

public abstract class EnergyConsumer extends EnergyComponent {

    protected EnergyConsumer(EnergyType energyType) {
        super(energyType);
    }
}
