package fr.traqueur.energylib.commands;

import fr.traqueur.commands.api.arguments.ArgumentConverter;
import fr.traqueur.commands.api.arguments.TabConverter;
import fr.traqueur.energylib.api.EnergyManager;
import fr.traqueur.energylib.api.components.EnergyNetwork;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NetworkArgument implements ArgumentConverter<EnergyNetwork>, TabConverter {

    private final EnergyManager manager;

    public NetworkArgument(EnergyManager manager) {
        this.manager = manager;
    }

    @Override
    public EnergyNetwork apply(String s) {
        try {
            UUID uuid = UUID.fromString(s);
            return this.manager.getNetworks()
                    .stream()
                    .filter(network -> network.getId().equals(uuid))
                    .findFirst()
                    .orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public List<String> onCompletion(CommandSender commandSender) {
        return this.manager.getNetworks()
                .stream()
                .map(network -> network.getId().toString())
                .collect(Collectors.toList());
    }
}
