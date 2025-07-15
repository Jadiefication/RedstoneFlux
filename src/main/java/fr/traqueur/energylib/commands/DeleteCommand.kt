package fr.traqueur.energylib.commands

import fr.traqueur.commands.api.Arguments
import fr.traqueur.commands.api.Command
import fr.traqueur.energylib.EnergyLib
import fr.traqueur.energylib.api.EnergyManager
import fr.traqueur.energylib.api.components.EnergyNetwork
import org.bukkit.command.CommandSender

class DeleteCommand(plugin: EnergyLib) : Command<EnergyLib?>(plugin, "delete") {
    private val manager: EnergyManager? = plugin.manager

    init {
        this.permission = "energy.admin.delete"
        this.usage = "/energy-admin delete <network>"
        this.description = "Delete a network."
        this.addArgs("network:network")
    }

    override fun execute(commandSender: CommandSender, arguments: Arguments) {
        val network = arguments.get<EnergyNetwork>("network")
        manager?.deleteNetwork(network)
        commandSender.sendMessage(
            "§aThe network §e" + network.id + " §ain chunk §e" + network.chunk
                ?.x + " " + network.chunk?.z + " §ahas been deleted."
        )
    }
}
