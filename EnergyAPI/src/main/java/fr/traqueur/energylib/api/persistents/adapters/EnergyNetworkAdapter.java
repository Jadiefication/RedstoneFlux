package fr.traqueur.energylib.api.persistents.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fr.traqueur.energylib.api.EnergyAPI;
import fr.traqueur.energylib.api.components.EnergyComponent;
import fr.traqueur.energylib.api.components.EnergyNetwork;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.JsonSyntaxException;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EnergyNetworkAdapter extends TypeAdapter<EnergyNetwork> {

    private final EnergyAPI api;
    private final Gson gson;

    public EnergyNetworkAdapter(EnergyAPI api, Gson gson) {
        this.api = api;
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, EnergyNetwork value) throws IOException {
        out.beginObject();
        out.name("components");
        out.beginObject();
        for (Map.Entry<Location, EnergyComponent<?>> entry : value.getComponents().entrySet()) {
            out.name(this.fromLocation(entry.getKey()));
            gson.toJson(entry.getValue(), EnergyComponent.class, out);
        }
        out.endObject();
        out.endObject();
    }

    @Override
    public EnergyNetwork read(JsonReader in) throws IOException {
        Map<Location, EnergyComponent<?>> components = new ConcurrentHashMap<>();

        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            if (name.equals("components")) {
                in.beginObject();
                while (in.hasNext()) {
                    Location location = this.toLocation(in.nextName());
                    EnergyComponent<?> component = gson.fromJson(in, EnergyComponent.class);
                    components.put(location, component);
                }
                in.endObject();
            } else {
                throw new JsonSyntaxException("Unknown field in EnergyNetwork: " + name);
            }
        }
        in.endObject();

        EnergyNetwork network = new EnergyNetwork(api);
        components.forEach((location, component) -> {
            try {
                network.addComponent(component, location);
            } catch (SameEnergyTypeException e) {
                throw new RuntimeException(e);
            }
        });

        return network;
    }

    private Location toLocation(String string) {
        String[] parts = string.split(",");
        return new Location(parts[0].equals("null") ? null : Bukkit.getServer().getWorld(UUID.fromString(parts[0])), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
    }

    private String fromLocation(Location location) {
        String world = location.getWorld() == null ? "null" : location.getWorld().getUID().toString();
        return world + "," + location.getX() + "," + location.getY() + "," + location.getZ();
    }
}

