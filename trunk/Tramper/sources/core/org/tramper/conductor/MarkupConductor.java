package org.tramper.conductor;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.tramper.doc.DocumentItem;
import org.tramper.doc.MarkupDocument;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.SpeakableItem;
import org.tramper.doc.Target;
import org.tramper.doc.WebPage;
import org.fingon.player.PlayEvent;
import org.fingon.player.PlayException;
import org.fingon.player.PlayListener;
import org.fingon.player.Player;
import org.fingon.player.PlayerFactory;
import org.fingon.synthesizer.SpeechSynthesizer;
import org.fingon.synthesizer.SynthesisException;
import org.fingon.synthesizer.VoiceDesc;
import org.tramper.ui.Renderer;
import org.tramper.ui.RenderingException;

/**
 * 
 * @author Paul-Emile
 */
public class MarkupConductor extends Conductor implements Runnable {
    /** logger */
    private Logger logger = Logger.getLogger(MarkupConductor.class);
    /** a list of speakable to play in the run method */
    protected List<SpeakableItem> listToSpeak;
    /** step by step flag */
    protected boolean stepByStep;
    /** current speakable index */
    protected int speakableIndex;
    /**  */
    protected List<Player> players;
    /** list of play listener */
    protected List<PlayListener> playListener;
    /** the speech synthesizer */
    protected SpeechSynthesizer principal;

    /**
     * 
     */
    public MarkupConductor() {
	super();
	players = new ArrayList<Player>();
	playListener = new ArrayList<PlayListener>();
	try {
	    principal = (SpeechSynthesizer)PlayerFactory.getPlayerByExtension("html");
	} catch (PlayException e) {
	    logger.error("couldn't have a speech synthesizer, so can't play the document", e);
	}
	players.add(principal);
	
    }
    
    public Player getPrincipal() {
	return principal;
    }
    
    /**
     * @see org.tramper.ui.Renderer#isDocumentSupported(org.tramper.doc.SimpleDocument)
     */
    public boolean isDocumentSupported(SimpleDocument document) {
	if (document instanceof WebPage) {
	    return true;
	}
	return false;
    }

    /**
     * @see org.tramper.player.Player#addPlayListener(org.tramper.player.PlayListener)
     */
    public void addPlayListener(PlayListener listener) {
	if (!playListener.contains(listener)) {
	    playListener.add(listener);
	}
    }

    /**
     * @see org.tramper.player.Player#removePlayListener(org.tramper.player.PlayListener)
     */
    public void removePlayListener(PlayListener listener) {
	playListener.remove(listener);
    }

    private void fireReadingStartedEvent(PlayEvent event) {
	for (int i = 0; i < playListener.size(); i++) {
	    PlayListener aListener = playListener.get(i);
	    aListener.readingStarted(event);
	}
    }

    private void fireReadingPausedEvent(PlayEvent event) {
	for (int i = 0; i < playListener.size(); i++) {
	    PlayListener aListener = playListener.get(i);
	    aListener.readingPaused(event);
	}
    }

    private void fireReadingResumedEvent(PlayEvent event) {
	for (int i = 0; i < playListener.size(); i++) {
	    PlayListener aListener = playListener.get(i);
	    aListener.readingResumed(event);
	}
    }

    private void fireReadingStoppedEvent(PlayEvent event) {
	for (int i = 0; i < playListener.size(); i++) {
	    PlayListener aListener = playListener.get(i);
	    aListener.readingStopped(event);
	}
    }

    private void fireReadingEndedEvent(PlayEvent event) {
	for (int i = 0; i < playListener.size(); i++) {
	    PlayListener aListener = playListener.get(i);
	    aListener.readingEnded(event);
	}
    }

    private void fireNextReadEvent(PlayEvent event) {
	for (int i = 0; i < playListener.size(); i++) {
	    PlayListener aListener = playListener.get(i);
	    aListener.nextRead(event);
	}
    }

    private void firePreviousReadEvent(PlayEvent event) {
	for (int i = 0; i < playListener.size(); i++) {
	    PlayListener aListener = playListener.get(i);
	    aListener.previousRead(event);
	}
    }
    
    private void fireVolumeChangedEvent(PlayEvent event) {
	for (int i = 0; i < playListener.size(); i++) {
	    PlayListener aListener = playListener.get(i);
	    aListener.volumeChanged(event);
	}
    }

    private void fireSampleRateChangedEvent(PlayEvent event) {
	for (int i = 0; i < playListener.size(); i++) {
	    PlayListener aListener = playListener.get(i);
	    aListener.sampleRateChanged(event);
	}
    }
    
    /**
     * @see org.tramper.player.Player#getSpeed()
     */
    public int getSpeed() throws PlayException {
	if (players.size() > 0) {
	    return players.get(0).getSpeed();
	}
	return 0;
    }

    /**
     * @see org.tramper.player.Player#setSpeed(int)
     */
    public void setSpeed(int speed) {
	for (Player player : players) {
	    player.setSpeed(speed);
	}
	PlayEvent event = new PlayEvent(this);
	event.setNewValue(speed);
	this.fireSampleRateChangedEvent(event);
    }

    /**
     * @see org.tramper.player.Player#getVolume()
     */
    public int getVolume() throws PlayException {
	if (players.size() > 0) {
	    return players.get(0).getVolume();
	}
	return 0;
    }

    /**
     * @see org.tramper.player.Player#setVolume(int)
     */
    public void setVolume(int volume) {
	for (Player player : players) {
	    player.setVolume(volume);
	}
	PlayEvent event = new PlayEvent(this);
	event.setNewValue(volume);
	this.fireVolumeChangedEvent(event);
    }

    /**
     * @see org.tramper.player.Player#isPaused()
     */
    public boolean isPaused() {
	synchronized (players) {
	    return paused;
	}
    }

    /**
     * @see org.tramper.player.Player#isRunning()
     */
    public boolean isRunning() {
	return !stopped;
    }

    /**
     * @see org.tramper.player.Player#next()
     */
    public void next() {
	boolean isLast = (speakableIndex == listToSpeak.size() - 1);
	if (isLast) {
	    return;
	}
	goNext = true;
	
	if (isPaused()) {
	    resume();
	}

	for (Player player : players) {
	    player.next();
	}
    }

    /**
     * @see org.tramper.player.Player#previous()
     */
    public void previous() {
	boolean isFirst = (speakableIndex == 0);
	if (isFirst) {
	    return;
	}
	goPrevious = true;
	speakableIndex = speakableIndex - 2;
	
	if (isPaused()) {
	    resume();
	}

	for (Player player : players) {
	    player.previous();
	}
    }

    /**
     * @see org.tramper.player.Player#pause()
     */
    public void pause() {
	synchronized (players) {
	    paused = true;
	}

	for (Player player : players) {
	    player.pause();
	}
	
	PlayEvent event = new PlayEvent(this);
	fireReadingPausedEvent(event);
    }

    /**
     * @see org.tramper.player.Player#resume()
     */
    public void resume() {
	synchronized (players) {
	    paused = false;
	    players.notify();
	}

	for (Player player : players) {
	    player.resume();
	}
	
	PlayEvent event = new PlayEvent(this);
	fireReadingResumedEvent(event);
    }

    /**
     * @see org.tramper.player.Player#stop()
     */
    public void stop() {
	stopped = true;
	
	if (isPaused()) {
	    resume();
	}

	for (Player player : players) {
	    player.stop();
	}
	
	if (document != null) {
	    document.setIndex(0);
	}
	
	PlayEvent event = new PlayEvent(this);
	fireReadingStoppedEvent(event);
    }

    /**
     * 
     * @see org.tramper.synthesizer.SpeechSynthesizer#isStepByStep()
     */
    public boolean isStepByStep() {
	return stepByStep;
    }

    /**
     * 
     * @see org.tramper.synthesizer.SpeechSynthesizer#setStepByStep(boolean)
     */
    public void setStepByStep(boolean stepByStep) {
	this.stepByStep = stepByStep;
    }

    /**
     * @see org.tramper.ui.Renderer#render(org.tramper.doc.SimpleDocument, org.tramper.doc.Target, int)
     */
    public void render(SimpleDocument doc, Target target, int documentPart) throws RenderingException {
	if (!(doc instanceof MarkupDocument)) {
	    throw new RenderingException("unknown document: "+doc);
	}
	
	this.target = target;
	this.document = doc;

	listToSpeak = new ArrayList<SpeakableItem>();
	if (Renderer.ALL_PART == documentPart) {
	    List<DocumentItem> items = ((MarkupDocument)doc).getItems();
	    listToSpeak.addAll(items);
	} else {
	    return;
	}
	
	// set the language of the synthesizer with the language of the document
	Locale locale = ((MarkupDocument) document).getLanguage();
	if (locale != null) {
	    try {
		boolean match = principal.matchEngineLanguage(locale);
		if (match == false) {
		    principal.load(locale);
		}
	    } catch (SynthesisException se) {
		logger.warn("no available speaker, can't check the language");
	    }
	}
	
	Thread aReading = new Thread(this, "reading");
	aReading.start();
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
	stopped = false;
	
	PlayEvent event = new PlayEvent(this);
	this.fireReadingStartedEvent(event);

	int startIndex = (int)document.getIndex();
	for (speakableIndex = startIndex; speakableIndex < listToSpeak.size(); speakableIndex++) {
	    SpeakableItem anItem = listToSpeak.get(speakableIndex);
	    document.setIndex(speakableIndex);

	    //make a pause before the speech
	    Float pause = anItem.getPauseBefore();
	    if (pause != null) {
                try {
                    Thread.sleep(pause.longValue());
                } catch (InterruptedException e) {}
	    }
	    
	    //play a sound before the speech if any
	    URL cue = anItem.getCueBefore();
	    if (cue != null) {
                String cuePath = cue.getPath();
                String extension = cuePath.substring(cuePath.lastIndexOf('.')+1);
                try {
                    Player player = PlayerFactory.getPlayerByExtension(extension);
                    players.add(player);
		    player.playAndWait(cue);
		    players.remove(player);
		} catch (PlayException e) {}
	    }

	    //have a rest before the speech
	    Float rest = anItem.getRestBefore();
	    if (rest != null) {
                try {
                    Thread.sleep(rest.longValue());
                } catch (InterruptedException e) {}
	    }
	    
	    //set some parameters
	    Float rate = anItem.getRate();
	    if (rate != null) {
		principal.setSpeed(rate.intValue());
	    }
	    Float volume = anItem.getVolume();
	    if (volume != null) {
		principal.setVolume(volume.intValue());
	    }
	    Float pitch = anItem.getPitch();
	    if (pitch != null) {
		principal.setPitch(pitch.floatValue());
	    }
	    Float pitchRange = anItem.getPitchRange();
	    if (pitchRange != null) {
		principal.setPitchRange(pitchRange.floatValue());
	    }
	    List<VoiceDesc> voiceFamily = anItem.getVoiceFamily();
	    if (voiceFamily != null) {
                for (int i=0; i<voiceFamily.size(); i++) {
                    VoiceDesc voiceDesc = voiceFamily.get(i);
                    boolean found = principal.loadVoice(voiceDesc);
                    if (found) {
                        break;
                    }
                }
	    }
	    
	    //speak the text
	    String text = anItem.getText(principal.getEngineLocale());
	    principal.playAndWait(text);

	    //have a rest after the speech
	    rest = anItem.getRestAfter();
	    if (rest != null) {
                try {
                    Thread.sleep(rest.longValue());
                } catch (InterruptedException e) {}
	    }

	    //play a sound after the speech if any
	    cue = anItem.getCueAfter();
	    if (cue != null) {
                String cuePath = cue.getPath();
                String extension = cuePath.substring(cuePath.lastIndexOf('.'));
                try {
                    Player player = PlayerFactory.getPlayerByExtension(extension);
                    players.add(player);
		    player.playAndWait(cue);
		    players.remove(player);
		} catch (PlayException e) {}
	    }

	    //make a pause after the speech
	    pause = anItem.getPauseAfter();
	    if (pause != null) {
                try {
                    Thread.sleep(pause.longValue());
                } catch (InterruptedException e) {}
	    }
	    
	    // stop the thread if requested
	    if (stopped) {
		return;
	    }

	    // request to pause the thread is step by step mode
            synchronized (players) {
                if (stepByStep) {
                    paused = true;
                }
            }
            
	    // pause the thread if requested
	    synchronized (players) {
		if (paused) {
		    try {
			players.wait();
		    } catch (InterruptedException e) {
			logger.warn("waiting speech interrupted", e);
		    }
		}
	    }

	    // Refresh the speakable list in the document viewer
	    if (goPrevious) {
		goPrevious = false;
		event.setFirstSelected((speakableIndex == 0));
		firePreviousReadEvent(event);
	    } else {
		goNext = false;
		event.setLastSelected((speakableIndex == listToSpeak.size() - 1));
		fireNextReadEvent(event);
	    }
	}

	fireReadingEndedEvent(event);
	
	stopped = true;
    }
}
