package io.github.Jadiefication.redstoneflux.api.items

import io.github.Jadiefication.redstoneflux.api.components.EnergyComponent
import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyMechanic
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.util.*

/**
 * This class is used to register items for each EnergyMechanic.
 * You can register an item with a class or a material.
 * If you register an item with a class, you can get it with the class.
 * If you register an item with a material, you can get it with the class.
 */
object ItemsFactory {
    /**
     * The key to store the energy type in the item meta.
     */
    lateinit var energyTypeKey: NamespacedKey

    /**
     * The key to store the mechanic class in the item meta.
     */
    lateinit var mechanicClassKey: NamespacedKey

    /**
     * The key to store the mechanic in the item meta.
     */
    lateinit var mechanicKey: NamespacedKey

    /**
     * The key to store the network in the chunk.
     */
    lateinit var networkKey: NamespacedKey

    /**
     * This map contains all the items registered for each EnergyMechanic.
     */
    private val ITEM_STACKS_MAP: MutableMap<EnergyComponent<*>, ItemStack?> =
        mutableMapOf()

    /**
     * This method is used to register all the items for each EnergyMechanic.
     */
    fun <N : EnergyMechanic> registerItemHolder(builder: ItemHolder<N>.() -> Unit): ItemStack {
        val item = ItemHolder<N>()
        item.builder()
        item.modify()
        ITEM_STACKS_MAP.put(item.mechanic!!, item.item!!)
        return item.item!!
    }

    /**
     * This method is used to register all the items for each EnergyMechanic.
     */
    fun <N : EnergyMechanic> registerItem(builder: MaterialHolder<N>.() -> Unit): ItemStack {
        val item = MaterialHolder<N>()
        item.builder()
        item.modify()
        ITEM_STACKS_MAP.put(item.mechanic!!, item.actualItem)
        return item.actualItem!!
    }

    /**
     * This method is used to get an item for a class.
     */
    fun getItem(component: EnergyComponent<*>): Optional<ItemStack> {
        return Optional.ofNullable(ITEM_STACKS_MAP[component])
    }

    fun getComponent(item: ItemStack): Optional<EnergyComponent<*>> {
        return Optional.ofNullable(ITEM_STACKS_MAP.entries
            .find { it.value == item }?.key)
    }
}
