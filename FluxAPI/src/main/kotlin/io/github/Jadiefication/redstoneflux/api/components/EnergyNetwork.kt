package io.github.Jadiefication.redstoneflux.api.components

import io.github.Jadiefication.redstoneflux.api.EnergyAPI
import io.github.Jadiefication.redstoneflux.api.EnergyManager
import io.github.Jadiefication.redstoneflux.api.event.EnergyConsumeEvent
import io.github.Jadiefication.redstoneflux.api.event.EnergyProduceEvent
import io.github.Jadiefication.redstoneflux.api.event.NotEnoughEnergyEvent
import io.github.Jadiefication.redstoneflux.api.event.StoreEnergyEvent
import io.github.Jadiefication.redstoneflux.api.exceptions.SameEnergyTypeException
import io.github.Jadiefication.redstoneflux.api.items.ItemsFactory
import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyConsumer
import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyProducer
import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyStorage
import io.github.Jadiefication.redstoneflux.api.types.EnergyType
import io.github.Jadiefication.redstoneflux.api.types.MechanicType
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType
import org.jetbrains.annotations.ApiStatus
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors

/**
 * Represents an energy network.
 */
class EnergyNetwork(
    /**
     * The API instance.
     */
    private val api: EnergyAPI,
    /**
     * The network's unique identifier.
     */
    val id: UUID?
) {
    /**
     * Get the network's unique identifier.
     *
     * @return The network's unique identifier.
     */

    /**
     * The network's chunk.
     */
    var chunk: Chunk? = null

    /**
     * Get the network's components.
     *
     * @return The network's components.
     */
    /**
     * The network's components.
     */
    val components: MutableMap<Location?, EnergyComponent<*>?> = ConcurrentHashMap<Location?, EnergyComponent<*>?>()

    /**
     * The key to store the network in the chunk.
     */
    var networkKey: NamespacedKey = ItemsFactory.networkKey

    /**
     * Creates a new energy network.
     *
     * @param api       The API instance.
     * @param component The component to add.
     * @param location  The location of the component.
     */
    constructor(api: EnergyAPI, component: EnergyComponent<*>?, location: Location) : this(api, UUID.randomUUID()) {
        this.components.put(location, component)
        this.chunk = location.getChunk()
    }

    /**
     * Add a component to the network.
     *
     * @param component The component to add.
     * @param location  The location of the component.
     * @throws SameEnergyTypeException If the component is not the same type.
     */
    @Throws(SameEnergyTypeException::class)
    fun addComponent(component: EnergyComponent<*>, location: Location) {
        for (entry in this.components.entries.stream()
            .filter { entry: MutableMap.MutableEntry<Location?, EnergyComponent<*>?>? -> entry!!.key!!.distance(location) == 1.0 }
            .toList()) {
            entry.value!!.connect(component)
        }
        if (chunk == null) {
            this.chunk = location.getChunk()
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
            .filter { entry: MutableMap.MutableEntry<Location?, EnergyComponent<*>?>? -> entry!!.key!!.distance(location) == 1.0 }
            .forEach { entry: MutableMap.MutableEntry<Location?, EnergyComponent<*>?>? ->
                entry!!.value!!.disconnect(this.components[location]!!)
            }
        this.components.remove(location)
    }

    /**
     * Get if the network contains a location.
     *
     * @param location The location to check.
     * @return If the network contains the location.
     */
    fun contains(location: Location?): Boolean {
        return this.components.containsKey(location)
    }

    /**
     * Merge the network with another network.
     *
     * @param network The network to merge with.
     */
    fun mergeWith(network: EnergyNetwork) {
        this.components.putAll(network.components)
    }

    /**
     * Update the network.
     */
    suspend fun update() {
        handleProduction()
        handleConsumers()
        handleExcess()
    }

    /**
     * Update the network production asynchronously.
     */
    private suspend fun handleProduction(): List<Unit> {
        val producers = this.getComponentByType(MechanicType.PRODUCER)
        val defers = mutableListOf<Deferred<Unit>>()
        producers.forEach { (location, producer) ->
            val defer = api.scope.async {
                val produceEvent = EnergyProduceEvent(
                    (producer.mechanic as EnergyProducer).produce(location),
                    producer as EnergyComponent<EnergyProducer>
                )
                Bukkit.getServer().pluginManager.callEvent(produceEvent)
            }
            defers.add(defer)
        }
        return defers.awaitAll()
    }

    /**
     * Update the network excess asynchronously.
     */
    private suspend fun handleExcess(): List<Unit> {
        val producers = getComponentByType(MechanicType.PRODUCER)
        val defers = mutableListOf<Deferred<Unit>>()
        producers.forEach { (location, producer) ->
            val defer = api.scope.async {
                asyncExcessEnergy(producer)
            }
            defers.add(defer)
        }
        return defers.awaitAll()
    }

    /**
     * Internal async method to update the network excess asynchronously.
     */
    @ApiStatus.Internal
    private fun asyncExcessEnergy(producerC: EnergyComponent<*>) {
        val producer = producerC.mechanic as EnergyProducer
        var excessEnergy = producer.excessEnergy

        if (excessEnergy > 0) {
            val connectedStorages =
                getConnectedComponents(producerC, MechanicType.STORAGE)

            for (storageComponent in connectedStorages) {
                val storage = storageComponent.mechanic as EnergyStorage
                val energyStored = storage.storeEnergy(excessEnergy)
                val storeEvent = StoreEnergyEvent(
                    energyStored,
                    storageComponent as EnergyComponent<EnergyStorage>,
                    producerC as EnergyComponent<EnergyProducer>
                )
                Bukkit.getServer().pluginManager.callEvent(storeEvent)
                excessEnergy -= energyStored

                if (excessEnergy <= 0) {
                    break
                }
            }
        }
        if (excessEnergy > 0 && api.isDebug) {
            println("The excess energy from the producer $producerC is lost.")
        }
    }

    /**
     * Update the network consumers asynchronously.
     */
    private suspend fun handleConsumers(): List<Unit> {
        val consumers = this.getComponentByType(MechanicType.CONSUMER)
        val defers = mutableListOf<Deferred<Unit>>()
        consumers.forEach { (location, consumerComponent) ->
            val future = api.scope.async {
                asyncConsumerUpdate(consumerComponent)
            }
            defers.add(future)
        }
        return defers.awaitAll()
    }

    @ApiStatus.Internal
    private fun asyncConsumerUpdate(consumerComponent: EnergyComponent<*>) {
        val consumer = consumerComponent.mechanic as EnergyConsumer
        var requiredEnergy = consumer.energyDemand
        var providedEnergy = 0.0

        val connectedProducers =
            getConnectedComponents(consumerComponent, MechanicType.PRODUCER)

        for (producerComponent in connectedProducers) {
            val producer = producerComponent.mechanic as EnergyProducer
            val energyAvailable = producer.extractEnergy(requiredEnergy)
            val produceEvent = EnergyConsumeEvent(
                energyAvailable,
                producerComponent,
                consumerComponent as EnergyComponent<EnergyConsumer>
            )
            Bukkit.getServer().pluginManager.callEvent(produceEvent)
            requiredEnergy -= energyAvailable
            providedEnergy += energyAvailable
            if (requiredEnergy <= 0) {
                break
            }
        }

        if (requiredEnergy > 0) {
            val connectedStorages =
                getConnectedComponents(consumerComponent, MechanicType.STORAGE)

            for (storageComponent in connectedStorages) {
                val storage = storageComponent.mechanic as EnergyStorage
                val energyFromStorage = storage.consumeEnergy(requiredEnergy)
                val produceEvent = EnergyConsumeEvent(
                    energyFromStorage,
                    storageComponent,
                    consumerComponent as EnergyComponent<EnergyConsumer>
                )
                Bukkit.getServer().pluginManager.callEvent(produceEvent)
                requiredEnergy -= energyFromStorage
                providedEnergy += energyFromStorage

                if (requiredEnergy <= 0) {
                    break
                }
            }
        }

        consumer.receiveEnergy(providedEnergy)
        if (requiredEnergy > 0) {
            val notEnoughEnergyEvent = NotEnoughEnergyEvent(
                requiredEnergy,
                providedEnergy,
                consumerComponent as EnergyComponent<EnergyConsumer>
            )
            Bukkit.getServer().pluginManager.callEvent(notEnoughEnergyEvent)
            if (api.isDebug) {
                println("The consumer $consumerComponent did not receive enough energy.")
            }
            consumer.isEnable = false
        } else {
            consumer.isEnable = true
        }
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
            .anyMatch { location: Location? -> this.isSameChunk(chunk, location!!.getChunk()) }
    }


    /**
     * Save the network in the chunk.
     */
    fun save() {
        val manager: EnergyManager = this.api.manager!!
        val chunk = this.chunk
        val container = chunk?.persistentDataContainer
        val gson = manager.gson
        val json: String? = gson?.toJson(this, EnergyNetwork::class.java)

        var networks =
            container?.getOrDefault(
                networkKey,
                PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING),
                ArrayList()
            )
        networks = ArrayList(networks)
        networks.removeIf { network: String? ->
            val energyNetwork: EnergyNetwork? = gson?.fromJson(network, EnergyNetwork::class.java)
            energyNetwork?.id == this.id
        }
        networks.add(json)

        container?.set(
            networkKey,
            PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING),
            networks
        )
    }

    /**
     * Delete the network from the chunk.
     */
    fun delete() {
        val container = chunk!!.getPersistentDataContainer()
        var networks: MutableList<String?> =
            container.getOrDefault(
                networkKey,
                PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING),
                mutableListOf()
            )
        networks = ArrayList(networks)
        networks.removeIf { json: String? ->
            val network: EnergyNetwork? = this.api.manager!!.gson?.fromJson(json, EnergyNetwork::class.java)
            network?.id == this.id
        }
        container.set(
            networkKey,
            PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING),
            networks
        )
    }

    val energyType: EnergyType?
        /**
         * Get the network's energy type.
         *
         * @return The network's energy type.
         */
        get() = this.root!!.energyType

    /**
     * Check if two chunks are the same.
     *
     * @param chunk  The first chunk.
     * @param chunk1 The second chunk.
     * @return If the chunks are the same.
     */
    private fun isSameChunk(chunk: Chunk, chunk1: Chunk): Boolean {
        return chunk.x == chunk1.x && chunk.z == chunk1.z && chunk.world
            .name == chunk1.world.name
    }

    private val root: EnergyComponent<*>?
        /**
         * Get the root component.
         *
         * @return The root component.
         */
        get() = this.components.values.iterator().next()

    /**
     * Get the connected components.
     *
     * @param component The component to check.
     * @param type      The type of the component.
     * @return The connected components.
     */
    private fun getConnectedComponents(
        component: EnergyComponent<*>,
        type: MechanicType
    ): MutableList<EnergyComponent<*>> {
        val visited: MutableSet<EnergyComponent<*>?> = HashSet()
        val connectedComponents: MutableList<EnergyComponent<*>> = ArrayList()
        this.dfsConnectedComponents(component, visited, connectedComponents, type)
        return connectedComponents
    }

    /**
     * Depth-first search to get the connected components.
     *
     * @param component           The component to check.
     * @param visited             The visited components.
     * @param connectedComponents The connected components.
     * @param type                The type of the component.
     */
    private fun dfsConnectedComponents(
        component: EnergyComponent<*>, visited: MutableSet<EnergyComponent<*>?>,
        connectedComponents: MutableList<EnergyComponent<*>>, type: MechanicType
    ) {
        if (visited.contains(component)) {
            return
        }

        visited.add(component)

        if (type.isInstance(component)) {
            connectedComponents.add(component)
        }

        for (neighbor in component.connectedComponents) {
            if (!visited.contains(neighbor) && (MechanicType.TRANSPORTER.isInstance(neighbor!!) || type.isInstance(
                    neighbor
                ))
            ) {
                this.dfsConnectedComponents(neighbor, visited, connectedComponents, type)
            }
        }
    }

    /**
     * Get the components by type.
     *
     * @param type The type of the component.
     * @return The components by type.
     */
    private fun getComponentByType(type: MechanicType): MutableMap<Location, EnergyComponent<*>> {
        return this.components.entries
            .stream()
            .filter { entry ->
                type.clazz.isAssignableFrom(
                    entry!!.value!!.mechanic!!.javaClass
                )
            }
            .collect(Collectors.toMap({ it.key }, { it.value }))
    }
}
