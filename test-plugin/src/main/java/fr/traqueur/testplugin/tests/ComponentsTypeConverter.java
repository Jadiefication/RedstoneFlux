package fr.traqueur.testplugin.tests;

import fr.traqueur.commands.api.arguments.ArgumentConverter;
import fr.traqueur.commands.api.arguments.TabConverter;
import fr.traqueur.energylib.api.types.MechanicType;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Stream;

public class ComponentsTypeConverter implements ArgumentConverter<MechanicType>, TabConverter {

        @Override
        public MechanicType apply(String s) {
            return MechanicType.valueOf(s.toUpperCase());
        }

        @Override
        public List<String> onCompletion(CommandSender commandSender) {
            return Stream.of(MechanicType.values()).map(Enum::name).toList();
        }
    }