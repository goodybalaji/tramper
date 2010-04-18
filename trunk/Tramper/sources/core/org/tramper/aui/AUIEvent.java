package org.tramper.aui;

import java.util.EventObject;

import org.tramper.conductor.Conductor;

/**
 * 
 * @author Paul-Emile
 */
public class AUIEvent extends EventObject {
    /** AUIEvent.java long */
    private static final long serialVersionUID = -6770708041346880960L;
    /** conductor */
    private Conductor conductor;

    /**
     * 
     * @param source
     */
    public AUIEvent(Object source) {
	super(source);
    }

    /**
     * @return conductor.
     */
    public Conductor getConductor() {
        return this.conductor;
    }

    /**
     * @param conductor conductor 
     */
    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }

}
