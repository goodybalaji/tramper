package org.tramper.doc;

/**
 * @author Paul-Emile
 * 
 */
public interface LibraryListener {
    
    /**
     * 
     * @param event
     */
    public void documentRemoved(LibraryEvent event);
    /**
     * 
     * @param event
     */
    public void documentAdded(LibraryEvent event);
    /**
     * 
     * @param event
     */
    public void documentModified(LibraryEvent event);
    /**
     * Document activated
     * @param event 
     */
    public void documentActivated(LibraryEvent event);
    /**
     * Document deactivated
     * @param event 
     */
    public void documentDeactivated(LibraryEvent event);
}
