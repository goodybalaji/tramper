package org.tramper.aui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tramper.conductor.Conductor;
import org.tramper.conductor.ConductorFactory;
import org.tramper.doc.Library;
import org.tramper.doc.LibraryEvent;
import org.tramper.doc.LibraryListener;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.gui.GraphicalUserInterface;
import org.tramper.recognizer.RecognitionException;
import org.tramper.recognizer.SpeechRecognizerFactory;
import org.tramper.ui.UserInterface;
import org.tramper.ui.UserInterfaceFactory;

/**
 * Auditory user interface.
 * @author Paul-Emile
 */
public class AudioUserInterface implements UserInterface, LibraryListener {
    /** logger */
    private Logger logger = Logger.getLogger(AudioUserInterface.class);
    /** document's conductor */
    protected Map<Target, Conductor> conductorByTarget;
    /** audio user interface listeners list */
    private List<AUIListener> auiListener = new ArrayList<AUIListener>();
    
    /**
     * 
     */
    public AudioUserInterface() {
	conductorByTarget = new HashMap<Target, Conductor>();
        //load the recognizer
        try {
            SpeechRecognizerFactory.getSpeechRecognizer();
        } catch (RecognitionException e) {
            logger.error("can't launch the recorder", e);
        }
	Library.getInstance().addLibraryListener(this);
    }
    
    /**
     * Starts rendering the document at the beginning.
     * @param document the document to render
     * @param target the target where to render the document
     */
    public void renderDocument(SimpleDocument document, Target target) {
	Conductor aConductor = null;
        try {
            aConductor = ConductorFactory.getConductorByDocument(document);
            aConductor.render(document, target);
        } catch (Exception e) {
            logger.error("unable to get a conductor for the document "+document);
            GraphicalUserInterface gui = UserInterfaceFactory.getGraphicalUserInterface();
            gui.raiseError("noplayer");
            return;
        }
        conductorByTarget.put(target, aConductor);
        
        AUIEvent event = new AUIEvent(this);
        event.setConductor(aConductor);
        firePlayerAddedEvent(event);
    }

    /**
     * 
     * @see org.tramper.ui.UserInterface#getActiveRenderer()
     */
    public Conductor getActiveRenderer() {
	for (Conductor aConductor : conductorByTarget.values()) {
	    if (aConductor.isActive()) {
		return aConductor;
	    }
	}
	return null;
    }

    /**
     * 
     * @param target the target where the conductor is supposed to be
     * @return the conductor
     */
    public Conductor getPlayer(Target target) {
	return conductorByTarget.get(target);
    }
    
    /**
     * 
     * @see org.tramper.doc.LibraryListener#documentAdded(org.tramper.doc.LibraryEvent)
     */
    public void documentAdded(LibraryEvent event) {
        SimpleDocument document = event.getDocument();
        Target target = event.getTarget();
        renderDocument(document, target);
    }

    /**
     * 
     * @see org.tramper.doc.LibraryListener#documentModified(org.tramper.doc.LibraryEvent)
     */
    public void documentModified(LibraryEvent event) {
        SimpleDocument document = event.getDocument();
        Target target = event.getTarget();
        removePlayer(target);
        renderDocument(document, target);
    }

    /**
     * 
     * @see org.tramper.doc.LibraryListener#documentRemoved(org.tramper.doc.LibraryEvent)
     */
    public void documentRemoved(LibraryEvent event) {
	Target target = event.getTarget();
	removePlayer(target);
    }

    /**
     * 
     * @param target 
     */
    public void removePlayer(Target target) {
	Conductor removedConductor = conductorByTarget.remove(target);
	if (removedConductor != null) {
	    if (removedConductor.isRunning()) {
		removedConductor.stop();
	    }
    
            AUIEvent auiEvent = new AUIEvent(this);
            auiEvent.setConductor(removedConductor);
            firePlayerRemovedEvent(auiEvent);
	}
    }
    
    /**
     * 
     * @see org.tramper.doc.LibraryListener#documentActivated(org.tramper.doc.LibraryEvent)
     */
    public void documentActivated(LibraryEvent event) {
	Conductor activatedConductor = conductorByTarget.get(event.getTarget());
	if (activatedConductor != null) {
            AUIEvent auiEvent = new AUIEvent(this);
            auiEvent.setConductor(activatedConductor);
            firePlayerActivatedEvent(auiEvent);
	}
    }

    /**
     * 
     * @see org.tramper.doc.LibraryListener#documentDeactivated(org.tramper.doc.LibraryEvent)
     */
    public void documentDeactivated(LibraryEvent event) {
	Conductor deactivatedConductor = conductorByTarget.get(event.getTarget());
	if (deactivatedConductor != null) {
            AUIEvent auiEvent = new AUIEvent(this);
            auiEvent.setConductor(deactivatedConductor);
            firePlayerDeactivatedEvent(auiEvent);
	}
    }

    /**
     * 
     * @param auiEvent 
     */
    private void firePlayerRemovedEvent(AUIEvent auiEvent) {
	for (AUIListener aListener : auiListener) {
	    aListener.playerRemoved(auiEvent);
	}
    }

    /**
     * 
     * @param event 
     */
    private void firePlayerAddedEvent(AUIEvent event) {
	for (AUIListener aListener : auiListener) {
	    aListener.playerAdded(event);
	}
    }

    /**
     * 
     * @param event 
     */
    private void firePlayerActivatedEvent(AUIEvent event) {
	for (AUIListener aListener : auiListener) {
	    aListener.playerActivated(event);
	}
    }

    /**
     * 
     * @param event 
     */
    private void firePlayerDeactivatedEvent(AUIEvent event) {
	for (AUIListener aListener : auiListener) {
	    aListener.playerDeactivated(event);
	}
    }

    /**
     * 
     * @param aListener 
     */
    public void addAUIListener(AUIListener aListener) {
	if (!auiListener.contains(aListener)) {
	    auiListener.add(aListener);
	}
    }
    
    /**
     * 
     * @param aListener
     */
    public void removeAUIListener(AUIListener aListener) {
	if (auiListener.contains(aListener)) {
	    auiListener.remove(aListener);
	}
    }

    /**
     * 
     * @see org.tramper.ui.UserInterface#unregister()
     */
    public void unregister() {
	Library.getInstance().removeLibraryListener(this);
    }
}
