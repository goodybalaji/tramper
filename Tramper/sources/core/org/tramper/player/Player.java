package org.tramper.player;

import java.io.File;
import java.net.URL;

/**
 * Player
 * @author Paul-Emile
 */
public interface Player extends Musician {
    /**
     * Redirect the output into a file
     * @param aFile file where to save document
     */
    public void setOutput(File aFile);
    /**
     * Redirect the output to the player
     */
    public void setOutput();
    /**
     * Play a media from an URL
     * @param anUrl
     * @exception PlayException 
     */
    public void play(URL anUrl) throws PlayException;
    /**
     * Play a media from an URL and wait for it's ending
     * @param anUrl
     * @exception PlayException 
     */
    public void playAndWait(URL anUrl) throws PlayException;
    /**
     * Play a media in a loop
     * @param aStream
     * @exception PlayException 
     */
    public void playLoop(URL anUrl) throws PlayException;
    
    /**
     * 
     * @param extension
     * @return
     */
    public boolean isExtensionSupported(String extension);
}
