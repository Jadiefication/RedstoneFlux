package fr.traqueur.energylib.api.persistents.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fr.traqueur.energylib.api.types.EnergyType;

import java.io.IOException;

public class EnergyTypeAdapter extends TypeAdapter<EnergyType> {
    @Override
    public void write(JsonWriter jsonWriter, EnergyType energyType) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("name").value(energyType.getName());
        jsonWriter.endObject();
    }

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
