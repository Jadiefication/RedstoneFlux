package fr.traqueur.energylib.api;

import fr.traqueur.energylib.api.components.EnergyComponent;
import fr.traqueur.energylib.api.components.EnergyNetwork;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import fr.traqueur.energylib.api.mechanics.EnergyMechanic;
import fr.traqueur.energylib.api.types.EnergyType;
import fr.traqueur.energylib.api.types.MechanicType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.Set;

public interface EnergyManager {

    void placeComponent(EnergyComponent<?> component, Location location) throws SameEnergyTypeException;

    void breakComponent(Location location);

    Optional<EnergyType> getEnergyType(ItemStack item);

    Optional<String> getMechanicClass(ItemStack item);

    Optional<? extends EnergyMechanic> getMechanic(ItemStack item);

    boolean isBlockComponent(Location neighbor);

    EnergyComponent<?> createComponent(ItemStack item);

    boolean isComponent(ItemStack item);

    ItemStack createItemComponent(EnergyType type, MechanicType mechanicType, EnergyMechanic mechanic);

    void startNetworkUpdater();

    void stopNetworkUpdater();

    NamespacedKey getEnergyTypeKey();

    NamespacedKey getMechanicClassKey();

    NamespacedKey getMechanicKey();

    Set<EnergyNetwork> getNetworks();
}
