package io.github.Jadiefication.redstoneflux.api

import com.google.gson.Gson
import io.github.Jadiefication.redstoneflux.api.components.BaseComponent
import io.github.Jadiefication.redstoneflux.api.components.BaseNetwork
import io.github.Jadiefication.redstoneflux.api.components.ItemComponentBuilder
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * The Manager is the principale class of the API, it is used to manage each separate type of components and networks.
 */
interface Manager<C : BaseComponent<C>> {

    /**
     * The list of the 6 block faces.
     */
    val neighbours: List<BlockFace>
        get() = listOf(
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST
        )

    /**
     * The RedstoneFlux instance for getting certain values.
     */
    val api: EnergyAPI

    /**
     * The builder for the itemstacks based on components.
     */
    val builder: ItemComponentBuilder<C>

    /**
     * Handle the placement of a component in the world.
     *
     * @param component The component to place.
     * @param location  The location where the component will be placed.
     */
    fun placeComponent(component: C, location: Location)

    /**
     * Handle the break of a component in the world.
     *
     * @param player
     * @param location The location of the component to break.
     */
    fun breakComponent(player: Player, location: Location)

    /**
     * Check if a location is a block component.
     *
     * @param location The location to check.
     * @return True if the location is a block component, false otherwise.
     */
    fun isBlockComponent(location: Location): Boolean

    /**
     * Create a component from an item.
     *
     * @param item The item to create the component from.
     * @return The component created.
     */
    fun createComponent(item: ItemStack): C

    /**
     * Check if an item is a component.
     *
     * @param item The item to check.
     * @return True if the item is a component, false otherwise.
     */
    fun isComponent(item: ItemStack): Boolean

    /**
     * Create an item from the component and builder.
     * @param component the component used for creation of the item.
     * @param builder the builder for the item.
     * @return The item created.
     */
    fun <T : ItemComponentBuilder<C>> createItemComponent(component: C, builder: T): ItemStack

    /**
     * Start the network updater.
     * The network updater is used to update the energy networks.
     * It is used to update the energy networks every tick.
     */
    fun startNetworkUpdater()

    /**
     * Stop the network updater.
     * The network updater is used to update the energy networks.
     * It is used to update the energy networks every tick.
     */
    fun stopNetworkUpdater()

    /**
     * Delete a network.
     *
     * @param network The network to delete.
     */
    fun <T : BaseNetwork<C>> deleteNetwork(network: T)

    /**
     * Get all the networks.
     *
     * @return The networks.
     */
    val networks: MutableSet<out BaseNetwork<C>>

    /**
     * Save the networks.
     */
    fun saveNetworks()

    /**
     * Load the networks.
     *
     * @param chunk The chunk to load the networks from.
     */
    fun loadNetworks(chunk: Chunk)

    /**
     * Get the component from a block.
     *
     * @param location The location of the block.
     * @return The component of the block.
     */
    fun getComponentFromBlock(location: Location): Optional<C>

    /**
     * Cleans up the networks
     */
    fun cleanUpNetworks()

    /**
     * Get the gson instance.
     *
     * @return The gson instance.
     */
    val gson: Gson

    companion object {
        /**
         * Used for keeping a track of which managers to register
         */
        val managers: MutableSet<Manager<*>> = mutableSetOf()
    }
}