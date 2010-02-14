package org.tramper.recognizer;

import java.applet.AudioClip;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineErrorEvent;
import javax.speech.EngineEvent;
import javax.speech.EngineException;
import javax.speech.EngineList;
import javax.speech.EngineModeDesc;
import javax.speech.EngineStateError;
import javax.speech.recognition.DictationGrammar;
import javax.speech.recognition.FinalDictationResult;
import javax.speech.recognition.FinalResult;
import javax.speech.recognition.FinalRuleResult;
import javax.speech.recognition.Grammar;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.RecognizerEvent;
import javax.speech.recognition.RecognizerListener;
import javax.speech.recognition.RecognizerModeDesc;
import javax.speech.recognition.RecognizerProperties;
import javax.speech.recognition.ResultEvent;
import javax.speech.recognition.ResultListener;
import javax.speech.recognition.ResultToken;
import javax.speech.recognition.SpeakerManager;
import javax.speech.recognition.SpeakerProfile;
import javax.swing.Action;

import org.apache.log4j.Logger;
import org.tramper.action.LoadAboutAction;
import org.tramper.action.LoadFavoritesAction;
import org.tramper.action.LoadHelpAction;
import org.tramper.action.LoadHistoryAction;
import org.tramper.action.NextPlayAction;
import org.tramper.action.PausePlayAction;
import org.tramper.action.PreviousPlayAction;
import org.tramper.action.QuitAction;
import org.tramper.action.ResumePlayAction;
import org.tramper.action.StartPlayAction;
import org.tramper.action.StopPlayAction;
import org.tramper.recognizer.RecognitionException;

/**
 * A speech recorder with Java Speech API
 * @author Paul-Emile
 */
public class JSAPISpeechRecognizer implements SpeechRecognizer, ResultListener, RecognizerListener {
    /** logger */
    private static Logger logger = Logger.getLogger(JSAPISpeechRecognizer.class);
    /** JSAPI recognizer */
    private Recognizer recognizer;
    /** recording listeners list */
    private List<RecognitionListener> listener;
    /** tag / action hashtable */
    private Map<String, Action> actionMap;
    
    /**
     * load an engine
     * @throws RecognitionException
     */
    public JSAPISpeechRecognizer() throws RecognitionException {
        listener = new ArrayList<RecognitionListener>();
        actionMap = new HashMap<String, Action>();
        actionMap.put("favorites", LoadFavoritesAction.getInstance());
        actionMap.put("history", LoadHistoryAction.getInstance());
        actionMap.put("help", LoadHelpAction.getInstance());
        actionMap.put("about", LoadAboutAction.getInstance());
        actionMap.put("play", StartPlayAction.getInstance());
        actionMap.put("stop", StopPlayAction.getInstance());
        actionMap.put("resume", ResumePlayAction.getInstance());
        actionMap.put("pause", PausePlayAction.getInstance());
        actionMap.put("next", NextPlayAction.getInstance());
        actionMap.put("previous", PreviousPlayAction.getInstance());
        actionMap.put("quit", QuitAction.getInstance());
        load();
    }
    
    /**
     * 
     */
    protected void load() throws RecognitionException {
        // try to find an engine with the default locale
        Locale defaultLocale = Locale.getDefault();
        boolean engineFound = this.load(defaultLocale);
        // if not found, try to find an engine with the english locale
        if (!engineFound) {
            engineFound = this.load(Locale.ENGLISH);
        }
        // if not found, load the first engine available
        if (!engineFound) {
            EngineList list = Central.availableRecognizers(null);
            if (list != null && list.size() > 0) {
                RecognizerModeDesc eng = (RecognizerModeDesc) list.get(0);
                Locale engineLocale = eng.getLocale();
                loadEngine(eng.getEngineName(), eng.getModeName(), engineLocale);
                SpeakerProfile[] speakerProfiles = eng.getSpeakerProfiles();
                if (speakerProfiles != null && speakerProfiles.length > 0) {
                    loadSpeakerProfile(speakerProfiles[0].getId());
                }
            }
        }
    }

    /**
     * load a recognizer with the following locale if possible
     * @param locale
     * @return true if loaded, false otherwise
     */
    protected boolean load(Locale locale) throws RecognitionException {
        EngineList list = Central.availableRecognizers(null);
        for (int i=0; i<list.size(); i++) {
            RecognizerModeDesc eng = (RecognizerModeDesc) list.get(i);
            Locale engineLocale = eng.getLocale();
            if (locale.getLanguage().equals(engineLocale.getLanguage())) {
                loadEngine(eng.getEngineName(), eng.getModeName(), engineLocale);
                SpeakerProfile[] speakerProfiles = eng.getSpeakerProfiles();
                if (speakerProfiles != null && speakerProfiles.length > 0) {
                    loadSpeakerProfile(speakerProfiles[0].getId());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Load an engine using it's name, mode and locale. Some engines can have
     * the same name and/or mode.
     * @param name engine name
     * @param mode engine mode
     * @param locale engine locale
     * @throws RecognitionException
     */
    protected void loadEngine(String name, String mode, Locale locale) throws RecognitionException {
        RecognizerModeDesc required = null;

        EngineList list = Central.availableRecognizers(null);
        for (int i = 0; i < list.size(); i++) {
            RecognizerModeDesc eng = (RecognizerModeDesc) list.get(i);
            String engineName = eng.getEngineName();
            String modeName = eng.getModeName();
            Locale engineLocale = eng.getLocale();
            if (engineName.equals(name) && modeName.equals(mode) && engineLocale.equals(locale)) {
                required = eng;
                break;
            }
        }

        try {
            recognizer = Central.createRecognizer(required);
            recognizer.addEngineListener(this);
            recognizer.addResultListener(this);
            recognizer.allocate();
            recognizer.waitEngineState(Recognizer.ALLOCATED);

            RecognizerModeDesc recoMode = (RecognizerModeDesc)recognizer.getEngineModeDesc();
            Boolean dictationGrammarSupported = recoMode.isDictationGrammarSupported();
            if (dictationGrammarSupported.equals(Boolean.FALSE)) {
                recoMode.setDictationGrammarSupported(Boolean.TRUE);
            }

            // RecognizerProperties prop = recognizer.getRecognizerProperties();
            // prop.setTrainingProvided(true);

            // choose a default grammar
            loadGrammar(null);
            recognizer.waitEngineState(Recognizer.LISTENING);

            RecognitionEvent event = new RecognitionEvent(this);
            EngineModeDesc engineDesc = recognizer.getEngineModeDesc();
            event.setEngine(engineDesc);
            this.fireEngineChanged(event);
        } catch (IllegalArgumentException e) {
            logger.error(e);
            throw new RecognitionException(e.getMessage());
        } catch (EngineException e) {
            logger.error(e);
            throw new RecognitionException(e.getMessage());
        } catch (SecurityException e) {
            logger.error(e);
            throw new RecognitionException(e.getMessage());
        } catch (EngineStateError e) {
            logger.error(e);
            throw new RecognitionException(e.getMessage());
        } catch (InterruptedException e) {
            logger.error(e);
            throw new RecognitionException(e.getMessage());
        }
    }

    /**
     * Load a speaker profile in the engine
     * 
     * @param selectedProfileId
     */
    protected void loadSpeakerProfile(String selectedProfileId) {
        logger.debug("try load speaker profile " + selectedProfileId);
        SpeakerProfile aProfile = null;
        RecognizerModeDesc desc = (RecognizerModeDesc) recognizer.getEngineModeDesc();
        SpeakerProfile[] speakerProfiles = desc.getSpeakerProfiles();
        for (int i = 0; i < speakerProfiles.length; i++) {
            if (speakerProfiles[i].getId().equals(selectedProfileId)) {
                aProfile = speakerProfiles[i];
                logger.debug("speaker profile found : " + aProfile.getId());
                break;
            }
        }

        SpeakerManager speakerMgr = recognizer.getSpeakerManager();
        speakerMgr.setCurrentSpeaker(aProfile);
    }
    
    /**
     * Load the specified grammar in the current engine, or the dictation grammar if null
     * @param grammarName grammar file name without the .gram extension, or null for the dictation grammar
     * @throws RecognitionException
     */
    public void loadGrammar(String grammarName) throws RecognitionException {
        Grammar grammar = null;
        if (grammarName == null) {
            grammar = recognizer.getDictationGrammar(null);
        }
        else {
            URL urlGrammmar = JSAPISpeechRecognizer.class.getResource("../../..");
            try {
                grammar = recognizer.loadJSGF(urlGrammmar, "org.tramper.recognizer."+grammarName);
                
            } catch (GrammarException e) {
                logger.error(e);
                throw new RecognitionException(e.getMessage());
            } catch (MalformedURLException e) {
                logger.error(e);
                throw new RecognitionException(e.getMessage());
            } catch (IOException e) {
                logger.error(e);
                throw new RecognitionException(e.getMessage());
            }
        }
        
        grammar.setEnabled(true);
        
        //Tell recognizer to commit changes in grammars
        //recognizer.suspend();
        try {
            recognizer.commitChanges();
        } catch (GrammarException e) {
            logger.error(e);
            throw new RecognitionException(e.getMessage());
        } catch (EngineStateError e) {
            logger.error(e);
            throw new RecognitionException(e.getMessage());
        }
    }
    
    /**
     * Start recording
     */
    public void record() {
        try {
            // Request focus of microphone away for other apps.
            recognizer.requestFocus();
            // Start listening
            recognizer.resume();
        } catch (EngineStateError e) {
            logger.error(e);
        }
         catch (AudioException e) {
             logger.error(e);
        }
        catch (IllegalArgumentException e) {
            logger.error(e);
        }
    }

    public void pause() {
        try {
            recognizer.pause();
        } catch (EngineStateError e) {
            logger.error("engine state error : " + e.getMessage());
        }
    }

    public void resume() {
        try {
            recognizer.resume();
        } catch (EngineStateError e) {
            logger.error("engine state error while resuming : "
                    + e.getMessage());
        } catch (AudioException e) {
            logger.error("audio : " + e.getMessage());
        }
    }

    public void stop() {
        try {
            recognizer.releaseFocus();
        } catch (EngineStateError e) {
            logger.error("engine state error : " + e.getMessage());
        }
    }

    /**
     * return the current engine locale
     * 
     * @see org.tramper.recognizer.SpeechRecognizer#getLocale()
     */
    public Locale getLocale() {
        EngineModeDesc engineDesc = recognizer.getEngineModeDesc();
        return engineDesc.getLocale();
    }

    /**
     * is the locale parameter matching the current engine locale ?
     * 
     * @see org.tramper.recognizer.SpeechRecognizer#matchEngineLanguage(java.util.Locale)
     */
    public boolean matchEngineLanguage(Locale locale) {
        Locale engineLocale = getLocale();
        String engineLanguage = engineLocale.getLanguage();
        String otherLanguage = locale.getLanguage();
        if (engineLanguage.equals(otherLanguage))
            return true;
        else
            return false;
    }

    /**
     * return the engine mode description
     * 
     * @see org.tramper.recognizer.SpeechRecognizer#getEngineModeDesc()
     */
    public EngineModeDesc getEngineModeDesc() {
        return recognizer.getEngineModeDesc();
    }
    
    /**
     * 
     * @see org.tramper.recognizer.SpeechRecognizer#setEngineModeDesc(javax.speech.EngineModeDesc)
     */
    public void setEngineModeDesc(EngineModeDesc engineMode) {
	EngineModeDesc currentMode = recognizer.getEngineModeDesc();

        try {
	    this.loadEngine(currentMode.getEngineName(), currentMode.getModeName(), currentMode.getLocale());
            Properties engineProp = this.getEngineProperties();
            RecognitionEvent recordEvent = new RecognitionEvent(this);
            recordEvent.setEngineProp(engineProp);
            this.fireEnginePropertiesChanged(recordEvent);
            List<SpeakerProfile> profiles = this.listAvailableSpeakerProfiles();
            RecognitionEvent voiceEvent = new RecognitionEvent(this);
            voiceEvent.setSpeakerProfiles(profiles);
            this.fireSpeakerProfilesListChanged(voiceEvent);
	} catch (RecognitionException e) {
	    logger.error(e);
	}
    }
    
    /**
     * return the current engine properties
     * 
     * @see org.tramper.recognizer.SpeechRecognizer#getEngineProperties()
     */
    public Properties getEngineProperties() {
        Properties engineProp = new Properties();

        RecognizerProperties prop = (RecognizerProperties) recognizer.getEngineProperties();
        engineProp.setProperty("confidence", String.valueOf(prop.getConfidenceLevel()*100));
        engineProp.setProperty("sensitivity", String.valueOf(prop.getSensitivity()*100));
        engineProp.setProperty("speedVsAccuracy", String.valueOf(prop.getSpeedVsAccuracy()*100));

        return engineProp;
    }

    /**
     * 
     * @see org.tramper.recognizer.SpeechRecognizer#setEngineProperties(java.util.Properties)
     */
    public void setEngineProperties(Properties prop) {
        RecognizerProperties recoProp = (RecognizerProperties) recognizer.getEngineProperties();
        try {
            String confidence = prop.getProperty("confidence");
            if (confidence != null) {
                recoProp.setConfidenceLevel(Float.valueOf(confidence).floatValue()/100);
            }
            String sensitivity = prop.getProperty("sensitivity");
            if (sensitivity != null) {
                recoProp.setConfidenceLevel(Float.valueOf(sensitivity).floatValue()/100);
            }
            String speedVsAccuracy = prop.getProperty("speedVsAccuracy");
            if (speedVsAccuracy != null) {
                recoProp.setConfidenceLevel(Float.valueOf(speedVsAccuracy).floatValue()/100);
            }
        } catch (PropertyVetoException e) {
            logger.error(e);
        }
    }

    /**
     * 
     * @see org.tramper.recognizer.SpeechRecognizer#setSpeakerProfile(javax.speech.recognition.SpeakerProfile)
     */
    public void setSpeakerProfile(SpeakerProfile speakerProfile) {
        this.loadSpeakerProfile(speakerProfile.getId());
        Properties engineProp = this.getEngineProperties();
        RecognitionEvent recordEvent = new RecognitionEvent(this);
        recordEvent.setEngineProp(engineProp);
        this.fireEnginePropertiesChanged(recordEvent);
    }

    /**
     * list te available engines
     * 
     * @see org.tramper.recognizer.SpeechRecognizer#listAvailableEngines()
     */
    public List<EngineModeDesc> listAvailableEngines() {
        List<EngineModeDesc> availableEngines = new ArrayList<EngineModeDesc>();

        EngineList list = Central.availableRecognizers(null);
        logger.debug(list.size() + " engines available");
        for (int i = 0; i < list.size(); i++) {
            EngineModeDesc engineDesc = (EngineModeDesc)list.get(i);
            availableEngines.add(engineDesc);
        }

        return availableEngines;
    }

    /**
     * list the available speaker profiles for the current engine
     * 
     * @see org.tramper.recognizer.SpeechRecognizer#listAvailableSpeakerProfiles()
     */
    public List<SpeakerProfile> listAvailableSpeakerProfiles() {
        List<SpeakerProfile> availableSpeakerProfiles = new ArrayList<SpeakerProfile>();

        RecognizerModeDesc desc = (RecognizerModeDesc) recognizer.getEngineModeDesc();
        SpeakerProfile[] speakerProfiles = desc.getSpeakerProfiles();
        logger.debug(speakerProfiles.length + " speaker profiles available");
        for (int i = 0; i < speakerProfiles.length; i++) {
            availableSpeakerProfiles.add(speakerProfiles[i]);
        }

        return availableSpeakerProfiles;
    }

    /**
     * add a recording listener
     * 
     * @param aListener
     */
    public void addRecordingListener(RecognitionListener aListener) {
        if (this.listener.contains(aListener) == false)
            this.listener.add(aListener);
    }

    /**
     * remove a recording listener from the list
     * 
     * @param aListener
     */
    public void removeRecordingListener(RecognitionListener aListener) {
        this.listener.remove(aListener);
    }

    /**
     * fire a engine properties changed event
     * 
     * @param event
     */
    protected void fireEnginePropertiesChanged(RecognitionEvent event) {
        for (int i = 0; i < listener.size(); i++) {
            RecognitionListener aListener = listener.get(i);
            aListener.enginePropertiesChanged(event);
        }
    }

    /**
     * fire a speaker profiles list changed event
     * 
     * @param event
     */
    protected void fireSpeakerProfilesListChanged(RecognitionEvent event) {
        for (int i = 0; i < listener.size(); i++) {
            RecognitionListener aListener = listener.get(i);
            aListener.speakerProfilesListChanged(event);
        }
    }

    /**
     * fire a engines list changed event
     * 
     * @param event
     */
    protected void fireEngineChanged(RecognitionEvent event) {
        for (int i = 0; i < listener.size(); i++) {
            RecognitionListener aListener = listener.get(i);
            aListener.engineChanged(event);
        }
    }

    /**
     * fire a engine state changed event
     * 
     * @param event
     */
    protected void fireEngineStateChanged(RecognitionEvent event) {
        for (int i = 0; i < listener.size(); i++) {
            RecognitionListener aListener = listener.get(i);
            aListener.engineStateChanged(event);
        }
    }

    public void audioReleased(ResultEvent arg0) {
    }

    public void trainingInfoReleased(ResultEvent arg0) {
    }

    public void grammarFinalized(ResultEvent arg0) {
    }

    public void resultCreated(ResultEvent arg0) {
    }

    public void resultUpdated(ResultEvent arg0) {
    }

    public void resultAccepted(ResultEvent e) {
        Object source = e.getSource();
        FinalResult result = (FinalResult) source;

        ResultToken[] bestTokens = result.getBestTokens();
        for (int i = 0; i < bestTokens.length; i++) {
            ResultToken aToken = bestTokens[i];
            String writtenText = aToken.getWrittenText();
            logger.debug("best result : " + writtenText);
        }

        boolean audioAvailable = result.isAudioAvailable();
        if (audioAvailable) {
            AudioClip audioClip = result.getAudio();
            audioClip.play();
            result.releaseAudio();
        }

        Grammar grammar = result.getGrammar();
        boolean trainingInfoAvailable = result.isTrainingInfoAvailable();
        if (trainingInfoAvailable) {
            if (grammar instanceof DictationGrammar) {
                ResultToken[][] alternativeTokens = ((FinalDictationResult) result).getAlternativeTokens(bestTokens[0], bestTokens[bestTokens.length - 1], 5);
                if (alternativeTokens != null) {
                    for (int i = 0; i < alternativeTokens.length; i++) {
                        for (int j = 0; j < alternativeTokens[i].length; j++) {
                            String spokenText = alternativeTokens[i][j].getSpokenText();
                            logger.debug("alternative result with dictation grammar : " + spokenText);
                        }
                    }
                }
            } else {
                int numberGuesses = ((FinalRuleResult) result).getNumberGuesses();
                for (int j = 0; j < numberGuesses; j++) {
                    ResultToken[] alternativeTokens = ((FinalRuleResult) result).getAlternativeTokens(j);
                    if (alternativeTokens != null) {
                        for (int i = 0; i < alternativeTokens.length; i++) {
                            String spokenText = alternativeTokens[i].getSpokenText();
                            logger.debug("alternative result with rule grammar : " + spokenText);
                        }
                    }
                }
            }
            result.releaseTrainingInfo();
        }
    }

    public void resultRejected(ResultEvent e) {
        Object source = e.getSource();
        FinalResult result = (FinalResult) source;

        ResultToken[] bestTokens = result.getBestTokens();
        for (int i = 0; i < bestTokens.length; i++) {
            ResultToken aToken = bestTokens[i];
            String writtenText = aToken.getWrittenText();
            logger.debug("rejected best result : " + writtenText);
        }

        boolean audioAvailable = result.isAudioAvailable();
        if (audioAvailable) {
            AudioClip audioClip = result.getAudio();
            audioClip.play();
            result.releaseAudio();
        }

        Grammar grammar = result.getGrammar();
        boolean trainingInfoAvailable = result.isTrainingInfoAvailable();
        if (trainingInfoAvailable) {
            if (grammar instanceof DictationGrammar) {
                ResultToken[][] alternativeTokens = ((FinalDictationResult) result).getAlternativeTokens(bestTokens[0], bestTokens[bestTokens.length - 1], 5);
                if (alternativeTokens != null) {
                    for (int i = 0; i < alternativeTokens.length; i++) {
                        for (int j = 0; j < alternativeTokens[i].length; j++) {
                            String spokenText = alternativeTokens[i][j].getSpokenText();
                            logger.debug("rejected alternative result with dictation grammar : "+ spokenText);
                        }
                    }
                }
            } else {
                int numberGuesses = ((FinalRuleResult) result).getNumberGuesses();
                for (int j = 0; j < numberGuesses; j++) {
                    ResultToken[] alternativeTokens = ((FinalRuleResult) result).getAlternativeTokens(j);
                    if (alternativeTokens != null) {
                        for (int i = 0; i < alternativeTokens.length; i++) {
                            String spokenText = alternativeTokens[i].getSpokenText();
                            logger.debug("rejected alternative result with rule grammar : "+ spokenText);
                        }
                    }
                }
            }
            result.releaseTrainingInfo();
        }
    }

    public void changesCommitted(RecognizerEvent e) {
        RecognitionEvent recordEvent = new RecognitionEvent(this);
        recordEvent.setEngineState(RecognitionEvent.NOT_LISTENING);
        this.fireEngineStateChanged(recordEvent);
        logger.debug("recognizer changes committed");
    }

    public void focusGained(RecognizerEvent e) {
        RecognitionEvent recordEvent = new RecognitionEvent(this);
        recordEvent.setEngineState(RecognitionEvent.LISTENING);
        this.fireEngineStateChanged(recordEvent);
        logger.debug("recognizer focus gained");
    }

    public void focusLost(RecognizerEvent e) {
        RecognitionEvent recordEvent = new RecognitionEvent(this);
        recordEvent.setEngineState(RecognitionEvent.NOT_LISTENING);
        this.fireEngineStateChanged(recordEvent);
        logger.debug("recognizer focus lost");
    }

    public void recognizerProcessing(RecognizerEvent e) {
        RecognitionEvent recordEvent = new RecognitionEvent(this);
        recordEvent.setEngineState(RecognitionEvent.LISTENING);
        this.fireEngineStateChanged(recordEvent);
        logger.debug("recognizer processing");
    }
    
    /**
     * Method from Recognizer listener added by Cloud Garden : not in JSAPI but necessary
     * @param e
     */
    public void recognizerListening(RecognizerEvent e) {
        RecognitionEvent recordEvent = new RecognitionEvent(this);
        recordEvent.setEngineState(RecognitionEvent.LISTENING);
        this.fireEngineStateChanged(recordEvent);
        logger.debug("recognizer listening");
    }

    public void recognizerSuspended(RecognizerEvent e) {
        RecognitionEvent recordEvent = new RecognitionEvent(this);
        recordEvent.setEngineState(RecognitionEvent.NOT_LISTENING);
        this.fireEngineStateChanged(recordEvent);
        logger.debug("recognizer suspended");
    }

    public void engineAllocated(EngineEvent e) {
        RecognitionEvent recordEvent = new RecognitionEvent(this);
        recordEvent.setEngineState(RecognitionEvent.NOT_LISTENING);
        this.fireEngineStateChanged(recordEvent);
        logger.debug("recognizer allocated");
    }

    public void engineAllocatingResources(EngineEvent e) {
        RecognitionEvent recordEvent = new RecognitionEvent(this);
        recordEvent.setEngineState(RecognitionEvent.NOT_LISTENING);
        this.fireEngineStateChanged(recordEvent);
        logger.debug("recognizer allocating resources");
    }

    public void engineDeallocated(EngineEvent e) {
        RecognitionEvent recordEvent = new RecognitionEvent(this);
        recordEvent.setEngineState(RecognitionEvent.NOT_LISTENING);
        this.fireEngineStateChanged(recordEvent);
        logger.debug("recognizer deallocated");
    }

    public void engineDeallocatingResources(EngineEvent e) {
        RecognitionEvent recordEvent = new RecognitionEvent(this);
        recordEvent.setEngineState(RecognitionEvent.NOT_LISTENING);
        this.fireEngineStateChanged(recordEvent);
        logger.debug("recognizer deallocating resources");
    }

    public void engineError(EngineErrorEvent e) {
        RecognitionEvent recordEvent = new RecognitionEvent(this);
        recordEvent.setEngineState(RecognitionEvent.NOT_LISTENING);
        this.fireEngineStateChanged(recordEvent);
        logger.debug("recognizer error");
    }

    public void enginePaused(EngineEvent e) {
        RecognitionEvent recordEvent = new RecognitionEvent(this);
        recordEvent.setEngineState(RecognitionEvent.NOT_LISTENING);
        this.fireEngineStateChanged(recordEvent);
        logger.debug("recognizer paused");
    }

    public void engineResumed(EngineEvent e) {
        RecognitionEvent recordEvent = new RecognitionEvent(this);
        recordEvent.setEngineState(RecognitionEvent.LISTENING);
        this.fireEngineStateChanged(recordEvent);
        logger.debug("recognizer resumed");
    }

    /**
     * 
     * @see org.tramper.recognizer.SpeechRecognizer#listAvailableMicrophones()
     */
    public List<Mixer.Info> listAvailableMicrophones() {
        List<Mixer.Info> microphones = new ArrayList<Mixer.Info>();
        
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        for (int i=0; i<mixerInfo.length; i++) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo[i]);
            boolean microphoneSupported = mixer.isLineSupported(Port.Info.MICROPHONE);
            if (microphoneSupported) {
                microphones.add(mixerInfo[i]);
            }
        }
        
        return microphones;
    }

    /**
     * Does nothing
     * @see org.tramper.recognizer.SpeechRecognizer#setMicrophone(Mixer.Info)
     */
    public void setMicrophone(Mixer.Info mixer) {
    }

    /**
     * 
     * @see org.tramper.recognizer.SpeechRecognizer#isRecording()
     */
    public boolean isRecording() {
	return recognizer.testEngineState(Recognizer.FOCUS_ON);
    }

    /**
     * Perform an action 
     * @param tag the key of the action
     */
    public void performAction(String tag) {
	Action action = (Action)actionMap.get(tag);
	ActionEvent e = new ActionEvent(this, 0, tag);
	action.actionPerformed(e);
    }
    
    /**
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        super.finalize();
        recognizer.deallocate();
    }
}
