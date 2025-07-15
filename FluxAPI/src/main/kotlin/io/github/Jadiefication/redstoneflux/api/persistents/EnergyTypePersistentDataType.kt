package io.github.Jadiefication.redstoneflux.api.persistents

import io.github.Jadiefication.redstoneflux.api.types.EnergyType
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType

/**
 * This class is used to save the EnergyType in a ItemStack
 */
class EnergyTypePersistentDataType : PersistentDataType<String, EnergyType> {
    /**
     * Get the primitive type of the class
     * @return the primitive type
     */
    override fun getPrimitiveType(): Class<String?> {
        return String::class.java as Class<String?>
    }

    /**
     * Get the complex type of the class
     * @return the complex type
     */
    override fun getComplexType(): Class<EnergyType?> {
        return EnergyType::class.java as Class<EnergyType?>
    }

    /**
     * Convert the EnergyType to a primitive type
     * @param energyType the EnergyType
     * @param persistentDataAdapterContext the context
     * @return the primitive type
     */
    override fun toPrimitive(
        energyType: EnergyType,
        persistentDataAdapterContext: PersistentDataAdapterContext
    ): String {
        return energyType.name!!
    }

    /**
     * Convert the primitive type to a EnergyType
     * @param s the primitive type
     * @param persistentDataAdapterContext the context
     * @return the EnergyType
     */
    override fun fromPrimitive(s: String, persistentDataAdapterContext: PersistentDataAdapterContext): EnergyType {
        return EnergyType.Companion.TYPES.stream()
            .filter { type: EnergyType? -> type!!.name == s }
            .findFirst()
            .orElseThrow()!!
    }

    companion object {
        /**
         * The instance of the class
         */
        val INSTANCE: EnergyTypePersistentDataType = EnergyTypePersistentDataType()
    }
}
