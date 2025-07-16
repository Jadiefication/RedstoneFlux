package io.github.Jadiefication.redstoneflux.api.items

import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyMechanic
import org.bukkit.Material
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
    private val ITEM_STACKS_MAP: MutableMap<Class<out EnergyMechanic?>?, ItemStack?> =
        HashMap<Class<out EnergyMechanic?>?, ItemStack?>()

    /**
     * This method is used to register all the items for each EnergyMechanic.
     */
    fun <T : EnergyMechanic> registerItemHolder(builder: ItemHolder<T>.() -> Unit) {
        val item = ItemHolder<T>()
        item.builder()
        item.modify()
        ITEM_STACKS_MAP.put(item.mechanic!!.javaClass, item.item!!)
    }

    /**
     * This method is used to register all the items for each EnergyMechanic.
     */
    fun <T : EnergyMechanic> registerItem(builder: MaterialHolder<T>.() -> Unit) {
        val item = MaterialHolder<T>()
        item.builder()
        item.modify()
        ITEM_STACKS_MAP.put(item.mechanic!!.javaClass, item.actualItem)
    }

    /**
     * This method is used to get an item for a class.
     */
    fun getItem(clazz: Class<out EnergyMechanic?>?): Optional<ItemStack> {
        return Optional.ofNullable(ITEM_STACKS_MAP[clazz])
    }
}
