package fr.traqueur.testplugin.tests.commands

import fr.traqueur.commands.api.Arguments
import fr.traqueur.commands.api.Command
import fr.traqueur.energylib.api.EnergyAPI
import fr.traqueur.energylib.api.EnergyManager
import fr.traqueur.energylib.api.types.EnergyTypes
import fr.traqueur.energylib.api.types.MechanicType
import fr.traqueur.testplugin.TestPlugin
import fr.traqueur.testplugin.tests.BlockConsumer
import fr.traqueur.testplugin.tests.BlockProducer
import fr.traqueur.testplugin.tests.BlockStorage
import fr.traqueur.testplugin.tests.BlockTransporter
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EnergyCommand(plugin: TestPlugin?) : Command<TestPlugin?>(plugin, "energy") {
    init {
        this.addArgs("component-type:component-type")
        this.setGameOnly(true)
    }

    override fun execute(commandSender: CommandSender?, arguments: Arguments) {
        val player = commandSender as Player
        val api =
            this.getPlugin()!!.getServer().getServicesManager().getRegistration<EnergyAPI>(EnergyAPI::class.java)!!
                .getProvider()

        val energyManager: EnergyManager = api.manager!!
        val componentType = arguments.get<MechanicType?>("component-type")
        val item = when (componentType) {
            MechanicType.PRODUCER -> energyManager.createItemComponent(EnergyTypes.RF, componentType, BlockProducer())
            MechanicType.CONSUMER -> energyManager.createItemComponent(EnergyTypes.RF, componentType, BlockConsumer())
            MechanicType.STORAGE -> energyManager.createItemComponent(EnergyTypes.RF, componentType, BlockStorage())
            MechanicType.TRANSPORTER -> energyManager.createItemComponent(
                EnergyTypes.RF,
                componentType,
                BlockTransporter()
            )
        }
        player.getInventory().addItem(item!!)
        player.sendMessage("§aVous avez reçu un composant d'énergie.")
    }
}
