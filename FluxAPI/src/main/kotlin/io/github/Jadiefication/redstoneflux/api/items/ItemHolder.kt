package io.github.Jadiefication.redstoneflux.api.items

import io.github.Jadiefication.redstoneflux.api.components.EnergyComponent
import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyMechanic
import io.github.Jadiefication.redstoneflux.api.persistents.EnergyTypePersistentDataType
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemLore
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Holder of items defined by ItemStack
 */
data class ItemHolder<T : EnergyMechanic>(
    var item: ItemStack? = null,
    override var name: Component? = null,
    override var lore: ItemLore? = null,
    override var mechanic: EnergyComponent<T>? = null,
    /**
     * Applicable modify function to affect the mechanic.
     */
    override var modify: (T.() -> Unit)? = null
) : ItemCreation<T> {
    internal fun modify() {
        if (modify != null && mechanic != null) {
            modify?.invoke(mechanic!!.mechanic!!)
        }
        if (name != null) {
            item!!.setData(DataComponentTypes.ITEM_NAME, name!!)
        }
        if (lore != null) {
            item!!.setData(DataComponentTypes.LORE, lore!!)
        }

        val meta = item!!.itemMeta
        meta.persistentDataContainer.set(
            ItemsFactory.energyTypeKey,
            EnergyTypePersistentDataType.INSTANCE,
            mechanic!!.energyType!!
        )
        meta.persistentDataContainer.set(
            ItemsFactory.mechanicClassKey,
            PersistentDataType.STRING,
            mechanic!!.mechanic!!.javaClass.name
        )
        meta.persistentDataContainer.set(
            ItemsFactory.mechanicKey,
            PersistentDataType.STRING,
            ItemsFactory.gson.toJson(mechanic!!.mechanic!!)
        )
        item!!.itemMeta = meta
    }
}

/**
 * Holder of items defined by Material
 */
data class MaterialHolder<T : EnergyMechanic>(
    var item: Material? = null,
    override var name: Component? = null,
    override var lore: ItemLore? = null,
    override var mechanic: EnergyComponent<T>? = null,
    /**
     * Applicable modify function to affect the mechanic.
     */
    override var modify: (T.() -> Unit)? = null
) : ItemCreation<T> {

    internal var actualItem: ItemStack? = null
    internal fun modify() {
        actualItem = ItemStack.of(item!!)
        if (modify != null && mechanic != null) {
            modify?.invoke(mechanic!!.mechanic!!)
        }
        if (name != null) {
            actualItem!!.setData(DataComponentTypes.ITEM_NAME, name!!)
        }
        if (lore != null) {
            actualItem!!.setData(DataComponentTypes.LORE, lore!!)
        }
        val meta = actualItem!!.itemMeta
        val container = meta.persistentDataContainer
        container.set(ItemsFactory.energyTypeKey, EnergyTypePersistentDataType.INSTANCE, mechanic!!.energyType!!)
        container.set(ItemsFactory.mechanicClassKey, PersistentDataType.STRING, mechanic!!.mechanic!!.javaClass.name)
        container.set(
            ItemsFactory.mechanicKey,
            PersistentDataType.STRING,
            ItemsFactory.gson.toJson(mechanic!!.mechanic!!)
        )
        actualItem!!.itemMeta = meta
    }
}
