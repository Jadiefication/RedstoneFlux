package fr.traqueur.energylib;

import fr.traqueur.energylib.api.EnergyManager;
import fr.traqueur.energylib.api.EnergyType;
import fr.traqueur.energylib.api.components.ComponentsType;
import fr.traqueur.energylib.api.components.EnergyComponent;
import fr.traqueur.energylib.api.components.EnergyNetwork;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import fr.traqueur.energylib.api.persistents.ComponentTypePersistentDataType;
import fr.traqueur.energylib.api.persistents.EnergyTypePersistentDataType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class EnergyManagerImpl implements EnergyManager {

    private static final List<BlockFace> NEIBHORS = List.of(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

    private final NamespacedKey componentClassKey;
    private final NamespacedKey energyTypeKey;
    private final NamespacedKey componentTypeKey;
    private final Set<EnergyNetwork> networks;

    public EnergyManagerImpl(EnergyLib energyLib) {
        this.networks = new HashSet<>();
        this.componentTypeKey = new NamespacedKey(energyLib, "component-type");
        this.energyTypeKey = new NamespacedKey(energyLib, "energy-type");
        this.componentClassKey = new NamespacedKey(energyLib, "component-class");
    }

    @Override
    public void placeComponent(EnergyComponent component, Location location) throws SameEnergyTypeException {
        List<EnergyNetwork> energyNetworks = new ArrayList<>();
        for (BlockFace neibhorFace : NEIBHORS) {
            var neibhor = location.getBlock().getRelative(neibhorFace);
            this.networks.stream().filter(network -> network.contains(neibhor.getLocation())).findFirst().ifPresent(energyNetworks::add);
        }

        energyNetworks = energyNetworks.stream()
                .filter(network -> network.getEnergyType() == component.getEnergyType())
                .collect(Collectors.toList());

        if(energyNetworks.isEmpty()) {
            System.out.println("Create new network");
            EnergyNetwork network = new EnergyNetwork(component, location);
            this.networks.add(network);
        } else if (energyNetworks.size() == 1) {
            System.out.println("Add component to network");
            energyNetworks.getFirst().addComponent(component, location);
        } else {
            System.out.println("Merge networks");
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
    public boolean isComponent(ItemStack item) {
        if(item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if(meta == null) {
            return false;
        }
        PersistentDataContainer persistentDataContainer = meta.getPersistentDataContainer();
        return persistentDataContainer.has(this.getComponentTypeKey(), ComponentTypePersistentDataType.INSTANCE)
                && persistentDataContainer.has(this.getEnergyTypeKey(), EnergyTypePersistentDataType.INSTANCE)
                && persistentDataContainer.has(this.getComponentClassKey(), PersistentDataType.STRING);
    }

    @Override
    public EnergyComponent createComponent(ItemStack item, Location location) {
        var optComponentsType = this.getComponentType(item);
        var optEnergyType = this.getEnergyType(item);
        var optComponentClass = this.getComponentClassType(item);
        if (optComponentsType.isEmpty() || optEnergyType.isEmpty() || optComponentClass.isEmpty()) {
            throw new IllegalArgumentException("The item is not a component.");
        }

        Class<? extends EnergyComponent> componentClass = optComponentClass.get();
        ComponentsType componentsType = optComponentsType.get();

        if (!componentsType.getClazz().isAssignableFrom(componentClass)) {
            throw new IllegalArgumentException("The component class is not the same as the item component type.");
        }

        if(componentClass.getConstructors().length != 1) {
            throw new IllegalArgumentException("The component class must have only one constructor.");
        }

        try {
            return componentClass.getConstructor(EnergyType.class).newInstance(optEnergyType.get());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<EnergyType> getEnergyType(ItemStack item) {
        return this.getPersistentData(item, this.getEnergyTypeKey(), EnergyTypePersistentDataType.INSTANCE);
    }

    @Override
    public Optional<ComponentsType> getComponentType(ItemStack item) {
        return this.getPersistentData(item, this.getComponentTypeKey(), ComponentTypePersistentDataType.INSTANCE);
    }

    @Override
    public Optional<Class<? extends EnergyComponent>> getComponentClassType(ItemStack item) {
        Optional<String> className = this.getPersistentData(item, this.getComponentClassKey(), PersistentDataType.STRING);
        if(className.isEmpty()) {
            return Optional.empty();
        }
        Class<?> clazz;
        try {
            clazz = Class.forName(className.get());
            if (!EnergyComponent.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException(className + " is not a subclass of EnergyComponent.");
            }
            return Optional.of(clazz.asSubclass(EnergyComponent.class));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public NamespacedKey getComponentTypeKey() {
        return this.componentTypeKey;
    }

    @Override
    public NamespacedKey getEnergyTypeKey() {
        return this.energyTypeKey;
    }

    @Override
    public NamespacedKey getComponentClassKey() {
        return this.componentClassKey;
    }

    @Override
    public ItemStack createItemComponent(Material material, ComponentsType componentType, EnergyType type, Class<? extends EnergyComponent> componentClass) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if(meta == null) {
            return item;
        }
        PersistentDataContainer persistentDataContainer = meta.getPersistentDataContainer();
        persistentDataContainer.set(this.getComponentTypeKey(), ComponentTypePersistentDataType.INSTANCE, componentType);
        persistentDataContainer.set(this.getComponentClassKey(), PersistentDataType.STRING, componentClass.getName());
        persistentDataContainer.set(this.getEnergyTypeKey(), EnergyTypePersistentDataType.INSTANCE, type);
        meta.setDisplayName(componentType.name());
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public Set<EnergyNetwork> getNetworks() {
        return this.networks;
    }

    private <C> Optional<C> getPersistentData(ItemStack item, NamespacedKey key, PersistentDataType<?,C> type) {
        if(!this.isComponent(item)) {
            return Optional.empty();
        }

        ItemMeta meta = item.getItemMeta();
        if(meta == null) {
            return Optional.empty();
        }
        PersistentDataContainer persistentDataContainer = meta.getPersistentDataContainer();
        return Optional.ofNullable(persistentDataContainer.get(key, type));
    }

}
