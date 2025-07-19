package io.github.Jadiefication.redstoneflux

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.Jadiefication.redstoneflux.api.EnergyAPI
import io.github.Jadiefication.redstoneflux.api.EnergyManager
import io.github.Jadiefication.redstoneflux.api.components.EnergyComponent
import io.github.Jadiefication.redstoneflux.api.components.EnergyNetwork
import io.github.Jadiefication.redstoneflux.api.exceptions.SameEnergyTypeException
import io.github.Jadiefication.redstoneflux.api.items.ItemsFactory
import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyMechanic
import io.github.Jadiefication.redstoneflux.api.persistents.EnergyTypePersistentDataType
import io.github.Jadiefication.redstoneflux.api.persistents.adapters.EnergyComponentAdapter
import io.github.Jadiefication.redstoneflux.api.persistents.adapters.EnergyNetworkAdapter
import io.github.Jadiefication.redstoneflux.api.persistents.adapters.EnergyTypeAdapter
import io.github.Jadiefication.redstoneflux.api.types.EnergyType
import io.github.Jadiefication.redstoneflux.api.types.MechanicType
import kotlinx.coroutines.*
import org.bukkit.*
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.*
import java.util.List
import java.util.Queue
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.Collectors
import kotlin.jvm.optionals.getOrNull


/**
 * This class is the implementation of the EnergyManager interface.
 * It allows to place and break energy components in the world.
 */
class EnergyManagerImpl(
    val energyLib: RedstoneFlux
) : EnergyManager {
    /**
     * The EnergyLib instance.
     */
    private val api: EnergyAPI = energyLib

    /**
     * The Gson instance.
     */
    override val gson: Gson

    /**
     * The set of all the energy networks.
     */
    override val networks: MutableSet<EnergyNetwork?>

    /**
     * The task that updates the networks.
     */
    private var updaterTask: Job? = null

    /**
     * Create a new EnergyManagerImpl instance.
     *
     * @param energyLib the EnergyLib instance
     */
    init {
        this.gson = this.createGson()
        ItemsFactory.gson = this.gson
        this.networks = HashSet<EnergyNetwork?>()
    }

    /**
     * {@inheritDoc}
     */
    @Throws(SameEnergyTypeException::class)
    override fun placeComponent(component: EnergyComponent<*>?, location: Location?) {
        var energyNetworks: MutableList<EnergyNetwork> = ArrayList()
        for (neibhorFace in NEIBHORS) {
            val neighbor = location?.block?.getRelative(neibhorFace)
            val networkNeighbor: Optional<EnergyNetwork?> =
                this.networks.stream()
                    .filter { network: EnergyNetwork? -> network?.contains(neighbor?.location) == true }
                    .findFirst()
            if (networkNeighbor.isPresent) {
                if (!energyNetworks.contains(networkNeighbor.get())) energyNetworks.add(networkNeighbor.get())
            }
        }

        energyNetworks = energyNetworks.stream()
            .filter { network: EnergyNetwork? -> network?.energyType === component?.energyType }
            .collect(Collectors.toList())

        if (energyNetworks.isEmpty()) {
            val network = EnergyNetwork(this.api, component, location!!)
            this.networks.add(network)
        } else if (energyNetworks.size == 1) {
            energyNetworks.first().addComponent(component!!, location!!)
        } else {
            val firstNetwork: EnergyNetwork = energyNetworks.first()
            firstNetwork.addComponent(component!!, location!!)
            for (i in 1..<energyNetworks.size) {
                val network: EnergyNetwork = energyNetworks.get(i)
                firstNetwork.mergeWith(network)
                this.deleteNetwork(network)
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    override suspend fun breakComponent(player: Player?, location: Location?) {
        val network: EnergyNetwork? =
            this.networks.stream().filter { n: EnergyNetwork? -> n?.contains(location) == true }.findFirst()
                .orElse(null)
        if (network == null) {
            return
        }

        val component: EnergyComponent<*>? = network.components[location]
        val energyType: EnergyType? = component?.energyType
        val mechanicType: MechanicType = MechanicType.fromComponent(component!!)

        location?.block?.type = Material.AIR
        if (player?.gameMode != GameMode.CREATIVE) {
            val result: ItemStack? = this.createItemComponent(energyType, mechanicType, component)
            player?.world?.dropItemNaturally(location!!, result!!)
        }

        network.removeComponent(location!!)

        if (network.isEmpty) {
            this.deleteNetwork(network)
            return
        }

        this.splitNetworkIfNecessary(network)
    }

    /**
     * {@inheritDoc}
     */
    override fun getEnergyType(item: ItemStack?): Optional<EnergyType?>? {
        val pdcOptional = this.getPersistentData<String, EnergyType>(
            item!!,
            energyLib.energyTypeKey,
            EnergyTypePersistentDataType.INSTANCE
        )
        return (if (pdcOptional.isEmpty) {
            Optional.ofNullable(ItemsFactory.getComponent(item).getOrNull()?.energyType)
        } else {
            pdcOptional
        }) as Optional<EnergyType?>?
    }

    /**
     * {@inheritDoc}
     */
    override fun getMechanicClass(item: ItemStack?): Optional<String?>? {
        val pdcOptional = this.getPersistentData<String, String>(item!!, energyLib.mechanicClassKey, PersistentDataType.STRING)
        return (if (pdcOptional.isEmpty) {
            Optional.ofNullable(ItemsFactory.getComponent(item).getOrNull()?.mechanic?.javaClass?.name)
        } else {
            pdcOptional
        }) as Optional<String?>?
    }

    /**
     * {@inheritDoc}
     */
    override fun getMechanic(item: ItemStack?): Optional<out EnergyMechanic?>? {
        val mechanicClass: String? = this.getMechanicClass(item)?.orElseThrow()
        val clazz: Class<*>?
        try {
            clazz = Class.forName(mechanicClass)
        } catch (_: ClassNotFoundException) {
            throw IllegalArgumentException("Class $mechanicClass not found!")
        }
        require(EnergyMechanic::class.java.isAssignableFrom(clazz)) { "Class $mechanicClass is not an EnergyMechanic!" }
        val mechanicClazz: Class<out EnergyMechanic?> = clazz.asSubclass(EnergyMechanic::class.java)
        val opt = this.getPersistentData<String, String>(item!!, energyLib.mechanicKey, PersistentDataType.STRING)
        if (opt.isEmpty) {
            return Optional.empty<EnergyMechanic?>()
        }
        val mechanicData = opt.get()
        return Optional.of(this.gson.fromJson(mechanicData, mechanicClazz))
    }

    /**
     * {@inheritDoc}
     */
    override fun isBlockComponent(location: Location?): Boolean {
        return this.networks.stream().anyMatch { network: EnergyNetwork? -> network?.contains(location) == true }
    }

    /**
     * {@inheritDoc}
     */
    override fun createComponent(item: ItemStack?): EnergyComponent<*>? {
        val component = ItemsFactory.getComponent(item!!)
        return if (component.isEmpty) {
            val energyType = this.getEnergyType(item)!!.orElseThrow()!!
            val mechanic = this.getMechanic(item)!!.orElseThrow()
            return EnergyComponent(energyType, mechanic)
        } else {
            component.get()
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun isComponent(item: ItemStack?): Boolean {
        println("EnergyType: ${getEnergyType(item)}")
        println("MechanicClass: ${getMechanicClass(item)}")
        println("Mechanic: ${getMechanic(item)}")
        return this.getEnergyType(item)?.isPresent == true
                && this.getMechanicClass(item)?.isPresent == true
                && this.getMechanic(item)!!.isPresent
    }

    /**
     * {@inheritDoc}
     */
    override fun createItemComponent(
        type: EnergyType?,
        mechanicType: MechanicType?,
        mechanic: EnergyComponent<*>?
    ): ItemStack? {
        require(mechanic?.javaClass?.isAssignableFrom(mechanicType?.clazz) == true) { "Mechanic type " + mechanicType?.clazz + " is not compatible with mechanic " + mechanic?.mechanic?.javaClass }

        val item: ItemStack = ItemsFactory.getItem(mechanic)
            .orElseThrow(Supplier { IllegalArgumentException("Item not found for mechanic " + mechanic.javaClass) })

        val meta: ItemMeta? = item.itemMeta
        requireNotNull(meta) { "ItemMeta is null!" }
        val persistentDataContainer: PersistentDataContainer = meta.persistentDataContainer
        persistentDataContainer.set<String?, EnergyType?>(
            energyLib.energyTypeKey,
            EnergyTypePersistentDataType.INSTANCE,
            type!!
        )
        persistentDataContainer.set<String?, String?>(
            energyLib.mechanicClassKey,
            PersistentDataType.STRING,
            mechanic.javaClass.getName()
        )
        persistentDataContainer.set<String?, String?>(
            energyLib.mechanicKey,
            PersistentDataType.STRING,
            this.gson.toJson(mechanic, mechanic.javaClass)
        )
        item.setItemMeta(meta)
        return item
    }

    /**
     * {@inheritDoc}
     */
    override fun startNetworkUpdater() {
        this.updaterTask = api.scope.launch {
            withContext(NonCancellable) {
                UpdaterNetworksTask(this@EnergyManagerImpl).run()
                delay(1000L)  // adjust delay as needed
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun stopNetworkUpdater() {
        checkNotNull(this.updaterTask) { "Updater task is not running!" }
        this.updaterTask!!.cancel()
    }

    override fun deleteNetwork(network: EnergyNetwork?) {
        network?.delete()
        this.networks.remove(network)
    }

    /**
     * {@inheritDoc}
     */
    override fun saveNetworks() {
        this.networks.forEach(Consumer { obj: EnergyNetwork? -> obj?.save() })
    }

    /**
     * {@inheritDoc}
     */
    override fun loadNetworks(chunk: Chunk?) {
        val chunkData: PersistentDataContainer? = chunk?.getPersistentDataContainer()
        if (chunkData?.has(
                energyLib.networkKey,
                PersistentDataType.LIST.listTypeFrom<String?, String?>(PersistentDataType.STRING)
            ) == true
        ) {
            val networkDatas: MutableList<String?> =
                chunkData.getOrDefault(
                    energyLib.networkKey,
                    PersistentDataType.LIST.listTypeFrom<String?, String?>(PersistentDataType.STRING),
                    ArrayList<String?>()
                )
            for (networkData in networkDatas) {
                val network: EnergyNetwork = this.gson.fromJson<EnergyNetwork>(networkData, EnergyNetwork::class.java)
                if (this.networks.stream().noneMatch { n: EnergyNetwork? -> n?.id == network.id }) {
                    this.networks.add(network)
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun getComponentFromBlock(location: Location?): Optional<EnergyComponent<*>?>? {
        val optionalEnergyNetwork: Optional<EnergyNetwork?> = this.networks.stream()
            .filter { network: EnergyNetwork? -> network?.contains(location) == true }
            .findFirst()

        return optionalEnergyNetwork.map<EnergyComponent<*>?>(Function { energyNetwork: EnergyNetwork? ->
            energyNetwork?.components?.get(location)
        })
    }

    /**
     * Check if th network must be split.
     *
     * @param network the network
     */
    private suspend fun splitNetworkIfNecessary(network: EnergyNetwork) {
        val visited: MutableSet<Location?> = HashSet()
        val newNetworks: MutableList<EnergyNetwork?> = ArrayList()
        val defers = mutableListOf<Deferred<Unit>>()
        network.components.keys.forEach { component ->
            val defer = api.scope.async {
                asyncNetworkSplit(visited, component, newNetworks)
            }
            defers.add(defer)
        }

        defers.awaitAll().forEach { _ ->
            this.deleteNetwork(network)
            this.networks.addAll(newNetworks)
        }
    }

    private fun asyncNetworkSplit(visited: MutableSet<Location?>, component: Location?, newNetworks: MutableList<EnergyNetwork?>) {
        if (!visited.contains(component)) {
            val subNetworkComponents: MutableSet<MutableMap.MutableEntry<Location?, EnergyComponent<*>?>> =
                discoverSubNetwork(component, visited)
            if (!subNetworkComponents.isEmpty()) {
                val newNetwork = EnergyNetwork(this.api, UUID.randomUUID())
                for (subComponent in subNetworkComponents) {
                    try {
                        newNetwork.addComponent(subComponent.value!!, subComponent.key!!)
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
        startBlock: Location?,
        visited: MutableSet<Location?>
    ): MutableSet<MutableMap.MutableEntry<Location?, EnergyComponent<*>?>> {
        val subNetwork: MutableSet<MutableMap.MutableEntry<Location?, EnergyComponent<*>?>> =
            HashSet()
        val queue: Queue<Location> = LinkedList()
        queue.add(startBlock)

        while (!queue.isEmpty()) {
            val current = queue.poll()
            if (!visited.contains(current)) {
                visited.add(current)
                subNetwork.add(
                    AbstractMap.SimpleEntry<Location?, EnergyComponent<*>?>(
                        current, this.networks.stream()
                            .filter { network: EnergyNetwork? -> network?.contains(current) == true }
                            .findFirst()
                            .map { network: EnergyNetwork? -> network?.components?.get(current) }
                            .orElse(null)
                    )
                )

                for (face in NEIBHORS) {
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
        val meta: ItemMeta? = item.getItemMeta()
        if (meta == null) {
            return Optional.empty<C?>() as Optional<C?>
        }
        val persistentDataContainer: PersistentDataContainer = meta.persistentDataContainer
        return Optional.ofNullable<C?>(persistentDataContainer.get(key, type)) as Optional<C?>
    }

    /**
     * Create the Gson instance.
     *
     * @return the Gson instance
     */
    private fun createGson(): Gson {
        val builder: GsonBuilder = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(EnergyType::class.java, EnergyTypeAdapter())

        val temp: Gson = builder.create()
        builder.registerTypeAdapter(EnergyComponent::class.java, EnergyComponentAdapter(temp))

        val temp2: Gson = builder.create()
        builder.registerTypeAdapter(EnergyNetwork::class.java, EnergyNetworkAdapter(this.api, temp2))

        return builder.create()
    }

    companion object {
        /**
         * The list of the 6 block faces.
         */
        private val NEIBHORS: MutableList<BlockFace> = List.of<BlockFace?>(
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST
        )
    }
}
