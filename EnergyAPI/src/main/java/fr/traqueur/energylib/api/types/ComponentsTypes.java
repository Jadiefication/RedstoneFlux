package fr.traqueur.energylib.api.types;

import fr.traqueur.energylib.api.mechanics.*;

public enum ComponentsTypes implements ComponentType {

    PRODUCER(EnergyProducer.class),
    CONSUMER(EnergyConsumer.class),
    STORAGE(EnergyStorage.class),
    TRANSPORTER(EnergyTransporter.class);

    private final Class<? extends EnergyMechanic> clazz;

    ComponentsTypes(Class<? extends EnergyMechanic> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends EnergyMechanic> getClazz() {
        return clazz;
    }
}
