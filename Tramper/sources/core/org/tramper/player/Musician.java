package org.tramper.player;

/**
 * A musician can be either the conductor, or a player (or instrumentalist).
 * He is a member of the orchestra which plays a document.
 * @author Paul-Emile
 */
public interface Musician {
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
}
