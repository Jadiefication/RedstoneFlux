package fr.traqueur.energylib.tests;

import fr.traqueur.commands.api.CommandManager;
import fr.traqueur.energylib.EnergyLib;
import fr.traqueur.energylib.api.components.ComponentsType;
import fr.traqueur.energylib.api.components.EnergyNetwork;

public class EnergyTest {

    public EnergyTest(EnergyLib energyLib) {
        CommandManager commandManager = new CommandManager(energyLib);

        commandManager.registerConverter(ComponentsType.class,"component-type", new ComponentsTypeConverter());

        commandManager.registerCommand(new EnergyCommand(energyLib));

        energyLib.getScheduler().runTimerAsync(() -> {
            energyLib.getManager().getNetworks().forEach(EnergyNetwork::update);
        }, 20L, 20L);
    }

}
