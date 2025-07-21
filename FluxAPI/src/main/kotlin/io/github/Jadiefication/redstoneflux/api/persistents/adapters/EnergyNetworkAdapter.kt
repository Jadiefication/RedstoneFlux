package io.github.Jadiefication.redstoneflux.api.persistents.adapters

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import io.github.Jadiefication.redstoneflux.api.EnergyAPI
import io.github.Jadiefication.redstoneflux.api.components.EnergyComponent
import io.github.Jadiefication.redstoneflux.api.components.EnergyNetwork
import io.github.Jadiefication.redstoneflux.api.exceptions.SameEnergyTypeException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Location
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * This class is a Gson adapter for EnergyNetworks.
 * It is used to serialize and deserialize EnergyNetworks.
 */
class EnergyNetworkAdapter
/**
 * Creates a new EnergyNetworkAdapter.
 *
 * @param api The EnergyAPI instance.
 * @param gson The Gson instance.
 */(
    /**
     * The EnergyAPI instance.
     */
    private val api: EnergyAPI?,
    /**
     * The Gson instance.
     */
    private val gson: Gson
) : TypeAdapter<EnergyNetwork?>() {
    /**
     * Writes an EnergyNetwork to a JsonWriter.
     *
     * @param out The JsonWriter.
     * @param value The EnergyNetwork.
     * @throws IOException If an I/O error occurs.
     */
    override fun write(out: JsonWriter, value: EnergyNetwork?) {
        out.beginObject()
        out.name("components")
        out.beginObject()
        for (entry in value?.components?.entries!!) {
            out.name(this.fromLocation(entry.key!!))
            gson.toJson(entry.value, EnergyComponent::class.java, out)
        }
        out.endObject()
        out.name("id")
        out.value(value.id.toString())
        out.endObject()
    }

    /**
     * Reads an EnergyNetwork from a JsonReader.
     *
     * @param in The JsonReader.
     * @return The EnergyNetwork.
     * @throws IOException If an I/O error occurs.
     */
    @Throws(IOException::class)
    override fun read(`in`: JsonReader): EnergyNetwork {
        val components: MutableMap<Location?, EnergyComponent<*>?> = ConcurrentHashMap<Location?, EnergyComponent<*>?>()
        var id: String? = null
        `in`.beginObject()
        while (`in`.hasNext()) {
            val name = `in`.nextName()
            if (name == "components") {
                `in`.beginObject()
                while (`in`.hasNext()) {
                    val location = this.toLocation(`in`.nextName())
                    val component = gson.fromJson<EnergyComponent<*>?>(`in`, EnergyComponent::class.java)
                    components.put(location, component)
                }
                `in`.endObject()
            } else if (name.equals("id", ignoreCase = true)) {
                id = `in`.nextString()
            } else {
                throw JsonSyntaxException("Unknown field in EnergyNetwork: $name")
            }
        }
        `in`.endObject()

        val network = EnergyNetwork(api!!, UUID.fromString(id))
        val defers = mutableListOf<Deferred<Unit>>()
        components.forEach { (location: Location?, component: EnergyComponent<*>?) ->
            defers.add(api.scope.async {
                try {
                    network.addComponent(component!!, location!!)
                } catch (e: SameEnergyTypeException) {
                    throw RuntimeException(e)
                }
            })
        }

        api.scope.launch {
            defers.awaitAll()
        }

        return network
    }

    /**
     * Converts a string to a Location.
     *
     * @param string The string.
     * @return The Location.
     */
    private fun toLocation(string: String): Location {
        val parts: Array<String?> = string.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return Location(
            if (parts[0] == "null") null else Bukkit.getServer().getWorld(UUID.fromString(parts[0])),
            parts[1]!!.toDouble(),
            parts[2]!!.toDouble(),
            parts[3]!!.toDouble()
        )
    }

    /**
     * Converts a Location to a string.
     *
     * @param location The Location.
     * @return The string.
     */
    private fun fromLocation(location: Location): String {
        val world: String? = if (location.getWorld() == null) "null" else location.getWorld()!!.uid.toString()
        return world + "," + location.x + "," + location.y + "," + location.z
    }
}

