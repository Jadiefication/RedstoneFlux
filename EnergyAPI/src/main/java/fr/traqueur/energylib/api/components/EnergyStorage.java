package fr.traqueur.energylib.api.components;

import fr.traqueur.energylib.api.EnergyType;

public abstract class EnergyStorage extends EnergyComponent {

    protected EnergyStorage(EnergyType energyType) {
        super(energyType);
    }
}
