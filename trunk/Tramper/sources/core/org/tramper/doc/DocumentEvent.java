package org.tramper.doc;

import java.util.EventObject;

/**
 * @author Paul-Emile
 * 
 */
public class DocumentEvent extends EventObject {
    /** DocumentEvent.java long */
    private static final long serialVersionUID = -4769813821301734760L;

    /**
     * 
     * @param source
     */
    public DocumentEvent(Object source) {
	super(source);
    }

}
