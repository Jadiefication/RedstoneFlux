package io.github.Jadiefication.redstoneflux

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.Jadiefication.redstoneflux.api.EnergyManager
import io.github.Jadiefication.redstoneflux.api.components.*
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
import java.util.Queue
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors
import kotlin.jvm.optionals.getOrNull


/**
 * This class is the implementation of the EnergyManager interface.
 * It allows to place and break energy components in the world.
 */
class EnergyManagerImpl(
    override val api: RedstoneFlux
) : EnergyManager {

    /**
     * The Gson instance.
     */
    override val gson: Gson

    /**
     * The set of all the energy networks.
     */
    override val networks: MutableSet<EnergyNetwork>

    /**
     * The task that updates the networks.
     */
    override var updaterTask: Job? = null

    /**
     * Items builder for energy components.
     */
    override val builder: EnergyComponentBuilder

    /**
     * Create a new EnergyManagerImpl instance.
     *
     * @param api the EnergyLib instance
     */
    init {
        this.gson = this.createGson()
        ItemsFactory.gson = this.gson
        this.networks = HashSet<EnergyNetwork>()

        builder = EnergyComponentBuilder(
            gson,
            api.energyTypeKey,
            api.mechanicClassKey,
            api.mechanicKey
        )
    }

    @Throws(SameEnergyTypeException::class)
    override fun placeComponent(component: EnergyComponent<*>, location: Location) {
        var energyNetworks: MutableList<EnergyNetwork> = ArrayList()
        for (neighbour in neighbours) {
            val neighbor = location.block.getRelative(neighbour)
            val networkNeighbor: Optional<EnergyNetwork?> =
                this.networks.stream()
                    .filter { network: EnergyNetwork? -> network?.contains(neighbor.location) == true }
                    .findFirst()
            if (networkNeighbor.isPresent) {
                if (!energyNetworks.contains(networkNeighbor.get())) energyNetworks.add(networkNeighbor.get())
            }
        }

        energyNetworks = energyNetworks.stream()
            .filter { network: EnergyNetwork? -> network?.energyType === component.energyType }
            .collect(Collectors.toList())

        if (energyNetworks.isEmpty()) {
            val network = EnergyNetwork(this.api, component, location)
            this.networks.add(network)
        } else if (energyNetworks.size == 1) {
            energyNetworks.first().addComponent(component, location)
        } else {
            val firstNetwork: EnergyNetwork = energyNetworks.first()
            firstNetwork.addComponent(component, location)
            for (i in 1..<energyNetworks.size) {
                val network: EnergyNetwork = energyNetworks[i]
                firstNetwork.mergeWith(network)
                this.deleteNetwork(network)
            }
        }
    }

    override fun createNetwork(
        uuid: UUID
    ): BaseNetwork<EnergyComponent<*>> {
        return EnergyNetwork(api, uuid)
    }

    override fun createNetwork(
        component: EnergyComponent<*>,
        location: Location
    ): BaseNetwork<EnergyComponent<*>> {
        return EnergyNetwork(api, component, location)
    }

    override fun breakComponent(player: Player, location: Location) {
        val network: EnergyNetwork? =
            this.networks.stream().filter { n -> n.contains(location) }.findFirst()
                .orElse(null)
        if (network == null) {
            return
        }

        val component: EnergyComponent<*>? = network.components[location]

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
            this@EnergyManagerImpl.splitNetworkIfNecessary(network, originalComponents)
        }
    }

    override fun getEnergyType(item: ItemStack): Optional<EnergyType?> {
        val pdcOptional = this.getPersistentData<String, EnergyType>(
            item,
            api.energyTypeKey,
            EnergyTypePersistentDataType.INSTANCE
        )
        return (if (pdcOptional.isEmpty) {
            Optional.ofNullable(ItemsFactory.getComponent(item).getOrNull()?.energyType)
        } else {
            pdcOptional
        }) as Optional<EnergyType?>
    }

    override fun getMechanicClass(item: ItemStack): Optional<String?> {
        val pdcOptional =
            this.getPersistentData<String, String>(item, api.mechanicClassKey, PersistentDataType.STRING)
        return (if (pdcOptional.isEmpty) {
            Optional.ofNullable(ItemsFactory.getComponent(item).getOrNull()?.mechanic?.javaClass?.name)
        } else {
            pdcOptional
        }) as Optional<String?>
    }

    override fun getMechanic(item: ItemStack): Optional<out EnergyMechanic?> {
        val mechanicClass: String? = this.getMechanicClass(item).orElseThrow()
        val clazz: Class<*>?
        try {
            clazz = Class.forName(mechanicClass)
        } catch (_: ClassNotFoundException) {
            throw IllegalArgumentException("Class $mechanicClass not found!")
        }
        require(EnergyMechanic::class.java.isAssignableFrom(clazz)) { "Class $mechanicClass is not an EnergyMechanic!" }
        val mechanicClazz: Class<out EnergyMechanic?> = clazz.asSubclass(EnergyMechanic::class.java)
        val opt = this.getPersistentData<String, String>(item, api.mechanicKey, PersistentDataType.STRING)
        if (opt.isEmpty) {
            return Optional.empty<EnergyMechanic?>()
        }
        val mechanicData = opt.get()
        return Optional.of(this.gson.fromJson(mechanicData, mechanicClazz))
    }

    override fun isBlockComponent(location: Location): Boolean {
        return this.networks.stream().anyMatch { network: EnergyNetwork? -> network?.contains(location) == true }
    }

    override fun createComponent(item: ItemStack): EnergyComponent<*> {
        val component = ItemsFactory.getComponent(item)
        return if (component.isEmpty) {
            val energyType = this.getEnergyType(item).orElseThrow()!!
            val mechanic = this.getMechanic(item).orElseThrow()
            return EnergyComponent(energyType, mechanic)
        } else {
            component.get()
        }
    }

    override fun isComponent(item: ItemStack): Boolean {
        if (api.isDebug) {
            println("EnergyType: ${getEnergyType(item)}")
            println("MechanicClass: ${getMechanicClass(item)}")
            println("Mechanic: ${getMechanic(item)}")
        }
        return this.getEnergyType(item).isPresent && this.getMechanicClass(item).isPresent && this.getMechanic(item).isPresent
    }

    override fun <T : ItemComponentBuilder<EnergyComponent<*>>> createItemComponent(
        component: EnergyComponent<*>,
        builder: T
    ): ItemStack {
        return (builder as EnergyComponentBuilder).buildItem(component)
    }

    override fun startNetworkUpdater() {
        this.updaterTask = api.scope.launch {
            withContext(NonCancellable) {
                UpdaterNetworksTask(this@EnergyManagerImpl).run()
                delay(1000L)  // adjust delay as needed
            }
        }
    }

    override fun stopNetworkUpdater() {
        checkNotNull(this.updaterTask) { "Updater task is not running!" }
        this.updaterTask!!.cancel()
    }

    override fun <T : BaseNetwork<EnergyComponent<*>>> deleteNetwork(network: T) {
        network.delete()
        this.networks.remove(network as EnergyNetwork)
    }

    override fun saveNetworks() {
        this.networks.forEach(Consumer { obj: EnergyNetwork? -> obj?.save() })
    }

    override fun loadNetworks(chunk: Chunk) {
        val chunkData: PersistentDataContainer? = chunk.persistentDataContainer
        if (chunkData?.has(
                api.networkKey,
                PersistentDataType.LIST.listTypeFrom<String?, String?>(PersistentDataType.STRING)
            ) == true
        ) {
            val networkDatas: MutableList<String?> =
                chunkData.getOrDefault(
                    api.networkKey,
                    PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING),
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

    override fun getComponentFromBlock(location: Location): Optional<EnergyComponent<*>> {
        val optionalEnergyNetwork: Optional<EnergyNetwork?> = this.networks.stream()
            .filter { network: EnergyNetwork? -> network?.contains(location) == true }
            .findFirst()

        return optionalEnergyNetwork.map(Function { energyNetwork: EnergyNetwork? ->
            energyNetwork?.components?.get(location)
        })
    }

    override fun cleanUpNetworks() {
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
     * Check if th network must be split.
     *
     * @param network the network
     */
    private suspend fun splitNetworkIfNecessary(
        network: EnergyNetwork,
        originalComponents: Map<Location, EnergyComponent<*>>
    ) {
        val visited: MutableSet<Location> = HashSet()
        val newNetworks: MutableList<EnergyNetwork> = ArrayList()
        val defers = mutableListOf<Deferred<Unit>>()
        network.components.keys.forEach { component ->
            val defer = api.scope.async {
                asyncNetworkSplit(visited, component, newNetworks, originalComponents)
            }
            defers.add(defer)
        }

        defers.awaitAll().forEach { _ ->
            this.deleteNetwork(network)
            this.networks.addAll(newNetworks)
        }
    }

    private fun asyncNetworkSplit(
        visited: MutableSet<Location>,
        component: Location,
        newNetworks: MutableList<EnergyNetwork>,
        originalComponents: Map<Location, EnergyComponent<*>>
    ) {
        if (!visited.contains(component)) {
            val subNetworkComponents: MutableSet<MutableMap.MutableEntry<Location, EnergyComponent<*>>> =
                discoverSubNetwork(component, visited, originalComponents)
            if (!subNetworkComponents.isEmpty()) {
                val newNetwork = EnergyNetwork(this.api, UUID.randomUUID())
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
}
