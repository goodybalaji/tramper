package org.tramper.recognizer;

import javax.speech.Central;
import javax.speech.EngineList;

/**
 * A speechRecognizer factory
 * @author Paul-Emile
 */
public class SpeechRecognizerFactory {
    /** current speechRecognizer */
    private static SpeechRecognizer speechRecognizer;
    
    /**
     * 
     */
    private SpeechRecognizerFactory() {
        super();
    }
    
    /**
     * instanciate and return the available speechRecognizer
     * @return
     * @throws RecognitionException 
     */
    public static SpeechRecognizer getSpeechRecognizer() throws RecognitionException {
        if (speechRecognizer == null) {
            EngineList list = Central.availableRecognizers(null);
            if (list != null && list.size() > 0) {
                speechRecognizer = new JSAPISpeechRecognizer();
            } else {
                speechRecognizer = new SphinxSpeechRecognizer();
            }
        }
        return speechRecognizer;
    }

}
