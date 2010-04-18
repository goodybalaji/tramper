package org.tramper.aui;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.tramper.conductor.Conductor;
import org.tramper.conductor.ConductorFactory;
import org.tramper.doc.Library;
import org.tramper.doc.LibraryEvent;
import org.tramper.doc.LibraryListener;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.player.PlayException;
import org.tramper.player.Player;
import org.tramper.player.PlayerFactory;
import org.tramper.recognizer.RecognitionException;
import org.tramper.recognizer.SpeechRecognizerFactory;
import org.tramper.synthesizer.SpeechSynthesizer;
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
     * speak a confirmation message with an appropriate sound.
     * @param msgKey message key
     * @return true if confirmed by the user, false otherwise
     */
    public boolean confirmMessage(String msgKey) {
	return confirmMessage(msgKey, new Object[0]);
    }

    /**
     * speak a confirmation message with an appropriate sound.
     * @param msgKey message key
     * @param params messages optional parameters
     * @return true if confirmed by the user, false otherwise
     */
    public boolean confirmMessage(String msgKey, Object[] params) {
        URL url = getClass().getResource("/org/fingon/question.wav");
        try {
            Player soundPlayer = PlayerFactory.getPlayerByExtension("wav");
            soundPlayer.play(url);
        } catch (PlayException e) {
            logger.error(e.getMessage(), e);
        }

        try {
	    SpeechSynthesizer synthe = PlayerFactory.getSpeechSynthesizer();
            ResourceBundle bundle = ResourceBundle.getBundle("label");
            String message = bundle.getString("javaspeaker.message." + msgKey);
            MessageFormat msgFormat = new MessageFormat(message);
            String formatedMsg = msgFormat.format(params);
            synthe.play(formatedMsg);
            return true;
        } catch (PlayException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * speak a info message with an appropriate sound. 
     * @param msgKey message key
     */
    public void raiseInfo(String msgKey) {
        URL url = getClass().getResource("/org/fingon/Balloon.wav");
        try {
            Player soundPlayer = PlayerFactory.getPlayerByExtension("wav");
            soundPlayer.play(url);
        } catch (PlayException e) {
            logger.error(e.getMessage(), e);
        }

        try {
	    SpeechSynthesizer synthe = PlayerFactory.getSpeechSynthesizer();
            ResourceBundle bundle = ResourceBundle.getBundle("label");
            String message = bundle.getString("javaspeaker.message." + msgKey);
            synthe.play(message);
        } catch (PlayException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * speak a warning message with an appropriate sound. 
     * @param msgKey message key
     */
    public void raiseWarning(String msgKey) {
        URL url = getClass().getResource("/org/fingon/Exclamation.wav");
        try {
            Player soundPlayer = PlayerFactory.getPlayerByExtension("wav");
            soundPlayer.play(url);
        } catch (PlayException e) {
            logger.error(e.getMessage(), e);
        }

        try {
	    SpeechSynthesizer synthe = PlayerFactory.getSpeechSynthesizer();
            ResourceBundle bundle = ResourceBundle.getBundle("label");
            String message = bundle.getString("javaspeaker.message." + msgKey);
            synthe.play(message);
        } catch (PlayException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * speak an error message with an appropriate sound. 
     * @param msgKey message key
     */
    public void raiseError(String msgKey) {
        URL urlError = getClass().getResource("/org/fingon/error.wav");
        try {
            Player soundPlayer = PlayerFactory.getPlayerByExtension("wav");
            soundPlayer.play(urlError);
        } catch (PlayException e) {
            logger.error(e.getMessage(), e);
        }
        
        try {
	    SpeechSynthesizer synthe = PlayerFactory.getSpeechSynthesizer();
            ResourceBundle bundle = ResourceBundle.getBundle("label");
            String message = bundle.getString("javaspeaker.message." + msgKey);
            synthe.play(message);
        } catch (PlayException e) {
            logger.error(e.getMessage(), e);
        }
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
            List<UserInterface> allUI = UserInterfaceFactory.getAllUserInterfaces();
            for (UserInterface anUI : allUI) {
        	anUI.raiseError("noplayer");
            }
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
