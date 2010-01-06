package org.tramper.doc;

/**
 * @author Paul-Emile
 * 
 */
public interface DocumentListener {
    
    /**
     * 
     * @param event
     */
    public void documentActivated(DocumentEvent event);
    
    /**
     * 
     * @param event
     */
    public void documentDeactivated(DocumentEvent event);
}
