package org.tramper.synthesizer;

import java.util.EventListener;


/**
 * Speech listener
 * @author Paul-Emile
 */
public interface SynthesisListener extends EventListener {
    /**
     * fired when the pitch has changed
     * @param event
     */
    public void pitchChanged(SynthesisEvent event);
    /**
     * fired when the pitch range has changed
     * @param event
     */
    public void pitchRangeChanged(SynthesisEvent event);
    
    /**
     * fired when the voices have changed
     * @param event
     */
    public void voicesListChanged(SynthesisEvent event);

    /**
     * fired when the current engine has changed
     * @param event
     */
    public void engineChanged(SynthesisEvent event);
    
    /**
     * fired when the current voice has changed
     * @param event
     */
    public void voiceChanged(SynthesisEvent event);
}
