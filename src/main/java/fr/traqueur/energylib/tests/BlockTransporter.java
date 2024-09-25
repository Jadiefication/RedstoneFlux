package fr.traqueur.energylib.tests;

import fr.traqueur.energylib.EnergyLib;
import fr.traqueur.energylib.api.types.EnergyType;
import fr.traqueur.energylib.api.components.EnergyComponent;
import fr.traqueur.energylib.api.components.EnergyTransporter;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockTransporter extends EnergyComponent implements EnergyTransporter {

    public BlockTransporter(EnergyType type) {
        super(type);
    }

    @Override
    public void update() {
        JavaPlugin.getPlugin(EnergyLib.class).getLogger().info("BlockTransporter update");
    }
}
