package fr.traqueur.energylib.tests;

import fr.traqueur.commands.api.Arguments;
import fr.traqueur.commands.api.Command;
import fr.traqueur.energylib.EnergyLib;
import fr.traqueur.energylib.api.EnergyManager;
import fr.traqueur.energylib.api.components.ComponentsType;
import fr.traqueur.energylib.api.types.EnergyTypes;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnergyCommand extends Command<EnergyLib> {
    public EnergyCommand(EnergyLib plugin) {
        super(plugin, "energy");
        this.addArgs("component-type:component-type");
        this.setGameOnly(true);
    }

    @Override
    public void execute(CommandSender commandSender, Arguments arguments) {
        Player player = (Player) commandSender;
        EnergyManager energyManager = this.getPlugin().getManager();
        ComponentsType componentType = arguments.get("component-type");
        var item = energyManager.createItemComponent(Material.CHEST, componentType, EnergyTypes.RF, BlockProducer.class);
        player.getInventory().addItem(item);
        player.sendMessage("§aVous avez reçu un composant d'énergie.");
    }
}
