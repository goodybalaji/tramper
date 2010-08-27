package org.tramper.gui;

import java.awt.Dimension;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.speech.EngineModeDesc;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.EnhancedIcon;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.tramper.action.LoadAboutAction;
import org.tramper.action.MuteAction;
import org.tramper.action.NextPlayAction;
import org.tramper.action.OpenDisplayAction;
import org.tramper.action.OpenRecognizerAction;
import org.tramper.action.PausePlayAction;
import org.tramper.action.PreviousPlayAction;
import org.tramper.action.ResumePlayAction;
import org.tramper.action.StartPlayAction;
import org.tramper.action.StopPlayAction;
import org.tramper.conductor.Conductor;
import org.fingon.player.PlayEvent;
import org.fingon.player.PlayException;
import org.fingon.player.PlayListener;
import org.tramper.recognizer.RecognitionEvent;
import org.tramper.recognizer.RecognitionException;
import org.tramper.recognizer.RecognitionListener;
import org.tramper.recognizer.SpeechRecognizer;
import org.tramper.recognizer.SpeechRecognizerFactory;
import org.tramper.ui.UserInterfaceFactory;

/**
 * player control panel
 * @author Paul-Emile
 */
public class PlayerControlPanel extends JPanel implements PlayListener, ChangeListener, RecognitionListener {
    /** PlayerControlPanel.java long */
    private static final long serialVersionUID = -5349218321894331492L;
    /** logger */
    private Logger logger = Logger.getLogger(PlayerControlPanel.class);
    /** start button */
    private JButton startButton;
    /** stop button */
    private JButton stopButton;
    /** pause button */
    private JButton pauseButton;
    /** resume button */
    private JButton resumeButton;
    /** next button */
    private JButton nextButton;
    /** previous button */
    private JButton previousButton;
    /** Volume control label */
    private JLabel volumeLabel;
    /** Volume control */
    private JSlider volumeSlider;
    /** Minimum volume label */
    private JLabel volumeLabelMin;
    /** Maximum volume label */
    private JLabel volumeLabelMax;
    /** Speed control label */
    private JLabel speedLabel;
    /** Speed control */
    private JSlider speedSlider;
    /** Minimum speed label */
    private JLabel speedLabelMin;
    /** Maximum speed label */
    private JLabel speedLabelMax;
    /** mute button */
    private JToggleButton muteButton;
    /** recognizer control panel display button */
    private JButton recognizerButton;
    /** recognizer enhanced icon */
    private EnhancedIcon recognizerIcon;
    /** display control panel display button */
    private JButton displayButton;
    /** about button */
    private JButton aboutButton;
    
    /**
     * 
     * @param main
     */
    public PlayerControlPanel(GraphicalUserInterface main) {
        this.setOpaque(true);
	BoxLayout panelLayout = new BoxLayout(this, BoxLayout.X_AXIS);
	this.setLayout(panelLayout);
	
        ResourceBundle label = ResourceBundle.getBundle("label", Locale.getDefault());

        muteButton = new JToggleButton();
        muteButton.setAction(MuteAction.getInstance());
        Icon muteIcon = new EnhancedIcon(getClass().getResource("images/speaker.png"));
        muteButton.setIcon(muteIcon);
        Icon mutedIcon = new EnhancedIcon(getClass().getResource("images/speaker_mute.png"));
        muteButton.setSelectedIcon(mutedIcon);
        String tooltip = TooltipManager.createTooltip("mute");
        muteButton.setToolTipText(tooltip);
        this.add(muteButton);

        recognizerButton = new JButton();
        recognizerButton.setAction(OpenRecognizerAction.getInstance());
        recognizerIcon = new EnhancedIcon(getClass().getResource("images/microphone.png"));
        recognizerButton.setIcon(recognizerIcon);
        tooltip = TooltipManager.createTooltip("recognizer");
        recognizerButton.setToolTipText(tooltip);
        this.add(recognizerButton);
        
        this.add(Box.createHorizontalGlue());
        
        //speed control
        speedLabel = new JLabel(label.getString("javaspeaker.speakingRateLabel"));
        this.add(speedLabel);

        speedSlider = new JSlider();
        speedSlider.setName("speedSlider");
        speedSlider.setMinimum(0);
        speedSlider.setMaximum(100);
        speedSlider.setPreferredSize(new Dimension(130, 30));
        speedSlider.setMaximumSize(new Dimension(130, 30));
        speedSlider.setOrientation(JSlider.HORIZONTAL);
        speedSlider.setMinorTickSpacing(10);
        speedSlider.setPaintTicks(true);
        Hashtable<Integer, JLabel> sampleRateTable = new Hashtable<Integer, JLabel>();
        speedLabelMin = new JLabel(label.getString("javaspeaker.speakingRateLabel.min"));
        sampleRateTable.put(Integer.valueOf(speedSlider.getMinimum()), speedLabelMin);
        speedLabelMax = new JLabel(label.getString("javaspeaker.speakingRateLabel.max"));
        sampleRateTable.put(Integer.valueOf(speedSlider.getMaximum()), speedLabelMax);
        speedSlider.setLabelTable(sampleRateTable);
        speedSlider.setPaintLabels(false);
        speedSlider.addChangeListener(this);
        this.add(speedSlider);

        previousButton = new JButton();
        EnhancedIcon backwardIcon = new EnhancedIcon(getClass().getResource("images/Backward.png"));
        previousButton.setIcon(backwardIcon);
        previousButton.setDisabledIcon(backwardIcon.toGray());
        previousButton.addActionListener(PreviousPlayAction.getInstance());
        String formated = TooltipManager.createTooltip("backward");
        previousButton.setToolTipText(formated);
        this.add(previousButton);

        this.add(Box.createRigidArea(new Dimension(4, 4)));
	
        startButton = new JButton();
        EnhancedIcon startIcon = new EnhancedIcon(getClass().getResource("images/Play.png"));
        startButton.setIcon(startIcon);
        startButton.setDisabledIcon(startIcon.toGray());
        startButton.addActionListener(StartPlayAction.getInstance());
        formated = TooltipManager.createTooltip("play");
        startButton.setToolTipText(formated);
        this.add(startButton);

        stopButton = new JButton();
        EnhancedIcon stopIcon = new EnhancedIcon(getClass().getResource("images/Stop.png"));
        stopButton.setIcon(stopIcon);
        stopButton.setDisabledIcon(stopIcon.toGray());
        stopButton.setVisible(false);
        stopButton.addActionListener(StopPlayAction.getInstance());
        formated = TooltipManager.createTooltip("stop");
        stopButton.setToolTipText(formated);
        this.add(stopButton);

        this.add(Box.createRigidArea(new Dimension(4, 4)));
        
        pauseButton = new JButton();
        EnhancedIcon pauseIcon = new EnhancedIcon(getClass().getResource("images/Pause.png"));
        pauseButton.setIcon(pauseIcon);
        pauseButton.setDisabledIcon(pauseIcon.toGray());
        pauseButton.setVisible(false);
        pauseButton.addActionListener(PausePlayAction.getInstance());
        formated = TooltipManager.createTooltip("pause");
        pauseButton.setToolTipText(formated);
        this.add(pauseButton);

        resumeButton = new JButton();
        EnhancedIcon resumeIcon = new EnhancedIcon(getClass().getResource("images/Play.png"));
        resumeButton.setIcon(resumeIcon);
        resumeButton.setDisabledIcon(resumeIcon.toGray());
        resumeButton.setVisible(false);
        resumeButton.addActionListener(ResumePlayAction.getInstance());
        formated = TooltipManager.createTooltip("resume");
        resumeButton.setToolTipText(formated);
        this.add(resumeButton);

        this.add(Box.createRigidArea(new Dimension(4, 4)));
        
        nextButton = new JButton();
        EnhancedIcon forwardIcon = new EnhancedIcon(getClass().getResource("images/Forward.png"));
        nextButton.setIcon(forwardIcon);
        nextButton.setDisabledIcon(forwardIcon.toGray());
        nextButton.addActionListener(NextPlayAction.getInstance());
        formated = TooltipManager.createTooltip("forward");
        nextButton.setToolTipText(formated);
        this.add(nextButton);

        this.add(Box.createRigidArea(new Dimension(4, 4)));
        
        //volume control
        volumeLabel = new JLabel(label.getString("javaspeaker.volumeLabel"));
        this.add(volumeLabel);
        
        volumeSlider = new JSlider();
        volumeSlider.setName("volumeSlider");
        volumeSlider.setMinimum(0);
        volumeSlider.setMaximum(100);
        volumeSlider.setPreferredSize(new Dimension(130, 30));
        volumeSlider.setMaximumSize(new Dimension(130, 30));
        volumeSlider.setOrientation(JSlider.HORIZONTAL);
        volumeSlider.setMinorTickSpacing(10);
        volumeSlider.setPaintTicks(true);
        Hashtable<Integer, JLabel> volumeTable = new Hashtable<Integer, JLabel>();
        volumeLabelMin = new JLabel(label.getString("javaspeaker.volumeLabel.min"));
        volumeTable.put(Integer.valueOf(volumeSlider.getMinimum()), volumeLabelMin);
        volumeLabelMax = new JLabel(label.getString("javaspeaker.volumeLabel.max"));
        volumeTable.put(Integer.valueOf(volumeSlider.getMaximum()), volumeLabelMax);
        volumeSlider.setLabelTable(volumeTable);
        volumeSlider.setPaintLabels(false);
        volumeSlider.addChangeListener(this);
        this.add(volumeSlider);

        this.add(Box.createHorizontalGlue());

        displayButton = new JButton();
        Icon buttonIcon = new EnhancedIcon(getClass().getResource("images/Display.png"));
        displayButton.setIcon(buttonIcon);
        tooltip = TooltipManager.createTooltip("display");
        displayButton.setToolTipText(tooltip);
        displayButton.addActionListener(OpenDisplayAction.getInstance());
        this.add(displayButton);

        this.add(Box.createRigidArea(new Dimension(4, 4)));
        
        aboutButton = new JButton();
        Icon aboutIcon = new EnhancedIcon(getClass().getResource("images/about.png"));
        aboutButton.setIcon(aboutIcon);
        tooltip = TooltipManager.createTooltip("about");
        aboutButton.setToolTipText(tooltip);
        aboutButton.addActionListener(LoadAboutAction.getInstance());
        this.add(aboutButton);

        if (UserInterfaceFactory.isAudioUserInterfaceInstanciated()) {
            Conductor currentConductor = UserInterfaceFactory.getAudioUserInterface().getActiveRenderer();
            if (currentConductor != null) {
                setControlValues(currentConductor);
            } else {
        	setEnabled(false);
            }
        }
        
        try {
            SpeechRecognizer speechRecognizer = SpeechRecognizerFactory.getSpeechRecognizer();
            speechRecognizer.addRecordingListener(this);
            Locale engineLocale = speechRecognizer.getLocale();
            ImageIcon flagIcon = IconFactory.getFlagIconByLocale(engineLocale);
            recognizerIcon.addDecorationIcon(flagIcon, SwingConstants.SOUTH_EAST);
        } catch (RecognitionException e) {}
    }

    /**
     * Localizes all the texts in the selected locale.
     */
    public void relocalize() {
       ResourceBundle label = ResourceBundle.getBundle("label");

       String formated = TooltipManager.createTooltip("play");
       startButton.setToolTipText(formated);
       formated = TooltipManager.createTooltip("stop");
       stopButton.setToolTipText(formated);
       formated = TooltipManager.createTooltip("pause");
       pauseButton.setToolTipText(formated);
       formated = TooltipManager.createTooltip("resume");
       resumeButton.setToolTipText(formated);
       formated = TooltipManager.createTooltip("forward");
       nextButton.setToolTipText(formated);
       formated = TooltipManager.createTooltip("backward");
       previousButton.setToolTipText(formated);
       formated = TooltipManager.createTooltip("recognizer");
       recognizerButton.setToolTipText(formated);
       formated = TooltipManager.createTooltip("display");
       displayButton.setToolTipText(formated);
       formated = TooltipManager.createTooltip("about");
       aboutButton.setToolTipText(formated);
       formated = TooltipManager.createTooltip("mute");
       muteButton.setToolTipText(formated);
       
       speedLabel.setText(label.getString("javaspeaker.speakingRateLabel"));
       speedLabelMin.setText(label.getString("javaspeaker.speakingRateLabel.min"));
       speedLabelMax.setText(label.getString("javaspeaker.speakingRateLabel.max"));
       
       volumeLabel.setText(label.getString("javaspeaker.volumeLabel"));
       volumeLabelMin.setText(label.getString("javaspeaker.volumeLabel.min"));
       volumeLabelMax.setText(label.getString("javaspeaker.volumeLabel.max"));
    }
    
    /**
     * @see org.tramper.player.PlayListener#nextRead(org.tramper.player.PlayEvent)
     */
    public void nextRead(PlayEvent event) {
        Runnable r = null;
        boolean firstSelected = event.isFirstSelected();
        boolean lastSelected = event.isLastSelected();
        if (lastSelected) {
            r = new Runnable() {
                public void run() {
                    nextButton.setEnabled(false);
                    previousButton.setEnabled(true);
                }
            };
        } else if (firstSelected) {
            r = new Runnable() {
                public void run() {
                    nextButton.setEnabled(true);
                    previousButton.setEnabled(false);
                }
            };
        } else {
            r = new Runnable() {
                public void run() {
                    nextButton.setEnabled(true);
                    previousButton.setEnabled(true);
                }
            };
        }
        SwingUtilities.invokeLater(r);
    }

    /**
     * @see org.tramper.player.PlayListener#previousRead(org.tramper.player.PlayEvent)
     */
    public void previousRead(PlayEvent event) {
        Runnable r = null;
        boolean firstSelected = event.isFirstSelected();
        boolean lastSelected = event.isLastSelected();
        if (lastSelected) {
            r = new Runnable() {
                public void run() {
                    nextButton.setEnabled(false);
                    previousButton.setEnabled(true);
                }
            };
        } else if (firstSelected) {
            r = new Runnable() {
                public void run() {
                    nextButton.setEnabled(true);
                    previousButton.setEnabled(false);
                }
            };
        } else {
            r = new Runnable() {
                public void run() {
                    nextButton.setEnabled(true);
                    previousButton.setEnabled(true);
                }
            };
        }
        SwingUtilities.invokeLater(r);
    }

    /**
     * @see org.tramper.player.PlayListener#readingEnded(org.tramper.player.PlayEvent)
     */
    public void readingEnded(PlayEvent event) {
	Runnable r = new Runnable() {
            public void run() {
        	startButton.setEnabled(true);
        	startButton.setVisible(true);
                stopButton.setEnabled(false);
                stopButton.setVisible(false);
                pauseButton.setEnabled(false);
                pauseButton.setVisible(false);
                resumeButton.setEnabled(false);
                resumeButton.setVisible(false);
                nextButton.setEnabled(false);
                previousButton.setEnabled(false);
            }
        };
        SwingUtilities.invokeLater(r);
    }

    /**
     * @see org.tramper.player.PlayListener#readingPaused(org.tramper.player.PlayEvent)
     */
    public void readingPaused(PlayEvent event) {
	Runnable r = new Runnable() {
            public void run() {
        	startButton.setEnabled(false);
        	startButton.setVisible(false);
                pauseButton.setEnabled(false);
                pauseButton.setVisible(false);
                resumeButton.setEnabled(true);
                resumeButton.setVisible(true);
                stopButton.setEnabled(false);
                stopButton.setVisible(false);
                nextButton.setEnabled(false);
                previousButton.setEnabled(false);
            }
        };
        SwingUtilities.invokeLater(r);
    }

    /**
     * @see org.tramper.player.PlayListener#readingResumed(org.tramper.player.PlayEvent)
     */
    public void readingResumed(PlayEvent event) {
	Runnable r = new Runnable() {
            public void run() {
        	startButton.setEnabled(false);
        	startButton.setVisible(false);
                pauseButton.setEnabled(true);
                pauseButton.setVisible(true);
                resumeButton.setEnabled(false);
                resumeButton.setVisible(false);
                stopButton.setEnabled(true);
                stopButton.setVisible(true);
                nextButton.setEnabled(true);
                previousButton.setEnabled(true);
            }
        };
        SwingUtilities.invokeLater(r);
    }

    /**
     * @see org.tramper.player.PlayListener#readingStarted(org.tramper.player.PlayEvent)
     */
    public void readingStarted(PlayEvent event) {
	Runnable r = new Runnable() {
            public void run() {
        	startButton.setEnabled(false);
        	startButton.setVisible(false);
                pauseButton.setEnabled(true);
                pauseButton.setVisible(true);
                resumeButton.setEnabled(false);
                resumeButton.setVisible(false);
                stopButton.setEnabled(true);
                stopButton.setVisible(true);
                nextButton.setEnabled(true);
                previousButton.setEnabled(true);
            }
        };
        SwingUtilities.invokeLater(r);
    }

    /**
     * @see org.tramper.player.PlayListener#readingStopped(org.tramper.player.PlayEvent)
     */
    public void readingStopped(PlayEvent event) {
	Runnable r = new Runnable() {
            public void run() {
        	startButton.setEnabled(true);
        	startButton.setVisible(true);
                pauseButton.setEnabled(false);
                pauseButton.setVisible(false);
                resumeButton.setEnabled(false);
                resumeButton.setVisible(false);
                stopButton.setEnabled(false);
                stopButton.setVisible(false);
                nextButton.setEnabled(false);
                previousButton.setEnabled(false);
            }
        };
        SwingUtilities.invokeLater(r);
    }

    /**
     * 
     * @see org.tramper.player.PlayListener#sampleRateChanged(org.tramper.player.PlayEvent)
     */
    public void sampleRateChanged(PlayEvent event) {
	final long newValue = event.getNewValue();
	Runnable r = new Runnable() {
            public void run() {
        	int currentValue = speedSlider.getValue();
        	if (currentValue != newValue) {
        	    speedSlider.setValue((int)newValue);
        	}
            }
        };
        SwingUtilities.invokeLater(r);
    }

    /**
     * 
     * @see org.tramper.player.PlayListener#volumeChanged(org.tramper.player.PlayEvent)
     */
    public void volumeChanged(PlayEvent event) {
	final long newValue = event.getNewValue();
	Runnable r = new Runnable() {
            public void run() {
        	int currentValue = volumeSlider.getValue();
        	if (currentValue != newValue) {
        	    volumeSlider.setValue((int)newValue);
        	}
            }
        };
        SwingUtilities.invokeLater(r);
    }

    /**
     * @see javax.swing.JComponent#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
	nextButton.setEnabled(enabled);
	previousButton.setEnabled(enabled);
	pauseButton.setEnabled(enabled);
	resumeButton.setEnabled(enabled);
	startButton.setEnabled(enabled);
	stopButton.setEnabled(enabled);
	speedSlider.setEnabled(enabled);
	volumeSlider.setEnabled(enabled);
    }

    /**
     * 
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
	if (UserInterfaceFactory.isAudioUserInterfaceInstanciated()) {
            Conductor aConductor = UserInterfaceFactory.getAudioUserInterface().getActiveRenderer();
            if (aConductor != null) {
                if (!source.getValueIsAdjusting()) {
                    int value = source.getValue();
                    String name = source.getName();
                    if (name.equals("speedSlider")) {
                	aConductor.setSpeed(value);
                    }
                    else if (name.equals("volumeSlider")) {
                	aConductor.setVolume(value);
                    }
                }
            }
	}
    }

    /**
     * Set or reset the controls values from the conductor's states.
     * @param aConductor
     */
    public void setControlValues(Conductor aConductor) {
	aConductor.addPlayListener(this);
        
        boolean paused = aConductor.isPaused();
        boolean running = aConductor.isRunning();
        if (paused) {
            startButton.setVisible(false);
            startButton.setEnabled(false);
            stopButton.setVisible(false);
            stopButton.setEnabled(false);
            pauseButton.setVisible(false);
            pauseButton.setEnabled(false);
            resumeButton.setVisible(true);
            resumeButton.setEnabled(true);
            nextButton.setEnabled(false);
            previousButton.setEnabled(false);
        } else if (!running) {
            startButton.setVisible(true);
            startButton.setEnabled(true);
            stopButton.setVisible(false);
            stopButton.setEnabled(false);
            pauseButton.setVisible(false);
            pauseButton.setEnabled(false);
            resumeButton.setVisible(false);
            resumeButton.setEnabled(false);
            nextButton.setEnabled(false);
            previousButton.setEnabled(false);
        } else {// running
            startButton.setVisible(false);
            startButton.setEnabled(false);
            stopButton.setVisible(true);
            stopButton.setEnabled(true);
            pauseButton.setVisible(true);
            pauseButton.setEnabled(true);
            resumeButton.setVisible(false);
            resumeButton.setEnabled(false);
            nextButton.setEnabled(true);
            previousButton.setEnabled(true);
        }
        
        volumeSlider.setEnabled(true);
        speedSlider.setEnabled(true);
        
        try {
            volumeSlider.setValue(aConductor.getVolume());
            speedSlider.setValue(aConductor.getSpeed());
        } catch (PlayException e) {
            logger.warn("error when setting volume and speed values: "+e.getMessage());
        }
    }

    /**
     * Decorates the recognizer icon with a flag to indicate the locale of the current engine.
     * @see org.tramper.synthesizer.SynthesisListener#engineChanged(org.tramper.synthesizer.SynthesisEvent)
     */
    public void engineChanged(RecognitionEvent event) {
	EngineModeDesc engineMode = event.getEngine();
	Locale engineLocale = engineMode.getLocale();
	ImageIcon flagIcon = IconFactory.getFlagIconByLocale(engineLocale);
	recognizerIcon.addDecorationIcon(flagIcon, SwingConstants.SOUTH_EAST);
    }

    public void enginePropertiesChanged(RecognitionEvent event) {
    }

    /**
     * Decorates the recognizer icon with a green check if the recognizer is ready and a red cross if not. 
     * @see org.tramper.recognizer.RecognitionListener#engineStateChanged(org.tramper.recognizer.RecognitionEvent)
     */
    public void engineStateChanged(RecognitionEvent event) {
	short state = event.getEngineState();
	ImageIcon stateIcon = null;
	if (state == RecognitionEvent.LISTENING) {
	    stateIcon = new EnhancedIcon(getClass().getResource("images/ok.png"));
	} else if (state == RecognitionEvent.NOT_LISTENING) {
	    stateIcon = new EnhancedIcon(getClass().getResource("images/Error.png"));
	}
	recognizerIcon.addDecorationIcon(stateIcon, SwingConstants.SOUTH_WEST);
    }

    public void speakerProfilesListChanged(RecognitionEvent event) {
    }
}
