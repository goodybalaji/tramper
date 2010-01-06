package org.tramper.synthesizer;

import java.beans.PropertyVetoException;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.tramper.doc.DocumentEvent;
import org.tramper.doc.DocumentItem;
import org.tramper.doc.DocumentListener;
import org.tramper.doc.Library;
import org.tramper.doc.Link;
import org.tramper.doc.Sound;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.MarkupDocument;
import org.tramper.doc.SpeakableItem;
import org.tramper.doc.Target;
import org.tramper.loader.Loader;
import org.tramper.loader.LoaderFactory;
import org.tramper.player.PlayEvent;
import org.tramper.player.PlayException;
import org.tramper.player.PlayListener;
import org.tramper.player.PlayerFactory;
import org.tramper.player.SoundPlayer;
import org.tramper.ui.Renderer;
import org.tramper.ui.RenderingException;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineList;
import javax.speech.EngineModeDesc;
import javax.speech.EngineStateError;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.SynthesizerProperties;
import javax.speech.synthesis.Voice;

/**
 * A synthesizer implementing the Sun's JSAPI.
 * @author Paul-Emile
 */
public class JSAPISpeechSynthesizer implements SpeechSynthesizer, Runnable, DocumentListener {
    /** logger */
    private Logger logger = Logger.getLogger(JSAPISpeechSynthesizer.class);
    /** speech synthesizer */
    protected Synthesizer synthe;
    /** a list of speakables to read in the run method */
    protected List<SpeakableItem> listToSpeak;
    /** flag to stop the current reading (break from the thread) */
    protected boolean stopped = true;
    /** flag to pause the current reading (wait for notify) */
    protected boolean paused = false;
    /** flag to go to the next item */
    protected boolean goNext = false;
    /** flag to go to the previous item */
    protected boolean goPrevious = false;
    /** flag to skip media playing (when saving in file) */
    protected boolean skipMedia = false;
    /** current speakable index */
    protected int speakableIndex;
    /** speech listeners list */
    protected List<SynthesisListener> listener;
    /** list of reading listener */
    protected List<PlayListener> playListener;
    /** step by step flag */
    protected boolean stepByStep;
    /** document to speak */
    protected SimpleDocument document;
    /** target */
    private Target target;

    /**
     * Load the default speech synthesizer
     */
    public JSAPISpeechSynthesizer() throws SynthesisException {
	listener = new ArrayList<SynthesisListener>();
	playListener = new ArrayList<PlayListener>();
	this.load();
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
     * @see org.tramper.player.Player#play(SimpleDocument)
     */
    public void render(SimpleDocument document, Target target) throws RenderingException {
	render(document, target, Renderer.ALL_PART);
    }
    
    /**
     * 
     * @param document
     * @param rendering
     */
    public void render(SimpleDocument doc, Target target, int documentPart) throws RenderingException {
	this.target = target;
	
        if (!(doc instanceof MarkupDocument)) {
            throw new RenderingException("wrong document class");
        }
        if (document != null) {
            document.removeDocumentListener(this);
        }
	document = doc;
	document.addDocumentListener(this);

	listToSpeak = new ArrayList<SpeakableItem>();
	if (Renderer.ALL_PART == documentPart) {
	    skipMedia = false;
	    List<DocumentItem> items = ((MarkupDocument) document).getItems();
	    listToSpeak.addAll(items);
	} else if (Renderer.LINK_PART == documentPart) {
	    skipMedia = true;
	    List<DocumentItem> speakable = ((MarkupDocument) document).getItems();
	    for (int i = 0; i < speakable.size(); i++) {
		DocumentItem item = speakable.get(i);
		List<Link> links = item.getLinks();
		listToSpeak.addAll(links);
	    }
	} else {
	    return;
	}

	// check the language of the doc
	Locale locale = ((MarkupDocument) document).getLanguage();
	if (locale != null) {
	    try {
		boolean match = this.matchEngineLanguage(locale);
		if (match == false) {
		    this.load(locale);
		    /*if (loaded == false) {
			List<UserInterface> ui = UserInterfaceFactory.getAllUserInterfaces();
			for (UserInterface anUi : ui) {
			    anUi.raiseWarning("warningLanguage");
			}
		    }*/
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
	if (listToSpeak == null) {
	    return;
	}

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
                    SoundPlayer player = (SoundPlayer)PlayerFactory.getPlayerByExtension(extension);
		    player.playAndWait(cue);
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
		setSpeed(rate.intValue());
	    }
	    Float volume = anItem.getVolume();
	    if (volume != null) {
		setVolume(volume.intValue());
	    }
	    Float pitch = anItem.getPitch();
	    if (pitch != null) {
		setPitch(pitch.floatValue());
	    }
	    Float pitchRange = anItem.getPitchRange();
	    if (pitchRange != null) {
		setPitchRange(pitchRange.floatValue());
	    }
	    List<VoiceDesc> voiceFamily = anItem.getVoiceFamily();
	    if (voiceFamily != null) {
                for (int i=0; i<voiceFamily.size(); i++) {
                    VoiceDesc voiceDesc = voiceFamily.get(i);
                    boolean found = loadVoice(voiceDesc);
                    if (found) {
                        break;
                    }
                }
	    }
	    
	    //speak the text
	    String text = anItem.getText(getEngineLocale());
	    play(text);
	    try {
		synthe.waitEngineState(Synthesizer.QUEUE_EMPTY);
	    } catch (InterruptedException e) {
                logger.warn("speak interrupted : " + e.getMessage());
            }

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
                    SoundPlayer player = (SoundPlayer)PlayerFactory.getPlayerByExtension(extension);
		    player.playAndWait(cue);
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

	    // pause the thread if requested
	    synchronized (listener) {
		if (paused) {
		    try {
			listener.wait();
		    } catch (InterruptedException e) {
			logger.warn("waiting speech interrupted", e);
		    }
		}
	    }

	    // skip the current speakable
	    if (!goNext && !goPrevious) {

		// play the media
		if (skipMedia == false) {
		    List<Sound> media = ((DocumentItem) anItem).getMedia();
		    for (int i = 0; i < media.size(); i++) {
			Sound aMedia = (Sound) media.get(i);
                        URL url = aMedia.getUrl();
                        Loader loader = LoaderFactory.getLoader();
                        loader.download(url.toString(), new Target(Library.PRIMARY_FRAME, null));
                        pause();

			// stop the thread if requested
			if (stopped) {
			    return;
			}

			// skip the current speakable
			if (goNext || goPrevious) {
			    break;
			}

			// pause the thread if requested
			synchronized (listener) {
			    if (paused) {
				try {
				    listener.wait();
				} catch (InterruptedException e) {
				    logger.warn("waiting speech interrupted", e);
				}
			    }
			}
		    }
		}
	    }

            synchronized (listener) {
                if (stepByStep) {
            	paused = true;
                    try {
                	listener.wait();
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

    /**
     * load the first engine with the default locale and the firt voice of
     * this engine. if there is no engine with default locale, select
     * english
     */
    public void load() throws SynthesisException {
	// try to find an engine with the default locale
	Locale defaultLocale = Locale.getDefault();
	boolean engineFound = this.load(defaultLocale);
	// if not found, try to find an engine with the english locale
	if (!engineFound) {
	    engineFound = this.load(Locale.ENGLISH);
	}
	// if not found, load the first engine available
	if (!engineFound) {
	    EngineList list = Central.availableSynthesizers(null);
	    if (list.size() > 0) {
                SynthesizerModeDesc eng = (SynthesizerModeDesc) list.get(0);
                Locale engineLocale = eng.getLocale();
                loadEngine(eng.getEngineName(), eng.getModeName(), engineLocale);
                Voice[] voices = eng.getVoices();
                
                VoiceDesc voiceDesc = new VoiceDesc();
                voiceDesc.setName(voices[0].getName());
                voiceDesc.setGender(voices[0].getGender());
                voiceDesc.setAge(voices[0].getAge());
                loadVoice(voiceDesc);
	    } else {
		throw new SynthesisException();
	    }
	}
    }

    /**
     * Loads an engine for the given locale if possible.
     * @param locale
     * @throws SynthesisException
     */
    public boolean load(Locale locale) throws SynthesisException {
	String currentEngine = null;
	if (synthe != null) {
	    currentEngine = synthe.getEngineModeDesc().getEngineName();
	}
	EngineList list = Central.availableSynthesizers(null);
	for (int i = 0; i < list.size(); i++) {
	    SynthesizerModeDesc eng = (SynthesizerModeDesc) list.get(i);
	    Locale engineLocale = eng.getLocale();
	    String engineName = eng.getEngineName();
	    if (locale.getLanguage().equals(engineLocale.getLanguage())) {
		if (currentEngine != null) {
		    if (engineName.equals(currentEngine)) {
                        loadEngine(engineName, eng.getModeName(), engineLocale);
                        Voice[] voices = eng.getVoices();

                        VoiceDesc voiceDesc = new VoiceDesc();
                        voiceDesc.setName(voices[0].getName());
                        voiceDesc.setGender(voices[0].getGender());
                        voiceDesc.setAge(voices[0].getAge());
                        loadVoice(voiceDesc);
                        return true;
		    }
		} else {
		    loadEngine(engineName, eng.getModeName(), engineLocale);
                    Voice[] voices = eng.getVoices();

        	    VoiceDesc voiceDesc = new VoiceDesc();
        	    voiceDesc.setName(voices[0].getName());
        	    voiceDesc.setGender(voices[0].getGender());
        	    voiceDesc.setAge(voices[0].getAge());
                    loadVoice(voiceDesc);
                    return true;
		}
	    }
	}
	return false;
    }

    /**
     * add a speech listener
     * @param aListener
     */
    public void addSpeechListener(SynthesisListener aListener) {
	if (this.listener.contains(aListener) == false) {
	    this.listener.add(aListener);
	}
    }

    /**
     * remove a speech listener from the list
     * @param aListener
     */
    public void removeSpeechListener(SynthesisListener aListener) {
	this.listener.remove(aListener);
    }

    /**
     * fire a pitch changed event
     * @param event
     */
    protected void firePitchChanged(SynthesisEvent event) {
	for (int i = 0; i < listener.size(); i++) {
	    SynthesisListener aListener = listener.get(i);
	    aListener.pitchChanged(event);
	}
    }

    /**
     * fire a pitch range changed event
     * @param event
     */
    protected void firePitchRangeChanged(SynthesisEvent event) {
	for (int i = 0; i < listener.size(); i++) {
	    SynthesisListener aListener = listener.get(i);
	    aListener.pitchRangeChanged(event);
	}
    }

    /**
     * fire a voices list changed event
     * @param event
     */
    protected void fireVoicesListChanged(SynthesisEvent event) {
	for (int i = 0; i < listener.size(); i++) {
	    SynthesisListener aListener = listener.get(i);
	    aListener.voicesListChanged(event);
	}
    }

    /**
     * fire a engines list changed event
     * @param event
     */
    protected void fireEngineChanged(SynthesisEvent event) {
	for (int i = 0; i < listener.size(); i++) {
	    SynthesisListener aListener = listener.get(i);
	    aListener.engineChanged(event);
	}
    }

    /**
     * Fires a event when the voice has changed
     * @param event
     */
    protected void fireVoiceChanged(SynthesisEvent event) {
	for (int i = 0; i < listener.size(); i++) {
	    SynthesisListener aListener = listener.get(i);
	    aListener.voiceChanged(event);
	}
    }

    /**
     * Lists all the available engines but the time engines.
     * @return
     */
    public List<SynthesizerModeDesc> listAvailableEngines() {
	List<SynthesizerModeDesc> availableEngines = new ArrayList<SynthesizerModeDesc>();

	EngineList list = Central.availableSynthesizers(null);
	logger.debug(list.size() + " engines available");
	for (int i = 0; i < list.size(); i++) {
	    SynthesizerModeDesc engineDesc = (SynthesizerModeDesc) list.get(i);
	    String mode = engineDesc.getModeName();
	    if (mode.equals("time")) {
		logger.info("time engine skipped");
		continue;
	    }
	    availableEngines.add(engineDesc);
	}

	return availableEngines;
    }

    /**
     * Lists all the available voices for the current engine
     * @return
     */
    public List<VoiceDesc> listAvailableVoices() {
	List<VoiceDesc> availableVoices = new ArrayList<VoiceDesc>();

	SynthesizerModeDesc desc = (SynthesizerModeDesc) synthe.getEngineModeDesc();
	Voice[] voices = desc.getVoices();
	for (int i = 0; i < voices.length; i++) {
	    VoiceDesc voiceDesc = new VoiceDesc();
	    String voiceName = voices[i].getName();
	    voiceDesc.setName(voiceName);
	    String voiceStyle = voices[i].getStyle();
	    voiceDesc.setStyle(voiceStyle);
	    int voiceAge = voices[i].getAge();
	    voiceDesc.setAge(voiceAge);
	    int voiceGender = voices[i].getGender();
	    voiceDesc.setGender(voiceGender);

	    availableVoices.add(voiceDesc);
	}

	return availableVoices;
    }

    /**
     * Load an engine using it's name, mode and locale. Some engines can
     * have the same name and/or mode.
     * @param name engine name
     * @param mode engine mode
     * @param locale engine locale
     * @throws SynthesisException
     */
    public void loadEngine(String name, String mode, Locale locale) throws SynthesisException {
	SynthesizerModeDesc required = null;

	EngineList list = Central.availableSynthesizers(null);
	for (int i = 0; i < list.size(); i++) {
	    SynthesizerModeDesc eng = (SynthesizerModeDesc) list.get(i);
	    String engineName = eng.getEngineName();
	    String modeName = eng.getModeName();
	    Locale engineLocale = eng.getLocale();
	    if (engineName.equals(name) && modeName.equals(mode) && engineLocale.equals(locale)) {
		required = eng;
		break;
	    }
	}
	try {
	    logger.debug("try load engine " + name);
	    synthe = Central.createSynthesizer(required);
	} catch (IllegalArgumentException e) {
	    logger.error("No engine available for the specified locale, gender and age", e);
	    throw new SynthesisException("No engine available for the specified locale, gender and age");
	} catch (EngineException e) {
	    logger.error("Impossible to create engine for the specified locale, gender and age", e);
	    throw new SynthesisException("Impossible to create engine for the specified locale, gender and age");
	}

	if (synthe == null) {
	    logger.warn("synthetizer required not created");
	    throw new SynthesisException("synthetizer required not created");
	}
        try {
            synthe.allocate();
        } catch (EngineException e) {
            logger.error("engine can't be allocated", e);
            throw new SynthesisException("engine can't be allocated");
        } catch (EngineStateError e) {
            logger.error("engine is not in the right state", e);
            throw new SynthesisException("engine is not in the right state");
        }
            
            float volume = synthe.getSynthesizerProperties().getVolume();
            if (volume == 0) {
        	try {
		    synthe.getSynthesizerProperties().setVolume(1f);
		} catch (PropertyVetoException e) {
	            logger.error("error when setting synthesizer volume to 1.0", e);
		}
            }
            
            SynthesisEvent event = new SynthesisEvent(this);
            EngineModeDesc engineDesc = synthe.getEngineModeDesc();
            event.setEngine(engineDesc);
            this.fireEngineChanged(event);

	    float pitch = this.getPitch();
	    SynthesisEvent pitchEvent = new SynthesisEvent(this);
	    pitchEvent.setPitch(pitch);
	    this.firePitchChanged(pitchEvent);

	    float pitchRange = this.getPitchRange();
	    SynthesisEvent synthesisEvent = new SynthesisEvent(this);
	    synthesisEvent.setPitchRange(pitchRange);
	    this.firePitchRangeChanged(synthesisEvent);
	    
	    List<VoiceDesc> voices = this.listAvailableVoices();
	    SynthesisEvent voiceListEvent = new SynthesisEvent(this);
	    voiceListEvent.setVoices(voices);
	    this.fireVoicesListChanged(voiceListEvent);

	    VoiceDesc voice = this.getVoiceDesc();
	    SynthesisEvent voiceEvent = new SynthesisEvent(this);
	    voiceEvent.setVoice(voice);
	    this.fireVoiceChanged(voiceEvent);
    }

    /**
     * Try to load the voice with the description in parameter. 
     * Check the name first, then gender and age, gender alone, and age alone next.
     * @param selectedVoice desired voice description
     */
    public boolean loadVoice(VoiceDesc selectedVoice) {
	logger.debug("try load voice " + selectedVoice);
	Voice voice = null;
	SynthesizerModeDesc desc = (SynthesizerModeDesc) synthe.getEngineModeDesc();
	Voice[] voices = desc.getVoices();
	String name = selectedVoice.getName();
	if (name != null) {
            for (int i = 0; i < voices.length; i++) {
        	String testedVoiceName = voices[i].getName().toLowerCase();
                if (testedVoiceName.equals(name)) {
                    voice = voices[i];
                    logger.debug("voice found : " + voice.getName());
                    break;
                }
            }
	}
	if (voice == null) {
            int age = selectedVoice.getAge();
            int gender = selectedVoice.getGender();
            for (int i = 0; i < voices.length; i++) {
                if (voices[i].getGender() == gender && voices[i].getAge() == age) {
                    voice = voices[i];
                    logger.debug("voice found : " + voice.getName());
                    break;
                }
            }
            if (voice == null) {
                for (int i = 0; i < voices.length; i++) {
                    if (voices[i].getGender() == gender) {
                        voice = voices[i];
                        logger.debug("voice found : " + voice.getName());
                        break;
                    }
                }
        	if (voice == null) {
                    for (int i = 0; i < voices.length; i++) {
                        if (voices[i].getAge() == age) {
                            voice = voices[i];
                            logger.debug("voice found : " + voice.getName());
                            break;
                        }
                    }
                    if (voice == null) {
                        return false;
                    }
        	}
            }
	}

	if (isPaused()) {
	    resume();
	    synthe.cancel();
	}
	SynthesizerProperties syntheProp = synthe.getSynthesizerProperties();
	try {
	    syntheProp.setVoice(voice);

	    float pitch = this.getPitch();
	    SynthesisEvent pitchEvent = new SynthesisEvent(this);
	    pitchEvent.setPitch(pitch);
	    this.firePitchChanged(pitchEvent);

	    float pitchRange = this.getPitchRange();
	    SynthesisEvent synthesisEvent = new SynthesisEvent(this);
	    synthesisEvent.setPitchRange(pitchRange);
	    this.firePitchRangeChanged(synthesisEvent);
	    
	    SynthesisEvent voiceEvent = new SynthesisEvent(this);
	    selectedVoice.setName(voice.getName());
	    selectedVoice.setAge(voice.getAge());
	    selectedVoice.setGender(voice.getGender());
	    voiceEvent.setVoice(selectedVoice);
	    this.fireVoiceChanged(voiceEvent);
	    
	    return true;
	} catch (PropertyVetoException e) {
	    logger.error("impossible to select the voice " + selectedVoice, e);
	    return false;
	}
    }

    /**
     * return the language of the synthesizer
     * @return the engine locale
     */
    public Locale getEngineLocale() {
	EngineModeDesc engineDesc = synthe.getEngineModeDesc();
	return engineDesc.getLocale();
    }

    /**
     * compare the language of the engine with the language in parameter
     * @param locale a locale
     * @return true if equal, false otherwise
     */
    protected boolean matchEngineLanguage(Locale locale) {
	Locale engineLocale = getEngineLocale();
	String engineLanguage = engineLocale.getLanguage();
	String otherLanguage = locale.getLanguage();
	if (engineLanguage.equals(otherLanguage)) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Prononce le texte en paramètre avec le moteur et la voix choisie
     * @param text texte à prononcer
     */
    public void play(String text) {
	try {
	    synthe.speakPlainText(text, null);
	} catch (EngineStateError e) {
	    logger.error("engine state error : " + e.getMessage());
	}
    }

    /**
     * Stop the current speech : clear the synthesizer queue and set the
     * flag to stop the thread
     */
    public void stop() {
	stopped = true;
	
	if (isPaused()) {
	    resume();
	}
	
	try {
	    synthe.cancel();
	} catch (EngineStateError e) {
	    logger.error("engine state error : " + e.getMessage());
	} catch (Exception e) {
	    // a nullpointerexception occurs sometimes with FreeTTS during the start
	    logger.error(e);
	}
	if (document != null) {
	    document.setIndex(0);
	}
	
	PlayEvent event = new PlayEvent(this);
	fireReadingStoppedEvent(event);
    }

    /**
     * Pause the current speech : set a flag and pause the engine
     */
    public void pause() {
	synchronized (listener) {
	    paused = true;
	}
	try {
	    synthe.pause();
	} catch (EngineStateError e) {
	    logger.error("engine state error : " + e.getMessage());
	}
	PlayEvent event = new PlayEvent(this);
	fireReadingPausedEvent(event);
    }

    /**
     * Resume the current speech : reset a flag and resume the engine
     */
    public void resume() {
	synchronized (listener) {
	    paused = false;
	    listener.notify();
	}
	try {
	    synthe.resume();
	} catch (EngineStateError e) {
	    logger.error("engine state error while resuming : " + e.getMessage());
	} catch (AudioException e) {
	    logger.error("audio : " + e.getMessage());
	}
	PlayEvent event = new PlayEvent(this);
	fireReadingResumedEvent(event);
    }

    /**
     * 
     * @see org.tramper.player.Player#isRunning()
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
     * read the next speakable
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
	
	try {
	    synthe.cancel();
	} catch (EngineStateError e) {
	    logger.error("engine state error : " + e.getMessage());
	}
    }

    /**
     * Read the previous speakable
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
	
	try {
	    synthe.cancel();
	} catch (EngineStateError e) {
	    logger.error("engine state error : " + e.getMessage());
	}
    }

    /**
     * 
     * @see org.tramper.synthesizer.SpeechSynthesizer#getEngineModeDesc()
     */
    public EngineModeDesc getEngineModeDesc() {
	return synthe.getEngineModeDesc();
    }
    
    /**
     * 
     * @see org.tramper.synthesizer.SpeechSynthesizer#getVoiceDesc()
     */
    public VoiceDesc getVoiceDesc() {
	VoiceDesc voiceDesc = new VoiceDesc();
	SynthesizerProperties syntheProp = synthe.getSynthesizerProperties();
	Voice voice = syntheProp.getVoice();
	voiceDesc.setName(voice.getName());
	voiceDesc.setAge(voice.getAge());
	voiceDesc.setGender(voice.getGender());
	return voiceDesc;
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
    
    protected void fireVolumeChangedEvent(PlayEvent event) {
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

    public void addPlayListener(PlayListener listener) {
	if (!playListener.contains(listener)) {
	    playListener.add(listener);
	}
    }

    public void removePlayListener(PlayListener listener) {
	playListener.remove(listener);
    }

    public int getSpeed() throws PlayException {
	SynthesizerProperties prop = (SynthesizerProperties) synthe.getEngineProperties();
	return (int) prop.getSpeakingRate()/4;
    }

    public void setSpeed(int sampleRate) {
	SynthesizerProperties prop = (SynthesizerProperties) synthe.getEngineProperties();
	try {
	    prop.setSpeakingRate((float) sampleRate*4);
	    
	    PlayEvent event = new PlayEvent(this);
	    event.setNewValue(sampleRate);
	    fireSampleRateChangedEvent(event);
	} catch (PropertyVetoException pe) {
	    logger.error("Bad speaking rate value : " + pe.getMessage());
	}
    }

    public int getVolume() throws PlayException {
	SynthesizerProperties prop = (SynthesizerProperties) synthe.getEngineProperties();
	return (int) prop.getVolume()*100;
    }

    public void setVolume(int volume) {
	SynthesizerProperties prop = (SynthesizerProperties) synthe.getEngineProperties();
	try {
	    prop.setVolume((float) volume / 100);
	    
	    PlayEvent event = new PlayEvent(this);
	    event.setNewValue(volume);
	    fireVolumeChangedEvent(event);
	} catch (PropertyVetoException pe) {
	    logger.error("Bad volume value : " + pe.getMessage());
	}
    }

    /**
     * 
     * @see org.tramper.synthesizer.SpeechSynthesizer#getPitch()
     */
    public float getPitch() {
	SynthesizerProperties prop = (SynthesizerProperties) synthe.getEngineProperties();
	return prop.getPitch()*100/300;
    }

    /**
     * 
     * @see org.tramper.synthesizer.SpeechSynthesizer#setPitch(float)
     */
    public void setPitch(float pitch) {
	SynthesizerProperties prop = (SynthesizerProperties) synthe.getEngineProperties();
	try {
	    prop.setPitch(pitch/100*300);

	    SynthesisEvent pitchEvent = new SynthesisEvent(this);
	    pitchEvent.setPitch(pitch);
	    this.firePitchChanged(pitchEvent);

	} catch (PropertyVetoException e) {
	    logger.error("Bad pitch value : " + e.getMessage());
	}
    }

    /**
     * 
     * @see org.tramper.synthesizer.SpeechSynthesizer#getPitchRange()
     */
    public float getPitchRange() {
	SynthesizerProperties prop = (SynthesizerProperties) synthe.getEngineProperties();
	float pitch = prop.getPitch();
	return prop.getPitchRange()*100/(pitch*80/100);
    }

    /**
     * 
     * @see org.tramper.synthesizer.SpeechSynthesizer#setPitchRange(float)
     */
    public void setPitchRange(float pitchRange) {
	SynthesizerProperties prop = (SynthesizerProperties) synthe.getEngineProperties();
	float pitch = prop.getPitch();
	try {
	    prop.setPitchRange(pitchRange*(pitch*80/100)/100);

	    SynthesisEvent synthesisEvent = new SynthesisEvent(this);
	    synthesisEvent.setPitchRange(pitchRange);
	    this.firePitchRangeChanged(synthesisEvent);
	    
	} catch (PropertyVetoException e) {
	    logger.error("Bad pitch range value : " + e.getMessage());
	}
    }

    /**
     * Unload the current synthesizer
     */
    public void unload() {
	if (synthe != null) {
	    try {
		synthe.deallocate();
		synthe.waitEngineState(Synthesizer.DEALLOCATED);
	    } catch (EngineException e) {
		logger.error("engine can't be deallocated : " + e.getMessage());
	    } catch (EngineStateError e) {
		logger.error("engine is not in the right state : " + e.getMessage());
	    } catch (IllegalArgumentException e) {
		logger.error("wrong waiting state : " + e.getMessage());
	    } catch (InterruptedException e) {
		logger.error("deallocation interrupted : " + e.getMessage());
	    }
	}
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
     * To overload in the subclass
     * @see org.tramper.player.Player#setOutput(java.io.File)
     */
    public void setOutput(File aFile) {
    }

    /**
     * To overload in the subclass
     * @see org.tramper.player.Player#setOutput()
     */
    public void setOutput() {
    }

    /**
     * 
     * @return
     */
    public List<String> getRenderings() {
        List<String> renderings = new ArrayList<String>();
        renderings.add("document");
        renderings.add("links");
        renderings.add("forms");
        return renderings;
    }
    
    /**
     * Unload the engine before destroying the oject
     */
    protected void finalize() throws Throwable {
	unload();
    }

    /**
     * @return document.
     */
    public SimpleDocument getDocument() {
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
     * @see org.tramper.doc.DocumentListener#documentActivated(org.tramper.doc.DocumentEvent)
     */
    public void documentActivated(DocumentEvent event) {
    }

    /**
     * 
     * @see org.tramper.doc.DocumentListener#documentDeactivated(org.tramper.doc.DocumentEvent)
     */
    public void documentDeactivated(DocumentEvent event) {
    }
}
