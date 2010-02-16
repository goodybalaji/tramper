package org.tramper.recognizer;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

/**
 * A speechRecognizer factory
 * @author Paul-Emile
 */
public class SpeechRecognizerFactory {
    /** logger */
    private static Logger logger = Logger.getLogger(SpeechRecognizerFactory.class);
    /** service provider loader for speech recognizer interface */
    private static ServiceLoader<SpeechRecognizer> recognizerLoader;
    /** lock preventing several threads to use the service loader concurrently */
    private static Lock lock;
    
    static {
	recognizerLoader = ServiceLoader.load(SpeechRecognizer.class);
	lock = new ReentrantLock();
    }

    /**
     * Instantiates and returns the first available speech recognizer.
     * @return a speech recognizer
     * @throws RecognitionException 
     */
    public static SpeechRecognizer getSpeechRecognizer() throws RecognitionException {
	lock.lock();
	Iterator<SpeechRecognizer> recognizerIterator = recognizerLoader.iterator();
	while (recognizerIterator.hasNext()) {
	    try {
		SpeechRecognizer aRecognizer = recognizerIterator.next();
		lock.unlock();
		return aRecognizer;
	    } catch (ServiceConfigurationError e) {
		logger.error("Error when loading a service provider", e);
	    }
	}
	lock.unlock();
        logger.error("Unable to load a speech recognizer");
        throw new RecognitionException("Unable to load a speech recognizer");
    }
}
