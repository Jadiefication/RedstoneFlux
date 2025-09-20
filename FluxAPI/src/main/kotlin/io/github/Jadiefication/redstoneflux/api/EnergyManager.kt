package io.github.Jadiefication.redstoneflux.api

import io.github.Jadiefication.redstoneflux.api.components.BaseNetwork
import io.github.Jadiefication.redstoneflux.api.components.EnergyComponent
import io.github.Jadiefication.redstoneflux.api.components.EnergyNetwork
import io.github.Jadiefication.redstoneflux.api.mechanics.EnergyMechanic
import io.github.Jadiefication.redstoneflux.api.types.EnergyType
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * The EnergyManager is the principale class of the API, it is used to manage the energy components and networks.
 */
interface EnergyManager : Manager<EnergyComponent<*>> {
    /**
     * Get the energy type of an item.
     *
     * @param item The item to get the energy type from.
     * @return The energy type of the item.
     */
    fun getEnergyType(item: ItemStack): EnergyType?

    /**
     * Get the mechanic type of an item.
     *
     * @param item The item to get the mechanic type from.
     * @return The mechanic type of the item.
     */
    fun getMechanicClass(item: ItemStack): String?

    /**
     * Get the mechanic of an item.
     *
     * @param item The item to get the mechanic from.
     * @return The mechanic of the item.
     */
    fun getMechanic(item: ItemStack): EnergyMechanic?

    /**
     * Delete a network.
     *
     * @param network The network to delete.
     */
    override fun <T : BaseNetwork<EnergyComponent<*>>> deleteNetwork(network: T)

    /**
     * Get all the networks.
     *
     * @return The networks.
     */
    override val networks: MutableSet<EnergyNetwork>
}
