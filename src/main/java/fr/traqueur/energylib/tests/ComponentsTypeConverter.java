package fr.traqueur.energylib.tests;

import fr.traqueur.commands.api.arguments.ArgumentConverter;
import fr.traqueur.commands.api.arguments.TabConverter;
import fr.traqueur.energylib.api.components.ComponentsType;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Stream;

public class ComponentsTypeConverter implements ArgumentConverter<ComponentsType>, TabConverter {

        @Override
        public ComponentsType apply(String s) {
            return ComponentsType.valueOf(s.toUpperCase());
        }

        @Override
        public List<String> onCompletion(CommandSender commandSender) {
            return Stream.of(ComponentsType.values()).map(Enum::name).toList();
        }
    }