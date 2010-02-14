package org.tramper.video;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.tramper.doc.DocumentEvent;
import org.tramper.doc.DocumentListener;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.doc.Video;
import org.tramper.player.MediaPlayer;
import org.tramper.player.PlayEvent;
import org.tramper.player.PlayException;
import org.tramper.player.PlayListener;
import org.tramper.ui.Renderer;
import org.tramper.ui.RenderingException;

import com.sun.media.jmc.MediaProvider;
import com.sun.media.jmc.control.AudioControl;
import com.sun.media.jmc.control.VideoControl;
import com.sun.media.jmc.control.VideoControl.ResizeBehavior;
import com.sun.media.jmc.event.MediaStateEvent;
import com.sun.media.jmc.event.MediaStateListener;
import com.sun.media.jmc.type.ContainerType;
import com.sun.media.jmc.type.ContainerType.Container;

/**
 * Video player using Java Media Component (JMC), from JavaFX.
 * @author Paul-Emile
 */
public class VideoPlayer implements MediaPlayer, DocumentListener, MediaStateListener {
    /** logger */
    private Logger logger = Logger.getLogger(VideoPlayer.class);
    /** play listeners list */
    private List<PlayListener> listener;
    /** document currently played */
    private Video document;
    /** target */
    private Target target;
    /** media provider */
    private MediaProvider mediaProvider;
    /** audio control */
    private AudioControl audioCtrl;
    /** video control */
    private VideoControl videoCtrl;

    /**
     * 
     */
    public VideoPlayer() throws PlayException {
        super();
        listener = new ArrayList<PlayListener>();
    }

    /**
     * @see org.tramper.ui.Renderer#render(int)
     */
    public void render(int documentPart) throws RenderingException {
	render(document, target, documentPart);
    }

    /**
     * @see org.tramper.ui.Renderer#render(org.tramper.doc.SimpleDocument, org.tramper.doc.Target)
     */
    public void render(SimpleDocument doc, Target target) throws RenderingException {
	render(doc, target, Renderer.ALL_PART);
    }

    /**
     * @see org.tramper.ui.Renderer#render(org.tramper.doc.SimpleDocument, org.tramper.doc.Target, int)
     */
    public void render(SimpleDocument doc, Target target, int documentPart) throws RenderingException {
	this.target = target;
	
	if (documentPart != Renderer.ALL_PART) {
	    return;
	}
	
        if (!(doc instanceof Video)) {
            logger.error("The video player received a non video document: "+doc);
            throw new RenderingException("wrong document class");
        }
        if (document != null) {
            document.removeDocumentListener(this);
        }
        document = (Video)doc;
        document.addDocumentListener(this);
        
        mediaProvider = document.getMediaProvider();
        audioCtrl = mediaProvider.getControl(AudioControl.class);
        videoCtrl = mediaProvider.getControl(VideoControl.class);
        videoCtrl.setResizeBehavior(ResizeBehavior.None);
        
        mediaProvider.addMediaStateListener(this);
	mediaProvider.setRepeating(false);
	mediaProvider.setMediaTime(0.0);
	mediaProvider.play();
    }

    /**
     * @see org.tramper.player.MediaPlayer#play(java.net.URL)
     */
    public void play(URL anUrl) throws PlayException {
	mediaProvider.setRepeating(false);
	mediaProvider.play();
    }

    /**
     * @see org.tramper.player.MediaPlayer#playAndWait(java.net.URL)
     */
    public void playAndWait(URL anUrl) throws PlayException {
	mediaProvider.setRepeating(false);
	mediaProvider.play();
    }

    /**
     * @see org.tramper.player.MediaPlayer#playLoop(java.net.URL)
     */
    public void playLoop(URL anUrl) throws PlayException {
	mediaProvider.setRepeating(true);
	mediaProvider.play();
    }

    /**
     * @see org.tramper.player.Player#addPlayListener(org.tramper.player.PlayListener)
     */
    public void addPlayListener(PlayListener aListener) {
        if (this.listener.contains(aListener) == false) {
            this.listener.add(aListener);
        }
    }

    /**
     * @see org.tramper.player.Player#removePlayListener(org.tramper.player.PlayListener)
     */
    public void removePlayListener(PlayListener aListener) {
        this.listener.remove(aListener);
    }

    /**
     * 
     * @see com.sun.media.jmc.event.MediaStateListener#endOfMediaReached(com.sun.media.jmc.event.MediaStateEvent)
     */
    public void endOfMediaReached(MediaStateEvent arg0) {
	PlayEvent event = new PlayEvent(this);
	fireReadingEndedEvent(event);
    }

    /**
     * 
     * @see com.sun.media.jmc.event.MediaStateListener#playerRepeated(com.sun.media.jmc.event.MediaStateEvent)
     */
    public void playerRepeated(MediaStateEvent arg0) {
    }

    /**
     * 
     * @see com.sun.media.jmc.event.MediaStateListener#playerStarted(com.sun.media.jmc.event.MediaStateEvent)
     */
    public void playerStarted(MediaStateEvent arg0) {
	PlayEvent event = new PlayEvent(this);
	fireReadingStartedEvent(event);
    }

    /**
     * 
     * @see com.sun.media.jmc.event.MediaStateListener#playerStopped(com.sun.media.jmc.event.MediaStateEvent)
     */
    public void playerStopped(MediaStateEvent arg0) {
	PlayEvent event = new PlayEvent(this);
	fireReadingPausedEvent(event);
    }

    /**
     * 
     * @see com.sun.media.jmc.event.MediaStateListener#stopTimeReached(com.sun.media.jmc.event.MediaStateEvent)
     */
    public void stopTimeReached(MediaStateEvent arg0) {
	PlayEvent event = new PlayEvent(this);
	fireReadingStoppedEvent(event);
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
     * @see org.tramper.player.Player#getRenderings()
     */
    public List<String> getRenderings() {
        List<String> renderings = new ArrayList<String>();
        renderings.add("document");
        return renderings;
    }

    /**
     * @see org.tramper.player.Player#getSpeed()
     */
    public int getSpeed() throws PlayException {
	return (int)mediaProvider.getRate()*50;
    }

    /**
     * @see org.tramper.player.Player#setSpeed(int)
     */
    public void setSpeed(int speed) {
	mediaProvider.setRate((double)speed/50);
    }

    /**
     * @see org.tramper.player.Player#getVolume()
     */
    public int getVolume() throws PlayException {
	return (int)audioCtrl.getVolume()*100;
    }

    /**
     * @see org.tramper.player.Player#setVolume(int)
     */
    public void setVolume(int volume) {
	audioCtrl.setVolume((float)volume/100);
    }

    /**
     * @see org.tramper.player.Player#isPaused()
     */
    public boolean isPaused() {
	return !mediaProvider.isPlaying();
    }

    /**
     * @see org.tramper.player.Player#pause()
     */
    public void pause() {
	mediaProvider.pause();
	PlayEvent event = new PlayEvent(this);
	fireReadingPausedEvent(event);
    }

    /**
     * @see org.tramper.player.Player#resume()
     */
    public void resume() {
	mediaProvider.play();
	PlayEvent event = new PlayEvent(this);
	fireReadingResumedEvent(event);
    }

    /**
     * @see org.tramper.player.Player#stop()
     */
    public void stop() {
	//mediaProvider.setMediaTime(0.0);
	mediaProvider.pause();
    }

    /**
     * @see org.tramper.player.Player#isRunning()
     */
    public boolean isRunning() {
	return true;
    }

    /**
     * @see org.tramper.player.Player#next()
     */
    public void next() {
    }

    /**
     * @see org.tramper.player.Player#previous()
     */
    public void previous() {
    }

    /**
     * @see org.tramper.player.Player#setOutput(java.io.File)
     */
    public void setOutput(File aFile) {
    }

    /**
     * @see org.tramper.player.Player#setOutput()
     */
    public void setOutput() {
    }

    /**
     * @see org.tramper.ui.Renderer#getDocument()
     */
    public SimpleDocument getDocument() {
	return document;
    }

    /**
     * @see org.tramper.ui.Renderer#isActive()
     */
    public boolean isActive() {
	return document.isActive();
    }

    /**
     * @see org.tramper.doc.DocumentListener#documentActivated(org.tramper.doc.DocumentEvent)
     */
    public void documentActivated(DocumentEvent event) {
    }

    /**
     * @see org.tramper.doc.DocumentListener#documentDeactivated(org.tramper.doc.DocumentEvent)
     */
    public void documentDeactivated(DocumentEvent event) {
    }

    public boolean isDocumentSupported(SimpleDocument document) {
	if (document instanceof Video) {
	    return true;
	}
	return false;
    }

    public boolean isExtensionSupported(String extension) {
	List<ContainerType> types = MediaProvider.getSupportedContainerTypes();
	for (ContainerType type : types) {
	    Container container = type.getContainer();
	    if (container.equals(Container.AVI) || container.equals(Container.FLV) || container.equals(Container.MOV) || container.equals(Container.MP4) || container.equals(Container.SWF)) {
        	for (String anExtension : type.getExtensions()) {
        	    if (anExtension.equalsIgnoreCase(extension)) {
        		return true;
        	    }
        	}
	    }
	}
	return false;
    }
}
