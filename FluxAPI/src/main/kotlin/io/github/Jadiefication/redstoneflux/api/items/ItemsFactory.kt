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
    fun registerItem(item: ItemStack?, clazz: Class<out EnergyMechanic?>?) {
        ITEM_STACKS_MAP.put(clazz, item)
    }

    /**
     * This method is used to register all the items for each EnergyMechanic.
     */
    fun registerItem(material: Material, clazz: Class<out EnergyMechanic?>?) {
        ITEM_STACKS_MAP.put(clazz, ItemStack(material))
    }

    /**
     * This method is used to get an item for a class.
     */
    fun getItem(clazz: Class<out EnergyMechanic?>?): Optional<ItemStack> {
        return Optional.ofNullable(ITEM_STACKS_MAP[clazz])
    }
}
