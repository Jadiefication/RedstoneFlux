package io.github.Jadiefication.redstoneflux.commands

import fr.traqueur.commands.api.arguments.Arguments
import fr.traqueur.commands.api.models.Command
import io.github.Jadiefication.redstoneflux.RedstoneFlux
import org.bukkit.command.CommandSender

class ReloadCommand(plugin: RedstoneFlux) : Command<RedstoneFlux?, CommandSender>(plugin, "reload") {

    init {
        this.permission = "energy.admin.reload"
        this.usage = "/energy-admin reload"
        this.description = "Reloads the plugin to load new config"
    }

    override fun execute(
        sender: CommandSender?,
        arguments: Arguments?
    ) {
        plugin?.isDebug = plugin?.config?.getBoolean("debug", false)!!
    }
}