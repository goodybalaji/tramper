package org.tramper.recognizer;

import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.sound.sampled.Mixer;
import javax.speech.EngineModeDesc;
import javax.speech.recognition.SpeakerProfile;

/**
 * Listen to the user having a microphone and recognize the speech
 * @author Paul-Emile
 */
public interface SpeechRecognizer {
    /** 
     * list the available engines (languages)
     */
    public List<EngineModeDesc> listAvailableEngines();
    /** 
     * list the available speaker profiles for the current engine
     */
    public List<SpeakerProfile> listAvailableSpeakerProfiles();
    /**
     * 
     * @return
     */
    public List<Mixer.Info> listAvailableMicrophones();
    /**
     * 
     * @param mixer
     */
    public void setMicrophone(Mixer.Info mixer);
    /** 
     * return the engine local 
     */
    public Locale getLocale();
    /**
     * return the current engine descriptor
     * @return
     */
    public EngineModeDesc getEngineModeDesc();
    /**
     * 
     * @param engineMode 
     */
    public void setEngineModeDesc(EngineModeDesc engineMode);
    /**
     * 
     * @param speakerProfile 
     */
    public void setSpeakerProfile(SpeakerProfile speakerProfile);
    /**
     * compare the language of the engine with the language in parameter
     * @param locale
     * @return true if equal, false otherwise
     */
    public boolean matchEngineLanguage(Locale locale);
    
    /** 
     * return the current engine properties 
     */
    public Properties getEngineProperties();
    
    /**
     * 
     */
    public void setEngineProperties(Properties prop);
    
    /** 
     * Record 
     */
    public void record();
    
    /** 
     * Stop the current recording
     */
    public void stop();
    
    /** 
     * Pause the current recording 
     */
    public void pause();
    
    /** 
     * resume the paused recording 
     */
    public void resume();
    
    /**
     * 
     * @return true if the recorder has the gained the focus of the microphone, false otherwise
     */
    public boolean isRecording();

    /**
     * add a recording listener
     * @param aListener
     */
    public void addRecordingListener(RecognitionListener aListener);
    
    /**
     * remove a recording listener from the list
     * @param aListener
     */
    public void removeRecordingListener(RecognitionListener aListener);
}
