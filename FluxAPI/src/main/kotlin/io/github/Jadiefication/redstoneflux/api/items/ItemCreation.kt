package io.github.Jadiefication.redstoneflux.api.items

import io.github.Jadiefication.redstoneflux.api.components.EnergyComponent
import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyMechanic
import io.papermc.paper.datacomponent.item.ItemLore
import net.kyori.adventure.text.Component

/**
 * Base for holding custom energy items.
 */
internal interface ItemCreation<T : EnergyMechanic> {
    val name: Component?
    val lore: ItemLore?
    val mechanic: EnergyComponent<T>?
    val modify: (T.() -> Unit)?
        get() = null
}
