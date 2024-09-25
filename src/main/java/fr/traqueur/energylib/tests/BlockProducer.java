package fr.traqueur.energylib.tests;

import fr.traqueur.energylib.EnergyLib;
import fr.traqueur.energylib.api.types.EnergyType;
import fr.traqueur.energylib.api.components.EnergyComponent;
import fr.traqueur.energylib.api.components.EnergyProducer;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockProducer extends EnergyComponent implements EnergyProducer {

    public BlockProducer(EnergyType type) {
        super(type);
    }

    @Override
    public void update() {
        JavaPlugin.getPlugin(EnergyLib.class).getLogger().info("BlockProducer update");
    }
}
