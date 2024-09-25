package fr.traqueur.energylib.api.types;

import fr.traqueur.energylib.api.components.EnergyMechanic;

import java.util.ArrayList;
import java.util.List;

public interface ComponentType {

    List<ComponentType> TYPES = new ArrayList<>(List.of(ComponentsTypes.values()));

    static void registerType(ComponentType type) {
        if(TYPES.stream().anyMatch(t -> t.getClazz().getName().equalsIgnoreCase(type.getClazz().getName()))) {
            throw new IllegalArgumentException("EnergyType with class " + type.getClazz().getName() + " already exists!");
        }
        TYPES.add(type);
    }

    Class<? extends EnergyMechanic> getClazz();

}
