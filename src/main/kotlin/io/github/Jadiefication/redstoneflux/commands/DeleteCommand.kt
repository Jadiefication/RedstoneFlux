package io.github.Jadiefication.redstoneflux.commands

import fr.traqueur.commands.api.arguments.Arguments
import fr.traqueur.commands.api.models.Command
import io.github.Jadiefication.redstoneflux.EnergyLib
import io.github.Jadiefication.redstoneflux.api.EnergyManager
import io.github.Jadiefication.redstoneflux.api.components.EnergyNetwork
import org.bukkit.command.CommandSender

class DeleteCommand(plugin: EnergyLib) : Command<EnergyLib?, CommandSender>(plugin, "delete") {
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
