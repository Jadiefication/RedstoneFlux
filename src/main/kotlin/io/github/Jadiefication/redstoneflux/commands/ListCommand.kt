package io.github.Jadiefication.redstoneflux.commands

import fr.traqueur.commands.api.arguments.Arguments
import fr.traqueur.commands.api.models.Command
import io.github.Jadiefication.redstoneflux.RedstoneFlux
import io.github.Jadiefication.redstoneflux.api.Manager
import io.github.Jadiefication.redstoneflux.api.components.EnergyNetwork
import org.bukkit.command.CommandSender

class ListCommand(plugin: RedstoneFlux) : Command<RedstoneFlux?, CommandSender>(plugin, "list") {
    private val manager: Set<Manager<*>> = plugin.managers

    init {
        this.permission = "energy.admin.list"
        this.usage = "/energy-admin list"
        this.description = "List all networks."
    }

    override fun execute(commandSender: CommandSender, arguments: Arguments?) {
        val builder = StringBuilder()
        builder.append("§7Networks:\n")
        manager.forEach {
            it.networks.forEach { network ->
                builder.append("§e").append(network.id).append("§7: ").append("Chunk: §e").append(
                    network.chunk.x
                ).append(" ").append(network.chunk.z).append("§7, ").append("Key: §e")
                    .append("§7 ${network.networkKey.key}.")
                .append(if (network is EnergyNetwork) {
                    ",§eEnergy: §7" + network.energyType + "\n"
                } else {
                    "\n"
                })
            }
        }
        commandSender.sendMessage(builder.toString())
    }
}
