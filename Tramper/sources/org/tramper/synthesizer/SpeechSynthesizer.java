package org.tramper.synthesizer;

import java.util.List;
import java.util.Locale;

import javax.speech.EngineModeDesc;
import javax.speech.synthesis.SynthesizerModeDesc;

import org.tramper.player.Player;


/**
 * A speaker integrating a speech synthesizer
 * @author Paul-Emile
 */
public interface SpeechSynthesizer extends Player {
    /** 
     * Speak some text
     * @param text 
     */
    public void play(String text);
    /**
     * load engine and default voice
     */
    public void load() throws SynthesisException;
    /**
     * load the first engine and voice with this locale
     * @param locale 
     */
    public boolean load(Locale locale) throws SynthesisException;
    /**
     * 
     * @param name
     * @param mode
     * @param locale
     * @throws SynthesisException
     */
    public void loadEngine(String name, String mode, Locale locale) throws SynthesisException;
    /**
     * 
     * @param selectedVoice
     * @return
     */
    public boolean loadVoice(VoiceDesc selectedVoice);
    
    /** 
     * list the available engines (languages)
     */
    public List<SynthesizerModeDesc> listAvailableEngines();
    /** 
     * list the available voices 
     */
    public List<VoiceDesc> listAvailableVoices();
    /** 
     * return the engine local 
     */
    public Locale getEngineLocale();
    /**
     * return the current engine descriptor
     * @return
     */
    public EngineModeDesc getEngineModeDesc();
    /**
     * Returns the current voice description
     * @return
     */
    public VoiceDesc getVoiceDesc();
    /**
     * add a speech listener
     * @param aListener
     */
    public void addSpeechListener(SynthesisListener aListener);
    /**
     * remove a speech listener from the list
     * @param aListener
     */
    public void removeSpeechListener(SynthesisListener aListener);
    /**
     * 
     * @return
     */
    public boolean isStepByStep();
    /**
     * 
     * @param stepByStep
     */
    public void setStepByStep(boolean stepByStep);
    /**
     * 
     * @return
     */
    public float getPitch();
    /**
     * 
     * @param pitch
     */
    public void setPitch(float pitch);
    /**
     * 
     * @return
     */
    public float getPitchRange();
    /**
     * 
     * @param pitchRange
     */
    public void setPitchRange(float pitchRange);
}
