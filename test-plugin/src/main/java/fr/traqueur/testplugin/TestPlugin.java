package fr.traqueur.testplugin;

import fr.traqueur.testplugin.tests.EnergyTest;
import org.bukkit.plugin.java.JavaPlugin;

public final class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        new EnergyTest(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
