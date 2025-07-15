package fr.traqueur.testplugin;

import fr.traqueur.commands.api.CommandManager;
import fr.traqueur.energylib.api.items.ItemsFactory;
import fr.traqueur.energylib.api.types.MechanicType;
import fr.traqueur.testplugin.tests.*;
import fr.traqueur.testplugin.tests.commands.EnergyCommand;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public final class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        CommandManager commandManager = new CommandManager(this);

        commandManager.registerConverter(MechanicType.class, "component-type", new ComponentsTypeConverter());

        commandManager.registerCommand(new EnergyCommand(this));

        ItemsFactory.registerItem(Material.FURNACE, BlockProducer.class);
        ItemsFactory.registerItem(Material.DISPENSER, BlockConsumer.class);
        ItemsFactory.registerItem(Material.CHEST, BlockStorage.class);
        ItemsFactory.registerItem(Material.WHITE_STAINED_GLASS, BlockTransporter.class);
    }

    @Override
    public void onDisable() {
    }
}
