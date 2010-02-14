package org.tramper.loader;

import java.util.EventObject;

import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;

/**
 * Event fired when loading a document
 * @author Paul-Emile
 */
public class LoadingEvent extends EventObject {
    /** LoadingEvent.java long */
    private static final long serialVersionUID = -3642707793507156826L;
    /** Loaded document if loading succeeded */
    private SimpleDocument loadedDocument;
    /** loading type */
    private int loadingType;
    /** url of the loading being loaded */
    private String loadedUrl;
    /** target where the document will be loaded */
    private Target loadingTarget;
    
    /**
     * 
     * @param source
     */
    public LoadingEvent(Object source) {
        super(source);
    }
    
    /**
     * 
     * @param source
     * @param document
     * @param type
     * @param url
     * @param target
     */
    public LoadingEvent(Object source, SimpleDocument document, int type, String url, Target target) {
	super(source);
	loadedDocument = document;
	loadingType = type;
	loadedUrl = url;
	loadingTarget = target;
    }
    
    /**
     * 
     * @return loaded document
     */
    public SimpleDocument getLoadedDocument() {
        return loadedDocument;
    }

    /**
     * @return loadingType.
     */
    public int getLoadingType() {
        return this.loadingType;
    }

    /**
     * @return url.
     */
    public String getUrl() {
        return this.loadedUrl;
    }

    /**
     * @return target.
     */
    public Target getTarget() {
        return this.loadingTarget;
    }
}
