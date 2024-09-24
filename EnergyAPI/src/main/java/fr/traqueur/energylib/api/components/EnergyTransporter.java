package fr.traqueur.energylib.api.components;

import fr.traqueur.energylib.api.EnergyType;

public abstract class EnergyTransporter extends EnergyComponent {

    protected EnergyTransporter(EnergyType energyType) {
        super(energyType);
    }
}
