package io.github.Jadiefication.redstoneflux.api.components

abstract class BaseComponent<C : BaseComponent<C>> {

    /**
     * The components that this component is connected to.
     */
    abstract val connectedComponents: MutableSet<C>

    /**
     * A function to run when connecting components.
     * @param component the component to check.
     * @return if to connect.
     */
    abstract fun checker(component: C): Boolean

    /**
     * Called after the component is connected.
     * @param component the component to do stuff with.
     */
    abstract fun connectionFunction(component: C)

    /**
     * Called after the component is disconnected.
     * @param component the component to do stuff with.
     */
    abstract fun disconnectionFunction(component: C)

    /**
     * Connects this component to another component.
     * @param component The component to connect to.
     */
    fun connect(component: C) {
        if (connectedComponents.contains(component) || !checker(component)) return
        connectedComponents.add(component)
        component.connect(this as C)
        connectionFunction(component)
    }

    /**
     * Disconnects this component from another component.
     * @param component The component to disconnect from.
     */
    fun disconnect(component: C) {
        if (!connectedComponents.contains(component)) return
        connectedComponents.remove(component)
        component.disconnect(this as C)
        disconnectionFunction(component)
    }
}