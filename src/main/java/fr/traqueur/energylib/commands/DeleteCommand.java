package fr.traqueur.energylib.commands;

import fr.traqueur.commands.api.Arguments;
import fr.traqueur.commands.api.Command;
import fr.traqueur.energylib.EnergyLib;
import fr.traqueur.energylib.api.EnergyManager;
import fr.traqueur.energylib.api.components.EnergyNetwork;
import org.bukkit.command.CommandSender;

public class DeleteCommand extends Command<EnergyLib> {

    private final EnergyManager manager;

    public DeleteCommand(EnergyLib plugin) {
        super(plugin, "delete");
        this.manager = plugin.getManager();
        this.setPermission("energy.admin.delete");
        this.setUsage("/energy-admin delete <network>");
        this.setDescription("Delete a network.");
        this.addArgs("network:network");
    }

    @Override
    public void execute(CommandSender commandSender, Arguments arguments) {
        EnergyNetwork network = arguments.get("network");
        manager.deleteNetwork(network);
        commandSender.sendMessage("§aThe network §e" + network.getId() + " §ain chunk §e" + network.getChunk().getX() + " " + network.getChunk().getZ() + " §ahas been deleted.");
    }
}
