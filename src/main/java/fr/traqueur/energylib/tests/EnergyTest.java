package fr.traqueur.energylib.tests;

import fr.traqueur.commands.api.CommandManager;
import fr.traqueur.energylib.EnergyLib;
import fr.traqueur.energylib.api.items.ItemsFactory;
import fr.traqueur.energylib.api.types.MechanicTypes;
import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.Material;

public class EnergyTest {

    public EnergyTest(EnergyLib energyLib) {
        CommandManager commandManager = new CommandManager(energyLib);

        commandManager.registerConverter(MechanicTypes.class,"component-type", new ComponentsTypeConverter());

        commandManager.registerCommand(new EnergyCommand(energyLib));

        ItemsFactory.registerItem(OraxenItems.getItemById("caveblock").build(), BlockProducer.class);
        ItemsFactory.registerItem(Material.DISPENSER, BlockConsumer.class);
        ItemsFactory.registerItem(Material.CHEST, BlockStorage.class);
        ItemsFactory.registerItem(Material.WHITE_STAINED_GLASS, BlockTransporter.class);
    }

}
