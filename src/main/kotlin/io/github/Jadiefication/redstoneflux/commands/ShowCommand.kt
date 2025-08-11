package io.github.Jadiefication.redstoneflux.commands

import fr.traqueur.commands.api.arguments.Arguments
import fr.traqueur.commands.api.models.Command
import io.github.Jadiefication.redstoneflux.RedstoneFlux
import io.github.Jadiefication.redstoneflux.api.components.BaseNetwork
import io.github.Jadiefication.redstoneflux.api.components.EnergyNetwork
import org.bukkit.command.CommandSender

class ShowCommand(plugin: RedstoneFlux) : Command<RedstoneFlux?, CommandSender>(plugin, "show") {
    init {
        this.permission = "energy.admin.show"
        this.usage = "/energy-admin show <network>"
        this.description = "Show a network."
        this.addArgs("network:network")
    }

    override fun execute(commandSender: CommandSender, arguments: Arguments) {
        val network = arguments.get<BaseNetwork<*>>("network")
        val builder = "§7Network §e" + network.id + "§7:\n" +
                "§7- §eChunk: §7" + network.chunk.x + " " + network.chunk.z + "\n" +
                "§7- §eKey: §7" + network.networkKey.key + "\n" +
                if (network is EnergyNetwork) {
                    "§7- §eEnergy: §7" + network.energyType + "\n"
                } else {
                    ""
                }  +
                "§7- §eComponents: §7" + network.components.size + "\n" +
                "§7- §eLoaded: §7" + network.chunk.isLoaded + "\n"
        commandSender.sendMessage(builder)
    }
}
