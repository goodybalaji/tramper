package org.tramper.player;

import java.io.File;
import java.util.List;

import org.tramper.doc.SimpleDocument;
import org.tramper.ui.Renderer;

/**
 * Player
 * @author Paul-Emile
 */
public interface Player extends Renderer {
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
     * pause the play
     */
    public void pause();
    /**
     * resume the play
     */
    public void resume();
    /**
     * stop the play
     */
    public void stop();
    /** 
     * read the next speakable 
     */
    public void next();
    /** 
     * read the previous speakable 
     */
    public void previous();
    /**
     * is the player running?
     * @return true if running, false otherwise
     */
    public boolean isRunning();
    /**
     * is the player paused?
     * @return true if paused, false otherwise
     */
    public boolean isPaused();
    /**
     * get the volume 
     * @return volume
     */
    public int getVolume() throws PlayException;

    /**
     * Set the volume 
     * @param volume
     */
    public void setVolume(int volume);

    /**
     * get the speed 
     * @return speed
     */
    public int getSpeed() throws PlayException;

    /**
     * Set the speed 
     * @param speed
     */
    public void setSpeed(int speed);

    /**
     * add a play listener
     * @param listener
     */
    public void addPlayListener(PlayListener listener);
    
    /**
     * remove a play listener
     * @param listener
     */
    public void removePlayListener(PlayListener listener);
    
    /**
     * 
     * @return
     */
    public List<String> getRenderings();
    
    /**
     * 
     * @param document
     * @return
     */
    public boolean isDocumentSupported(SimpleDocument document);
    
    /**
     * 
     * @param extension
     * @return
     */
    public boolean isExtensionSupported(String extension);
}
