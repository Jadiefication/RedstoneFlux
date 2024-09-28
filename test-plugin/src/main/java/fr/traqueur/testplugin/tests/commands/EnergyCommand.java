package fr.traqueur.testplugin.tests.commands;

import fr.traqueur.commands.api.Arguments;
import fr.traqueur.commands.api.Command;
import fr.traqueur.energylib.api.EnergyAPI;
import fr.traqueur.energylib.api.EnergyManager;
import fr.traqueur.energylib.api.types.EnergyTypes;
import fr.traqueur.energylib.api.types.MechanicTypes;
import fr.traqueur.testplugin.TestPlugin;
import fr.traqueur.testplugin.tests.BlockConsumer;
import fr.traqueur.testplugin.tests.BlockProducer;
import fr.traqueur.testplugin.tests.BlockStorage;
import fr.traqueur.testplugin.tests.BlockTransporter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnergyCommand extends Command<TestPlugin> {
    public EnergyCommand(TestPlugin plugin) {
        super(plugin, "energy");
        this.addArgs("component-type:component-type");
        this.setGameOnly(true);
    }

    @Override
    public void execute(CommandSender commandSender, Arguments arguments) {
        Player player = (Player) commandSender;
        EnergyAPI api = this.getPlugin().getServer().getServicesManager().getRegistration(EnergyAPI.class).getProvider();
        EnergyManager energyManager = api.getManager();
        MechanicTypes componentType = arguments.get("component-type");
        var item = switch (componentType) {
            case PRODUCER -> energyManager.createItemComponent(EnergyTypes.RF, componentType, new BlockProducer());
            case CONSUMER -> energyManager.createItemComponent(EnergyTypes.RF, componentType, new BlockConsumer());
            case STORAGE -> energyManager.createItemComponent(EnergyTypes.RF, componentType, new BlockStorage());
            case TRANSPORTER -> energyManager.createItemComponent(EnergyTypes.RF, componentType, new BlockTransporter());
        };
        player.getInventory().addItem(item);
        player.sendMessage("§aVous avez reçu un composant d'énergie.");
    }
}
