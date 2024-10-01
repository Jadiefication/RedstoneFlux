package fr.traqueur.energylib.commands;

import fr.traqueur.commands.api.Arguments;
import fr.traqueur.commands.api.Command;
import fr.traqueur.energylib.EnergyLib;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnergyCommand extends Command<EnergyLib> {

    public EnergyCommand(EnergyLib plugin) {
        super(plugin, "energy-admin");

        this.setDescription("The admin command of the plugin energylib.");
        this.setPermission("energy.admin");
        this.setUsage("/energy-admin");

        this.setGameOnly(true);
    }

    @Override
    public void execute(CommandSender commandSender, Arguments arguments) {
        Player player = (Player) commandSender;
        player.sendMessage("§cThis command is not implemented yet.");
    }
}
