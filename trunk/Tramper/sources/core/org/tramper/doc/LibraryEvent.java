package org.tramper.doc;

import java.util.EventObject;

/**
 * @author Paul-Emile
 * 
 */
public class LibraryEvent extends EventObject {
    /** LibraryEvent.java long */
    private static final long serialVersionUID = -7396441510608680393L;
    /** document impacted by the event */
    private SimpleDocument document;
    /** target of the document */
    private Target target;
    
    /**
     * 
     * @param source
     */
    public LibraryEvent(Object source) {
	super(source);
    }

    /**
     * @return document.
     */
    public SimpleDocument getDocument() {
        return this.document;
    }

    /**
     * @param document document 
     */
    public void setDocument(SimpleDocument document) {
        this.document = document;
    }

    /**
     * @return target.
     */
    public Target getTarget() {
        return this.target;
    }

    /**
     * @param target target 
     */
    public void setTarget(Target target) {
        this.target = target;
    }

}
