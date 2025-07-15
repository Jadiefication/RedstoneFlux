package fr.traqueur.energylib

import com.tcoded.folialib.FoliaLib
import com.tcoded.folialib.impl.PlatformScheduler
import fr.traqueur.commands.api.CommandManager
import fr.traqueur.energylib.api.EnergyAPI
import fr.traqueur.energylib.api.EnergyManager
import fr.traqueur.energylib.api.components.EnergyNetwork
import fr.traqueur.energylib.commands.EnergyCommand
import fr.traqueur.energylib.commands.NetworkArgument
import fr.traqueur.energylib.hooks.EnergyItemsAdderCompatibility
import org.bukkit.Chunk
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.ServicesManager
import org.bukkit.plugin.java.JavaPlugin
import java.util.Arrays
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

/**
 * This class is the main class of the plugin.
 * It is responsible for the initialization of the plugin.
 */
class EnergyLib : JavaPlugin(), EnergyAPI {
    /**
     * The scheduler of the plugin.
     */
    override var scheduler: PlatformScheduler? = null

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
     * Initialize the plugin.
     */
    override fun onEnable() {
        Updater.update("EnergyLib")

        this.scheduler = FoliaLib(this).getScheduler()
        this.manager = EnergyManagerImpl(this)
        this.isDebug = false

        val pluginManager: PluginManager = this.server.pluginManager
        pluginManager.registerEvents(EnergyListener(this), this)

        this.registerProvider(this, this::class.java as Class<EnergyLib?>)
        this.registerProvider(this.manager, EnergyManager::class.java as Class<EnergyManager?>)

        val commandManager = CommandManager(this)
        commandManager.isDebug = this.isDebug
        commandManager.registerConverter(
            EnergyNetwork::class.java,
            "network",
            NetworkArgument(this.manager!!)
        )
        commandManager.registerCommand(EnergyCommand(this))

        this.scheduler?.runNextTick({ t ->
            this.hooks()
            this.server.worlds.forEach(Consumer { world ->
                Arrays.stream<Chunk?>(world.loadedChunks)
                    .forEach { chunk: Chunk? -> this.manager!!.loadNetworks(chunk) }
            })

            this.manager!!.startNetworkUpdater()
            this.scheduler!!.runTimerAsync({ _ -> this.manager!!.saveNetworks() }, 1, 1, TimeUnit.HOURS)
        })
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
    private fun hooks() {
        val pluginManager: PluginManager = this.server.pluginManager
        /*if (pluginManager.isPluginEnabled("Nexo")) CompatibilitiesManager.addCompatibility(
            "EnergyLib",
            EnergyNexoCompatibility::class.java
        )*/

        if (pluginManager.isPluginEnabled("ItemsAdder")) pluginManager.registerEvents(
            EnergyItemsAdderCompatibility(this),
            this
        )
    }

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
