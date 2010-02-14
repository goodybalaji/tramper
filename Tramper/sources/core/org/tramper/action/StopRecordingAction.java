package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.tramper.recognizer.RecognitionException;
import org.tramper.recognizer.SpeechRecognizer;
import org.tramper.recognizer.SpeechRecognizerFactory;

/**
 * @author Paul-Emile
 * 
 */
public class StopRecordingAction extends AbstractAction {
    /** StopRecordingAction.java long */
    private static final long serialVersionUID = 6064225521302749691L;
    /** singleton */
    private static StopRecordingAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static StopRecordingAction getInstance() {
	if (instance == null) {
	    instance = new StopRecordingAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private StopRecordingAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            SpeechRecognizer speechRecognizer = SpeechRecognizerFactory.getSpeechRecognizer();
            speechRecognizer.stop();
        } catch (RecognitionException re) {
        }
    }

}
