package io.github.Jadiefication.redstoneflux.api.components

import com.google.gson.Gson
import io.github.Jadiefication.redstoneflux.api.items.ItemsFactory
import io.github.Jadiefication.redstoneflux.api.persistents.EnergyTypePersistentDataType
import io.github.Jadiefication.redstoneflux.api.types.MechanicType
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.function.Supplier

@Deprecated(
    "Deprecated since 2.0.2",
    ReplaceWith("buildItem()"),
    DeprecationLevel.WARNING,
)
class EnergyComponentBuilder(
    val gson: Gson,
    val energyTypeKey: NamespacedKey,
    val mechanicClassKey: NamespacedKey,
    val mechanicKey: NamespacedKey,
) : ItemComponentBuilder<EnergyComponent<*>> {
    override fun invoke(component: EnergyComponent<*>): ItemStack {
        val mechanic = component.mechanic
        val mechanicType = MechanicType.fromComponent(component)
        val type = component.energyType
        require(mechanic.javaClass.isAssignableFrom(mechanicType.clazz)) {
            "Mechanic type " + mechanicType.clazz +
                " is not compatible with mechanic " +
                mechanic.javaClass
        }

        val item: ItemStack =
            ItemsFactory
                .getItem(component)
                .orElseThrow(Supplier { IllegalArgumentException("Item not found for mechanic " + mechanic.javaClass) })

        val meta: ItemMeta? = item.itemMeta
        requireNotNull(meta) { "ItemMeta is null!" }
        val persistentDataContainer: PersistentDataContainer = meta.persistentDataContainer
        persistentDataContainer.set(
            energyTypeKey,
            EnergyTypePersistentDataType.INSTANCE,
            type,
        )
        persistentDataContainer.set(
            mechanicClassKey,
            PersistentDataType.STRING,
            mechanic.javaClass.getName(),
        )
        persistentDataContainer.set(
            mechanicKey,
            PersistentDataType.STRING,
            this.gson.toJson(mechanic, mechanic.javaClass),
        )
        item.setItemMeta(meta)
        return item
    }
}
