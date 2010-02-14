package org.tramper.recognizer;

import java.util.EventObject;
import java.util.List;
import java.util.Properties;

import javax.speech.EngineModeDesc;
import javax.speech.recognition.SpeakerProfile;

/**
 * recording event
 * @author Paul-Emile
 */
public class RecognitionEvent extends EventObject {
    /** RecognitionEvent.java long */
    private static final long serialVersionUID = 8658351408781779075L;
    /** engine properties */
    private Properties engineProp;
    /** engine  */
    private EngineModeDesc engine;
    /** speaker profiles list */
    private List<SpeakerProfile> speakerProfiles;
    /** engine state */
    private short engineState;
    /** ready state */
    public static short LISTENING = 1;
    /** ready state */
    public static short NOT_LISTENING = 2;

    /**
     * 
     */
    public RecognitionEvent(Object source) {
        super(source);
    }

    /**
     * @return engineProp.
     */
    public Properties getEngineProp() {
        return this.engineProp;
    }

    /**
     * @param engineProp engineProp 
     */
    public void setEngineProp(Properties engineProp) {
        this.engineProp = engineProp;
    }

    /**
     * @return speakerProfiles.
     */
    public List<SpeakerProfile> getSpeakerProfiles() {
        return this.speakerProfiles;
    }

    /**
     * @param speakerProfiles speakerProfiles 
     */
    public void setSpeakerProfiles(List<SpeakerProfile> speakerProfiles) {
        this.speakerProfiles = speakerProfiles;
    }

    /**
     * @return engine.
     */
    public EngineModeDesc getEngine() {
        return this.engine;
    }

    /**
     * @param engine  
     */
    public void setEngine(EngineModeDesc engine) {
        this.engine = engine;
    }

    /**
     * @return engineState.
     */
    public short getEngineState() {
        return this.engineState;
    }

    /**
     * @param engineState engineState 
     */
    public void setEngineState(short engineState) {
        this.engineState = engineState;
    }

}
