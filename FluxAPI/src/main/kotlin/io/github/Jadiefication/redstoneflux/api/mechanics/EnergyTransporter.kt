package io.github.Jadiefication.redstoneflux.api.mechanics

/**
 * This interface is used to represent an energy transporter.
 */
@Deprecated(
    message = "Deprecated since 1.3.3",
    replaceWith = ReplaceWith("Transporter"),
    level = DeprecationLevel.WARNING
)
interface EnergyTransporter : EnergyMechanic
