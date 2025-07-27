package io.github.Jadiefication.redstoneflux

import com.tcoded.folialib.FoliaLib
import com.tcoded.folialib.impl.PlatformScheduler
import fr.traqueur.commands.spigot.CommandManager
import io.github.Jadiefication.redstoneflux.api.EnergyAPI
import io.github.Jadiefication.redstoneflux.api.EnergyManager
import io.github.Jadiefication.redstoneflux.api.components.EnergyNetwork
import io.github.Jadiefication.redstoneflux.api.items.ItemsFactory
import io.github.Jadiefication.redstoneflux.commands.EnergyCommand
import io.github.Jadiefication.redstoneflux.commands.NetworkArgument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.bukkit.NamespacedKey
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.ServicesManager
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

/**
 * This class is the main class of the plugin.
 * It is responsible for the initialization of the plugin.
 */
class RedstoneFlux : JavaPlugin(), EnergyAPI {
    /**
     * The scheduler of the plugin.
     */
    override var scheduler: PlatformScheduler? = null

    /**
     * The coroutine scope of the plugin.
     */
    override val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /**
     * The energy manager of the plugin.
     */
    override var manager: EnergyManager? = null

    /**
     * {@inheritDoc}
     */
    /**
     * {@inheritDoc}
     */
    /**
     * The debug state of the plugin.
     */
    override var isDebug: Boolean = false

    /**
     * The key to store the energy type in the item meta.
     */
    val energyTypeKey: NamespacedKey = NamespacedKey(this, "energy-type")

    /**
     * The key to store the mechanic class in the item meta.
     */
    val mechanicClassKey: NamespacedKey = NamespacedKey(this, "mechanic-class")

    /**
     * The key to store the mechanic in the item meta.
     */
    val mechanicKey: NamespacedKey = NamespacedKey(this, "mechanic")

    /**
     * The key to store the network in the chunk.
     */
    val networkKey: NamespacedKey = NamespacedKey(this, "network")

    /**
     * Initialize the plugin.
     */
    override fun onEnable() {
        saveDefaultConfig()
        ItemsFactory.energyTypeKey = energyTypeKey
        ItemsFactory.mechanicClassKey = mechanicClassKey
        ItemsFactory.mechanicKey = mechanicKey
        ItemsFactory.networkKey = networkKey
        Updater.update("RedstoneFlux")

        this.scheduler = FoliaLib(this).scheduler
        this.manager = EnergyManagerImpl(this)
        this.isDebug = false

        val pluginManager: PluginManager = this.server.pluginManager
        pluginManager.registerEvents(EnergyListener(this), this)

        this.registerProvider(this, this::class.java as Class<RedstoneFlux?>)
        this.registerProvider(this.manager, EnergyManager::class.java as Class<EnergyManager?>)

        val commandManager = CommandManager(this)
        commandManager.isDebug = this.isDebug
        commandManager.registerConverter(
            EnergyNetwork::class.java,
            "network",
            NetworkArgument(this.manager!!)
        )
        commandManager.registerCommand(EnergyCommand(this))

        this.scheduler?.runNextTick { t ->
            this.server.worlds.forEach(Consumer { world ->
                Arrays.stream(world.loadedChunks)
                    .forEach { chunk -> this.manager!!.loadNetworks(chunk) }
            })

            this.manager!!.startNetworkUpdater()
            this.scheduler!!.runTimerAsync({ _ -> this.manager!!.saveNetworks() }, 1, 1, TimeUnit.HOURS)
            this.scheduler!!.runTimerAsync({ _ -> this.manager!!.cleanUpNetworks() }, 1, 1, TimeUnit.HOURS)
        }
    }

    /**
     * Disable the plugin.
     */
    override fun onDisable() {
        this.manager?.stopNetworkUpdater()
        this.manager?.saveNetworks()
    }

    /**
     * Initialize the hooks of the plugin.
     */
    /*private fun hooks() {
        val pluginManager: PluginManager = this.server.pluginManager
        /*if (pluginManager.isPluginEnabled("Nexo")) CompatibilitiesManager.addCompatibility(
            "EnergyLib",
            EnergyNexoCompatibility::class.java
        )*/

        if (pluginManager.isPluginEnabled("ItemsAdder")) pluginManager.registerEvents(
            EnergyItemsAdderCompatibility(this),
            this
        )
    }*/

    /**
     * Register a provider for a service. The provider can be use in other plugins.
     *
     * @param instance the instance of the provider
     * @param clazz    the class of the provider
     * @param <T>      the type of the provider
    </T> */
    private fun <T> registerProvider(instance: T?, clazz: Class<T?>) {
        val servicesManager: ServicesManager = this.server.servicesManager
        servicesManager.register<T?>(clazz, instance!!, this, ServicePriority.Normal)
    }
}
