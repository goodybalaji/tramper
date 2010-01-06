package org.tramper.loader;

import java.util.EventObject;

/**
 * 
 * @author Paul-Emile
 */
public class LoaderFactoryEvent extends EventObject {
    /** LoaderFactoryEvent.java long */
    private static final long serialVersionUID = 512625286133138560L;
    /** loader created */
    private Loader loader;
    
    /**
     * 
     * @param arg0
     */
    public LoaderFactoryEvent(Object source) {
        super(source);
    }

    /**
     * @return loader.
     */
    public Loader getLoader() {
        return this.loader;
    }

    /**
     * @param loader loader 
     */
    public void setLoader(Loader loader) {
        this.loader = loader;
    }
}
