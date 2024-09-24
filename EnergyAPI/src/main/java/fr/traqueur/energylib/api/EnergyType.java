package fr.traqueur.energylib.api;

import fr.traqueur.energylib.api.types.EnergyTypes;

import java.util.ArrayList;
import java.util.List;

public interface EnergyType {

    List<EnergyType> TYPES = new ArrayList<>(List.of(EnergyTypes.values()));

    static void registerType(EnergyType type) {
        if(TYPES.stream().anyMatch(t -> t.getName().equalsIgnoreCase(type.getName()))) {
            throw new IllegalArgumentException("EnergyType with name " + type.getName() + " already exists!");
        }
        TYPES.add(type);
    }

    String getName();

}
