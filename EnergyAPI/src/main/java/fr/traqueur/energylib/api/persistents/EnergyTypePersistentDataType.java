package fr.traqueur.energylib.api.persistents;

import fr.traqueur.energylib.api.EnergyType;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class EnergyTypePersistentDataType implements PersistentDataType<String, EnergyType> {

    public static final EnergyTypePersistentDataType INSTANCE = new EnergyTypePersistentDataType();


    @NotNull
    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @NotNull
    @Override
    public Class<EnergyType> getComplexType() {
        return EnergyType.class;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull EnergyType energyType, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return energyType.getName();
    }

    @NotNull
    @Override
    public EnergyType fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return EnergyType.TYPES.stream()
                .filter(type -> type.getName().equals(s))
                .findFirst()
                .orElseThrow();
    }
}
