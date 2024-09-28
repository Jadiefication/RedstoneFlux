package fr.traqueur.testplugin.tests;

import fr.traqueur.commands.api.CommandManager;
import fr.traqueur.energylib.api.items.ItemsFactory;
import fr.traqueur.energylib.api.types.MechanicType;
import fr.traqueur.testplugin.TestPlugin;
import fr.traqueur.testplugin.tests.commands.EnergyCommand;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.Material;

public class EnergyTest {

    public EnergyTest(TestPlugin energyLib) {
        CommandManager commandManager = new CommandManager(energyLib);

        commandManager.registerConverter(MechanicType.class,"component-type", new ComponentsTypeConverter());

        commandManager.registerCommand(new EnergyCommand(energyLib));

        ItemsFactory.registerItem(OraxenItems.getItemById("caveblock").build(), BlockProducer.class);
        ItemsFactory.registerItem(Material.DISPENSER, BlockConsumer.class);
        ItemsFactory.registerItem(Material.CHEST, BlockStorage.class);
        ItemsFactory.registerItem(Material.WHITE_STAINED_GLASS, BlockTransporter.class);
    }

}
