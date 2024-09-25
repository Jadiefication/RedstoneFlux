package fr.traqueur.energylib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.traqueur.energylib.api.EnergyManager;
import fr.traqueur.energylib.api.components.EnergyComponent;
import fr.traqueur.energylib.api.components.EnergyNetwork;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import fr.traqueur.energylib.api.mechanics.EnergyMechanic;
import fr.traqueur.energylib.api.persistents.EnergyTypePersistentDataType;
import fr.traqueur.energylib.api.types.EnergyType;
import fr.traqueur.energylib.api.types.MechanicType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Collectors;

public class EnergyManagerImpl implements EnergyManager {

    private static final List<BlockFace> NEIBHORS = List.of(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

    private final EnergyLib api;
    private final Gson gson;
    private final NamespacedKey energyTypeKey;
    private final NamespacedKey mechanicClassKey;
    private final NamespacedKey mechanicKey;
    private final Set<EnergyNetwork> networks;

    public EnergyManagerImpl(EnergyLib energyLib) {
        this.api = energyLib;
        this.gson = this.createGson();
        this.networks = new HashSet<>();
        this.energyTypeKey = new NamespacedKey(energyLib, "energy-type");
        this.mechanicClassKey = new NamespacedKey(energyLib, "mechanic-class");
        this.mechanicKey = new NamespacedKey(energyLib, "mechanic");
    }

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
                this.networks.remove(network);
            }
        }
    }

    @Override
    public void breakComponent(Location location) {
        EnergyNetwork network = this.networks.stream().filter(n -> n.contains(location)).findFirst().orElse(null);
        if(network == null) {
            return;
        }
        network.removeComponent(location);
        if(network.isEmpty()) {
            this.networks.remove(network);
        }

        try {
            this.splitNetworkIfNecessary(network);
        } catch (SameEnergyTypeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<EnergyType> getEnergyType(ItemStack item) {
        return this.getPersistentData(item, this.getEnergyTypeKey(), EnergyTypePersistentDataType.INSTANCE);
    }

    @Override
    public Optional<String> getMechanicClass(ItemStack item) {
        return this.getPersistentData(item, this.getMechanicClassKey(), PersistentDataType.STRING);
    }

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

    @Override
    public boolean isBlockComponent(Location neighbor) {
        return this.networks.stream().anyMatch(network -> network.contains(neighbor));
    }

    @Override
    public EnergyComponent<?> createComponent(ItemStack item) {
        EnergyType energyType = this.getEnergyType(item).orElseThrow();
        EnergyMechanic mechanic = this.getMechanic(item).orElseThrow();
        return new EnergyComponent<>(energyType, mechanic);
    }

    @Override
    public boolean isComponent(ItemStack item) {
        return this.getEnergyType(item).isPresent()
                && this.getMechanicClass(item).isPresent()
                && this.getMechanic(item).isPresent();
    }

    @Override
    public ItemStack createItemComponent(Material material, EnergyType type, MechanicType mechanicType, EnergyMechanic mechanic) {
        if (mechanicType.getClazz().isAssignableFrom(mechanic.getClass())) {
            throw new IllegalArgumentException("Mechanic type " + mechanicType + " is not compatible with mechanic " + mechanic.getClass());
        }

        ItemStack item = new ItemStack(material);
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

    @Override
    public NamespacedKey getEnergyTypeKey() {
        return this.energyTypeKey;
    }

    @Override
    public NamespacedKey getMechanicClassKey() {
        return this.mechanicClassKey;
    }

    @Override
    public NamespacedKey getMechanicKey() {
        return this.mechanicKey;
    }

    @Override
    public Set<EnergyNetwork> getNetworks() {
        return this.networks;
    }

    private void splitNetworkIfNecessary(EnergyNetwork network) throws SameEnergyTypeException {
        Set<Location> visited = new HashSet<>();
        List<EnergyNetwork> newNetworks = new ArrayList<>();

        for (Location component : network.getComponents().keySet()) {
            if (!visited.contains(component)) {
                Set<Map.Entry<Location, EnergyComponent<?>>> subNetworkComponents = discoverSubNetwork(component, visited);
                if (!subNetworkComponents.isEmpty()) {
                    EnergyNetwork newNetwork = new EnergyNetwork(this.api);
                    for (Map.Entry<Location, EnergyComponent<?>> subComponent : subNetworkComponents) {
                        newNetwork.addComponent(subComponent.getValue(), subComponent.getKey());
                    }
                    newNetworks.add(newNetwork);
                }
            }
        }

        this.networks.remove(network);
        this.networks.addAll(newNetworks);
    }

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

    private <C> Optional<C> getPersistentData(ItemStack item, NamespacedKey key, PersistentDataType<?,C> type) {
        ItemMeta meta = item.getItemMeta();
        if(meta == null) {
            return Optional.empty();
        }
        PersistentDataContainer persistentDataContainer = meta.getPersistentDataContainer();
        return Optional.ofNullable(persistentDataContainer.get(key, type));
    }

    private Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
    }

}
