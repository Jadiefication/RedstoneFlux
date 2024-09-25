package fr.traqueur.energylib.api.persistents;

import fr.traqueur.energylib.api.types.ComponentsTypes;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ComponentTypePersistentDataType implements PersistentDataType<String, ComponentsTypes> {

    public static final ComponentTypePersistentDataType INSTANCE = new ComponentTypePersistentDataType();

    @NotNull
    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @NotNull
    @Override
    public Class<ComponentsTypes> getComplexType() {
        return ComponentsTypes.class;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull ComponentsTypes components, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return components.name();
    }

    @NotNull
    @Override
    public ComponentsTypes fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return ComponentsTypes.valueOf(s);
    }
}
