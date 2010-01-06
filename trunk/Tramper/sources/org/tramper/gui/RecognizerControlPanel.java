package org.tramper.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.sound.sampled.Mixer;
import javax.speech.EngineModeDesc;
import javax.speech.recognition.SpeakerProfile;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.EnhancedIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import javax.swing.SpringUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.tramper.recognizer.RecognitionEvent;
import org.tramper.recognizer.RecognitionException;
import org.tramper.recognizer.RecognitionListener;
import org.tramper.recognizer.SpeechRecognizer;
import org.tramper.recognizer.SpeechRecognizerFactory;

/**
 * recorder control panel
 * @author Paul-Emile
 */
public class RecognizerControlPanel extends JPanel implements RecognitionListener, ChangeListener, ItemListener {
    /** RecognizerControlPanel.java long */
    private static final long serialVersionUID = 1752392586447080870L;
    /** logger */
    private Logger logger = Logger.getLogger(RecognizerControlPanel.class);
    /** image showing synthesizer icon */
    private JLabel engineIconLabel;
    /** close control panel button */
    private JButton closeEngineButton;
    /**  */
    private JLabel listEnginesLabel;
    /**  */
    private JComboBox listEngine;
    /**  */
    private JLabel listSpeakerProfilesLabel;
    /**  */
    private JComboBox listSpeakerProfiles;
    /**  */
    private JLabel confidenceLabel;
    /**  */
    private JSlider confidenceSlide;
    /**  */
    private JLabel sensitivityLabel;
    /**  */
    private JSlider sensitivitySlide;
    /**  */
    private JLabel speedVsAccuracyLabel;
    /**  */
    private JSlider speedVsAccuracySlide;
    /**  */
    private JLabel confidenceLabelMin;
    /**  */
    private JLabel confidenceLabelMax;
    /**  */
    private JLabel sensitivityLabelMin;
    /**  */
    private JLabel sensitivityLabelMax;
    /**  */
    private JLabel speedVsAccuracyLabelMin;
    /**  */
    private JLabel speedVsAccuracyLabelMax;
    /**  */
    private JLabel listMicrophoneLabel;
    /**  */
    private JComboBox listMicrophone;
    /** stop/record button */
    private JToggleButton recordStopButton;
    /** pause/resume button */
    //private JToggleButton pauseResumeButton;
    
    /**
     * 
     */
    public RecognizerControlPanel(GraphicalUserInterface main) {
        ResourceBundle label = ResourceBundle.getBundle("label");
        BorderLayout speakerControlPanelLayout = new BorderLayout();
        this.setLayout(speakerControlPanelLayout);
        
        //Title bar
        JPanel engineTitlePanel = new JPanel();
        Color bgColor = engineTitlePanel.getBackground();
        int newRed = bgColor.getRed()+10 > 255 ? 255 : bgColor.getRed()+10;
        int newGreen = bgColor.getGreen()+10 > 255 ? 255 : bgColor.getGreen()+10;
        int newBlue = bgColor.getBlue()+10 > 255 ? 255 : bgColor.getBlue()+10;
        Color newBgColor = new Color(newRed, newGreen, newBlue);
        engineTitlePanel.setBackground(newBgColor);
        engineTitlePanel.setOpaque(true);
        BoxLayout engineTitleLayout = new BoxLayout(engineTitlePanel, BoxLayout.X_AXIS);
        engineTitlePanel.setLayout(engineTitleLayout);

        Icon engineIcon = new EnhancedIcon(getClass().getResource("images/microphone.png"));
        engineIconLabel = new JLabel(label.getString("recognizer.name"), engineIcon, JLabel.LEFT);
        engineTitlePanel.add(engineIconLabel);
        
        engineTitlePanel.add(Box.createHorizontalGlue());

        Icon closeIcon = new EnhancedIcon(getClass().getResource("images/Error.png"));
        closeEngineButton = new JButton(closeIcon);
        closeEngineButton.setActionCommand("closeRecognizer");
        Insets marginCloseButton = new Insets(0, 0, 0, 0);
        closeEngineButton.setMargin(marginCloseButton);
        closeEngineButton.addActionListener(main);
        engineTitlePanel.add(closeEngineButton);
        
        this.add(engineTitlePanel, BorderLayout.NORTH);
        
        //engine properties panel
        JPanel enginePropPanel = new JPanel();
        enginePropPanel.setOpaque(false);
        SpringLayout enginePropLayout = new SpringLayout();
        enginePropPanel.setLayout(enginePropLayout);

        enginePropPanel.add(Box.createVerticalStrut(20));
        enginePropPanel.add(Box.createVerticalStrut(20));
        
        listEnginesLabel = new JLabel(label.getString("javaspeaker.listEnginesLabel"));
        enginePropPanel.add(listEnginesLabel);
        
        listEngine = new JComboBox();
        listEngine.setEditable(false);
        listEngine.setMaximumSize(listEngine.getPreferredSize());
        listEngine.setName("engineList");
        EngineListCellRenderer engineRenderer = new EngineListCellRenderer();
        listEngine.setRenderer(engineRenderer);
        listEngine.addItemListener(this);
        enginePropPanel.add(listEngine);

        listSpeakerProfilesLabel = new JLabel(label.getString("javaspeaker.speakerProfile"));
        enginePropPanel.add(listSpeakerProfilesLabel);
        
        listSpeakerProfiles = new JComboBox();
        listSpeakerProfiles.setEditable(false);
        listSpeakerProfiles.setMaximumSize(listSpeakerProfiles.getPreferredSize());
        listSpeakerProfiles.setName("speakerProfileList");
        SpeakerProfileListCellRenderer profileRenderer = new SpeakerProfileListCellRenderer();
        listSpeakerProfiles.setRenderer(profileRenderer);
        listSpeakerProfiles.addItemListener(this);
        enginePropPanel.add(listSpeakerProfiles);

        listMicrophoneLabel = new JLabel(label.getString("javaspeaker.microphone"));
        enginePropPanel.add(listMicrophoneLabel);
        
        listMicrophone = new JComboBox();
        listMicrophone.setEditable(false);
        listMicrophone.setMaximumSize(listMicrophone.getPreferredSize());
        listMicrophone.setName("microphoneList");
        MicrophoneListCellRenderer microRenderer = new MicrophoneListCellRenderer();
        listMicrophone.setRenderer(microRenderer);
        listMicrophone.addItemListener(this);
        enginePropPanel.add(listMicrophone);

        confidenceLabel = new JLabel(label.getString("javaspeaker.confidence"));
        enginePropPanel.add(confidenceLabel);

        confidenceSlide = new JSlider();
        confidenceSlide.setName("confidence");
        confidenceSlide.setMinimum(0);
        confidenceSlide.setMaximum(100);
        confidenceSlide.setOrientation(JSlider.HORIZONTAL);
        confidenceSlide.setPaintTicks(true);
        Hashtable<Integer, JLabel> pitchTable = new Hashtable<Integer, JLabel>();
        confidenceLabelMin = new JLabel(label.getString("javaspeaker.confidence.min"));
        pitchTable.put(Integer.valueOf(0), confidenceLabelMin);
        confidenceLabelMax = new JLabel(label.getString("javaspeaker.confidence.max"));
        pitchTable.put(Integer.valueOf(100), confidenceLabelMax);
        confidenceSlide.setLabelTable(pitchTable);
        confidenceSlide.setPaintLabels(true);
        confidenceSlide.addChangeListener(this);
        enginePropPanel.add(confidenceSlide);
        
        sensitivityLabel = new JLabel(label.getString("javaspeaker.sensitivity"));
        enginePropPanel.add(sensitivityLabel);

        sensitivitySlide = new JSlider();
        sensitivitySlide.setName("sensitivity");
        sensitivitySlide.setMinimum(0);
        sensitivitySlide.setMaximum(100);
        sensitivitySlide.setOrientation(JSlider.HORIZONTAL);
        sensitivitySlide.setPaintTicks(true);
        Hashtable<Integer, JLabel> pitchRangeTable = new Hashtable<Integer, JLabel>();
        sensitivityLabelMin = new JLabel(label.getString("javaspeaker.sensitivity.min"));
        pitchRangeTable.put(Integer.valueOf(0), sensitivityLabelMin);
        sensitivityLabelMax = new JLabel(label.getString("javaspeaker.sensitivity.max"));
        pitchRangeTable.put(Integer.valueOf(100), sensitivityLabelMax);
        sensitivitySlide.setLabelTable(pitchRangeTable);
        sensitivitySlide.setPaintLabels(true);
        sensitivitySlide.addChangeListener(this);
        enginePropPanel.add(sensitivitySlide);
        
        speedVsAccuracyLabel = new JLabel(label.getString("javaspeaker.speedVsAccuracy"));
        enginePropPanel.add(speedVsAccuracyLabel);

        speedVsAccuracySlide = new JSlider();
        speedVsAccuracySlide.setName("speedVsAccuracy");
        speedVsAccuracySlide.setMinimum(0);
        speedVsAccuracySlide.setMaximum(100);
        speedVsAccuracySlide.setOrientation(JSlider.HORIZONTAL);
        speedVsAccuracySlide.setPaintTicks(true);
        Hashtable<Integer, JLabel> speakingRateTable = new Hashtable<Integer, JLabel>();
        speedVsAccuracyLabelMin = new JLabel(label.getString("javaspeaker.speedVsAccuracy.min"));
        speakingRateTable.put(Integer.valueOf(0), speedVsAccuracyLabelMin);
        speedVsAccuracyLabelMax = new JLabel(label.getString("javaspeaker.speedVsAccuracy.max"));
        speakingRateTable.put(Integer.valueOf(100), speedVsAccuracyLabelMax);
        speedVsAccuracySlide.setLabelTable(speakingRateTable);
        speedVsAccuracySlide.setPaintLabels(true);
        speedVsAccuracySlide.addChangeListener(this);
        enginePropPanel.add(speedVsAccuracySlide);
        
        recordStopButton = new JToggleButton();
        Icon stopIcon = new EnhancedIcon(getClass().getResource("images/Stop.png"));
        recordStopButton.setSelectedIcon(stopIcon);
        recordStopButton.setMaximumSize(recordStopButton.getPreferredSize());
        Icon playIcon = new EnhancedIcon(getClass().getResource("images/Play.png"));
        recordStopButton.setIcon(playIcon);
        recordStopButton.setName("recordStopButton");
        recordStopButton.addItemListener(this);
        enginePropPanel.add(recordStopButton);

        /*pauseResumeButton = new JToggleButton();
        Icon resumeIcon = new EnhancedIcon(getClass().getResource("images/Synchronize.png"));
        pauseResumeButton.setSelectedIcon(resumeIcon);
        pauseResumeButton.setMaximumSize(pauseResumeButton.getPreferredSize());
        Icon pauseIcon = new EnhancedIcon(getClass().getResource("images/Pause.png"));
        pauseResumeButton.setIcon(pauseIcon);
        pauseResumeButton.setName("pauseResumeButton");
        pauseResumeButton.addItemListener(this);
        enginePropPanel.add(pauseResumeButton);*/
        enginePropPanel.add(Box.createHorizontalGlue());
        
        enginePropPanel.add(Box.createGlue());
        enginePropPanel.add(Box.createGlue());
        
        SpringUtilities.makeCompactGrid(enginePropPanel, //parent
                9, 2, //rows, cols
                3, 3,  //initX, initY
                5, 5); //xPad, yPad
        
        this.add(enginePropPanel, BorderLayout.CENTER);

        try {
            SpeechRecognizer speechRecognizer = SpeechRecognizerFactory.getSpeechRecognizer();
            this.initControlValue(speechRecognizer);
            speechRecognizer.addRecordingListener(this);
        }
        catch (RecognitionException e) {
            logger.error("no available recorder");
        }
    }

    /**
     * localize all the texts of the panel in the selected locale
     */
    public void relocalize() {
        ResourceBundle label = ResourceBundle.getBundle("label", Locale.getDefault());
        
        listEnginesLabel.setText(label.getString("javaspeaker.listEnginesLabel"));
        listSpeakerProfilesLabel.setText(label.getString("javaspeaker.speakerProfile"));
        engineIconLabel.setText(label.getString("recognizer.name"));
        confidenceLabel.setText(label.getString("javaspeaker.confidence"));
        confidenceLabelMin.setText(label.getString("javaspeaker.confidence.min"));
        confidenceLabelMax.setText(label.getString("javaspeaker.confidence.max"));
        sensitivityLabel.setText(label.getString("javaspeaker.sensitivity"));
        sensitivityLabelMin.setText(label.getString("javaspeaker.sensitivity.min"));
        sensitivityLabelMax.setText(label.getString("javaspeaker.sensitivity.max"));
        speedVsAccuracyLabel.setText(label.getString("javaspeaker.speedVsAccuracy"));
        speedVsAccuracyLabelMin.setText(label.getString("javaspeaker.speedVsAccuracy.min"));
        speedVsAccuracyLabelMax.setText(label.getString("javaspeaker.speedVsAccuracy.max"));
    }

    /**
     * initialize the panel with recorder values
     * @param aSpeaker 
     */
    public void initControlValue(SpeechRecognizer aRecorder) {
        List<EngineModeDesc> engines = aRecorder.listAvailableEngines();
        displayEngines(engines);
        EngineModeDesc engineDesc = aRecorder.getEngineModeDesc();
        selectEngine(engineDesc);
        
        Properties engineProp = aRecorder.getEngineProperties();
        displayEngineProperties(engineProp);
        
        List<SpeakerProfile> speakerProfiles = aRecorder.listAvailableSpeakerProfiles();
        displaySpeakerProfiles(speakerProfiles);
        
        List<Mixer.Info> microphones = aRecorder.listAvailableMicrophones();
        displayMicrophones(microphones);
        
        recordStopButton.setSelected(aRecorder.isRecording());
    }

    /**
     * @param speakerProfiles
     */
    public void displaySpeakerProfiles(List<SpeakerProfile> speakerProfiles) {
	ItemListener[] listener = listSpeakerProfiles.getItemListeners();
	//remove all the listeners not to fire item changed events
	for (int i=0; i<listener.length; i++) {
	    listSpeakerProfiles.removeItemListener(listener[i]);
	}
        listSpeakerProfiles.removeAllItems();
        for (int i=0; i<speakerProfiles.size(); i++) {
            SpeakerProfile speakerProfile = speakerProfiles.get(i);
            listSpeakerProfiles.addItem(speakerProfile);
        }
        //add the listeners previously removed
	for (int i=0; i<listener.length; i++) {
	    listSpeakerProfiles.addItemListener(listener[i]);
	}
    }

    /**
     * @param engines
     */
    public void displayEngines(List<EngineModeDesc> engines) {
	ItemListener[] listener = listEngine.getItemListeners();
	//remove all the listeners not to fire item changed events
	for (int i=0; i<listener.length; i++) {
	    listEngine.removeItemListener(listener[i]);
	}
        listEngine.removeAll();
        for (int i=0; i<engines.size(); i++) {
            EngineModeDesc engine = engines.get(i);
            listEngine.addItem(engine);
        }
        //add the listeners previously removed
	for (int i=0; i<listener.length; i++) {
	    listEngine.addItemListener(listener[i]);
	}
    }

    /**
     * @param engines
     */
    public void displayMicrophones(List<Mixer.Info> microphones) {
        listMicrophone.removeAll();
        for (int i=0; i<microphones.size(); i++) {
            Mixer.Info micro = microphones.get(i);
            listMicrophone.addItem(micro);
        }
    }
    
    /**
     * Display the engine properties
     * @param engineProperties
     */
    public void displayEngineProperties(Properties engineProperties) {
	if (engineProperties != null) {
            String confidence = engineProperties.getProperty("confidence");
            try {
                confidenceSlide.setValue((int)Float.parseFloat(confidence)*100);
            }
            catch (NumberFormatException e) {
                logger.warn("confidence "+confidence+" is not a float");
            }
            String sensitivity = engineProperties.getProperty("sensitivity");
            try {
                sensitivitySlide.setValue((int)Float.parseFloat(sensitivity)*100);
            }
            catch (NumberFormatException e) {
                logger.warn("sensitivity "+sensitivity+" is not a float");
            }
            String speedVsAccuracy = engineProperties.getProperty("speedVsAccuracy");
            try {
                speedVsAccuracySlide.setValue((int)Float.parseFloat(speedVsAccuracy)*100);
            }
            catch (NumberFormatException e) {
                logger.warn("speedVsAccuracy "+speedVsAccuracy+" is not a float");
            }
	}
    }
    
    /**
     * @see org.tramper.recognizer.RecognitionListener#engineChanged(org.tramper.recognizer.RecognitionEvent)
     */
    public void engineChanged(RecognitionEvent event) {
        EngineModeDesc engine = event.getEngine();
        selectEngine(engine);
    }

    /**
     * Select an engine in the list of engines
     * @param voice
     */
    public void selectEngine(EngineModeDesc engine) {
	String engineName = engine.getEngineName();
	Locale engineLocale = engine.getLocale();
	// if the desired engine is already selected, do nothing
	EngineModeDesc selectedEngine = (EngineModeDesc)listEngine.getSelectedItem();
	String selectedName = selectedEngine.getEngineName();
	Locale selectedLocale = selectedEngine.getLocale();
	if (selectedName.equalsIgnoreCase(engineName) && selectedLocale.equals(engineLocale)) {
	    return;
	}
	int itemCount = listEngine.getItemCount();
	for (int i=0; i<itemCount; i++) {
	    EngineModeDesc anEngine = (EngineModeDesc)listEngine.getItemAt(i);
	    String aName = anEngine.getEngineName();
	    Locale aLocale = anEngine.getLocale();
	    if (aName.equalsIgnoreCase(engineName) && aLocale.equals(engineLocale)) {
		listEngine.setSelectedIndex(i);
	    }
	}
    }
    
    /**
     * @see org.tramper.recognizer.RecognitionListener#enginePropertiesChanged(org.tramper.recognizer.RecognitionEvent)
     */
    public void enginePropertiesChanged(RecognitionEvent event) {
        Properties engineProp = event.getEngineProp();
        this.displayEngineProperties(engineProp);
    }

    /**
     * @see org.tramper.recognizer.RecognitionListener#speakerProfilesListChanged(org.tramper.recognizer.RecognitionEvent)
     */
    public void speakerProfilesListChanged(RecognitionEvent event) {
        List<SpeakerProfile> speakerProfiles = event.getSpeakerProfiles();
        this.displaySpeakerProfiles(speakerProfiles);
    }
    
    /**
     * 
     * @see org.tramper.recognizer.RecognitionListener#engineStateChanged(org.tramper.recognizer.RecognitionEvent)
     */
    public void engineStateChanged(RecognitionEvent event) {
        short engineState = event.getEngineState();
	if (engineState == RecognitionEvent.LISTENING) {
	    if (!recordStopButton.isSelected()) {
		recordStopButton.setSelected(true);
	    }
	} else if (engineState == RecognitionEvent.NOT_LISTENING) {
	    if (recordStopButton.isSelected()) {
		recordStopButton.setSelected(false);
	    }
	}
    }

    /**
     * 
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            int value = source.getValue();
            String name = source.getName();
            Properties prop = new Properties();
            prop.setProperty(name, String.valueOf(value));
	    try {
		SpeechRecognizer speechRecognizer = SpeechRecognizerFactory.getSpeechRecognizer();
	        speechRecognizer.setEngineProperties(prop);
	    } catch (RecognitionException e1) {
		e1.printStackTrace();
	    }
        }
    }
    
    /**
     * 
     * @param e
     */
    public void itemStateChanged(ItemEvent event) {
        Component source = (Component) event.getSource();
        String name = source.getName();
        if (name.equals("engineList")) {
            EngineModeDesc selected = (EngineModeDesc) event.getItem();
            int stateChange = event.getStateChange();
            if (stateChange == ItemEvent.SELECTED) {
		try {
		    SpeechRecognizer speechRecognizer = SpeechRecognizerFactory.getSpeechRecognizer();
	            speechRecognizer.setEngineModeDesc(selected);
		} catch (RecognitionException e) {
		    logger.error(e);
		}
            } else if (stateChange == ItemEvent.DESELECTED) {
                logger.debug("deselect " + selected + " from " + name);
            }
        } else if (name.equals("speakerProfileList")) {
            SpeakerProfile selected = (SpeakerProfile) event.getItem();
            int stateChange = event.getStateChange();
            if (stateChange == ItemEvent.SELECTED) {
		try {
		    SpeechRecognizer speechRecognizer = SpeechRecognizerFactory.getSpeechRecognizer();
	            speechRecognizer.setSpeakerProfile(selected);
		} catch (RecognitionException e) {
		    logger.error(e);
		}
            } else if (stateChange == ItemEvent.DESELECTED) {
                logger.debug("deselect " + selected + " from " + name);
            }
        } else if (name.equals("microphoneList")) {
            Mixer.Info selected = (Mixer.Info) event.getItem();
            int stateChange = event.getStateChange();
            if (stateChange == ItemEvent.SELECTED) {
    		try {
    		    SpeechRecognizer speechRecognizer = SpeechRecognizerFactory.getSpeechRecognizer();
    	            speechRecognizer.setMicrophone(selected);
    		} catch (RecognitionException e) {
    		    logger.error(e);
    		}
            } else if (stateChange == ItemEvent.DESELECTED) {
                logger.debug("deselect " + selected + " from " + name);
            }
        } else if (name.equals("recordStopButton")) {
            int stateChange = event.getStateChange();
            if (stateChange == ItemEvent.SELECTED) {
    		try {
    		    SpeechRecognizer speechRecognizer = SpeechRecognizerFactory.getSpeechRecognizer();
    	            speechRecognizer.record();
    		} catch (RecognitionException e) {
    		    logger.error(e);
    		}
            } else if (stateChange == ItemEvent.DESELECTED) {
    		try {
    		    SpeechRecognizer speechRecognizer = SpeechRecognizerFactory.getSpeechRecognizer();
    	            speechRecognizer.stop();
    		} catch (RecognitionException e) {
    		    logger.error(e);
    		}
            }
        } /*else if (name.equals("pauseResumeButton")) {
            int stateChange = event.getStateChange();
            if (stateChange == ItemEvent.SELECTED) {
    		try {
    		    SpeechRecognizer recorder = SpeechRecognizerFactory.getRecorder();
    	            recorder.pause();
    		} catch (RecognitionException e) {
    		    logger.error(e);
    		}
            } else if (stateChange == ItemEvent.DESELECTED) {
    		try {
    		    SpeechRecognizer recorder = SpeechRecognizerFactory.getRecorder();
    	            recorder.resume();
    		} catch (RecognitionException e) {
    		    logger.error(e);
    		}
            }
        }*/
    }

    /**
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        SpeechRecognizer speechRecognizer = SpeechRecognizerFactory.getSpeechRecognizer();
        speechRecognizer.removeRecordingListener(this);
    }
}
