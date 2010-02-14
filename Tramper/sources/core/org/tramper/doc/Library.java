package org.tramper.doc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.tramper.loader.Loader;
import org.tramper.loader.LoaderFactory;
import org.tramper.loader.LoaderFactoryEvent;
import org.tramper.loader.LoaderFactoryListener;
import org.tramper.loader.LoadingEvent;
import org.tramper.loader.LoadingListener;

/**
 * A library of documents
 * @author Paul-Emile
 */
public class Library implements LoadingListener, LoaderFactoryListener {
    /** The group of document targets */
    protected Map<Target, SimpleDocument> docTargets;
    /** active document */
    protected Target activeTarget;
    /** singleton */
    private static Library instance;
    /** library listeners */
    private List<LibraryListener> libraryListener;
    /**  */
    public static final String PRIMARY_FRAME = "1";
    /**  */
    public static final String SECONDARY_FRAME = "2";
    /**  */
    private int primaryTabIndex = 0;
    /**  */
    private int secondaryTabIndex = 0;
    
    
    /**
     * Returns the unique library.
     * @return
     */
    public static Library getInstance() {
	if (instance == null) {
	    instance = new Library();
	}
	return instance;
    }
    
    /**
     * 
     */
    private Library() {
	docTargets = new HashMap<Target, SimpleDocument>();
	libraryListener = new ArrayList<LibraryListener>();
        LoaderFactory.addLoaderFactoryListener(this);
    }
    
    /**
     * Adds a document in the library.
     * @param documentToAdd the document to add
     * @param targetToAdd 
     */
    public void addDocument(SimpleDocument documentToAdd, Target targetToAdd) {
	String tab = targetToAdd.getTab();
	String frame = targetToAdd.getFrame();
	if (tab == null) {// create a new tab
            if (frame.equals(PRIMARY_FRAME)) {
                tab = String.valueOf(primaryTabIndex);
                primaryTabIndex++;
            } else if (frame.equals(SECONDARY_FRAME)) {
                tab = String.valueOf(secondaryTabIndex);
                secondaryTabIndex++;
            }
            targetToAdd = new Target(frame, tab);
	}
	
	SimpleDocument aDocument = docTargets.get(targetToAdd);
	docTargets.put(targetToAdd, documentToAdd);
	if (aDocument != null) {
	    LibraryEvent modifiedEvent = new LibraryEvent(this);
	    modifiedEvent.setDocument(documentToAdd);
	    modifiedEvent.setTarget(targetToAdd);
	    fireDocumentModifiedEvent(modifiedEvent);
	} else {
	    LibraryEvent addedEvent = new LibraryEvent(this);
	    addedEvent.setDocument(documentToAdd);
	    addedEvent.setTarget(targetToAdd);
	    fireDocumentAddedEvent(addedEvent);
	}
	
	setActiveDocument(targetToAdd);
        
        // add the document to the history
        History history = History.getInstance();
        history.addHistory(documentToAdd);
    }

    /**
     * Remove a document from the library
     * @param target 
     * @return 
     */
    public SimpleDocument removeDocument(Target target) {
        SimpleDocument removedDocument = docTargets.remove(target);
        if (removedDocument != null) {
            LibraryEvent event = new LibraryEvent(this);
            event.setDocument(removedDocument);
            event.setTarget(target);
            fireDocumentRemovedEvent(event);
        }
        return removedDocument;
    }

    /**
     * 
     * @param targetToRemove
     */
    public void removeDocumentAndActivateFirst(Target targetToRemove) {
	SimpleDocument removedDocument = removeDocument(targetToRemove);
	if (removedDocument != null) {
            if (removedDocument.isActive()) {
                if (!docTargets.isEmpty()) {
                    Iterator<Entry<Target, SimpleDocument>> docs = docTargets.entrySet().iterator();
                    Entry<Target, SimpleDocument> firstEntry = docs.next();
                    Target targetToActivate = firstEntry.getKey();
                    setActiveDocument(targetToActivate);
                }
            }
	}
    }

    /**
     * 
     * @return
     */
    public Target getActiveTarget() {
	return activeTarget;
    }
    
    /**
     * Returns the active document from the library.
     * @return the active document or null
     */
    public SimpleDocument getActiveDocument() {
	return docTargets.get(activeTarget);
    }
    
    /**
     * Set the active document. The target in parameter must exist in the library.
     * @param targetToActivate the target of the document to activate
     */
    public void setActiveDocument(Target targetToActivate) {
	if (docTargets.containsKey(targetToActivate)) {
            if (activeTarget != null) {
                LibraryEvent deactivatedEvent = new LibraryEvent(this);
                SimpleDocument activeDocument = docTargets.get(activeTarget);
                if (activeDocument != null) {
                    activeDocument.setActive(false);
                    deactivatedEvent.setDocument(activeDocument);
                }
                deactivatedEvent.setTarget(activeTarget);
                fireDocumentDeactivatedEvent(deactivatedEvent);
            }

            SimpleDocument documentToActivate = docTargets.get(targetToActivate);
            activeTarget = targetToActivate;
            documentToActivate.setActive(true);
            
            LibraryEvent activatedEvent = new LibraryEvent(this);
            activatedEvent.setDocument(documentToActivate);
            activatedEvent.setTarget(targetToActivate);
            fireDocumentActivatedEvent(activatedEvent);
        }
    }

    /**
     * 
     * @param event
     */
    private void fireDocumentAddedEvent(LibraryEvent event) {
	for (LibraryListener list : libraryListener) {
	    list.documentAdded(event);
	}
    }

    /**
     * 
     * @param event
     */
    private void fireDocumentRemovedEvent(LibraryEvent event) {
	for (LibraryListener list : libraryListener) {
	    list.documentRemoved(event);
	}
    }

    /**
     * 
     * @param event
     */
    private void fireDocumentModifiedEvent(LibraryEvent event) {
	for (LibraryListener list : libraryListener) {
	    list.documentModified(event);
	}
    }

    /**
     * 
     * @param event
     */
    private void fireDocumentActivatedEvent(LibraryEvent event) {
	for (LibraryListener list : libraryListener) {
	    list.documentActivated(event);
	}
    }

    /**
     * 
     * @param event
     */
    private void fireDocumentDeactivatedEvent(LibraryEvent event) {
	for (LibraryListener list : libraryListener) {
	    list.documentDeactivated(event);
	}
    }

    /**
     * 
     * @see org.tramper.loader.LoaderFactoryListener#newLoader(org.tramper.loader.LoaderFactoryEvent)
     */
    public void newLoader(LoaderFactoryEvent event) {
	Loader loader = event.getLoader();
	loader.addLoadingListener(this);
    }

    /**
     * 
     * @see org.tramper.loader.LoadingListener#loadingStarted(org.tramper.loader.LoadingEvent)
     */
    public void loadingStarted(LoadingEvent event) {
    }

    /**
     * 
     * @see org.tramper.loader.LoadingListener#loadingCompleted(org.tramper.loader.LoadingEvent)
     */
    public void loadingCompleted(LoadingEvent event) {
	int loadingType = event.getLoadingType();
	if (loadingType == Loader.DOWNLOAD || loadingType == Loader.CALL) {
            // add the new document in the library
	    Target target = event.getTarget();
            SimpleDocument doc = event.getLoadedDocument();
            addDocument(doc, target);
	}
    }

    /**
     * 
     * @see org.tramper.loader.LoadingListener#loadingFailed(org.tramper.loader.LoadingEvent)
     */
    public void loadingFailed(LoadingEvent event) {
    }

    /**
     * 
     * @see org.tramper.loader.LoadingListener#loadingStopped(org.tramper.loader.LoadingEvent)
     */
    public void loadingStopped(LoadingEvent event) {
    }
    
    /**
     * 
     * @param listener
     */
    public void addLibraryListener(LibraryListener listener) {
	libraryListener.add(listener);
    }
    
    /**
     * 
     * @param listener
     */
    public void removeLibraryListener(LibraryListener listener) {
	libraryListener.remove(listener);
    }
}
