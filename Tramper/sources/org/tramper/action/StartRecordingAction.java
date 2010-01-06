package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;
import org.tramper.recognizer.RecognitionException;
import org.tramper.recognizer.SpeechRecognizer;
import org.tramper.recognizer.SpeechRecognizerFactory;

/**
 * @author Paul-Emile
 * 
 */
public class StartRecordingAction extends AbstractAction {
    /** logger */
    private Logger logger = Logger.getLogger(StartRecordingAction.class);
    /** StartRecordingAction.java long */
    private static final long serialVersionUID = 2682882825077492848L;
    /** singleton */
    private static StartRecordingAction instance;
    
    /**
     * Returns the singleton
     * @return
     */
    public static StartRecordingAction getInstance() {
	if (instance == null) {
	    instance = new StartRecordingAction();
	}
	return instance;
    }
    
    /**
     * 
     */
    private StartRecordingAction() {
	super();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            SpeechRecognizer speechRecognizer = SpeechRecognizerFactory.getSpeechRecognizer();
            speechRecognizer.record();
        } catch (RecognitionException re) {
            logger.error(re);
        }
    }

}
