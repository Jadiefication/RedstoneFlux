package io.github.Jadiefication.redstoneflux.api.components

import org.bukkit.inventory.ItemStack

fun interface ItemComponentBuilder<C : BaseComponent<C>> {
    operator fun invoke(component: C): ItemStack
}

fun <C : BaseComponent<C>> C.buildItemWith(builder: ItemComponentBuilder<C>): ItemStack {
    return builder(this)
}
