package fr.traqueur.energylib.api.persistents.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fr.traqueur.energylib.api.types.EnergyType;

import java.io.IOException;

/**
 * This class is a Gson adapter for EnergyTypes.
 * It is used to serialize and deserialize EnergyTypes.
 */
public class EnergyTypeAdapter extends TypeAdapter<EnergyType> {

    /**
     * Writes an EnergyType to a JsonWriter.
     *
     * @param jsonWriter The JsonWriter.
     * @param energyType The EnergyType.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void write(JsonWriter jsonWriter, EnergyType energyType) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("name").value(energyType.getName());
        jsonWriter.endObject();
    }

    /**
     * Reads an EnergyType from a JsonReader.
     *
     * @param jsonReader The JsonReader.
     * @return The EnergyType.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public EnergyType read(JsonReader jsonReader) throws IOException {
        String name = null;
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String key = jsonReader.nextName();
            if (key.equals("name")) {
                name = jsonReader.nextString();
            }
        }
        jsonReader.endObject();
        String finalName = name;
        return EnergyType.TYPES.stream().filter(type -> type.getName().equals(finalName)).findFirst().orElseThrow(() -> new IOException("EnergyType not found."));
    }
}
