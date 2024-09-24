package fr.traqueur.energylib.api;

import com.tcoded.folialib.impl.PlatformScheduler;

public interface EnergyAPI {

    EnergyManager getManager();

    PlatformScheduler getScheduler();
}
