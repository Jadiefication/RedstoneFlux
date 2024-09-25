package fr.traqueur.energylib.tests;

import fr.traqueur.commands.api.arguments.ArgumentConverter;
import fr.traqueur.commands.api.arguments.TabConverter;
import fr.traqueur.energylib.api.types.ComponentsTypes;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Stream;

public class ComponentsTypeConverter implements ArgumentConverter<ComponentsTypes>, TabConverter {

        @Override
        public ComponentsTypes apply(String s) {
            return ComponentsTypes.valueOf(s.toUpperCase());
        }

        @Override
        public List<String> onCompletion(CommandSender commandSender) {
            return Stream.of(ComponentsTypes.values()).map(Enum::name).toList();
        }
    }