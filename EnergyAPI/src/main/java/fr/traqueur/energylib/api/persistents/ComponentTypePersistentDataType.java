package fr.traqueur.energylib.api.persistents;

import fr.traqueur.energylib.api.components.ComponentsType;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ComponentTypePersistentDataType implements PersistentDataType<String, ComponentsType> {

    public static final ComponentTypePersistentDataType INSTANCE = new ComponentTypePersistentDataType();

    @NotNull
    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @NotNull
    @Override
    public Class<ComponentsType> getComplexType() {
        return ComponentsType.class;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull ComponentsType components, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return components.name();
    }

    @NotNull
    @Override
    public ComponentsType fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return ComponentsType.valueOf(s);
    }
}
