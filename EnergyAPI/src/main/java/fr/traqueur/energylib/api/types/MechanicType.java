package fr.traqueur.energylib.api.types;

import fr.traqueur.energylib.api.mechanics.EnergyMechanic;

import java.util.ArrayList;
import java.util.List;

public interface MechanicType {

    List<MechanicType> TYPES = new ArrayList<>(List.of(MechanicTypes.values()));

    static void registerType(MechanicType type) {
        if(TYPES.stream().anyMatch(t -> t.getClazz().getName().equalsIgnoreCase(type.getClazz().getName()))) {
            throw new IllegalArgumentException("EnergyType with class " + type.getClazz().getName() + " already exists!");
        }
        TYPES.add(type);
    }

    Class<? extends EnergyMechanic> getClazz();

}
