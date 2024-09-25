package fr.traqueur.energylib.tests;

import fr.traqueur.commands.api.Arguments;
import fr.traqueur.commands.api.Command;
import fr.traqueur.energylib.EnergyLib;
import fr.traqueur.energylib.api.EnergyManager;
import fr.traqueur.energylib.api.types.ComponentsTypes;
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
        ComponentsTypes componentType = arguments.get("component-type");
        var item = switch (componentType) {
            case PRODUCER -> energyManager.createItemComponent(Material.FURNACE, componentType, EnergyTypes.RF, BlockProducer.class);
            case CONSUMER -> energyManager.createItemComponent(Material.DISPENSER, componentType, EnergyTypes.RF, BlockConsumer.class);
            case STORAGE -> energyManager.createItemComponent(Material.CHEST, componentType, EnergyTypes.RF, BlockStorage.class);
            case TRANSPORTER -> energyManager.createItemComponent(Material.GRAY_STAINED_GLASS, componentType, EnergyTypes.RF, BlockTransporter.class);
        };
        player.getInventory().addItem(item);
        player.sendMessage("§aVous avez reçu un composant d'énergie.");
    }
}
