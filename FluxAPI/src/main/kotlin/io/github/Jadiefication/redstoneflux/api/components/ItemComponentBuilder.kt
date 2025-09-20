package io.github.Jadiefication.redstoneflux.api.components

import org.bukkit.inventory.ItemStack


/**
 * A builder for converting a component into a corresponding [ItemStack].
 *
 * This is a functional interface (SAM) and can be implemented using a lambda.
 *
 * @param C The type of the component being converted.
 */
fun interface ItemComponentBuilder<C : BaseComponent<C>> {

    /**
     * Builds an [ItemStack] representation of the given component.
     *
     * @param component The component to convert into an item.
     * @return The [ItemStack] representing the component.
     */
    operator fun invoke(component: C): ItemStack
}

/**
 * Builds an [ItemStack] from this component using the provided [builder].
 *
 * This is an extension function to allow a more Kotlin-idiomatic syntax:
 * ```
 * val itemStack = myComponent.buildItemWith(builder)
 * ```
 *
 * @param builder The [ItemComponentBuilder] to use for building the item.
 * @return The [ItemStack] produced by the builder.
 */
fun <C : BaseComponent<C>> C.buildItemWith(builder: ItemComponentBuilder<C>): ItemStack {
    return builder(this)
}

