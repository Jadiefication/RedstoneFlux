package io.github.Jadiefication.redstoneflux.api.components

import org.bukkit.inventory.ItemStack

interface ItemComponentBuilder<C : BaseComponent<C>> {
    fun buildItem(component: C): ItemStack
}
