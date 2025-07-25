package io.github.Jadiefication.redstoneflux.api.types

import java.util.List

/**
 * Represents an energy type.
 */
interface EnergyType {
    /**
     * Gets the name of the energy type.
     * @return the name of the energy type
     */
    val name: String?

    companion object {
        /**
         * Registers a new energy type.
         * @param type the type to register
         */
        fun registerType(type: EnergyType) {
            require(
                !TYPES.stream().anyMatch { t: EnergyType? ->
                    t!!.name.equals(
                        type.name,
                        ignoreCase = true
                    )
                }) { "EnergyType with name " + type.name + " already exists!" }
            TYPES.add(type)
        }

        /**
         * The default energy type.
         */
        val TYPES: MutableList<EnergyType?> =
            ArrayList<EnergyType?>(List.of<EnergyTypes?>(*EnergyTypes.entries.toTypedArray()))
    }
}
