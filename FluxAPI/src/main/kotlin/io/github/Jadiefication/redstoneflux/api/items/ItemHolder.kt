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
    val item: ItemStack? = null,
    override val name: Component? = null,
    override val lore: ItemLore? = null,
    override val mechanic: T? = null,
    /**
     * Applicable modify function to affect the mechanic.
     */
    override val modify: (T.() -> Unit)? = null
) : ItemCreation<T> {
    init {
        if (modify != null) {
            mechanic?.modify()
        }
    }
}
/**
 * Holder of items defined by Material
 */
data class MaterialHolder<T : EnergyMechanic>(
    val item: Material? = null,
    override val name: Component? = null,
    override val lore: ItemLore? = null,
    override val mechanic: T? = null,
    /**
     * Applicable modify function to affect the mechanic.
     */
    override val modify: (T.() -> Unit)? = null
) : ItemCreation<T> {
    init {
        if (modify != null) {
            mechanic?.modify()
        }
    }
}
