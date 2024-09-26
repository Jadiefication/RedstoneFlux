package fr.traqueur.energylib.api.items;

import fr.traqueur.energylib.api.EnergyAPI;
import fr.traqueur.energylib.api.mechanics.EnergyMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemsFactory {

    private static final Map<Class<? extends EnergyMechanic>, ItemStack> ITEM_STACKS_MAP = new HashMap<>();

    public static void registerItem(ItemStack item, Class<? extends EnergyMechanic> clazz) {
        ITEM_STACKS_MAP.put(clazz, item);
    }

    public static void registerItem(Material material, Class<? extends EnergyMechanic> clazz) {
        ITEM_STACKS_MAP.put(clazz, new ItemStack(material));
    }

    public static Optional<ItemStack> getItem(Class<? extends EnergyMechanic> clazz) {
        return Optional.ofNullable(ITEM_STACKS_MAP.get(clazz));
    }

}
