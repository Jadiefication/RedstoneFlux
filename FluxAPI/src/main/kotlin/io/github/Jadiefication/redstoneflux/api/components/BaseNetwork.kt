package io.github.Jadiefication.redstoneflux.api.components

import io.github.Jadiefication.redstoneflux.api.EnergyAPI
import io.github.Jadiefication.redstoneflux.api.Manager
import io.github.Jadiefication.redstoneflux.api.exceptions.SameEnergyTypeException
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

abstract class BaseNetwork<C : BaseComponent<C>> {
    /**
     * The API instance.
     */
    internal abstract val api: EnergyAPI
    /**
     * The network's unique identifier.
     */
    abstract val id: UUID?

    /**
     * The network's chunk.
     */
    lateinit var chunk: Chunk

    /**
     * The network's components.
     */
    val components: MutableMap<Location, C> = ConcurrentHashMap<Location, C>()

    /**
     * The key to store the network in the chunk.
     */
    abstract val networkKey: NamespacedKey

    /**
     * The specific manager for this network
     */
    abstract val manager: KClass<out Manager<C>>

    /**
     * Add a component to the network.
     *
     * @param component The component to add.
     * @param location  The location of the component.
     * @throws SameEnergyTypeException If the component is not the same type.
     */
    @Throws(SameEnergyTypeException::class)
    fun addComponent(component: C, location: Location) {
        for (entry in this.components.entries.stream()
            .filter { entry -> entry.key.distance(location) == 1.0 }
            .toList()) {
            entry.value.connect(component)
        }
        if (!::chunk.isInitialized) {
            this.chunk = location.chunk
        }
        this.components.put(location, component)
    }

    /**
     * Remove a component from the network.
     *
     * @param location The location of the component.
     */
    fun removeComponent(location: Location) {
        this.components.entries.stream()
            .filter { entry -> entry.key.distance(location) == 1.0 }
            .forEach { entry ->
                entry.value.disconnect(this.components[location]!!)
            }
        this.components.remove(location)
    }

    /**
     * Get if the network contains a location.
     *
     * @param location The location to check.
     * @return If the network contains the location.
     */
    fun contains(location: Location): Boolean {
        return this.components.containsKey(location)
    }

    /**
     * Merge the network with another network.
     *
     * @param network The network to merge with.
     */
    fun mergeWith(network: BaseNetwork<C>) {
        this.components.putAll(network.components)
    }

    val isEmpty: Boolean
        /**
         * Get if the network is empty.
         *
         * @return If the network is empty.
         */
        get() = this.components.isEmpty()

    /**
     * Get if the network is in a chunk.
     *
     * @param chunk The chunk to check.
     * @return If the network is in the chunk.
     */
    fun isInChunk(chunk: Chunk): Boolean {
        return this.components.keys
            .stream()
            .anyMatch { location: Location? -> this.isSameChunk(chunk, location!!.chunk) }
    }


    /**
     * Save the network in the chunk.
     */
    fun save() {
        val manager = this.api.managers.first { manager.isInstance(it) }
        val container: PersistentDataContainer
        try {
            container = chunk.persistentDataContainer
        } catch (_: Exception) {
            delete()
            return
        }
        val gson = manager.gson
        val json: String? = gson.toJson(this, EnergyNetwork::class.java)

        var networks =
            container.getOrDefault(
                networkKey,
                PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING),
                ArrayList()
            )
        networks = ArrayList(networks)
        networks.removeIf { network: String? ->
            val energyNetwork: EnergyNetwork? = gson.fromJson(network, EnergyNetwork::class.java)
            energyNetwork?.id == this.id
        }
        networks.add(json)

        container.set(
            networkKey,
            PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING),
            networks
        )
    }

    /**
     * Delete the network from the chunk.
     */
    fun delete() {
        val container: PersistentDataContainer
        try {
            container = chunk.persistentDataContainer
        } catch (_: Exception) {
            api.managers.first { manager.isInstance(it) }.networks.remove(this)
            return
        }
        var networks: MutableList<String?> =
            container.getOrDefault(
                networkKey,
                PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING),
                mutableListOf()
            )
        networks = ArrayList(networks)
        networks.removeIf { json: String? ->
            val network: EnergyNetwork? = this.api.managers.first {manager.isInstance(it)}.gson.fromJson(json, EnergyNetwork::class.java)
            network?.id == this.id
        }
        container.set(
            networkKey,
            PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING),
            networks
        )
    }

    private fun isSameChunk(chunk: Chunk, chunk1: Chunk): Boolean {
        return chunk.x == chunk1.x && chunk.z == chunk1.z && chunk.world
            .name == chunk1.world.name
    }

    private val root: C?
        /**
         * Get the root component.
         *
         * @return The root component.
         */
        get() = this.components.values.firstOrNull()

}