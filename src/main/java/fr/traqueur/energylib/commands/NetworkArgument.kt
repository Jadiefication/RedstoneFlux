package fr.traqueur.energylib.commands

import fr.traqueur.commands.api.arguments.ArgumentConverter
import fr.traqueur.commands.api.arguments.TabConverter
import fr.traqueur.energylib.api.EnergyManager
import fr.traqueur.energylib.api.components.EnergyNetwork
import org.bukkit.command.CommandSender
import java.util.*
import java.util.stream.Collectors

class NetworkArgument(private val manager: EnergyManager) : ArgumentConverter<EnergyNetwork?>, TabConverter {
    override fun apply(s: String): EnergyNetwork? {
        try {
            val uuid = UUID.fromString(s)
            return this.manager.networks!!
                .stream()
                .filter { network -> network!!.id!! == uuid }
                .findFirst()
                .orElse(null)
        } catch (e: IllegalArgumentException) {
            return null
        }
    }

    override fun onCompletion(commandSender: CommandSender?): MutableList<String?> {
        return this.manager.networks!!
            .stream()
            .map({ network -> network!!.id.toString() })
            .collect(Collectors.toList())
    }
}
