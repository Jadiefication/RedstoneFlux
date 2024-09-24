package fr.traqueur.energylib.tests;

import fr.traqueur.energylib.EnergyLib;
import fr.traqueur.energylib.api.EnergyType;
import fr.traqueur.energylib.api.components.EnergyComponent;
import fr.traqueur.energylib.api.components.EnergyConsumer;
import fr.traqueur.energylib.api.components.EnergyProducer;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockConsumer extends EnergyComponent implements EnergyConsumer {

    public BlockConsumer(EnergyType type) {
        super(type);
    }

    @Override
    public void update() {
        JavaPlugin.getPlugin(EnergyLib.class).getLogger().info("BlockConsumer update");
    }
}
