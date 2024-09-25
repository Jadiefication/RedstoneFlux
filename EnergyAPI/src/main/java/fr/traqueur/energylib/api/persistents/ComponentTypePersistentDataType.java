package fr.traqueur.energylib.api.persistents;

import fr.traqueur.energylib.api.types.MechanicTypes;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ComponentTypePersistentDataType implements PersistentDataType<String, MechanicTypes> {

    public static final ComponentTypePersistentDataType INSTANCE = new ComponentTypePersistentDataType();

    @NotNull
    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @NotNull
    @Override
    public Class<MechanicTypes> getComplexType() {
        return MechanicTypes.class;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull MechanicTypes components, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return components.name();
    }

    @NotNull
    @Override
    public MechanicTypes fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return MechanicTypes.valueOf(s);
    }
}
