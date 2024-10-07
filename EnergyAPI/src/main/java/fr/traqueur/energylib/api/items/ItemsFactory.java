package fr.traqueur.energylib.api.items;

import fr.traqueur.energylib.api.EnergyAPI;
import fr.traqueur.energylib.api.mechanics.EnergyMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class is used to register items for each EnergyMechanic.
 * You can register an item with a class or a material.
 * If you register an item with a class, you can get it with the class.
 * If you register an item with a material, you can get it with the class.
 */
public class ItemsFactory {

    /**
     * This map contains all the items registered for each EnergyMechanic.
     */
    private static final Map<Class<? extends EnergyMechanic>, ItemStack> ITEM_STACKS_MAP = new HashMap<>();

    /**
     * This method is used to register all the items for each EnergyMechanic.
     */
    public static void registerItem(ItemStack item, Class<? extends EnergyMechanic> clazz) {
        ITEM_STACKS_MAP.put(clazz, item);
    }

    /**
     * This method is used to register all the items for each EnergyMechanic.
     */
    public static void registerItem(Material material, Class<? extends EnergyMechanic> clazz) {
        ITEM_STACKS_MAP.put(clazz, new ItemStack(material));
    }

    /**
     * This method is used to get an item for a class.
     */
    public static Optional<ItemStack> getItem(Class<? extends EnergyMechanic> clazz) {
        return Optional.ofNullable(ITEM_STACKS_MAP.get(clazz));
    }

}
