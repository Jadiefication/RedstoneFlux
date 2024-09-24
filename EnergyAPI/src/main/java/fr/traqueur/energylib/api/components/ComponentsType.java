package fr.traqueur.energylib.api.components;

public enum ComponentsType {

    PRODUCER(EnergyProducer.class),
    CONSUMER(EnergyConsumer.class),
    STORAGE(EnergyStorage.class),
    TRANSPORTER(EnergyTransporter.class);

    private final Class<? extends EnergyMechanic> clazz;

    ComponentsType(Class<? extends EnergyMechanic> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends EnergyMechanic> getClazz() {
        return clazz;
    }
}
