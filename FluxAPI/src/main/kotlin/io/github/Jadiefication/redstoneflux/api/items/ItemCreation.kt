package io.github.Jadiefication.redstoneflux.api.items

import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyMechanic
import io.papermc.paper.datacomponent.item.ItemLore
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

/**
 * Base for holding custom energy items.
 */
internal interface ItemCreation<T : EnergyMechanic> {
    val name: Component?
    val lore: ItemLore?
    val mechanic: T?
    val modify: (T.() -> Unit)?
        get() = null
}