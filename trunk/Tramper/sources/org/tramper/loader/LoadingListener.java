package org.tramper.loader;

import java.util.EventListener;

/**
 * Loading listener
 * @author Paul-Emile
 */
public interface LoadingListener extends EventListener {
    /**
     * called when loading completed
     * @param event
     */
    public void loadingCompleted(LoadingEvent event);
    
    /**
     * called when loading failed
     * @param event
     */
    public void loadingFailed(LoadingEvent event);

    /**
     * called when loading started 
     * @param event
     */
    public void loadingStarted(LoadingEvent event);
    
    /**
     * called when loading stopped for any reason before end
     * @param event
     */
    public void loadingStopped(LoadingEvent event);
}
