package io.github.Jadiefication.redstoneflux.api

import com.google.gson.Gson
import io.github.Jadiefication.redstoneflux.api.components.BaseComponent
import io.github.Jadiefication.redstoneflux.api.components.BaseNetwork
import io.github.Jadiefication.redstoneflux.api.components.EnergyComponentBuilder
import io.github.Jadiefication.redstoneflux.api.components.EnergyNetwork
import io.github.Jadiefication.redstoneflux.api.components.ItemComponentBuilder
import io.github.Jadiefication.redstoneflux.api.exceptions.SameEnergyTypeException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.Chunk
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors

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
     * The task that updates the networks.
     */
    var updaterTask: Job

    /**
     * Handle the placement of a component in the world.
     *
     * @param component The component to place.
     * @param location  The location where the component will be placed.
     */
    fun placeComponent(component: C, location: Location) {
        var networks: MutableList<BaseNetwork<C>> = ArrayList()
        for (neighbour in neighbours) {
            val neighbor = location.block.getRelative(neighbour)
            val networkNeighbor =
                this.networks.stream()
                    .filter { network -> network?.contains(neighbor.location) == true }
                    .findFirst()
            if (networkNeighbor.isPresent) {
                if (!networks.contains(networkNeighbor.get())) networks.add(networkNeighbor.get())
            }
        }

        if (networks.isEmpty()) {
            val network = createNetwork<BaseNetwork<C>>(component, location)
            (this.networks as MutableSet<BaseNetwork<C>>).add(network)
        } else if (networks.size == 1) {
            networks.first().addComponent(component, location)
        } else {
            val firstNetwork = networks.first()
            firstNetwork.addComponent(component, location)
            for (i in 1..<networks.size) {
                val network = networks[i]
                firstNetwork.mergeWith(network)
                this.deleteNetwork(network)
            }
        }
    }

    /**
     * Helper function to create networks
     * @param uuid the ID of the network.
     * @return the created network.
     */
    fun <T : BaseNetwork<C>> createNetwork(uuid: UUID): T

    /**
     * Helper function to create networks
     * @param component beginning component of the network.
     * @param location the location of the component.
     * @return the created network.
     */
    fun <T : BaseNetwork<C>> createNetwork(component: C, location: Location): T

    /**
     * Handle the break of a component in the world.
     *
     * @param player
     * @param location The location of the component to break.
     */
    fun breakComponent(player: Player, location: Location) {
        val network =
            this.networks.stream().filter { n -> n.contains(location) }.findFirst()
                .orElse(null)
        if (network == null) {
            return
        }

        val component = network.components[location]

        location.block.type = Material.AIR
        if (player.gameMode != GameMode.CREATIVE) {
            val result = this.createItemComponent(component!!, builder)
            player.world.dropItemNaturally(location, result)
        }

        val originalComponents = network.components.toMap()

        network.removeComponent(location)

        if (network.isEmpty) {
            this.deleteNetwork(network)
            return
        }

        api.scope.launch {
            splitNetworkIfNecessary(network, originalComponents)
        }
    }

    /**
     * Check if a location is a block component.
     *
     * @param location The location to check.
     * @return True if the location is a block component, false otherwise.
     */
    fun isBlockComponent(location: Location): Boolean {
        return this.networks.stream().anyMatch { network -> network?.contains(location) == true }
    }

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
    fun <T : ItemComponentBuilder<C>> createItemComponent(component: C, builder: T): ItemStack {
        return builder.buildItem(component)
    }

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
    fun stopNetworkUpdater() {
        checkNotNull(this.updaterTask) { "Updater task is not running!" }
        this.updaterTask!!.cancel()
    }

    /**
     * Delete a network.
     *
     * @param network The network to delete.
     */
    fun <T : BaseNetwork<C>> deleteNetwork(network: T) {
        network.delete()
        this.networks.remove(network)
    }

    /**
     * Get all the networks.
     *
     * @return The networks.
     */
    val networks: MutableSet<out BaseNetwork<C>>

    /**
     * Save the networks.
     */
    fun saveNetworks() {
        this.networks.forEach { obj -> obj.save() }
    }

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
    fun getComponentFromBlock(location: Location): Optional<C> {
        val optionalEnergyNetwork = this.networks.stream()
            .filter { network -> network?.contains(location) == true }
            .findFirst()

        return optionalEnergyNetwork.map { energyNetwork ->
            energyNetwork.components[location]
        }
    }

    /**
     * Cleans up the networks
     */
    fun cleanUpNetworks() {
        networks.removeIf {
            if (it.components.isEmpty()) {
                it.delete()
                true
            } else {
                false
            }
        }
    }

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

    /**
     * Check if th network must be split.
     *
     * @param network the network
     */
    private suspend fun splitNetworkIfNecessary(
        network: BaseNetwork<C>,
        originalComponents: Map<Location, C>
    ) {
        val visited: MutableSet<Location> = HashSet()
        val newNetworks: MutableList<BaseNetwork<C>> = ArrayList()
        val defers = mutableListOf<Deferred<Unit>>()
        network.components.keys.forEach { component ->
            val defer = api.scope.async {
                asyncNetworkSplit(visited, component, newNetworks, originalComponents)
            }
            defers.add(defer)
        }

        defers.awaitAll().forEach { _ ->
            this.deleteNetwork(network)
            (this.networks as MutableSet<BaseNetwork<C>>).addAll(newNetworks)
        }
    }

    private fun asyncNetworkSplit(
        visited: MutableSet<Location>,
        component: Location,
        newNetworks: MutableList<BaseNetwork<C>>,
        originalComponents: Map<Location, C>
    ) {
        if (!visited.contains(component)) {
            val subNetworkComponents =
                discoverSubNetwork(component, visited, originalComponents)
            if (!subNetworkComponents.isEmpty()) {
                val newNetwork = createNetwork<BaseNetwork<C>>(UUID.randomUUID())
                for (subComponent in subNetworkComponents) {
                    try {
                        newNetwork.addComponent(subComponent.value, subComponent.key)
                    } catch (e: SameEnergyTypeException) {
                        throw RuntimeException(e)
                    }
                }
                newNetworks.add(newNetwork)
            }
        }
    }

    /**
     * Discover the sub network of a block.
     *
     * @param startBlock the start block
     * @param visited    the set of visited blocks
     * @return the set of components
     */
    private fun discoverSubNetwork(
        startBlock: Location,
        visited: MutableSet<Location>,
        originalComponents: Map<Location, C>
    ): MutableSet<MutableMap.MutableEntry<Location, C>> {
        val subNetwork: MutableSet<MutableMap.MutableEntry<Location, C>> =
            HashSet()
        val queue: Queue<Location> = LinkedList()
        queue.add(startBlock)

        while (!queue.isEmpty()) {
            val current = queue.poll()
            if (!visited.contains(current)) {
                visited.add(current)
                val component = originalComponents[current]
                if (component != null) {
                    subNetwork.add(AbstractMap.SimpleEntry(current, component))
                }

                for (face in neighbours) {
                    val neighbor = current.block.getRelative(face).location
                    if (isBlockComponent(neighbor) && !visited.contains(neighbor)) {
                        queue.add(neighbor)
                    }
                }
            }
        }

        return subNetwork
    }

    /**
     * Get the persistent data of an item.
     *
     * @param item the item
     * @param key  the key
     * @param type the type
     * @param <C>  the type of the data
     * @return the optional of the data
    </C> */
    private fun <P : Any, C : Any> getPersistentData(
        item: ItemStack,
        key: NamespacedKey,
        type: PersistentDataType<P, C>
    ): Optional<C?> {
        val meta: ItemMeta? = item.itemMeta
        if (meta == null) {
            return Optional.empty<C?>() as Optional<C?>
        }
        val persistentDataContainer: PersistentDataContainer = meta.persistentDataContainer
        return Optional.ofNullable<C?>(persistentDataContainer.get(key, type)) as Optional<C?>
    }
}