package fr.traqueur.energylib.tests;

import fr.traqueur.commands.api.arguments.ArgumentConverter;
import fr.traqueur.commands.api.arguments.TabConverter;
import fr.traqueur.energylib.api.types.MechanicTypes;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Stream;

public class ComponentsTypeConverter implements ArgumentConverter<MechanicTypes>, TabConverter {

        @Override
        public MechanicTypes apply(String s) {
            return MechanicTypes.valueOf(s.toUpperCase());
        }

        @Override
        public List<String> onCompletion(CommandSender commandSender) {
            return Stream.of(MechanicTypes.values()).map(Enum::name).toList();
        }
    }