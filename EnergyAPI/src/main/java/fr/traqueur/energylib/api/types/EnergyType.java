package fr.traqueur.energylib.api.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an energy type.
 */
public interface EnergyType {

    /**
     * The default energy type.
     */
    List<EnergyType> TYPES = new ArrayList<>(List.of(EnergyTypes.values()));

    /**
     * Registers a new energy type.
     * @param type the type to register
     */
    static void registerType(EnergyType type) {
        if(TYPES.stream().anyMatch(t -> t.getName().equalsIgnoreCase(type.getName()))) {
            throw new IllegalArgumentException("EnergyType with name " + type.getName() + " already exists!");
        }
        TYPES.add(type);
    }

    /**
     * Gets the name of the energy type.
     * @return the name of the energy type
     */
    String getName();

}
