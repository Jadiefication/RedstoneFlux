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
    abstract val api: EnergyAPI

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
     */
    fun addComponent(
        component: C,
        location: Location,
    ) {
        for (entry in this.components.entries
            .stream()
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
        this.components.entries
            .stream()
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
    fun contains(location: Location): Boolean = this.components.containsKey(location)

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
    fun isInChunk(chunk: Chunk): Boolean =
        this.components.keys
            .stream()
            .anyMatch { location: Location? -> this.isSameChunk(chunk, location!!.chunk) }

    /**
     * Save the network in the chunk.
     */
    abstract fun save()

    /**
     * Delete the network from the chunk.
     */
    abstract fun delete()

    private fun isSameChunk(
        chunk: Chunk,
        chunk1: Chunk,
    ): Boolean =
        chunk.x == chunk1.x && chunk.z == chunk1.z && chunk.world
            .name == chunk1.world.name

    abstract suspend fun update()

    private val root: C?
        /**
         * Get the root component.
         *
         * @return The root component.
         */
        get() = this.components.values.firstOrNull()
}
