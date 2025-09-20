package io.github.Jadiefication.redstoneflux.commands

import fr.traqueur.commands.api.arguments.Arguments
import fr.traqueur.commands.api.models.Command
import io.github.Jadiefication.redstoneflux.RedstoneFlux
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EnergyCommand(
    plugin: RedstoneFlux?,
) : Command<RedstoneFlux?, CommandSender>(plugin, "energy-admin") {
    init {
        this.description = "The admin command of the plugin energylib."
        this.permission = "energy.admin"
        this.usage = "/energy-admin"

        this.addSubCommand(
            DeleteCommand(plugin!!),
            ListCommand(plugin),
            ShowCommand(plugin),
            ReloadCommand(plugin),
            DeleteAllCommand(plugin),
        )

        this.setGameOnly(true)
    }

    override fun execute(
        sender: CommandSender,
        arguments: Arguments,
    ) {
        val player = sender as Player
        player.sendMessage("§cUsage: /energy-admin <delete/list/show>")
    }
}
