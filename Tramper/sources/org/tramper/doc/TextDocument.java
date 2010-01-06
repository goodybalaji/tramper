package org.tramper.doc;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A text document.
 * @author Paul-Emile
 */
public class TextDocument {
    /** mime type of document */
    protected String mimeType;
    /** URL */
    protected URL url;
    /** index of the current "item" in the document */
    private long index;
    /** document currently rendered and receiving input events */
    private boolean active;
    /** document listeners */
    private List<DocumentListener> docListener;

    /**
     * 
     */
    public TextDocument() {
	super();
	docListener = new ArrayList<DocumentListener>();
    }

    /**
     * @return url
     */
    public URL getUrl() {
        return url;
    }

    /**
     * @param url 
     */
    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * Returns the extension of the file, if any
     * @return null if no extension
     */
    public String getExtension() {
        String path = url.getPath();
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex != -1) {
            return path.substring(dotIndex+1);
        }
        return null;
    }
    
    /**
     * 
     * @return mime type
     */
    public String getMimeType() {
        return this.mimeType;
    }
    
    /**
     * 
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * 
     * @return
     */
    public long getIndex() {
	return index;
    }
    
    /**
     * 
     * @param index
     */
    public void setIndex(long index) {
	this.index = index;
    }
    
    /**
     * 
     * @return
     */
    public boolean hasLink() {
	return false;
    }

    /**
     * @return active.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * @param active active 
     */
    public void setActive(boolean active) {
        this.active = active;
        DocumentEvent event = new DocumentEvent(this);
        if (active) {
            fireDocumentActivatedEvent(event);
        } else {
            fireDocumentDeactivatedEvent(event);
        }
    }
    
    /**
     * 
     * @param listener
     */
    public void addDocumentListener(DocumentListener listener) {
	docListener.add(listener);
    }
    
    /**
     * 
     * @param listener
     */
    public void removeDocumentListener(DocumentListener listener) {
	docListener.remove(listener);
    }

    /**
     * 
     * @param event
     */
    private void fireDocumentDeactivatedEvent(DocumentEvent event) {
	for (int i=0; i<docListener.size(); i++) {
	    DocumentListener listener = docListener.get(i);
	    listener.documentDeactivated(event);
	}
    }

    /**
     * 
     * @param event
     */
    private void fireDocumentActivatedEvent(DocumentEvent event) {
	for (int i=0; i<docListener.size(); i++) {
	    DocumentListener listener = docListener.get(i);
	    listener.documentActivated(event);
	}
    }
}
