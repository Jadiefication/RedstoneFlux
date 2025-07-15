package fr.traqueur.energylib.commands

import fr.traqueur.commands.api.Arguments
import fr.traqueur.commands.api.Command
import fr.traqueur.energylib.EnergyLib
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EnergyCommand(plugin: EnergyLib?) : Command<EnergyLib?>(plugin, "energy-admin") {
    init {
        this.setDescription("The admin command of the plugin energylib.")
        this.setPermission("energy.admin")
        this.setUsage("/energy-admin")

        this.addSubCommand(
            DeleteCommand(plugin!!),
            ListCommand(plugin),
            ShowCommand(plugin)
        )

        this.setGameOnly(true)
    }

    override fun execute(commandSender: CommandSender?, arguments: Arguments?) {
        val player = commandSender as Player
        player.sendMessage("§cUsage: /energy-admin <delete/list/show>")
    }
}
