package org.tramper.aui;

/**
 * 
 * @author Paul-Emile
 */
public interface AUIListener {
    /**
     * 
     * @param event
     */
    public void playerAdded(AUIEvent event);
    
    /**
     * 
     * @param event
     */
    public void playerRemoved(AUIEvent event);
    
    /**
     * 
     * @param event
     */
    public void playerActivated(AUIEvent event);
    
    /**
     * 
     * @param event
     */
    public void playerDeactivated(AUIEvent event);
}
