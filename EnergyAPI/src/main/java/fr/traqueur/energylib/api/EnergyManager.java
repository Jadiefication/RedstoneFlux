package fr.traqueur.energylib.api;

import fr.traqueur.energylib.api.components.EnergyComponent;
import fr.traqueur.energylib.api.components.EnergyNetwork;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import fr.traqueur.energylib.api.mechanics.EnergyMechanic;
import fr.traqueur.energylib.api.types.EnergyType;
import org.bukkit.Location;
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

    NamespacedKey getEnergyTypeKey();

    NamespacedKey getMechanicClassKey();

    NamespacedKey getMechanicKey();

    Set<EnergyNetwork> getNetworks();
}
