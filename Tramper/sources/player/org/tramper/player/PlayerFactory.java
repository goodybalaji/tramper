package org.tramper.player;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.tramper.doc.SimpleDocument;
import org.tramper.synthesizer.JSAPISpeechSynthesizer;
import org.tramper.synthesizer.SpeechSynthesizer;
import org.tramper.synthesizer.SynthesisException;

/**
 * Returns the right player following document or extension file
 * @author Paul-Emile
 */
public class PlayerFactory {
    /** logger */
    private static Logger logger = Logger.getLogger(PlayerFactory.class);
    /** service provider loader for player interface */
    private static ServiceLoader<Player> playerLoader;
    /** A speech synthesizer for quick messages, notifications. Don't use it as a document player.
     * You can't manage it with the speech control panel. */ 
    private static SpeechSynthesizer speechSynthesizer;
    /** lock preventing several threads to use the service loader concurrently */
    private static Lock lock;
    
    static {
	playerLoader = ServiceLoader.load(Player.class);
	lock = new ReentrantLock();
        try {
	    speechSynthesizer = new JSAPISpeechSynthesizer();
	} catch (SynthesisException e) {
	    logger.error("can't instantiate the speech synthesizer", e);
	}
    }
    
    /**
     * Returns the current speech synthesizer
     * @return
     */
    public static SpeechSynthesizer getSpeechSynthesizer() throws PlayException {
        return speechSynthesizer;
    }
    
    /**
     * return the right player following the given document
     * @param document document
     * @return a player
     * @throws PlayException
     */
    public static Player getPlayerByDocument(SimpleDocument document) throws PlayException {
	lock.lock();
	playerLoader.reload();
	Iterator<Player> playerIterator = playerLoader.iterator();
	while (playerIterator.hasNext()) {
	    try {
		Player aPlayer = playerIterator.next();
        	if (aPlayer.isDocumentSupported(document)) {
        	    lock.unlock();
        	    return aPlayer;
        	}
	    } catch (ServiceConfigurationError e) {
		logger.error("Error when loading a service provider", e);
	    }
	}
        lock.unlock();
        logger.error("Unknown document: "+document);
        throw new PlayException("Unknown document: "+document);
    }
    
    /**
     * return the right player following the given extension
     * @param extension file's extension
     * @return the right player
     * @throws PlayException
     */
    public static Player getPlayerByExtension(String extension) throws PlayException {
	lock.lock();
	playerLoader.reload();
	Iterator<Player> playerIterator = playerLoader.iterator();
	while (playerIterator.hasNext()) {
	    try {
		Player aPlayer = playerIterator.next();
        	if (aPlayer.isExtensionSupported(extension)) {
        	    lock.unlock();
        	    return aPlayer;
        	}
	    } catch (ServiceConfigurationError e) {
		logger.error("Error when loading a service provider", e);
	    }
	}
        lock.unlock();
        logger.error("Unknown extension: "+extension);
        throw new PlayException("Unknown extension: "+extension);
    }
}
