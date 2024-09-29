package fr.traqueur.energylib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import fr.traqueur.energylib.api.EnergyAPI;
import fr.traqueur.energylib.api.EnergyManager;
import fr.traqueur.energylib.api.components.EnergyComponent;
import fr.traqueur.energylib.api.components.EnergyNetwork;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import fr.traqueur.energylib.api.items.ItemsFactory;
import fr.traqueur.energylib.api.mechanics.EnergyMechanic;
import fr.traqueur.energylib.api.persistents.EnergyTypePersistentDataType;
import fr.traqueur.energylib.api.persistents.adapters.EnergyComponentAdapter;
import fr.traqueur.energylib.api.persistents.adapters.EnergyNetworkAdapter;
import fr.traqueur.energylib.api.persistents.adapters.EnergyTypeAdapter;
import fr.traqueur.energylib.api.types.EnergyType;
import fr.traqueur.energylib.api.types.MechanicType;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * This class is the implementation of the EnergyManager interface.
 * It allows to place and break energy components in the world.
 */
public class EnergyManagerImpl implements EnergyManager {

    /**
     * The list of the 6 block faces.
     */
    private static final List<BlockFace> NEIBHORS = List.of(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

    /**
     * The EnergyLib instance.
     */
    private final EnergyAPI api;

    /**
     * The Gson instance.
     */
    private final Gson gson;

    /**
     * The key to store the energy type in the item meta.
     */
    private final NamespacedKey energyTypeKey;

    /**
     * The key to store the mechanic class in the item meta.
     */
    private final NamespacedKey mechanicClassKey;

    /**
     * The key to store the mechanic in the item meta.
     */
    private final NamespacedKey mechanicKey;

    /**
     * The key to store the network in the chunk.
     */
    private final NamespacedKey networkKey;

    /**
     * The set of all the energy networks.
     */
    private final Set<EnergyNetwork> networks;

    /**
     * The task that updates the networks.
     */
    private WrappedTask updaterTask;

    /**
     * Create a new EnergyManagerImpl instance.
     * @param energyLib the EnergyLib instance
     */
    public EnergyManagerImpl(EnergyLib energyLib) {
        this.api = energyLib;
        this.gson = this.createGson();
        this.networks = new HashSet<>();
        this.energyTypeKey = new NamespacedKey(energyLib, "energy-type");
        this.mechanicClassKey = new NamespacedKey(energyLib, "mechanic-class");
        this.mechanicKey = new NamespacedKey(energyLib, "mechanic");
        this.networkKey = new NamespacedKey(energyLib, "network");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void placeComponent(EnergyComponent<?> component, Location location) throws SameEnergyTypeException {
        List<EnergyNetwork> energyNetworks = new ArrayList<>();
        for (BlockFace neibhorFace : NEIBHORS) {
            var neibhor = location.getBlock().getRelative(neibhorFace);
            var networkNeighbor = this.networks.stream().filter(network -> network.contains(neibhor.getLocation())).findFirst();
            if(networkNeighbor.isPresent()) {
                if(!energyNetworks.contains(networkNeighbor.get()))
                    energyNetworks.add(networkNeighbor.get());
            }
        }

        energyNetworks = energyNetworks.stream()
                .filter(network -> network.getEnergyType() == component.getEnergyType())
                .collect(Collectors.toList());

        if(energyNetworks.isEmpty()) {
            EnergyNetwork network = new EnergyNetwork(this.api, component, location);
            this.networks.add(network);
        } else if (energyNetworks.size() == 1) {
            energyNetworks.getFirst().addComponent(component, location);
        } else {
            EnergyNetwork firstNetwork = energyNetworks.getFirst();
            firstNetwork.addComponent(component, location);
            for (int i = 1; i < energyNetworks.size(); i++) {
                EnergyNetwork network = energyNetworks.get(i);
                firstNetwork.mergeWith(network);
                network.delete();
                this.networks.remove(network);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void breakComponent(Player player, Location location) {
        EnergyNetwork network = this.networks.stream().filter(n -> n.contains(location)).findFirst().orElse(null);
        if(network == null) {
            return;
        }

        EnergyComponent<?> component = network.getComponents().get(location);
        EnergyType energyType = component.getEnergyType();
        MechanicType mechanicType = MechanicType.fromComponent(component);
        EnergyMechanic mechanic = component.getMechanic();

        location.getBlock().setType(Material.AIR);
        if(player.getGameMode() != GameMode.CREATIVE) {
            ItemStack result = this.createItemComponent(energyType, mechanicType, mechanic);
            player.getWorld().dropItemNaturally(location, result);
        }

        network.removeComponent(location);

        if(network.isEmpty()) {
            network.delete();
            this.networks.remove(network);
            return;
        }

        this.splitNetworkIfNecessary(network);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<EnergyType> getEnergyType(ItemStack item) {
        return this.getPersistentData(item, this.getEnergyTypeKey(), EnergyTypePersistentDataType.INSTANCE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> getMechanicClass(ItemStack item) {
        return this.getPersistentData(item, this.getMechanicClassKey(), PersistentDataType.STRING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<? extends EnergyMechanic> getMechanic(ItemStack item) {
        String mechanicClass = this.getMechanicClass(item).orElseThrow();
        Class<?> clazz;
        try {
            clazz = Class.forName(mechanicClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class " + mechanicClass + " not found!");
        }
        if(!EnergyMechanic.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Class " + mechanicClass + " is not an EnergyMechanic!");
        }
        Class<? extends EnergyMechanic> mechanicClazz = clazz.asSubclass(EnergyMechanic.class);
        var opt = this.getPersistentData(item, this.getMechanicKey(), PersistentDataType.STRING);
        if(opt.isEmpty()) {
            return Optional.empty();
        }
        String mechanicData = opt.get();
        return Optional.of(this.gson.fromJson(mechanicData, mechanicClazz));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBlockComponent(Location neighbor) {
        return this.networks.stream().anyMatch(network -> network.contains(neighbor));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EnergyComponent<?> createComponent(ItemStack item) {
        EnergyType energyType = this.getEnergyType(item).orElseThrow();
        EnergyMechanic mechanic = this.getMechanic(item).orElseThrow();
        return new EnergyComponent<>(energyType, mechanic);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isComponent(ItemStack item) {
        return this.getEnergyType(item).isPresent()
                && this.getMechanicClass(item).isPresent()
                && this.getMechanic(item).isPresent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ItemStack createItemComponent(EnergyType type, MechanicType mechanicType, EnergyMechanic mechanic) {
        if (mechanic.getClass().isAssignableFrom(mechanicType.getClazz())) {
            throw new IllegalArgumentException("Mechanic type " + mechanicType.getClazz() + " is not compatible with mechanic " + mechanic.getClass());
        }

        ItemStack item = ItemsFactory.getItem(mechanic.getClass())
                .orElseThrow(() -> new IllegalArgumentException("Item not found for mechanic " + mechanic.getClass()));

        ItemMeta meta = item.getItemMeta();
        if(meta == null) {
            throw new IllegalArgumentException("ItemMeta is null!");
        }
        PersistentDataContainer persistentDataContainer = meta.getPersistentDataContainer();
        persistentDataContainer.set(this.getEnergyTypeKey(), EnergyTypePersistentDataType.INSTANCE, type);
        persistentDataContainer.set(this.getMechanicClassKey(), PersistentDataType.STRING, mechanic.getClass().getName());
        persistentDataContainer.set(this.getMechanicKey(), PersistentDataType.STRING, this.gson.toJson(mechanic, mechanic.getClass()));
        item.setItemMeta(meta);
        return item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startNetworkUpdater() {
        this.updaterTask = this.api.getScheduler().runTimerAsync(() -> {
            this.networks.forEach(energyNetwork -> {
                if (energyNetwork.isEnable()) {
                    energyNetwork.update();
                }
            });
        }, 0L, 1L);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopNetworkUpdater() {
        if(this.updaterTask == null) {
            throw new IllegalStateException("Updater task is not running!");
        }
        this.updaterTask.cancel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disableInChunk(Chunk chunk) {
        Set<EnergyNetwork> networksInChunk = this.networks.stream()
                .filter(network -> network.isInChunk(chunk))
                .collect(Collectors.toSet());
        networksInChunk.forEach(network -> network.setEnable(false));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enableInChunk(Chunk chunk) {
        Set<EnergyNetwork> networksInChunk = this.networks.stream()
                .filter(network -> network.isInChunk(chunk))
                .collect(Collectors.toSet());
        networksInChunk.forEach(network -> network.setEnable(true));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveNetworks() {
        this.networks.forEach(EnergyNetwork::save);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadNetworks(Chunk chunk) {
        PersistentDataContainer chunkData = chunk.getPersistentDataContainer();
        if(chunkData.has(this.getNetworkKey(), PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING))) {
            List<String> networkDatas = chunkData.getOrDefault(this.getNetworkKey(), PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING), new ArrayList<>());
            for (String networkData : networkDatas) {
                EnergyNetwork network = this.gson.fromJson(networkData, EnergyNetwork.class);
                if(this.networks.stream().noneMatch(n -> n.getId().equals(network.getId()))) {
                    this.networks.add(network);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<EnergyComponent<?>> getComponentFromBlock(Location location) {
        Optional<EnergyNetwork> optionalEnergyNetwork = this.networks.stream()
                .filter(network -> network.contains(location))
                .findFirst();

        return optionalEnergyNetwork.map(energyNetwork -> energyNetwork.getComponents().get(location));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Gson getGson() {
        return this.gson;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NamespacedKey getEnergyTypeKey() {
        return this.energyTypeKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NamespacedKey getMechanicClassKey() {
        return this.mechanicClassKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NamespacedKey getMechanicKey() {
        return this.mechanicKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NamespacedKey getNetworkKey() {
        return this.networkKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<EnergyNetwork> getNetworks() {
        return this.networks;
    }

    /**
     * Check if th network must be split.
     * @param network the network
     */
    private void splitNetworkIfNecessary(EnergyNetwork network) {
        Set<Location> visited = new HashSet<>();
        List<EnergyNetwork> newNetworks = new ArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Location component : network.getComponents().keySet()) {
            var future = this.api.getScheduler().runAtLocation(component, (t) -> {
                if (!visited.contains(component)) {
                    Set<Map.Entry<Location, EnergyComponent<?>>> subNetworkComponents = discoverSubNetwork(component, visited);
                    if (!subNetworkComponents.isEmpty()) {
                        EnergyNetwork newNetwork = new EnergyNetwork(this.api, UUID.randomUUID());
                        for (Map.Entry<Location, EnergyComponent<?>> subComponent : subNetworkComponents) {
                            try {
                                newNetwork.addComponent(subComponent.getValue(), subComponent.getKey());
                            } catch (SameEnergyTypeException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        newNetworks.add(newNetwork);
                    }
                }
            });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenAccept((t) -> {
            network.delete();
            this.networks.remove(network);
            this.networks.addAll(newNetworks);
        });
    }

    /**
     * Discover the sub network of a block.
     * @param startBlock the start block
     * @param visited the set of visited blocks
     * @return the set of components
     */
    private Set<Map.Entry<Location, EnergyComponent<?>>> discoverSubNetwork(Location startBlock, Set<Location> visited) {
        Set<Map.Entry<Location, EnergyComponent<?>>> subNetwork = new HashSet<>();
        Queue<Location> queue = new LinkedList<>();
        queue.add(startBlock);

        while (!queue.isEmpty()) {
            Location current = queue.poll();
            if (!visited.contains(current)) {
                visited.add(current);
                subNetwork.add(new AbstractMap.SimpleEntry<>(current, this.networks.stream()
                        .filter(network -> network.contains(current))
                        .findFirst()
                        .map(network -> network.getComponents().get(current))
                        .orElse(null)));

                for (BlockFace face : NEIBHORS) {
                    Location neighbor = current.getBlock().getRelative(face).getLocation();
                    if (isBlockComponent(neighbor) && !visited.contains(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }
        }

        return subNetwork;
    }

    /**
     * Get the persistent data of an item.
     * @param item the item
     * @param key the key
     * @param type the type
     * @param <C> the type of the data
     * @return the optional of the data
     */
    private <C> Optional<C> getPersistentData(ItemStack item, NamespacedKey key, PersistentDataType<?,C> type) {
        ItemMeta meta = item.getItemMeta();
        if(meta == null) {
            return Optional.empty();
        }
        PersistentDataContainer persistentDataContainer = meta.getPersistentDataContainer();
        return Optional.ofNullable(persistentDataContainer.get(key, type));
    }

    /**
     * Create the Gson instance.
     * @return the Gson instance
     */
    private Gson createGson() {
        GsonBuilder builder = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeAdapter(EnergyType.class, new EnergyTypeAdapter());

        Gson temp = builder.create();
        builder.registerTypeAdapter(EnergyComponent.class, new EnergyComponentAdapter(temp));

        Gson temp2 = builder.create();
        builder.registerTypeAdapter(EnergyNetwork.class, new EnergyNetworkAdapter(this.api, temp2));

        return builder.create();
    }

}
