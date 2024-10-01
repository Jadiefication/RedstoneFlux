package fr.traqueur.energylib.api;

import com.google.gson.Gson;
import fr.traqueur.energylib.api.components.EnergyComponent;
import fr.traqueur.energylib.api.components.EnergyNetwork;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import fr.traqueur.energylib.api.mechanics.EnergyMechanic;
import fr.traqueur.energylib.api.types.EnergyType;
import fr.traqueur.energylib.api.types.MechanicType;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.Set;

/**
 * The EnergyManager is the principale class of the API, it is used to manage the energy components and networks.
 */
public interface EnergyManager {

    /**
     * Handle the placement of a component in the world.
     *
     * @param component The component to place.
     * @param location  The location where the component will be placed.
     * @throws SameEnergyTypeException If a component of the different type is next to the location.
     */
    void placeComponent(EnergyComponent<?> component, Location location) throws SameEnergyTypeException;

    /**
     * Handle the break of a component in the world.
     *
     * @param player
     * @param location The location of the component to break.
     */
    void breakComponent(Player player, Location location);

    /**
     * Get the energy type of an item.
     *
     * @param item The item to get the energy type from.
     * @return The energy type of the item.
     */
    Optional<EnergyType> getEnergyType(ItemStack item);

    /**
     * Get the mechanic type of an item.
     *
     * @param item The item to get the mechanic type from.
     * @return The mechanic type of the item.
     */
    Optional<String> getMechanicClass(ItemStack item);

    /**
     * Get the mechanic of an item.
     *
     * @param item The item to get the mechanic from.
     * @return The mechanic of the item.
     */
    Optional<? extends EnergyMechanic> getMechanic(ItemStack item);

    /**
     * Check if a location is a block component.
     *
     * @param location The location to check.
     * @return True if the location is a block component, false otherwise.
     */
    boolean isBlockComponent(Location location);

    /**
     * Create a component from an item.
     *
     * @param item The item to create the component from.
     * @return The component created.
     */
    EnergyComponent<?> createComponent(ItemStack item);

    /**
     * Check if an item is a component.
     *
     * @param item The item to check.
     * @return True if the item is a component, false otherwise.
     */
    boolean isComponent(ItemStack item);

    /**
     * Create an item from energytype, mechanictype and mechanic.
     *
     * @param type         The energy type of the item.
     * @param mechanicType The mechanic type of the item.
     * @param mechanic     The mechanic of the item.
     * @return The item created.
     */
    ItemStack createItemComponent(EnergyType type, MechanicType mechanicType, EnergyMechanic mechanic);

    /**
     * Start the network updater.
     * The network updater is used to update the energy networks.
     * It is used to update the energy networks every tick.
     */
    void startNetworkUpdater();

    /**
     * Stop the network updater.
     * The network updater is used to update the energy networks.
     * It is used to update the energy networks every tick.
     */
    void stopNetworkUpdater();

    /**
     * Get the energy type key.
     *
     * @return The energy type key.
     */
    NamespacedKey getEnergyTypeKey();

    /**
     * Get the mechanic class key.
     *
     * @return The mechanic class key.
     */
    NamespacedKey getMechanicClassKey();

    /**
     * Get the mechanic key.
     *
     * @return The mechanic key.
     */
    NamespacedKey getMechanicKey();

    /**
     * Get the network key.
     *
     * @return The network key.
     */
    NamespacedKey getNetworkKey();

    /**
     * Get all the networks.
     *
     * @return The networks.
     */
    Set<EnergyNetwork> getNetworks();

    /**
     * Save the networks.
     */
    void saveNetworks();

    /**
     * Load the networks.
     *
     * @param chunk The chunk to load the networks from.
     */
    void loadNetworks(Chunk chunk);

    /**
     * Get the component from a block.
     *
     * @param location The location of the block.
     * @return The component of the block.
     */
    Optional<EnergyComponent<?>> getComponentFromBlock(Location location);

    /**
     * Get the gson instance.
     *
     * @return The gson instance.
     */
    Gson getGson();
}
