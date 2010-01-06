package org.tramper.player;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.tramper.synthesizer.SpeechSynthesizer;

/**
 * Instanciates the right player following mime type or extension file
 * @author Paul-Emile
 */
public class PlayerFactory {
    /** logger */
    private static Logger logger = Logger.getLogger(PlayerFactory.class);
    /** mimetype/classname pairs */
    private static ResourceBundle playerByMimeType = ResourceBundle.getBundle("org.tramper.player.playerByMimeType");
    /** extension/classname pairs */
    private static ResourceBundle playerByExtension = ResourceBundle.getBundle("org.tramper.player.playerByExtension");
    /** A speech synthesizer for quick messages, notifications. Don't use it as a document player.
     * You can't manage it with the speech control panel. */ 
    private static SpeechSynthesizer speechSynthesizer;
    
    static {
        try {
	    speechSynthesizer = (SpeechSynthesizer)getPlayerByMimeType("text/html");
	} catch (PlayException e) {
	    logger.error("can't instantiate the speech synthesizer", e);
	}
    }
    
    /**
     * 
     */
    public PlayerFactory() {
        super();
    }

    /**
     * Returns the current speech synthesizer
     * @return
     */
    public static SpeechSynthesizer getSpeechSynthesizer() throws PlayException {
        return speechSynthesizer;
    }
    
    /**
     * return the right player following the given mime type
     * @param mimeType media mime type
     * @return a player
     * @throws Exception
     */
    public static Player getPlayerByMimeType(String mimeType) throws PlayException {
        //get the class name corresponding to the mime type
        String className = null;
        try {
            className = playerByMimeType.getString(mimeType);
        }
        catch (MissingResourceException e) {
            logger.error("Unknown mime type : "+mimeType);
            throw new PlayException("Unknown mime type : "+mimeType);
        }
        
        Player mediaPlayer = getPlayerByClassName(className);
        
        return mediaPlayer;
    }
    
    /**
     * return the right player following the given extension
     * @param extension file's extension
     * @return the right player
     * @throws PlayException
     */
    public static Player getPlayerByExtension(String extension) throws PlayException {
        //get the class name corresponding to the file name
        String className = null;
        try {
            className = playerByExtension.getString(extension);
        }
        catch (MissingResourceException e) {
            logger.error("Unknown extension : "+extension);
            throw new PlayException("Unknown extension : "+extension);
        }

        Player mediaPlayer = getPlayerByClassName(className);
        
        return mediaPlayer;
    }
    
    /**
     * 
     * @param className name of the player's class
     * @return a new player
     * @throws PlayException
     */
    protected static Player getPlayerByClassName(String className) throws PlayException {
        //get a class loader and load the class
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Class<?> desiredClass = null;
        try {
            desiredClass = classLoader.loadClass(className);
        }
        catch (ClassNotFoundException e) {
            logger.error("Unknown class : "+className);
            throw new PlayException("Unknown class : "+className);
        }
        
        //instanciate the class
        Player mediaPlayer = null;
        try {
            mediaPlayer = (Player)desiredClass.newInstance();
        }
        catch (InstantiationException e) {
            logger.error("Instantiation fail : "+className);
            throw new PlayException("Instantiation class fail : "+className);
        }
        catch (IllegalAccessException e) {
            logger.error("Instantiation fail : "+className);
            throw new PlayException("Instantiation class fail : "+className);
        }
        return mediaPlayer;
    }
}
