package org.tramper.player;

import java.net.URL;

/**
 * A media player
 * @author Paul-Emile
 */
public interface MediaPlayer extends Player {
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
     * Play a sound from a clip in loop
     * @param aStream
     * @exception PlayException 
     */
    public void playLoop(URL anUrl) throws PlayException;

}
