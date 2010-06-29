package org.tramper.recognizer;

import java.awt.event.ActionEvent;
import java.io.File;
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
import javax.speech.EngineModeDesc;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;
import javax.speech.recognition.SpeakerProfile;
import javax.swing.Action;

import org.apache.log4j.Logger;
import org.tramper.action.AddFavoriteAction;
import org.tramper.action.BackHistoryAction;
import org.tramper.action.ForwardHistoryAction;
import org.tramper.action.LoadAboutAction;
import org.tramper.action.LoadFavoritesAction;
import org.tramper.action.LoadHelpAction;
import org.tramper.action.LoadHistoryAction;
import org.tramper.action.NextPlayAction;
import org.tramper.action.OpenDisplayAction;
import org.tramper.action.OpenRecognizerAction;
import org.tramper.action.OpenSynthesizerAction;
import org.tramper.action.PausePlayAction;
import org.tramper.action.PreviousPlayAction;
import org.tramper.action.QuitAction;
import org.tramper.action.RemoveFavoriteAction;
import org.tramper.action.ResumePlayAction;
import org.tramper.action.StartPlayAction;
import org.tramper.action.StopPlayAction;
import org.tramper.gui.GraphicalUserInterface;
import org.tramper.ui.UserInterfaceFactory;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.jsapi.JSGFGrammar;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.recognizer.RecognizerState;
import edu.cmu.sphinx.recognizer.StateListener;
import edu.cmu.sphinx.result.ConfidenceResult;
import edu.cmu.sphinx.result.ConfidenceScorer;
import edu.cmu.sphinx.result.Path;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;

/**
 * A speech recorder with Sphinx4 API
 * @author Paul-Emile
 */
public class SphinxSpeechRecognizer implements StateListener, SpeechRecognizer, Runnable {
    /** logger */
    private static Logger logger = Logger.getLogger(SphinxSpeechRecognizer.class);
    /** configuration manager */
    protected ConfigurationManager confMgr;
    /** a recognizer */
    private Recognizer recognizer;
    /** a microphone */
    private Microphone microphone;
    /** pause flag */
    private boolean paused;
    /** recording listeners list */
    private List<RecognitionListener> listener;
    /** JSGF grammar */
    private JSGFGrammar jsgfGrammar;
    /** Confidence scorer */
    private ConfidenceScorer confidenceScorer;
    /** tag / action hashtable */
    private Map<String, Action> actionMap;
    
    /**
     * 
     */
    public SphinxSpeechRecognizer() throws RecognitionException {
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
        actionMap.put("addFavorite", AddFavoriteAction.getInstance());
        actionMap.put("removeFavorite", RemoveFavoriteAction.getInstance());
        actionMap.put("backHistory", BackHistoryAction.getInstance());
        actionMap.put("forwardHistory", ForwardHistoryAction.getInstance());
        actionMap.put("openDisplay", OpenDisplayAction.getInstance());
        actionMap.put("openSynthesizer", new OpenSynthesizerAction());
        actionMap.put("openRecognizer", OpenRecognizerAction.getInstance());
        load();
    }
    
    /**
     * 
     * @see org.tramper.recognizer.SpeechRecognizer#load()
     */
    protected void load() throws RecognitionException {
        // try to find an engine with the default locale
        //Locale defaultLocale = Locale.getDefault();
        String userDir = System.getProperty("user.dir");
        String sep = System.getProperty("file.separator");
        String configFile = userDir + sep + "sphinx-recognizer.config.xml";
        this.load(configFile);
        // if not found, try to find an engine with the english locale
        // if not found, load the first engine available
    }
    
    /**
     * 
     */
    protected boolean load(String config) throws RecognitionException {
	URL url = null;
	try {
	    url = new File(config).toURI().toURL();
	} catch (MalformedURLException e1) {}
	
        try {
            confMgr = new ConfigurationManager(url);
            recognizer = (Recognizer) confMgr.lookup("recognizer");
            microphone = (Microphone) confMgr.lookup("microphone");
            jsgfGrammar = (JSGFGrammar) confMgr.lookup("jsgfGrammar");
            confidenceScorer = (ConfidenceScorer) confMgr.lookup("confidenceScorer");

            recognizer.addStateListener(this);
            /* allocate the resource necessary for the recognizer */
            recognizer.allocate();
            return true;
        } catch (PropertyException e) {
            logger.error(e);
            return false;
        }
    }

    /**
     * Start recording (with a thread)
     */
    public void record() {
        Thread aThread = new Thread(this);
        aThread.start();
    }
    
    /**
     * Record from a microphone
     * @see java.lang.Runnable#run()
     */
    public void run() {
        RuleGrammar ruleGrammar = jsgfGrammar.getRuleGrammar();
        GraphicalUserInterface gui = UserInterfaceFactory.getGraphicalUserInterface();
        
        // the microphone will keep recording until the program exits
        if (microphone.startRecording()) {
            logger.debug("Sphinx4 started");
            while (microphone.isRecording()) {
                synchronized (listener) {
                    if (paused) {
                        try {
                            listener.wait();
                        } catch (InterruptedException e) {
                            logger.warn("pause interrupted");
                        }
                    }
                }
                /* This method will return when the end of speech
                 * is reached. Note that the endpointer will determine
                 * the end of speech. */ 
                Result result = recognizer.recognize();
                
                if (result != null) {
                    String finalResultText = result.getBestFinalResultNoFiller();
                    logger.debug("best final result : "+finalResultText);
                    
                    ConfidenceResult cr = confidenceScorer.score(result);
                    Path best = cr.getBestHypothesis();
                    double confidence = best.getConfidence();
                    logger.debug("confidence : "+confidence);
                    double score = best.getScore();
                    logger.debug("score : "+score);
                    double linearConfidence = best.getLogMath().logToLinear((float)confidence);
                    logger.debug("linearConfidence : "+linearConfidence);
                    WordResult[] words = best.getWords();
                    for (int i = 0; i < words.length; i++) {
                        WordResult wr = (WordResult) words[i];
                        String word = wr.getPronunciation().getWord().getSpelling();
                        double wordConf = wr.getLogMath().logToLinear((float)wr.getConfidence());
                        logger.debug("word "+word+" "+wordConf);
                    }
                    boolean confirmed = false;
                    gui.confirmMessage("chooseRecognized", new Object[] {finalResultText});
                    if (confirmed) {
                        //look for possible tag(s) in the results
                        try {
                            RuleParse ruleParse = ruleGrammar.parse(finalResultText, null);
                            if (ruleParse != null) {
                                String[] tags = ruleParse.getTags();
                                for (int i = 0; i < tags.length; i++) {
                                    performAction(tags[i]);
                                }
                            }
                        } catch (GrammarException e) {
                            logger.warn("result parse failed");
                        }
                    }
                } else {
                    logger.warn("recognition failed");
                }
            }
            logger.debug("Sphinx4 stopped");
        } else {
            logger.error("Cannot start microphone");
        }
    }
    
    /**
     * pause the recognizer
     * @see org.tramper.recognizer.SpeechRecognizer#pause()
     */
    public void pause() {
        synchronized (listener) {
            paused = true;
        }
    }
    
    /**
     * resume the recognizer
     * @see org.tramper.recognizer.SpeechRecognizer#resume()
     */
    public void resume() {
        synchronized (listener) {
            paused = false;
            listener.notify();
        }
    }
    
    /**
     * Stop the thread recording from the microphone
     */
    public void stop() {
        microphone.stopRecording();
    }
    
    /**
     * determine if a microphone port is available 
     * @return true if a microphone port exists, false otherwise
     */
    public static boolean hasMicrophone() {
        boolean microphoneSupported = AudioSystem.isLineSupported(Port.Info.MICROPHONE);
        return microphoneSupported;
    }
    
    /**
     * List available mixer supporting microphone
     * @return list of Mixer.Info
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
            logger.debug(i+": "+mixerInfo[i].getName()+" support microphone: "+microphoneSupported);
        }
        
        return microphones;
    }
    
    /**
     * 
     * @see org.tramper.recognizer.SpeechRecognizer#setMicrophone(java.lang.String)
     */
    public void setMicrophone(Mixer.Info mixer) {
        PropertySheet prop = confMgr.getPropertySheet("microphone");
        
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        for (int i=0; i<mixerInfo.length; i++) {
            if (mixerInfo[i].equals(mixer)) {
                try {
        	    prop.setString("selectMixer", String.valueOf(i));
        	    microphone.newProperties(prop);
        	} catch (PropertyException e) {
        	    logger.error(e);
        	}
            }
        }
    }
    
    public void addRecordingListener(RecognitionListener aListener) {
        if (this.listener.contains(aListener) == false) {
            this.listener.add(aListener);
        }
    }

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

    /**
     * Returns US locale
     * @see org.tramper.recognizer.SpeechRecognizer#getLocale()
     */
    public Locale getLocale() {
        return Locale.US;
    }

    /**
     * 
     * @see org.tramper.recognizer.SpeechRecognizer#matchEngineLanguage(java.util.Locale)
     */
    public boolean matchEngineLanguage(Locale locale) {
        String engineLanguage = getLocale().getLanguage();
        String otherLanguage = locale.getLanguage();
        if (engineLanguage.equals(otherLanguage)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Returns a default engine mode description for Sphinx 4
     * @see org.tramper.recognizer.SpeechRecognizer#getEngineModeDesc()
     */
    public EngineModeDesc getEngineModeDesc() {
	EngineModeDesc engineMode = new EngineModeDesc();
	engineMode.setEngineName("Sphinx 4");
	engineMode.setLocale(getLocale());
	engineMode.setModeName("commands");
        return engineMode;
    }
    
    /**
     * Does nothing
     * @see org.tramper.recognizer.SpeechRecognizer#setEngineModeDesc(javax.speech.EngineModeDesc)
     */
    public void setEngineModeDesc(EngineModeDesc engineMode) {
    }
    
    /**
     * Returns null
     * @see org.tramper.recognizer.SpeechRecognizer#getEngineProperties()
     */
    public Properties getEngineProperties() {
        return null;
    }

    /**
     * Does nothing
     * @see org.tramper.recognizer.SpeechRecognizer#setEngineProperties(java.util.Properties)
     */
    public void setEngineProperties(Properties prop) {
    }

    /**
     * Does nothing
     * @see org.tramper.recognizer.SpeechRecognizer#setSpeakerProfile(javax.speech.recognition.SpeakerProfile)
     */
    public void setSpeakerProfile(SpeakerProfile speakerProfile) {
    }

    /**
     * Returns the default engine mode description for Sphinx 4
     * @see org.tramper.recognizer.SpeechRecognizer#listAvailableEngines()
     */
    public List<EngineModeDesc> listAvailableEngines() {
	List<EngineModeDesc> availableEngines = new ArrayList<EngineModeDesc>();
	
	EngineModeDesc engineMode = getEngineModeDesc();
	availableEngines.add(engineMode);
	
        return availableEngines;
    }

    /**
     * Returns the default speaker profile for Shinx 4
     * @see org.tramper.recognizer.SpeechRecognizer#listAvailableSpeakerProfiles()
     */
    public List<SpeakerProfile> listAvailableSpeakerProfiles() {
	List<SpeakerProfile> availableSpeakers = new ArrayList<SpeakerProfile>();
	
	SpeakerProfile profile = new SpeakerProfile();
	profile.setId("1");
	profile.setName("Basic speaker");
	profile.setVariant("");
	availableSpeakers.add(profile);
	
        return availableSpeakers;
    }

    /**
     * 
     * @see org.tramper.recognizer.SpeechRecognizer#isRecording()
     */
    public boolean isRecording() {
	return microphone.isRecording();
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
     * 
     * @see edu.cmu.sphinx.recognizer.StateListener#statusChanged(edu.cmu.sphinx.recognizer.RecognizerState)
     */
    public void statusChanged(RecognizerState state) {
	RecognitionEvent event = new RecognitionEvent(this);
	if (state.equals(RecognizerState.ALLOCATED)) {
	    event.setEngineState(RecognitionEvent.NOT_LISTENING);
	} else if (state.equals(RecognizerState.ALLOCATING)) {
	    event.setEngineState(RecognitionEvent.NOT_LISTENING);
	} else if (state.equals(RecognizerState.DEALLOCATED)) {
	    event.setEngineState(RecognitionEvent.NOT_LISTENING);
	} else if (state.equals(RecognizerState.DEALLOCATING)) {
	    event.setEngineState(RecognitionEvent.NOT_LISTENING);
	} else if (state.equals(RecognizerState.ERROR)) {
	    event.setEngineState(RecognitionEvent.NOT_LISTENING);
	} else if (state.equals(RecognizerState.READY)) {
	    event.setEngineState(RecognitionEvent.LISTENING);
	} else if (state.equals(RecognizerState.RECOGNIZING)){
	    event.setEngineState(RecognitionEvent.LISTENING);
	}
	fireEngineStateChanged(event);
    }

    /**
     * 
     * @see edu.cmu.sphinx.util.props.Configurable#newProperties(edu.cmu.sphinx.util.props.PropertySheet)
     */
    public void newProperties(PropertySheet arg0) throws PropertyException {
    }
    
    /**
     * Deallocate the recognizer
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        super.finalize();
        recognizer.deallocate();
    }
}
