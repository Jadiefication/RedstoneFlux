package io.github.Jadiefication.redstoneflux.api.persistents.adapters

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import io.github.Jadiefication.redstoneflux.api.components.EnergyComponent
import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyMechanic
import io.github.Jadiefication.redstoneflux.api.types.EnergyType
import java.io.IOException

/**
 * This class is used to serialize and deserialize EnergyComponents.
 * It is used by the EnergyComponentTypeAdapter to serialize and deserialize EnergyComponents.
 */
class EnergyComponentAdapter
/**
 * Creates a new EnergyComponentAdapter with the given Gson instance.
 * @param gson The Gson instance used to serialize and deserialize the EnergyMechanic.
 */(
    /**
     * The Gson instance used to serialize and deserialize the EnergyMechanic.
     */
    private val gson: Gson
) : TypeAdapter<EnergyComponent<*>?>() {
    /**
     * Serializes the given EnergyComponent to the given JsonWriter.
     * @param out The JsonWriter to write the EnergyComponent to.
     * @param value The EnergyComponent to serialize.
     * @throws IOException If an error occurs while writing to the JsonWriter.
     */
    override fun write(out: JsonWriter, value: EnergyComponent<*>?) {
        out.beginObject()

        out.name("energyType")
        gson.toJson(value?.energyType, EnergyType::class.java, out)

        out.name("mechanic-class")
        out.value(value?.mechanic?.javaClass?.getName())

        out.name("mechanic")
        val data = gson.toJson(value?.mechanic, value?.mechanic?.javaClass)
        out.value(data)
        out.endObject()
    }

    /**
     * Deserializes an EnergyComponent from the given JsonReader.
     * @param in The JsonReader to read the EnergyComponent from.
     * @return The deserialized EnergyComponent.
     * @throws IOException If an error occurs while reading from the JsonReader.
     */
    @Throws(IOException::class)
    override fun read(`in`: JsonReader): EnergyComponent<*> {
        var energyType: EnergyType? = null
        var mechanic: Class<out EnergyMechanic?>? = null
        var energyMechanicData: String? = null

        `in`.beginObject()
        while (`in`.hasNext()) {
            val name = `in`.nextName()
            when (name) {
                "energyType" -> energyType = gson.fromJson<EnergyType?>(`in`, EnergyType::class.java)
                "mechanic-class" -> {
                    val mechanicClass = `in`.nextString()
                    val clazz: Class<*>?
                    try {
                        clazz = Class.forName(mechanicClass)
                    } catch (e: ClassNotFoundException) {
                        throw IllegalArgumentException("Class $mechanicClass not found!")
                    }
                    require(EnergyMechanic::class.java.isAssignableFrom(clazz)) { "Class $mechanicClass is not an EnergyMechanic!" }
                    mechanic = clazz.asSubclass(EnergyMechanic::class.java)
                }

                "mechanic" -> energyMechanicData = `in`.nextString()
            }
        }
        `in`.endObject()
        return EnergyComponent(energyType, gson.fromJson(energyMechanicData, mechanic))
    }
}
