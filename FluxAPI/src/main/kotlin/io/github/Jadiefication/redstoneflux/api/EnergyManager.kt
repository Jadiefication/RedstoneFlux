package io.github.Jadiefication.redstoneflux.api

import com.google.gson.Gson
import io.github.Jadiefication.redstoneflux.api.components.EnergyComponent
import io.github.Jadiefication.redstoneflux.api.components.EnergyNetwork
import io.github.Jadiefication.redstoneflux.api.exceptions.SameEnergyTypeException
import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyMechanic
import io.github.Jadiefication.redstoneflux.api.types.EnergyType
import io.github.Jadiefication.redstoneflux.api.types.MechanicType
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * The EnergyManager is the principale class of the API, it is used to manage the energy components and networks.
 */
interface EnergyManager {
    /**
     * Handle the placement of a component in the world.
     *
     * @param component The component to place.
     * @param location  The location where the component will be placed.
     * @throws SameEnergyTypeException If a component of the different type is next to the location.
     */
    @Throws(SameEnergyTypeException::class)
    fun placeComponent(component: EnergyComponent<*>?, location: Location?)

    /**
     * Handle the break of a component in the world.
     *
     * @param player
     * @param location The location of the component to break.
     */
    suspend fun breakComponent(player: Player?, location: Location?)

    /**
     * Get the energy type of an item.
     *
     * @param item The item to get the energy type from.
     * @return The energy type of the item.
     */
    fun getEnergyType(item: ItemStack?): Optional<EnergyType?>?

    /**
     * Get the mechanic type of an item.
     *
     * @param item The item to get the mechanic type from.
     * @return The mechanic type of the item.
     */
    fun getMechanicClass(item: ItemStack?): Optional<String?>?

    /**
     * Get the mechanic of an item.
     *
     * @param item The item to get the mechanic from.
     * @return The mechanic of the item.
     */
    fun getMechanic(item: ItemStack?): Optional<out EnergyMechanic?>?

    /**
     * Check if a location is a block component.
     *
     * @param location The location to check.
     * @return True if the location is a block component, false otherwise.
     */
    fun isBlockComponent(location: Location?): Boolean

    /**
     * Create a component from an item.
     *
     * @param item The item to create the component from.
     * @return The component created.
     */
    fun createComponent(item: ItemStack?): EnergyComponent<*>?

    /**
     * Check if an item is a component.
     *
     * @param item The item to check.
     * @return True if the item is a component, false otherwise.
     */
    fun isComponent(item: ItemStack?): Boolean

    /**
     * Create an item from energytype, mechanictype and mechanic.
     *
     * @param type         The energy type of the item.
     * @param mechanicType The mechanic type of the item.
     * @param mechanic     The mechanic of the item.
     * @return The item created.
     */
    fun createItemComponent(type: EnergyType?, mechanicType: MechanicType?, mechanic: EnergyMechanic?): ItemStack?

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
    fun deleteNetwork(network: EnergyNetwork?)

    /**
     * Get the energy type key.
     *
     * @return The energy type key.
     */
    val energyTypeKey: NamespacedKey?

    /**
     * Get the mechanic class key.
     *
     * @return The mechanic class key.
     */
    val mechanicClassKey: NamespacedKey?

    /**
     * Get the mechanic key.
     *
     * @return The mechanic key.
     */
    val mechanicKey: NamespacedKey?

    /**
     * Get the network key.
     *
     * @return The network key.
     */
    val networkKey: NamespacedKey?

    /**
     * Get all the networks.
     *
     * @return The networks.
     */
    val networks: MutableSet<EnergyNetwork?>?

    /**
     * Save the networks.
     */
    fun saveNetworks()

    /**
     * Load the networks.
     *
     * @param chunk The chunk to load the networks from.
     */
    fun loadNetworks(chunk: Chunk?)

    /**
     * Get the component from a block.
     *
     * @param location The location of the block.
     * @return The component of the block.
     */
    fun getComponentFromBlock(location: Location?): Optional<EnergyComponent<*>?>?

    /**
     * Get the gson instance.
     *
     * @return The gson instance.
     */
    val gson: Gson?
}
