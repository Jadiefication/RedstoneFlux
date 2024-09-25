package fr.traqueur.energylib.api.components;

import fr.traqueur.energylib.api.types.EnergyType;
import fr.traqueur.energylib.api.exceptions.SameEnergyTypeException;

import java.util.HashSet;
import java.util.Set;

public abstract class EnergyComponent {

    private final EnergyType energyType;
    private final Set<EnergyComponent> connectedComponents;

    protected EnergyComponent(EnergyType energyType) {
        this.connectedComponents = new HashSet<>();
        this.energyType = energyType;
    }

    public void connect(EnergyComponent component) throws SameEnergyTypeException {
        if(this.energyType != component.getEnergyType()) {
            throw new SameEnergyTypeException();
        }
        if(this.connectedComponents.contains(component)) {
            return;
        }
        this.connectedComponents.add(component);
        component.connect(this);
    }

    public void disconnect(EnergyComponent component) {
        if(!this.connectedComponents.contains(component)) {
            return;
        }
        this.connectedComponents.remove(component);
        component.disconnect(this);
    }

    public Set<EnergyComponent> getConnectedComponents() {
        return this.connectedComponents;
    }

    public EnergyType getEnergyType() {
        return this.energyType;
    }

    public abstract void update();

}
