package org.tramper.recognizer;

import java.util.EventListener;

/**
 * 
 * @author Paul-Emile
 */
public interface RecognitionListener extends EventListener {
    /**
     * fired when the current engine or speaker profile has changed
     * @param event
     */
    public void enginePropertiesChanged(RecognitionEvent event);
    
    /**
     * fired when the speaker profiles have changed
     * @param event
     */
    public void speakerProfilesListChanged(RecognitionEvent event);

    /**
     * fired when the current engine has changed
     * @param event
     */
    public void engineChanged(RecognitionEvent event);
    
    /**
     * 
     * @param event
     */
    public void engineStateChanged(RecognitionEvent event);
}
