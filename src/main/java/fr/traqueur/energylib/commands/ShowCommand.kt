package fr.traqueur.energylib.commands;

import fr.traqueur.commands.api.Arguments;
import fr.traqueur.commands.api.Command;
import fr.traqueur.energylib.EnergyLib;
import fr.traqueur.energylib.api.EnergyManager;
import fr.traqueur.energylib.api.components.EnergyNetwork;
import org.bukkit.command.CommandSender;

public class ShowCommand extends Command<EnergyLib> {

    private final EnergyManager manager;

    public ShowCommand(EnergyLib plugin) {
        super(plugin, "show");
        this.manager = plugin.getManager();
        this.setPermission("energy.admin.show");
        this.setUsage("/energy-admin show <network>");
        this.setDescription("Show a network.");
        this.addArgs("network:network");
    }

    @Override
    public void execute(CommandSender commandSender, Arguments arguments) {
        EnergyNetwork network = arguments.get("network");
        String builder = "§7Network §e" + network.getId() + "§7:\n" +
                "§7- §eChunk: §7" + network.getChunk().getX() + " " + network.getChunk().getZ() + "\n" +
                "§7- §eEnergy: §7" + network.getEnergyType() + "\n" +
                "§7- §eComponents: §7" + network.getComponents().size() + "\n" +
                "§7- §eLoaded: §7" + network.getChunk().isLoaded() + "\n";
        commandSender.sendMessage(builder);
    }
}
