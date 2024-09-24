package fr.traqueur.energylib.api.components;

public enum ComponentsType {

    PRODUCER(EnergyProducer.class),
    CONSUMER(EnergyConsumer.class),
    STORAGE(EnergyStorage.class),
    TRANSPORTER(EnergyTransporter.class);

    private final Class<? extends EnergyComponent> clazz;

    ComponentsType(Class<? extends EnergyComponent> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends EnergyComponent> getClazz() {
        return clazz;
    }
}
