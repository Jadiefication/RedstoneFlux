package fr.traqueur.testplugin.tests

import fr.traqueur.commands.api.arguments.ArgumentConverter
import fr.traqueur.commands.api.arguments.TabConverter
import fr.traqueur.energylib.api.types.MechanicType
import org.bukkit.command.CommandSender
import java.util.*
import java.util.stream.Stream

class ComponentsTypeConverter : ArgumentConverter<MechanicType?>, TabConverter {
    override fun apply(s: String): MechanicType {
        return MechanicType.valueOf(s.uppercase(Locale.getDefault()))
    }

    override fun onCompletion(commandSender: CommandSender?): MutableList<String?> {
        return Stream.of(*MechanicType.entries.toTypedArray()).map { obj: MechanicType? -> obj!!.name }
            .toList()
    }
}