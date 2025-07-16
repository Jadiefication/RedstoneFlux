package io.github.Jadiefication.redstoneflux.api.items

import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyMechanic
import io.papermc.paper.datacomponent.item.ItemLore
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * Holder of items defined by ItemStack
 */
data class ItemHolder<T : EnergyMechanic>(
    var item: ItemStack? = null,
    override var name: Component? = null,
    override var lore: ItemLore? = null,
    override var mechanic: T? = null,
    /**
     * Applicable modify function to affect the mechanic.
     */
    override var modify: (T.() -> Unit)? = null
) : ItemCreation<T> {
    init {
        if (modify != null && mechanic != null) {
            modify?.invoke(mechanic!!)
        }
    }
}
/**
 * Holder of items defined by Material
 */
data class MaterialHolder<T : EnergyMechanic>(
    var item: Material? = null,
    override var name: Component? = null,
    override var lore: ItemLore? = null,
    override var mechanic: T? = null,
    /**
     * Applicable modify function to affect the mechanic.
     */
    override var modify: (T.() -> Unit)? = null
) : ItemCreation<T> {
    init {
        if (modify != null && mechanic != null) {
            modify?.invoke(mechanic!!)
        }
    }
}
