package org.tramper.synthesizer;

import java.beans.PropertyVetoException;
import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.speech.synthesis.SynthesizerProperties;
import javax.speech.synthesis.Voice;

import org.apache.log4j.Logger;
import org.fingon.player.PlayEvent;
import org.fingon.player.PlayException;
import org.fingon.synthesizer.JSAPISpeechSynthesizer;
import org.fingon.synthesizer.SynthesisException;
import org.fingon.player.SoundPlayer;

import com.sun.speech.freetts.audio.JavaStreamingAudioPlayer;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;
import com.sun.speech.freetts.jsapi.FreeTTSVoice;

/**
 * A speech synthesizer implemented with the Sun's synthesizer FreeTTS.
 * @author Paul-Emile
 */
public class FreeTTSSpeechSynthesizer extends JSAPISpeechSynthesizer {
    /** logger */
    private Logger logger = Logger.getLogger(FreeTTSSpeechSynthesizer.class);

    /**
     * 
     * @throws SynthesisException
     */
    public FreeTTSSpeechSynthesizer() throws SynthesisException {
	super();
	// don't use the Mbrola voices for 2 reasons :
	// - no output audio data exception thrown often when speaking
	// - need a system dependant binary
	// Properties prop = System.getProperties();
	// prop.setProperty("mbrola.base", "c:\\Program Files\\mbrola");
    }

    /**
     * The volume's useful values in FreeTTS synthesizer are between 0.5 and 1.0 
     * @see org.tramper.synthesizer.JSAPISpeechSynthesizer#getVolume()
     */
    public int getVolume() throws PlayException {
	SynthesizerProperties prop = (SynthesizerProperties) synthe.getEngineProperties();
	return (int) ((prop.getVolume()-0.5)*200);
    }

    /**
     * The volume's useful values in FreeTTS synthesizer are between 0.5 and 1.0 
     * @see org.tramper.synthesizer.JSAPISpeechSynthesizer#setVolume(int)
     */
    public void setVolume(int volume) {
	SynthesizerProperties prop = (SynthesizerProperties) synthe.getEngineProperties();
	try {
	    prop.setVolume((float)((float)volume / 200 + 0.5));
	    
	    PlayEvent event = new PlayEvent(this);
	    event.setNewValue(volume);
	    fireVolumeChangedEvent(event);
	} catch (PropertyVetoException pe) {
	    logger.error("Bad volume value : " + pe.getMessage());
	}
    }

    /**
     * Set the engine output to a file
     * @param aFile
     */
    public void setOutput(File aFile) {
	SingleFileAudioPlayer singleFile = null;
	String filename = aFile.getAbsolutePath();
	String basename = filename.substring(0, filename.lastIndexOf("."));
	try {
	    singleFile = new SingleFileAudioPlayer(basename, SoundPlayer.getAudioTypeFromFileName(filename));
	} catch (PlayException e) {
	    logger.warn(e);
	    // default output audio format : wav
	    singleFile = new SingleFileAudioPlayer(basename, AudioFileFormat.Type.WAVE);
	}
	SynthesizerProperties syntheProp = synthe.getSynthesizerProperties();
	Voice currentVoice = syntheProp.getVoice();
	com.sun.speech.freetts.Voice freettsVoice = ((FreeTTSVoice) currentVoice).getVoice();
	freettsVoice.setAudioPlayer(singleFile);
    }

    /**
     * 
     */
    public void setOutput() {
	SynthesizerProperties syntheProp = synthe.getSynthesizerProperties();
	Voice currentVoice = syntheProp.getVoice();
	com.sun.speech.freetts.Voice freettsVoice = ((FreeTTSVoice) currentVoice).getVoice();
	SingleFileAudioPlayer singleFile = (SingleFileAudioPlayer) freettsVoice.getAudioPlayer();
	singleFile.close();
	JavaStreamingAudioPlayer streamAudio = new JavaStreamingAudioPlayer();
	freettsVoice.setAudioPlayer(streamAudio);
    }

}
