package fr.traqueur.energylib.tests;

import fr.traqueur.energylib.EnergyLib;
import fr.traqueur.energylib.api.EnergyType;
import fr.traqueur.energylib.api.components.EnergyComponent;
import fr.traqueur.energylib.api.components.EnergyConsumer;
import fr.traqueur.energylib.api.components.EnergyStorage;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockStorage extends EnergyComponent implements EnergyStorage {

    public BlockStorage(EnergyType type) {
        super(type);
    }

    @Override
    public void update() {
        JavaPlugin.getPlugin(EnergyLib.class).getLogger().info("BlockStorage update");
    }
}
