package fr.traqueur.testplugin

import fr.traqueur.commands.api.CommandManager
import fr.traqueur.energylib.api.items.ItemsFactory.registerItem
import fr.traqueur.energylib.api.types.MechanicType
import fr.traqueur.testplugin.tests.*
import fr.traqueur.testplugin.tests.commands.EnergyCommand
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class TestPlugin : JavaPlugin() {

    companion object {
        lateinit var plugin: TestPlugin
    }

    override fun onEnable() {
        plugin = this
        val commandManager = CommandManager(this)

        commandManager.registerConverter<MechanicType?>(
            MechanicType::class.java,
            "component-type",
            ComponentsTypeConverter()
        )

        commandManager.registerCommand(EnergyCommand(this))

        registerItem(ItemStack.of(Material.FURNACE), BlockProducer::class.java)
        registerItem(ItemStack.of(Material.DISPENSER), BlockConsumer::class.java)
        registerItem(ItemStack.of(Material.CHEST), BlockStorage::class.java)
        registerItem(ItemStack.of(Material.WHITE_STAINED_GLASS), BlockTransporter::class.java)
    }
}
