package io.github.Jadiefication.redstoneflux.commands

import fr.traqueur.commands.api.arguments.ArgumentConverter
import fr.traqueur.commands.api.arguments.TabCompleter
import io.github.Jadiefication.redstoneflux.api.Manager
import io.github.Jadiefication.redstoneflux.api.components.BaseNetwork
import org.bukkit.command.CommandSender
import java.util.*

class NetworkArgument(private val manager: Set<Manager<*>>) : ArgumentConverter<BaseNetwork<*>?>,
    TabCompleter<CommandSender> {
    override fun apply(s: String): BaseNetwork<*>? {
        try {
            val uuid = UUID.fromString(s)
            var foundNetwork: BaseNetwork<*>? = null
            this.manager.forEach {
                val network = it.networks
                    .stream()
                    .filter { network -> network!!.id!! == uuid }
                    .findFirst()
                if (network.isPresent) {
                    foundNetwork = network.get()
                }
            }
            return foundNetwork
        } catch (e: IllegalArgumentException) {
            return null
        }
    }

    override fun onCompletion(commandSender: CommandSender, args: MutableList<String>): MutableList<String> {
        return manager
            .flatMap { it.networks }
            .map { it.id.toString() }.toMutableList()
    }
}
