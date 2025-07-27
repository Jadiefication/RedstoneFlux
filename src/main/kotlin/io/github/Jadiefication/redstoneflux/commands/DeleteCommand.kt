package io.github.Jadiefication.redstoneflux.commands

import fr.traqueur.commands.api.arguments.Arguments
import fr.traqueur.commands.api.models.Command
import io.github.Jadiefication.redstoneflux.RedstoneFlux
import io.github.Jadiefication.redstoneflux.api.EnergyManager
import io.github.Jadiefication.redstoneflux.api.components.EnergyNetwork
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DeleteCommand(plugin: RedstoneFlux) : Command<RedstoneFlux?, CommandSender>(plugin, "delete") {
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

class DeleteAllCommand(plugin: RedstoneFlux) : Command<RedstoneFlux?, CommandSender>(plugin, "deleteAll") {
    private val manager: EnergyManager? = plugin.manager

    init {
        this.permission = "energy.admin.deleteAll"
        this.usage = "/energy-admin deleteAll"
        this.description = "Deletes all networks."
    }

    override fun execute(commandSender: CommandSender, arguments: Arguments) {
        manager?.networks?.forEach { manager.deleteNetwork(it) }
        commandSender.sendMessage(Component.text {
            it.append(Component.text("All networks ", Style.style(NamedTextColor.GREEN)))
            it.append(Component.text(if (commandSender is Player) {
                "at ${commandSender.chunk}"
            } else {
                ""
            }, Style.style(NamedTextColor.YELLOW)))
            it.append(Component.text(" have been deleted.", Style.style(NamedTextColor.GREEN)))
        })
    }
}
