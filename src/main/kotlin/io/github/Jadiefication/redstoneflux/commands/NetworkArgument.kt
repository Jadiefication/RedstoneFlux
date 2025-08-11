package io.github.Jadiefication.redstoneflux.commands

import fr.traqueur.commands.api.arguments.ArgumentConverter
import fr.traqueur.commands.api.arguments.TabCompleter
import io.github.Jadiefication.redstoneflux.api.EnergyManager
import io.github.Jadiefication.redstoneflux.api.components.EnergyNetwork
import org.bukkit.command.CommandSender
import java.util.*
import java.util.stream.Collectors

class NetworkArgument(private val manager: EnergyManager) : ArgumentConverter<EnergyNetwork?>,
    TabCompleter<CommandSender> {
    override fun apply(s: String): EnergyNetwork? {
        try {
            val uuid = UUID.fromString(s)
            return this.manager.networks
                .stream()
                .filter { network -> network!!.id!! == uuid }
                .findFirst()
                .orElse(null)
        } catch (e: IllegalArgumentException) {
            return null
        }
    }

    override fun onCompletion(commandSender: CommandSender, args: MutableList<String>): MutableList<String> {
        return this.manager.networks
            .stream()
            .map { network -> network!!.id.toString() }
            .collect(Collectors.toList())
    }
}
