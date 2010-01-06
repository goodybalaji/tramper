package org.tramper.player;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

import org.apache.log4j.Logger;
import org.tramper.doc.DocumentEvent;
import org.tramper.doc.DocumentListener;
import org.tramper.doc.ImageDocument;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.ui.Renderer;
import org.tramper.ui.RenderingException;

/**
 * Read the pixels of the image and translate them in midi notes with the following rules :
 * <ul>
 * <li>pixel's x-coordinate is translated in balance</li>
 * <li>pixel's brightness is translated in pitch</li>
 * <li>pixel's transparency is translated in volume</li>
 * </lu>
 * @author Paul-Emile
 */
public class ImagePlayer implements Player, Runnable, DocumentListener {
    /** logger */
    private static Logger logger = Logger.getLogger(ImagePlayer.class);
    /** play listeners list */
    private List<PlayListener> listener;
    /** flag to stop the current play */
    private boolean stopped = false;
    /** midi synthesizer */
    private Synthesizer synthesizer;
    /** Pixels grabber */
    private PixelGrabber pg;
    /** paused */
    private boolean paused;
    /** document currently played */
    private ImageDocument document;
    /** pause */
    private int pause = 100;
    /** skip the current pixel's row */
    private boolean next;
    /** current channel */
    private MidiChannel channel;
    /** current channel index */
    private int channelIndex;
    /** target */
    private Target target;
    
    /**
     * 
     */
    public ImagePlayer() {
        listener = new ArrayList<PlayListener>();
        try {
            synthesizer = MidiSystem.getSynthesizer();
            MidiChannel[] channels = synthesizer.getChannels();
            
            // choose the first available channel
            for (int i=0; i<channels.length; i++) {
        	MidiChannel aChannel = channels[i];
        	if (aChannel != null) {
        	    channel = aChannel;
        	    channelIndex = i;
        	    break;
        	}
            }
        } catch (MidiUnavailableException e) {
            //should never arrives as long as there is Sun java sound implementation or tritonus
            logger.error("no available midi device : "+e.getMessage());
        }
    }
    
    /**
     * @see org.tramper.player.MediaPlayer#play(java.net.URL)
     */
    public void play(URL anUrl) throws PlayException {
        //Image img = Toolkit.getDefaultToolkit().getImage(anUrl);
    }

    /**
     * 
     * @param anUrl
     * @throws PlayException
     */
    public void playAndWait(URL anUrl) throws PlayException {
        //Image img = Toolkit.getDefaultToolkit().getImage(anUrl);
    }
    
    /**
     * Grab the pixel of the image and fullfil the midi sequence
     * @param img image to play
     * @throws PlayException
     */
    protected void play(Image img) throws PlayException {
        this.stopped = false;
        
        //Grab the pixels first
        pg = new PixelGrabber(img, 0, 0, -1, -1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            logger.error("interrupted while waiting for pixels!");
            throw new PlayException();
        }
        
        if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
            logger.error("image fetch aborted or errored");
            throw new PlayException();
        }
        
        Receiver receiver = null;
        try {
            synthesizer.open();
            receiver = synthesizer.getReceiver();
        } catch (MidiUnavailableException e) {
            logger.warn("midi device unavailable : "+e.getMessage());
            throw new PlayException();
        }

        // load the instruments from the default sound bank
        Instrument[] instruments = synthesizer.getAvailableInstruments();
        /*for (int i=0; i<instruments.length; i++) {
            Instrument instrument = instruments[i];
            logger.debug("available : " + i + " " + instrument.getName());
        }*/
	//bright piano
	synthesizer.loadInstrument(instruments[1]);
	//Honky Tonk Piano
	synthesizer.loadInstrument(instruments[3]);
	//Vibraphone
	synthesizer.loadInstrument(instruments[11]);
	//harmonica
	synthesizer.loadInstrument(instruments[22]);
	//Nylon String guitar
	synthesizer.loadInstrument(instruments[24]);
	//Pizzicato Strings
	synthesizer.loadInstrument(instruments[45]);
	//Harp
	synthesizer.loadInstrument(instruments[46]);
	//Trumpet
	synthesizer.loadInstrument(instruments[56]);
	//Clarinet
	synthesizer.loadInstrument(instruments[71]);
	//Banjo
	synthesizer.loadInstrument(instruments[105]);
	//Bag pipe
	synthesizer.loadInstrument(instruments[109]);
	//Steel Drums
	synthesizer.loadInstrument(instruments[114]);
	//Reverse Cymbal
	synthesizer.loadInstrument(instruments[119]);
        
        
        int width = pg.getWidth();
        int height = pg.getHeight();
        int[] pixels = (int[])pg.getPixels();
        
        PlayEvent event = new PlayEvent(this);
        fireReadingStartedEvent(event);
        
        //process the pixels from top to bottom and left to right
        for (int x=0; x<width; x++) {
            //control the balance (8) pan (10) of the channel
            channel.controlChange(8, x*127/width);
            channel.controlChange(10, x*127/width);
            
            for (int y=0; y<height; y++) {
                if (stopped) {
                    return;
                }
                if (next) {
                    next = false;
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
                
                int aRGBPixel = pixels[y*width + x];
                int alpha = (aRGBPixel >> 24) & 0xff;
                int red   = (aRGBPixel >> 16) & 0xff;
                int green = (aRGBPixel >>  8) & 0xff;
                int blue  = (aRGBPixel      ) & 0xff;
                // select the instrument
                if (red > green && red > blue) {// red
                    if (green > blue) {// yellow
                	channel.programChange(3);
                    } else if (green < blue) {// magenta
                	channel.programChange(11);
                    } else {// full red
                	channel.programChange(1);
                    }
                } else if (green > red && green > blue) {// green
                    if (red > blue) {// yellow
                        channel.programChange(71);
                    } else if (red < blue) {// cyan
                        channel.programChange(56);
                    } else {// full green
                        channel.programChange(22);
                    }
                } else if (blue > red && blue > green) {// blue
                    if (red > green) {// magenta
                	channel.programChange(46);
                    } else if (red < green) {// cyan
                	channel.programChange(45);
                    } else {// full blue
                	channel.programChange(24);
                    }
                } else if (red == green) {// yellow
                    channel.programChange(105);
                } else if (red == blue) {// magenta
                    channel.programChange(109);
                } else if (green == blue) {// cyan
                    channel.programChange(114);
                } else {// white
                    channel.programChange(119);
                }
                
                //(255*3)/127 = 6
                int brightness = (red + green + blue)/6;

                //brightness -> pitch (0 - 127); alpha -> velocity (0 - 127)
                try {
                    ShortMessage aStartMidiMessage = new ShortMessage();
                    aStartMidiMessage.setMessage(ShortMessage.NOTE_ON, channelIndex, brightness, alpha/2);
                    receiver.send(aStartMidiMessage, -1);
                    
                    /*try {
    		    	Thread.sleep(50);
                    } catch (InterruptedException e) {}
                    
                    ShortMessage aStopMidiMessage = new ShortMessage();
                    aStopMidiMessage.setMessage(ShortMessage.NOTE_OFF, channelIndex, brightness, alpha/2);
                    receiver.send(aStopMidiMessage, -1);*/
                    
                } catch (InvalidMidiDataException e) {
                    logger.warn("wrong midi message (brightness="+brightness+") : "+e.getMessage());
                }
            }
            
            try {
        	Thread.sleep(pause);
            } catch (InterruptedException e) {}
            channel.allNotesOff();
            
            fireNextReadEvent(event);
        }
        
        receiver.close();
        synthesizer.close();

        fireReadingEndedEvent(event);
    }
    
    /**
     * @see org.tramper.player.MediaPlayer#pause()
     */
    public void pause() {
        synchronized (listener) {
            paused = true;
        }
        PlayEvent event = new PlayEvent(this);
        fireReadingPausedEvent(event);
    }

    /**
     * @see org.tramper.player.MediaPlayer#resume()
     */
    public void resume() {
        synchronized (listener) {
            paused = false;
            listener.notify();
        }
        PlayEvent event = new PlayEvent(this);
        fireReadingResumedEvent(event);
    }
    
    /**
     * @see org.tramper.player.MediaPlayer#stop()
     */
    public void stop() {
        stopped = true;
        if (pg != null) {
            pg.abortGrabbing();
        }
        if (synthesizer != null) {
            synthesizer.close();
        }
	PlayEvent event = new PlayEvent(this);
	fireReadingStoppedEvent(event);
    }
    
    /**
     * 
     * @see org.tramper.player.Player#next()
     */
    public void next() {
        next = true;
    }

    /**
     * 
     * @see org.tramper.player.Player#previous()
     */
    public void previous() {
	
    }

    /**
     * @see org.tramper.player.MediaPlayer#isRunning()
     */
    public boolean isRunning() {
	return !stopped;
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
     * @see org.tramper.player.MediaPlayer#setSpeed(int)
     */
    public void setSpeed(int speed) {
	pause = (int)(-(speed*9.5) + 1000);
    }

    /**
     * @see org.tramper.player.MediaPlayer#getSpeed()
     */
    public int getSpeed() throws PlayException {
        return (int)((1000 - pause)/9.5);
    }

    /**
     * @see org.tramper.player.MediaPlayer#setVolume(int)
     */
    public void setVolume(int volume) {
	channel.controlChange(7, volume*127/100);
    }

    /**
     * @see org.tramper.player.MediaPlayer#getVolume()
     */
    public int getVolume() throws PlayException {
        return channel.getController(7)*100/127;
    }
    
    /**
     * @see org.tramper.player.MediaPlayer#addPlayListener(org.tramper.player.PlayListener)
     */
    public void addPlayListener(PlayListener aListener) {
        if (listener.contains(aListener) == false) {
            listener.add(aListener);
        }
    }

    /**
     * @see org.tramper.player.MediaPlayer#removePlayListener(org.tramper.player.PlayListener)
     */
    public void removePlayListener(PlayListener aListener) {
        listener.remove(aListener);
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

    public List<String> getRenderings() {
	List<String> renderings = new ArrayList<String>();
	renderings.add("document");
	return renderings;
    }

    public void setOutput(File aFile) {
    }

    public void setOutput() {
    }

    public void render(SimpleDocument document, Target target) throws RenderingException {
	render(document, target, Renderer.ALL_PART);
    }

    public void render(int documentPart) throws RenderingException {
	render(document, target, documentPart);
    }

    public void render(SimpleDocument doc, Target target, int documentPart) throws RenderingException {
	this.target = target;
	if (documentPart != Renderer.ALL_PART) {
	    return;
	}
	
        if (!(doc instanceof ImageDocument)) {
            throw new RenderingException("wrong document class");
        }

        if (document != null) {
            document.removeDocumentListener(this);
        }
        doc.addDocumentListener(this);
	document = (ImageDocument)doc;

        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
	BufferedImage img = document.getImage();
	try {
	    play(img);
	} catch (PlayException e) {
	    logger.error(e.getMessage(), e);
	}
    }

    /**
     * @return document.
     */
    public ImageDocument getDocument() {
        return this.document;
    }

    /**
     * 
     * @see org.tramper.ui.Renderer#isActive()
     */
    public boolean isActive() {
	return document.isActive();
    }

    public void documentActivated(DocumentEvent event) {
    }

    public void documentDeactivated(DocumentEvent event) {
    }
}
