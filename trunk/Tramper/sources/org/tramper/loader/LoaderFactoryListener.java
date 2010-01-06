package org.tramper.loader;

import java.util.EventListener;

/**
 * 
 * @author Paul-Emile
 */
public interface LoaderFactoryListener extends EventListener {
    
    /**
     * called when a new loader is created 
     * @param event
     */
    public void newLoader(LoaderFactoryEvent event);
}
