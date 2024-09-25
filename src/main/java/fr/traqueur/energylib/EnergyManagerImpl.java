package fr.traqueur.energylib;

import fr.traqueur.energylib.api.EnergyManager;
import fr.traqueur.energylib.api.components.EnergyComponent;
import fr.traqueur.energylib.api.components.EnergyNetwork;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import fr.traqueur.energylib.api.persistents.EnergyTypePersistentDataType;
import fr.traqueur.energylib.api.types.EnergyType;
import org.bukkit.Location;
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
    private final NamespacedKey energyTypeKey;
    private final Set<EnergyNetwork> networks;

    public EnergyManagerImpl(EnergyLib energyLib) {
        this.api = energyLib;
        this.networks = new HashSet<>();
        this.energyTypeKey = new NamespacedKey(energyLib, "energy-type");
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
    public boolean isBlockComponent(Location neighbor) {
        return this.networks.stream().anyMatch(network -> network.contains(neighbor));
    }

    @Override
    public NamespacedKey getEnergyTypeKey() {
        return this.energyTypeKey;
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

}
