package fr.traqueur.energylib.api.persistents.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fr.traqueur.energylib.api.components.EnergyComponent;
import fr.traqueur.energylib.api.mechanics.EnergyMechanic;
import fr.traqueur.energylib.api.types.EnergyType;

import java.io.IOException;

/**
 * This class is used to serialize and deserialize EnergyComponents.
 * It is used by the EnergyComponentTypeAdapter to serialize and deserialize EnergyComponents.
 */
public class EnergyComponentAdapter extends TypeAdapter<EnergyComponent<?>> {

    /**
     * The Gson instance used to serialize and deserialize the EnergyMechanic.
     */
    private final Gson gson;

    /**
     * Creates a new EnergyComponentAdapter with the given Gson instance.
     * @param gson The Gson instance used to serialize and deserialize the EnergyMechanic.
     */
    public EnergyComponentAdapter(Gson gson) {
        this.gson = gson;
    }

    /**
     * Serializes the given EnergyComponent to the given JsonWriter.
     * @param out The JsonWriter to write the EnergyComponent to.
     * @param value The EnergyComponent to serialize.
     * @throws IOException If an error occurs while writing to the JsonWriter.
     */
    @Override
    public void write(JsonWriter out, EnergyComponent<?> value) throws IOException {
        out.beginObject();

        out.name("energyType");
        gson.toJson(value.getEnergyType(), EnergyType.class, out);

        out.name("mechanic-class");
        out.value(value.getMechanic().getClass().getName());

        out.name("mechanic");
        String data = gson.toJson(value.getMechanic(), value.getMechanic().getClass());
        out.value(data);
        out.endObject();
    }

    /**
     * Deserializes an EnergyComponent from the given JsonReader.
     * @param in The JsonReader to read the EnergyComponent from.
     * @return The deserialized EnergyComponent.
     * @throws IOException If an error occurs while reading from the JsonReader.
     */
    @Override
    public EnergyComponent<?> read(JsonReader in) throws IOException {
        EnergyType energyType = null;
        Class<? extends EnergyMechanic> mechanic  = null;
        String energyMechanicData = null;

        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            switch (name) {
                case "energyType":
                    energyType = gson.fromJson(in, EnergyType.class);
                    break;
                case "mechanic-class":
                    String mechanicClass = in.nextString();
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(mechanicClass);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalArgumentException("Class " + mechanicClass + " not found!");
                    }
                    if(!EnergyMechanic.class.isAssignableFrom(clazz)) {
                        throw new IllegalArgumentException("Class " + mechanicClass + " is not an EnergyMechanic!");
                    }
                    mechanic = clazz.asSubclass(EnergyMechanic.class);
                    break;
                case "mechanic":
                    energyMechanicData = in.nextString();
                    break;
            }
        }
        in.endObject();
        return new EnergyComponent<>(energyType, gson.fromJson(energyMechanicData, mechanic));
    }
}
