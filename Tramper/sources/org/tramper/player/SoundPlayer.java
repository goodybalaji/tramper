package org.tramper.player;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.Mixer.Info;

import javazoom.spi.mpeg.sampled.file.MpegFileFormatType;
//import javazoom.spi.vorbis.sampled.file.VorbisFileFormatType;

import org.apache.log4j.Logger;
import org.tramper.doc.DocumentEvent;
import org.tramper.doc.DocumentListener;
import org.tramper.doc.Sound;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.ui.Renderer;
import org.tramper.ui.RenderingException;


/**
 * Music player (au, wav, aif, mp3 and ogg files) using java sound API
 * and third party libraries from JLayer and JCraft.
 * One instance for each play.
 * @author Paul-Emile
 */
public class SoundPlayer implements MediaPlayer, Runnable, DocumentListener {
    /** logger */
    private Logger logger = Logger.getLogger(SoundPlayer.class);
    /** line used for the current play */
    private DataLine currentLine;
    /** buffered decoded audio stream */
    private BufferedInputStream bufferedDecodedStream;
    /** decoded audio format */
    private AudioFormat decodedFormat;
    /** flag to stop the current play */
    private boolean stopped = true;
    /** flag to pause the current play */
    private boolean paused = false;
    /** flag to play in loop */
    private boolean loop = false;
    /** play listeners list */
    private List<PlayListener> listener;
    /** decoded audio data buffer size */
    private static final int BUFFER_SIZE = 2048;
    /** step for next and previous actions in decoded bytes */
    private static final int STEP = 1000000;
    /** document currently played */
    private Sound document;
    /** target */
    private Target target;
    
    /**
     * Instanciate the first available mixer
     */
    public SoundPlayer() throws PlayException {
        super();
        listener = new ArrayList<PlayListener>();
        
        Info[] mixerInfo = AudioSystem.getMixerInfo();
        if (mixerInfo.length == 0) {
            logger.error("no available mixer");
            throw new PlayException("no available mixer");
        }
        
        Mixer defaultMixer = AudioSystem.getMixer(null);
        Mixer.Info defaultMixerInfo = defaultMixer.getMixerInfo();
        logger.debug("Default mixer: "+defaultMixerInfo.getName());
    }

    /**
     * 
     * @see org.tramper.ui.Renderer#render(int)
     */
    public void render(int documentPart) throws RenderingException {
	render(document, target, documentPart);
    }
    
    /**
     * 
     * @see org.tramper.ui.Renderer#render(org.tramper.doc.SimpleDocument)
     */
    public void render(SimpleDocument document, Target target) throws RenderingException {
	render(document, target, Renderer.ALL_PART);
    }
    
    /**
     * Play a document
     * @param document
     * @param documentPart
     * @throws PlayException
     */
    public void render(SimpleDocument doc, Target target, int documentPart) throws RenderingException {
	this.target = target;
	
	if (documentPart != Renderer.ALL_PART) {
	    return;
	}
	
        if (!(doc instanceof Sound)) {
            throw new RenderingException("wrong document class");
        }
        if (document != null) {
            document.removeDocumentListener(this);
        }
        document = (Sound)doc;
        document.addDocumentListener(this);
        
        URL url = document.getUrl();
        try {
	    play(url);
	} catch (PlayException e) {
	    throw new RenderingException(e);
	}
    }

    /**
     * Play a sound from an URL
     * @see org.tramper.player.MediaPlayer#play(java.net.URL)
     */
    public void play(URL audioUrl) throws PlayException {
        loop = false;
	AudioInputStream encodedStream = null;
        try {
            encodedStream = AudioSystem.getAudioInputStream(audioUrl);
        }
        catch (UnsupportedAudioFileException e) {
            logger.error("Audio url format unsupported", e);
            throw new PlayException();
        }
        catch (IOException e) {
            logger.error("url "+audioUrl+" unavailable", e);
            throw new PlayException();
        }
        AudioFormat encodedFormat = encodedStream.getFormat();
        decodedFormat = decodeFormat(encodedFormat);
        try {
            AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, encodedStream);
            bufferedDecodedStream = new BufferedInputStream(decodedStream);
        } catch (IllegalArgumentException e) {
            logger.error("audio conversion not supported.");
            logger.info("supported conversions: ");
            for (AudioFormat.Encoding encoding : AudioSystem.getTargetEncodings(encodedFormat)) {
        	logger.info("target encoding="+encoding);
            }
            throw new PlayException("audio conversion not supported.");
        }
        
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Play a sound from an URL and wait for the end
     * @see org.tramper.player.MediaPlayer#playAndWait(java.net.URL)
     */
    public void playAndWait(URL anUrl) throws PlayException {
        loop = false;

	AudioInputStream encodedStream = null;
        try {
            encodedStream = AudioSystem.getAudioInputStream(anUrl);
        }
        catch (UnsupportedAudioFileException e) {
            logger.error("Audio url format unsupported", e);
            throw new PlayException();
        }
        catch (IOException e) {
            logger.error("url "+anUrl+" unavailable", e);
            throw new PlayException();
        }

        AudioFormat encodedFormat = encodedStream.getFormat();
        decodedFormat = decodeFormat(encodedFormat);
        AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, encodedStream);
        bufferedDecodedStream = new BufferedInputStream(decodedStream);
        
        Thread thread = new Thread(this);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            logger.warn("play interrupted");
            //thread interrupted, lets continue what we did before
        }
    }

    /**
     * Play a sound from a clip in loop
     * @param audioUrl
     * @exception PlayException 
     */
    public void playLoop(URL audioUrl) throws PlayException {
	loop = true;
	AudioInputStream encodedStream = null;
        try {
            encodedStream = AudioSystem.getAudioInputStream(audioUrl);
        }
        catch (UnsupportedAudioFileException e) {
            logger.error("Audio url format unsupported", e);
            throw new PlayException();
        }
        catch (IOException e) {
            logger.error("url "+audioUrl+" unavailable", e);
            throw new PlayException();
        }
        AudioFormat encodedFormat = encodedStream.getFormat();
        decodedFormat = decodeFormat(encodedFormat);
        AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, encodedStream);
        bufferedDecodedStream = new BufferedInputStream(decodedStream);
        
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Play a sound from a buffered audio stream
     * @param audioStream
     */
    public void run() {
        stopped = false;
        DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, decodedFormat);
        boolean supportedLine = AudioSystem.isLineSupported(lineInfo);
        if (!supportedLine) {
            logger.error("line unsupported");
            return;
        }
        
        try {
            currentLine = (SourceDataLine)AudioSystem.getLine(lineInfo);
            ((SourceDataLine)currentLine).open(decodedFormat);
        }
        catch (LineUnavailableException e) {
            logger.error("line unavailable", e);
            return;
        }
        
        try {
            int volume = this.getVolume();
            PlayEvent volumeEvent = new PlayEvent(this);
            volumeEvent.setNewValue(volume);
            fireVolumeChangedEvent(volumeEvent);
        }
        catch (PlayException e) {
            logger.info("can't fire volume control changed");
        }
        
        try {
            int sampleRate = this.getSpeed();
            PlayEvent sampleRateEvent = new PlayEvent(this);
            sampleRateEvent.setNewValue(sampleRate);
            fireSampleRateChangedEvent(sampleRateEvent);
        }
        catch (PlayException e1) {
            logger.info("can't fire sample rate control changed");
        }

	PlayEvent event = new PlayEvent(this);
	this.fireReadingStartedEvent(event);
	
        //start playing the sound
        currentLine.start();
        
        byte[] data = new byte[BUFFER_SIZE];
        
        if (bufferedDecodedStream.markSupported()) {
            bufferedDecodedStream.mark(8000000);// minimum to loop the waiting music
        } else {
	    logger.error("mark unsupported for this audio stream");
        }
        
        do {
            int nBytesRead = 0;
            int nBytesWritten = 0;
            try {
                while (nBytesRead != -1) {
                    if (stopped) {
                        break;
                    }
                    synchronized (listener) {
                        if (paused) {
                            try {
                        	listener.wait();
                            } catch (InterruptedException e) {
                                logger.info("waiting play interrupted", e);
                            }
                        }
                    }
                    nBytesRead = bufferedDecodedStream.read(data, 0, data.length);
                    if (nBytesRead != -1) {
                        nBytesWritten = ((SourceDataLine)currentLine).write(data, 0, nBytesRead);
                        if (nBytesWritten != nBytesRead) {
                            logger.warn(nBytesRead+" bytes read, "+nBytesWritten+" bytes written");
                        }
                    }
                }
                currentLine.drain();
            } catch (IOException e) {
                logger.error("error when reading audio stream", e);
            }
            
            try {
		bufferedDecodedStream.reset();
	    } catch (IOException e) {}
	    
        } while (loop && !stopped);
        
        currentLine.flush();
        currentLine.close();
        try {
	    bufferedDecodedStream.close();
	} catch (IOException e) {}
        stopped = true;
	fireReadingEndedEvent(event);
    }

    /**
     * 
     * @see org.tramper.player.Player#setOutput(java.io.File)
     */
    public void setOutput(File aFile) {
    }

    /**
     * 
     * @see org.tramper.player.Player#setOutput()
     */
    public void setOutput() {
    }
    
    /**
     * add a play listener
     * @param aListener
     */
    public void addPlayListener(PlayListener aListener) {
        if (this.listener.contains(aListener) == false) {
            this.listener.add(aListener);
        }
    }
    
    /**
     * remove a play listener from the list
     * @param aListener
     */
    public void removePlayListener(PlayListener aListener) {
        this.listener.remove(aListener);
    }
    
    /**
     * fire a sample rate changed event
     * @param progressEvent
     */
    private void fireSampleRateChangedEvent(PlayEvent sampleRateEvent) {
        for (int i=0; i<listener.size(); i++) {
            PlayListener aListener = listener.get(i);
            aListener.sampleRateChanged(sampleRateEvent);
        }
    }

    /**
     * fire a volume changed event
     * @param progressEvent
     */
    private void fireVolumeChangedEvent(PlayEvent volumeEvent) {
        for (int i=0; i<listener.size(); i++) {
            PlayListener aListener = listener.get(i);
            aListener.volumeChanged(volumeEvent);
        }
    }

    /**
     * Pause the current play
     */
    public void pause() {
        if (currentLine != null) {
            if (currentLine.isOpen()) {
                if (currentLine.isRunning()) {
                    synchronized (listener) {
                        paused = true;
                        currentLine.drain();
                        currentLine.stop();
                    }
                    PlayEvent event = new PlayEvent(this);
                    fireReadingPausedEvent(event);
                }
            }
        }
    }
    
    /**
     * resume the current play
     */
    public void resume() {
        if (currentLine != null) {
            if (currentLine.isOpen()) {
                if (!currentLine.isRunning()) {
                    synchronized (listener) {
                        paused = false;
                        currentLine.start();
                        listener.notify();
                    }
                    PlayEvent event = new PlayEvent(this);
                    fireReadingResumedEvent(event);
                }
            }
        }
    }

    /**
     * Stop the current play
     */
    public void stop() {
        stopped = true;
        if (currentLine != null) {
            if (currentLine.isOpen()) {
                if (currentLine.isRunning()) {
                    currentLine.stop();
                }
                currentLine.flush();
                currentLine.close();
        	PlayEvent event = new PlayEvent(this);
        	fireReadingStoppedEvent(event);
            }
        }
    }

    /**
     * 
     * @see org.tramper.player.Player#next()
     */
    public void next() {
        if (currentLine != null) {
            try {
		bufferedDecodedStream.skip(STEP);
	    } catch (IOException e) {}
            PlayEvent e = new PlayEvent(this);
            e.setNewValue(currentLine.getMicrosecondPosition());
            fireNextReadEvent(e);
        }
    }

    /**
     * 
     * @see org.tramper.player.Player#previous()
     */
    public void previous() {
        if (currentLine != null) {
            try {
		bufferedDecodedStream.reset();
	    } catch (IOException e) {}
            PlayEvent e = new PlayEvent(this);
            e.setNewValue(currentLine.getMicrosecondPosition());
            firePreviousReadEvent(e);
        }
    }

    /**
     * is the player running ?
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return !stopped;
    }

    /**
     * 
     * @see org.tramper.player.Player#isPaused()
     */
    public boolean isPaused() {
        synchronized (listener) {
            return paused;
        }
    }

    /**
     * get the volume of the current line
     * @return volume
     */
    public int getVolume() throws PlayException {
        try {
            FloatControl volumeControl = (FloatControl)currentLine.getControl(FloatControl.Type.MASTER_GAIN);
            return (int)(volumeControl.getValue()*4 + 100);
        }
        catch (IllegalArgumentException e) {
            logger.warn("Master gain control unsupported");
            try {
                BooleanControl muteControl = (BooleanControl)currentLine.getControl(BooleanControl.Type.MUTE);
                boolean mute = muteControl.getValue();
                int muteValue = mute ? 0 : 100;
                return muteValue;
            }
            catch (IllegalArgumentException e1) {
                logger.warn("mute control unsupported");
                throw new PlayException();
            }
        }
        catch (NullPointerException e) {
            logger.warn("no current line");
            throw new PlayException();
        }
    }

    /**
     * Set the volume of the current line
     * @param volume
     */
    public void setVolume(int volume) {
        if (currentLine != null) {
            if (currentLine.isOpen()) {
                try {
                    FloatControl volumeControl = (FloatControl)currentLine.getControl(FloatControl.Type.MASTER_GAIN);
                    volumeControl.setValue(((float)volume - 100)/4);
                }
                catch (IllegalArgumentException e) {
                    logger.warn("master gain control unsupported or illegal value ("+volume+")");
                    try {
                        BooleanControl muteControl = (BooleanControl)currentLine.getControl(BooleanControl.Type.MUTE);
                        boolean mute = volume == 0 ? true : false;
                        muteControl.setValue(mute);
                    }
                    catch (IllegalArgumentException e1) {
                        logger.warn("mute control unsupported or illegal value ("+volume+")");
                    }
                }
            }
        }
    }

    /**
     * get the sample rate of the current line
     * @return sample rate
     */
    public int getSpeed() throws PlayException {
	float naturalRate = currentLine.getFormat().getSampleRate();
        try {
            FloatControl sampleRateControl = (FloatControl)currentLine.getControl(FloatControl.Type.SAMPLE_RATE);
            float currentRate = sampleRateControl.getValue();
            return (int)(currentRate*50/naturalRate);
        }
        catch (IllegalArgumentException e) {
            logger.warn("sample rate control unsupported");
            throw new PlayException();
        }
        catch (NullPointerException e) {
            logger.warn("no current line");
            throw new PlayException();
        }
    }

    /**
     * Set the sample rate of the current line
     * @param speed
     */
    public void setSpeed(int speed) {
        if (currentLine != null) {
            if (currentLine.isOpen()) {
        	float naturalRate = currentLine.getFormat().getSampleRate();
                try {
                    FloatControl sampleRateControl = (FloatControl)currentLine.getControl(FloatControl.Type.SAMPLE_RATE);
                    float newRate = speed*naturalRate/50;
                    sampleRateControl.setValue(newRate);
                }
                catch (IllegalArgumentException e) {
                    logger.warn("sample rate control unsupported or illegal value ("+speed+")");
                }
            }
        }
    }

    /**
     * get the balance of the current line
     * @return balance
     */
    public int getBalance() throws PlayException {
        try {
            FloatControl balanceControl = (FloatControl)currentLine.getControl(FloatControl.Type.BALANCE);
            return (int)balanceControl.getValue()*100;
        }
        catch (IllegalArgumentException e) {
            logger.warn("balance control unsupported");
            try {
                FloatControl panControl = (FloatControl)currentLine.getControl(FloatControl.Type.PAN);
                return (int)panControl.getValue()*100;
            }
            catch (IllegalArgumentException e1) {
                logger.warn("pan control unsupported");
                throw new PlayException();
            }
        }
        catch (NullPointerException e) {
            logger.warn("no current line");
            throw new PlayException();
        }
    }

    /**
     * Set the balance of the current line
     * @param balance
     */
    public void setBalance(int balance) {
        if (currentLine != null) {
            if (currentLine.isOpen()) {
                try {
                    FloatControl balanceControl = (FloatControl)currentLine.getControl(FloatControl.Type.BALANCE);
                    balanceControl.setValue((float)balance/100);
                }
                catch (IllegalArgumentException e) {
                    logger.warn("balance control unsupported or illegal value ("+balance+")");
                    try {
                        FloatControl panControl = (FloatControl)currentLine.getControl(FloatControl.Type.PAN);
                        panControl.setValue((float)balance/100);
                    }
                    catch (IllegalArgumentException e1) {
                        logger.warn("pan control unsupported or illegal value ("+balance+")");
                    }
                }
            }
        }
    }
    
    /**
     * return audio file type corresponding to the filename (based on the extension)
     * @param filename
     * @return
     * @throws PlayException
     */
    public static AudioFileFormat.Type getAudioTypeFromFileName(String filename) throws PlayException {
        if (filename.endsWith(".wav") || filename.endsWith(".WAV")) {
            return AudioFileFormat.Type.WAVE;
        } else if (filename.endsWith(".au") || filename.endsWith(".AU")) {
            return AudioFileFormat.Type.AU;
        } else if (filename.endsWith(".aif") || filename.endsWith(".aiff") || filename.endsWith(".AIF") || filename.endsWith(".AIFF")) {
            return AudioFileFormat.Type.AIFF;
        } else if (filename.endsWith(".aifc") || filename.endsWith(".AIFC")) {
            return AudioFileFormat.Type.AIFC;
        //} else if (filename.endsWith(".ogg") || filename.endsWith(".OGG")) {
            //return VorbisFileFormatType.OGG;
        } else if (filename.endsWith(".mp3") || filename.endsWith(".MP3")) {
            return MpegFileFormatType.MP3;
        } else {
            throw new PlayException("unknown extension");
        }
    }

    private void fireReadingStartedEvent(PlayEvent event) {
	for (int i = 0; i < listener.size(); i++) {
	    PlayListener aListener = listener.get(i);
	    aListener.readingStarted(event);
	}
    }

    private void fireReadingPausedEvent(PlayEvent event) {
	for (int i = 0; i < listener.size(); i++) {
	    PlayListener aListener = listener.get(i);
	    aListener.readingPaused(event);
	}
    }

    private void fireReadingResumedEvent(PlayEvent event) {
	for (int i = 0; i < listener.size(); i++) {
	    PlayListener aListener = listener.get(i);
	    aListener.readingResumed(event);
	}
    }

    private void fireReadingStoppedEvent(PlayEvent event) {
	for (int i = 0; i < listener.size(); i++) {
	    PlayListener aListener = listener.get(i);
	    aListener.readingStopped(event);
	}
    }

    private void fireReadingEndedEvent(PlayEvent event) {
	for (int i = 0; i < listener.size(); i++) {
	    PlayListener aListener = listener.get(i);
	    aListener.readingEnded(event);
	}
    }

    private void fireNextReadEvent(PlayEvent event) {
	for (int i = 0; i < listener.size(); i++) {
	    PlayListener aListener = listener.get(i);
	    aListener.nextRead(event);
	}
    }

    private void firePreviousReadEvent(PlayEvent event) {
	for (int i = 0; i < listener.size(); i++) {
	    PlayListener aListener = listener.get(i);
	    aListener.previousRead(event);
	}
    }

    /**
     * 
     * @return
     */
    public List<String> getRenderings() {
        List<String> renderings = new ArrayList<String>();
        renderings.add("document");
        return renderings;
    }
    
    /**
     * 
     * @param encodedStream
     * @return
     */
    public AudioFormat decodeFormat(AudioFormat encodedFormat) {
        logger.info("encoded audio format: encoding="+encodedFormat.getEncoding()+",sample rate="+encodedFormat.getSampleRate()+",channels="+encodedFormat.getChannels());
        
        AudioFormat decodedFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED, 	// Encoding to use
            encodedFormat.getSampleRate(),     // sample rate (same as base format)
            16,                         	// sample size in bits (thx to Javazoom)
            encodedFormat.getChannels(),       // # of Channels
            encodedFormat.getChannels()*2,     // Frame Size
            encodedFormat.getSampleRate(),     // Frame Rate
            false                       	// Big Endian
        );
        
        return decodedFormat;
    }

    /**
     * @return loop.
     */
    public boolean isLoop() {
        return this.loop;
    }

    /**
     * @param loop loop 
     */
    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    /**
     * @return document.
     */
    public Sound getDocument() {
        return this.document;
    }

    /**
     * 
     * @see org.tramper.ui.Renderer#isActive()
     */
    public boolean isActive() {
	return document.isActive();
    }

    /**
     * 
     * @param event
     */
    public void documentActivated(DocumentEvent event) {
    }

    /**
     * 
     * @param event
     */
    public void documentDeactivated(DocumentEvent event) {
    }
    
    /**
     * Stop the current clip if necessary
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        this.stop();
        bufferedDecodedStream.close();
        super.finalize();
    }
}
