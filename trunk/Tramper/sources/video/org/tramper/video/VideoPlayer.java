package org.tramper.video;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.tramper.player.PlayEvent;
import org.tramper.player.PlayException;
import org.tramper.player.PlayListener;
import org.tramper.player.Player;

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
public class VideoPlayer implements Player, MediaStateListener {
    /** logger */
    private Logger logger = Logger.getLogger(VideoPlayer.class);
    /** play listeners list */
    private List<PlayListener> listener;
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
     * @see org.tramper.player.MediaPlayer#play(java.net.URL)
     */
    public void play(URL anUrl) throws PlayException {
	try {
	    mediaProvider = new MediaProvider(anUrl.toURI());
	    mediaProvider.setRepeating(false);
	    mediaProvider.play();
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    throw new PlayException();
	}
    }

    /**
     * @see org.tramper.player.MediaPlayer#playAndWait(java.net.URL)
     */
    public void playAndWait(URL anUrl) throws PlayException {
	try {
	    mediaProvider = new MediaProvider(anUrl.toURI());
	    mediaProvider.setRepeating(false);
	    double duration = mediaProvider.getDuration();
	    mediaProvider.play();
	    Thread.sleep((long)duration*1000);
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    throw new PlayException();
	}
    }

    /**
     * @see org.tramper.player.MediaPlayer#playLoop(java.net.URL)
     */
    public void playLoop(URL anUrl) throws PlayException {
	try {
	    mediaProvider = new MediaProvider(anUrl.toURI());
            mediaProvider.setRepeating(true);
            mediaProvider.play();
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    throw new PlayException();
	}
    }

    /**
     * 
     * @param aMediaProvider
     * @throws PlayException
     */
    public void play(MediaProvider aMediaProvider) throws PlayException {
	mediaProvider = aMediaProvider;
        audioCtrl = mediaProvider.getControl(AudioControl.class);
        videoCtrl = mediaProvider.getControl(VideoControl.class);
        videoCtrl.setResizeBehavior(ResizeBehavior.None);
        
        mediaProvider.addMediaStateListener(this);
	mediaProvider.setRepeating(false);
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
