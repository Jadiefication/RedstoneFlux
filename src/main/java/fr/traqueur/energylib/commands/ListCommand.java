package fr.traqueur.energylib.commands;

import fr.traqueur.commands.api.Arguments;
import fr.traqueur.commands.api.Command;
import fr.traqueur.energylib.EnergyLib;
import fr.traqueur.energylib.api.EnergyManager;
import org.bukkit.command.CommandSender;

public class ListCommand extends Command<EnergyLib> {

    private final EnergyManager manager;

    public ListCommand(EnergyLib plugin) {
        super(plugin, "list");
        this.manager = plugin.getManager();

        this.setPermission("energy.admin.list");
        this.setUsage("/energy-admin list");
        this.setDescription("List all networks.");
    }

    @Override
    public void execute(CommandSender commandSender, Arguments arguments) {
        StringBuilder builder = new StringBuilder();
        builder.append("§7Networks:\n");
        manager.getNetworks().forEach(network -> {
            builder.append("§e").append(network.getId()).append("§7: ").append("Chunk: §e").append(network.getChunk().getX()).append(" ").append(network.getChunk().getZ()).append("§7, §e").append(network.getEnergyType()).append("§7 energy.\n");
        });
        commandSender.sendMessage(builder.toString());
    }
}
