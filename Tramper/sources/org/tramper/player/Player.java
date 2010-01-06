package org.tramper.player;

import java.io.File;
import java.util.List;

import org.tramper.ui.Renderer;

/**
 * Player of speakables
 * @author Paul-Emile
 */
public interface Player extends Renderer {
    /**
     * Redirect the output into a file
     * @param aFile file where save document
     */
    public void setOutput(File aFile);
    /**
     * Redirect the output to the speakers
     */
    public void setOutput();
    /**
     * pause the reading
     */
    public void pause();
    /**
     * resume the reading
     */
    public void resume();
    /**
     * stop the reading and deblock the next one
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
     * is the reader reading ?
     * @return true if running, false otherwise
     */
    public boolean isRunning();
    /**
     * is the reader paused ?
     * @return true if paused, false otherwise
     */
    public boolean isPaused();
    /**
     * get the volume of the current line
     * @return volume
     */
    public int getVolume() throws PlayException;

    /**
     * Set the volume of the current line
     * @param volume
     */
    public void setVolume(int volume);

    /**
     * get the speed of the current line
     * @return speed
     */
    public int getSpeed() throws PlayException;

    /**
     * Set the speed of the current line
     * @param speed
     */
    public void setSpeed(int speed);

    /**
     * add a listener
     * @param listener
     */
    public void addPlayListener(PlayListener listener);
    
    /**
     * remove a listener
     * @param listener
     */
    public void removePlayListener(PlayListener listener);
    
    /**
     * 
     * @return
     */
    public List<String> getRenderings();
}
