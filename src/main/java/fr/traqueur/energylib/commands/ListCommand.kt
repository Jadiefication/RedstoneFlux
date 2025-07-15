package fr.traqueur.energylib.commands

import fr.traqueur.commands.api.Arguments
import fr.traqueur.commands.api.Command
import fr.traqueur.energylib.EnergyLib
import fr.traqueur.energylib.api.EnergyManager
import org.bukkit.command.CommandSender

class ListCommand(plugin: EnergyLib) : Command<EnergyLib?>(plugin, "list") {
    private val manager: EnergyManager = plugin.manager!!

    init {
        this.permission = "energy.admin.list"
        this.usage = "/energy-admin list"
        this.description = "List all networks."
    }

    override fun execute(commandSender: CommandSender, arguments: Arguments?) {
        val builder = StringBuilder()
        builder.append("§7Networks:\n")
        manager.networks!!.forEach { network ->
            builder.append("§e").append(network!!.id).append("§7: ").append("Chunk: §e").append(
                network.chunk?.x
            ).append(" ").append(network.chunk?.z).append("§7, §e").append(network.energyType)
                .append("§7 energy.\n")
        }
        commandSender.sendMessage(builder.toString())
    }
}
