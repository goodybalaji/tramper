package org.tramper.doc;

import java.util.Locale;

import com.sun.media.jmc.MediaProvider;

/**
 * 
 * @author Paul-Emile
 */
public class VideoItem extends DocumentItem {

    private MediaProvider video;
    
    /**
     * @see org.tramper.doc.SpeakableItem#getText(java.util.Locale)
     */
    @Override
    public String getText(Locale locale) {
	return "";
    }

    /**
     * @return video.
     */
    public MediaProvider getVideo() {
        return this.video;
    }

    /**
     * @param video video 
     */
    public void setVideo(MediaProvider video) {
        this.video = video;
    }

}
