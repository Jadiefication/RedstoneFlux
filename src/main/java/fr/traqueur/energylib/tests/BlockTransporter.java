package fr.traqueur.energylib.tests;

import fr.traqueur.energylib.api.mechanics.EnergyTransporter;
import org.bukkit.Location;

public class BlockTransporter implements EnergyTransporter {

    @Override
    public double handle(Location location) {
        System.out.println("BlockTransporter handle at " + location);
        return 0;
    }
}
