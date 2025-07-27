package io.github.Jadiefication.redstoneflux.api.persistents.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import io.github.Jadiefication.redstoneflux.api.types.EnergyType
import java.io.IOException
import java.util.function.Supplier

/**
 * This class is a Gson adapter for EnergyTypes.
 * It is used to serialize and deserialize EnergyTypes.
 */
class EnergyTypeAdapter : TypeAdapter<EnergyType?>() {
    /**
     * Writes an EnergyType to a JsonWriter.
     *
     * @param jsonWriter The JsonWriter.
     * @param value The EnergyType.
     * @throws IOException If an I/O error occurs.
     */
    @Throws(IOException::class)
    override fun write(jsonWriter: JsonWriter, value: EnergyType?) {
        jsonWriter.beginObject()
        jsonWriter.name("name").value(value?.name)
        jsonWriter.endObject()
    }

    /**
     * Reads an EnergyType from a JsonReader.
     *
     * @param jsonReader The JsonReader.
     * @return The EnergyType.
     * @throws IOException If an I/O error occurs.
     */
    @Throws(IOException::class)
    override fun read(jsonReader: JsonReader): EnergyType? {
        var name: String? = null
        jsonReader.beginObject()
        while (jsonReader.hasNext()) {
            val key = jsonReader.nextName()
            if (key == "name") {
                name = jsonReader.nextString()
            }
        }
        jsonReader.endObject()
        val finalName = name
        return EnergyType.Companion.TYPES.stream().filter { type: EnergyType? -> type!!.name == finalName }
            .findFirst().orElseThrow(
                Supplier { IOException("EnergyType not found.") })
    }
}
