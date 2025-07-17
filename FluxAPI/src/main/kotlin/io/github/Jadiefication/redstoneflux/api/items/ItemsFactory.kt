package io.github.Jadiefication.redstoneflux.api.items

import io.github.Jadiefication.redstoneflux.api.components.EnergyComponent
import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyMechanic
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * This class is used to register items for each EnergyMechanic.
 * You can register an item with a class or a material.
 * If you register an item with a class, you can get it with the class.
 * If you register an item with a material, you can get it with the class.
 */
object ItemsFactory {
    /**
     * This map contains all the items registered for each EnergyMechanic.
     */
    private val ITEM_STACKS_MAP: MutableMap<EnergyComponent<*>, ItemStack?> =
        mutableMapOf()

    /**
     * This method is used to register all the items for each EnergyMechanic.
     */
    fun <N : EnergyMechanic> registerItemHolder(builder: ItemHolder<N>.() -> Unit) {
        val item = ItemHolder<N>()
        item.builder()
        item.modify()
        ITEM_STACKS_MAP.put(item.mechanic!!, item.item!!)
    }

    /**
     * This method is used to register all the items for each EnergyMechanic.
     */
    fun <N : EnergyMechanic> registerItem(builder: MaterialHolder<N>.() -> Unit) {
        val item = MaterialHolder<N>()
        item.builder()
        item.modify()
        ITEM_STACKS_MAP.put(item.mechanic!!, item.actualItem)
    }

    /**
     * This method is used to get an item for a class.
     */
    fun getItem(component: EnergyComponent<*>): Optional<ItemStack> {
        return Optional.ofNullable(ITEM_STACKS_MAP[component])
    }
}
