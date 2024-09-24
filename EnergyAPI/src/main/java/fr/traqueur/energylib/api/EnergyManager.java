package fr.traqueur.energylib.api;

import fr.traqueur.energylib.api.components.ComponentsType;
import fr.traqueur.energylib.api.components.EnergyComponent;
import fr.traqueur.energylib.api.components.EnergyNetwork;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.Set;

public interface EnergyManager {

    void placeComponent(EnergyComponent component, Location location) throws SameEnergyTypeException;

    void breakComponent(Location location);

    boolean isComponent(ItemStack item);

    EnergyComponent createComponent(ItemStack item, Location location);

    Optional<EnergyType> getEnergyType(ItemStack item);

    Optional<ComponentsType> getComponentType(ItemStack item);

    NamespacedKey getEnergyTypeKey();

    Optional<Class<? extends EnergyComponent>> getComponentClassType(ItemStack item);

    NamespacedKey getComponentTypeKey();

    NamespacedKey getComponentClassKey();

    ItemStack createItemComponent(Material material, ComponentsType componentType, EnergyType type, Class<? extends EnergyComponent> componentClass);

    Set<EnergyNetwork> getNetworks();

    boolean isElectricComponent(Location neighbor);
}
