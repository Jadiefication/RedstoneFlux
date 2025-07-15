package io.github.Jadiefication.redstoneflux.commands

import fr.traqueur.commands.api.arguments.Arguments
import fr.traqueur.commands.api.models.Command
import io.github.Jadiefication.redstoneflux.EnergyLib
import io.github.Jadiefication.redstoneflux.api.EnergyManager
import io.github.Jadiefication.redstoneflux.api.components.EnergyNetwork
import org.bukkit.command.CommandSender

class ShowCommand(plugin: EnergyLib) : Command<EnergyLib?, CommandSender>(plugin, "show") {
    private val manager: EnergyManager = plugin.manager!!

    init {
        this.setPermission("energy.admin.show")
        this.setUsage("/energy-admin show <network>")
        this.setDescription("Show a network.")
        this.addArgs("network:network")
    }

    override fun execute(commandSender: CommandSender, arguments: Arguments) {
        val network = arguments.get<EnergyNetwork>("network")
        val builder = "§7Network §e" + network.id + "§7:\n" +
                "§7- §eChunk: §7" + network.chunk?.x + " " + network.chunk?.z + "\n" +
                "§7- §eEnergy: §7" + network.energyType + "\n" +
                "§7- §eComponents: §7" + network.components.size + "\n" +
                "§7- §eLoaded: §7" + network.chunk?.isLoaded + "\n"
        commandSender.sendMessage(builder)
    }
}
