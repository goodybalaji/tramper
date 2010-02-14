package org.tramper.player;

import java.util.EventListener;



/**
 * Reading Listener
 * @author Paul-Emile
 */
public interface PlayListener extends EventListener {
    /**
     * when the reading is started
     * @param event
     */
    public void readingStarted(PlayEvent event);
    /**
     * when the reading is paused
     * @param event
     */
    public void readingPaused(PlayEvent event);
    /**
     * when the reading is resumed
     * @param event
     */
    public void readingResumed(PlayEvent event);
    /**
     * when the reading is stopped
     * @param event
     */
    public void readingStopped(PlayEvent event);
    /**
     * when the reading ends
     * @param event
     */
    public void readingEnded(PlayEvent event);
    /**
     * when the next speakable is read
     * @param event
     */
    public void nextRead(PlayEvent event);
    /**
     * when the previous speakable is read
     * @param event
     */
    public void previousRead(PlayEvent event);
    /**
     * Called when volume changed
     * @param event
     */
    public void volumeChanged(PlayEvent event);
    /**
     * Called when sample rate changed
     * @param event
     */
    public void sampleRateChanged(PlayEvent event);
}
