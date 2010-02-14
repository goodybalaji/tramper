package org.tramper.doc;

import java.net.URL;
import java.util.List;
import java.util.Locale;

import org.tramper.synthesizer.VoiceDesc;

/**
 * A speakable item of a speakable document
 * @author Paul-Emile
 */
public abstract class SpeakableItem {
    /** volume */
    protected Float volume;
    /** balance */
    protected Float balance;
    /** speak */
    protected String speak;
    /** pause before */
    protected Float pauseBefore;
    /** pause after */
    protected Float pauseAfter;
    /** rest before */
    protected Float restBefore;
    /** rest after */
    protected Float restAfter;
    /** cue before */
    protected URL cueBefore;
    /** cue after */
    protected URL cueAfter;
    /** rate */
    protected Float rate;
    /** pitch */
    protected Float pitch;
    /** pitch range */
    protected Float pitchRange;
    /** voice family */
    protected List<VoiceDesc> voiceFamily;
    
    /**
     * Returns the text to speak in the given locale
     * @param locale 
     * @return a speakable text 
     */
    public abstract String getText(Locale locale);

    /**
     * @return volume.
     */
    public Float getVolume() {
        return this.volume;
    }

    /**
     * @param volume volume 
     */
    public void setVolume(Float volume) {
        this.volume = volume;
    }

    /**
     * @return balance.
     */
    public Float getBalance() {
        return this.balance;
    }

    /**
     * @param balance balance 
     */
    public void setBalance(Float balance) {
        this.balance = balance;
    }

    /**
     * @return speak.
     */
    public String getSpeak() {
        return this.speak;
    }

    /**
     * @param speak speak 
     */
    public void setSpeak(String speak) {
        this.speak = speak;
    }

    /**
     * @return cueAfter.
     */
    public URL getCueAfter() {
        return this.cueAfter;
    }

    /**
     * @param cueAfter cueAfter 
     */
    public void setCueAfter(URL cueAfter) {
        this.cueAfter = cueAfter;
    }

    /**
     * @return cueBefore.
     */
    public URL getCueBefore() {
        return this.cueBefore;
    }

    /**
     * @param cueBefore cueBefore 
     */
    public void setCueBefore(URL cueBefore) {
        this.cueBefore = cueBefore;
    }

    /**
     * @return pauseAfter.
     */
    public Float getPauseAfter() {
        return this.pauseAfter;
    }

    /**
     * @param pauseAfter pauseAfter 
     */
    public void setPauseAfter(Float pauseAfter) {
        this.pauseAfter = pauseAfter;
    }

    /**
     * @return pauseBefore.
     */
    public Float getPauseBefore() {
        return this.pauseBefore;
    }

    /**
     * @param pauseBefore pauseBefore 
     */
    public void setPauseBefore(Float pauseBefore) {
        this.pauseBefore = pauseBefore;
    }

    /**
     * @return restAfter.
     */
    public Float getRestAfter() {
        return this.restAfter;
    }

    /**
     * @param restAfter restAfter 
     */
    public void setRestAfter(Float restAfter) {
        this.restAfter = restAfter;
    }

    /**
     * @return restBefore.
     */
    public Float getRestBefore() {
        return this.restBefore;
    }

    /**
     * @param restBefore restBefore 
     */
    public void setRestBefore(Float restBefore) {
        this.restBefore = restBefore;
    }

    /**
     * @return pitch.
     */
    public Float getPitch() {
        return this.pitch;
    }

    /**
     * @param pitch pitch 
     */
    public void setPitch(Float pitch) {
        this.pitch = pitch;
    }

    /**
     * @return pitchRange.
     */
    public Float getPitchRange() {
        return this.pitchRange;
    }

    /**
     * @param pitchRange pitchRange 
     */
    public void setPitchRange(Float pitchRange) {
        this.pitchRange = pitchRange;
    }

    /**
     * @return rate.
     */
    public Float getRate() {
        return this.rate;
    }

    /**
     * @param rate rate 
     */
    public void setRate(Float rate) {
        this.rate = rate;
    }

    /**
     * @return voiceFamily.
     */
    public List<VoiceDesc> getVoiceFamily() {
        return this.voiceFamily;
    }

    /**
     * @param voiceFamily voiceFamily 
     */
    public void setVoiceFamily(List<VoiceDesc> voiceFamily) {
        this.voiceFamily = voiceFamily;
    }
    
}
