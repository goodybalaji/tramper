package org.tramper.doc;

import java.awt.Dimension;

import com.sun.media.jmc.MediaProvider;

/**
 * 
 * @author Paul-Emile
 */
public class Video extends SimpleDocument {
    /** duration in seconds */
    private double duration;
    /** Frame size in pixels */
    private Dimension frameSize;
    /** media provider */
    private MediaProvider mediaProvider;
    
    /**
     * 
     */
    public Video() {
	super();
    }

    /**
     * @return mediaProvider.
     */
    public MediaProvider getMediaProvider() {
        return this.mediaProvider;
    }

    /**
     * @param mediaProvider mediaProvider 
     */
    public void setMediaProvider(MediaProvider mediaProvider) {
        this.mediaProvider = mediaProvider;
    }

    /**
     * @return duration.
     */
    public double getDuration() {
        return this.duration;
    }

    /**
     * @param duration duration 
     */
    public void setDuration(double duration) {
        this.duration = duration;
    }

    /**
     * @return frameSize.
     */
    public Dimension getFrameSize() {
        return this.frameSize;
    }

    /**
     * @param frameSize frameSize 
     */
    public void setFrameSize(Dimension frameSize) {
        this.frameSize = frameSize;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "video";
    }
}
