package org.tramper.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.speech.EngineModeDesc;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.EnhancedIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.SpringUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.tramper.gui.SaveAudioFileFilter;
import org.tramper.player.Player;
import org.tramper.synthesizer.SpeechSynthesizer;
import org.tramper.synthesizer.SynthesisEvent;
import org.tramper.synthesizer.SynthesisException;
import org.tramper.synthesizer.SynthesisListener;
import org.tramper.synthesizer.VoiceDesc;
import org.tramper.ui.UserInterface;
import org.tramper.ui.UserInterfaceFactory;

/**
 * Speech synthesizer control panel
 * @author Paul-Emile
 */
public class SynthesizerControlPanel extends JPanel implements SynthesisListener, ChangeListener, ActionListener, ItemListener {
    /** SynthesizerControlPanel.java long */
    private static final long serialVersionUID = -5621132632123579337L;
    /** logger */
    private Logger logger = Logger.getLogger(SynthesizerControlPanel.class);
    /** image showing synthesizer icon */
    private JLabel engineIconLabel;
    /** close control panel button */
    private JButton closeEngineButton;
    /**  */
    private JLabel listEnginesLabel;
    /**  */
    private JComboBox listEngine;
    /**  */
    private JLabel listVoicesLabel;
    /**  */
    private JComboBox listVoices;
    /**  */
    private JLabel pitchLabel;
    /**  */
    private JSlider pitchSlide;
    /**  */
    private JSlider pitchRangeSlide;
    /**  */
    private JLabel pitchLabelMin;
    /**  */
    private JLabel pitchLabelMax;
    /**  */
    private JLabel pitchRangeLabel;
    /**  */
    private JLabel pitchRangeLabelMin;
    /**  */
    private JLabel pitchRangeLabelMax;
    /**  */
    private JLabel stepByStepLabel;
    /** Step by step checkbox */
    private JCheckBox stepByStepBox;
    /** Output label */
    private JLabel outputLabel;
    /** Output choice */
    private JComboBox listOutput;
    /** file chooser button */
    private JButton fileChooserButton;

    /**
     * 
     */
    public SynthesizerControlPanel(GraphicalUserInterface main) {
	
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
        
        Icon engineIcon = new EnhancedIcon(getClass().getResource("images/speaker.png"));
        engineIconLabel = new JLabel(label.getString("synthesizer.name"), engineIcon, JLabel.LEFT);
        engineTitlePanel.add(engineIconLabel);
        
        engineTitlePanel.add(Box.createHorizontalGlue());

        Icon closeIcon = new EnhancedIcon(getClass().getResource("images/Error.png"));
        closeEngineButton = new JButton(closeIcon);
        closeEngineButton.setActionCommand("closeEngine");
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

        listVoicesLabel = new JLabel(label.getString("javaspeaker.listVoicesLabel"));
        enginePropPanel.add(listVoicesLabel);
        
        listVoices = new JComboBox();
        listVoices.setEditable(false);
        listVoices.setMaximumSize(listVoices.getPreferredSize());
        listVoices.setName("voiceList");
        VoiceListCellRenderer voiceRenderer = new VoiceListCellRenderer();
        listVoices.setRenderer(voiceRenderer);
        listVoices.addItemListener(this);
        enginePropPanel.add(listVoices);
        
        outputLabel = new JLabel(label.getString("javaspeaker.output"));
        enginePropPanel.add(outputLabel);
        
        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        outputPanel.setMaximumSize(outputPanel.getPreferredSize());
        
        listOutput = new JComboBox();
        listOutput.setEditable(false);
        listOutput.setName("outputList");
        OutputListCellRenderer outputRenderer = new OutputListCellRenderer();
        listOutput.setRenderer(outputRenderer);
        listOutput.addItem("speaker");
        listOutput.addItem("file");
        listOutput.addItemListener(this);
        outputPanel.add(listOutput);

        EnhancedIcon fileIcon = new EnhancedIcon(getClass().getResource("images/Folder.png"));
        fileChooserButton = new JButton();
        fileChooserButton.setIcon(fileIcon);
        fileChooserButton.setDisabledIcon(fileIcon.toGray());
        fileChooserButton.setActionCommand("chooseFile");
        fileChooserButton.setMargin(new Insets(2, 2, 2, 2));
        fileChooserButton.addActionListener(this);
        fileChooserButton.setEnabled(false);
        outputPanel.add(fileChooserButton);
        
        enginePropPanel.add(outputPanel);
        
        pitchLabel = new JLabel(label.getString("javaspeaker.pitchLabel"));
        enginePropPanel.add(pitchLabel);
        
        pitchSlide = new JSlider();
        pitchSlide.setName("pitchSlide");
        pitchSlide.setMinimum(0);
        pitchSlide.setMaximum(100);
        pitchSlide.setOrientation(JSlider.HORIZONTAL);
        pitchSlide.setPaintTicks(true);
        Hashtable<Integer, JLabel> pitchTable = new Hashtable<Integer, JLabel>();
        pitchLabelMin = new JLabel(label.getString("javaspeaker.pitchLabel.min"));
        pitchTable.put(Integer.valueOf(0), pitchLabelMin);
        pitchLabelMax = new JLabel(label.getString("javaspeaker.pitchLabel.max"));
        pitchTable.put(Integer.valueOf(100), pitchLabelMax);
        pitchSlide.setLabelTable(pitchTable);
        pitchSlide.setPaintLabels(true);
        pitchSlide.addChangeListener(this);
        enginePropPanel.add(pitchSlide);
        
        pitchRangeLabel = new JLabel(label.getString("javaspeaker.pitchRateLabel"));
        enginePropPanel.add(pitchRangeLabel);

        pitchRangeSlide = new JSlider();
        pitchRangeSlide.setName("pitchRangeSlide");
        pitchRangeSlide.setMinimum(0);
        pitchRangeSlide.setMaximum(100);
        pitchRangeSlide.setOrientation(JSlider.HORIZONTAL);
        pitchRangeSlide.setPaintTicks(true);
        Hashtable<Integer, JLabel> pitchRangeTable = new Hashtable<Integer, JLabel>();
        pitchRangeLabelMin = new JLabel(label.getString("javaspeaker.pitchRateLabel.min"));
        pitchRangeTable.put(Integer.valueOf(0), pitchRangeLabelMin);
        pitchRangeLabelMax = new JLabel(label.getString("javaspeaker.pitchRateLabel.max"));
        pitchRangeTable.put(Integer.valueOf(100), pitchRangeLabelMax);
        pitchRangeSlide.setLabelTable(pitchRangeTable);
        pitchRangeSlide.setPaintLabels(true);
        pitchRangeSlide.addChangeListener(this);
        enginePropPanel.add(pitchRangeSlide);

        stepByStepLabel = new JLabel(label.getString("javaspeaker.stepByStep"));
        enginePropPanel.add(stepByStepLabel);

        stepByStepBox = new JCheckBox();
        stepByStepBox.setName("stepByStep");
        stepByStepBox.setActionCommand("stepByStep");
        stepByStepBox.addChangeListener(this);
        stepByStepBox.setHorizontalTextPosition(JCheckBox.LEFT);
        enginePropPanel.add(stepByStepBox);

        enginePropPanel.add(Box.createGlue());
        enginePropPanel.add(Box.createGlue());
        
        SpringUtilities.makeCompactGrid(enginePropPanel, //parent
                8, 2, //rows, cols
                3, 3,  //initX, initY
                5, 5); //xPad, yPad
        
        this.add(enginePropPanel, BorderLayout.CENTER);

        if (UserInterfaceFactory.isAudioUserInterfaceInstanciated()) {
            Player currentPlayer = UserInterfaceFactory.getAudioUserInterface().getActiveRenderer();
            if (currentPlayer instanceof SpeechSynthesizer) {
                setControlValues((SpeechSynthesizer)currentPlayer);
            } else {
        	setEnabled(false);
            }
        }
    }

    /**
     * initialize the panel with speaker values
     * @param aSpeechSynthesizer 
     */
    public void setControlValues(SpeechSynthesizer aSpeechSynthesizer) {
	if (aSpeechSynthesizer != null) {
	    setEnabled(true);
	    aSpeechSynthesizer.addSpeechListener(this);
            List<SynthesizerModeDesc> engines = aSpeechSynthesizer.listAvailableEngines();
            displayEngines(engines);
            EngineModeDesc engineDesc = aSpeechSynthesizer.getEngineModeDesc();
            selectEngine(engineDesc);
            
            float pitch = aSpeechSynthesizer.getPitch();
            displayPitch(pitch);
            float pitchRange = aSpeechSynthesizer.getPitchRange();
            displayPitchRange(pitchRange);
            
            List<VoiceDesc> voices = aSpeechSynthesizer.listAvailableVoices();
            displayVoices(voices);
            VoiceDesc voiceDesc = aSpeechSynthesizer.getVoiceDesc();
            selectVoice(voiceDesc);
            
            stepByStepBox.setSelected(aSpeechSynthesizer.isStepByStep());
	}
    }

    /**
     * @see javax.swing.JComponent#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
	listEngine.setEnabled(enabled);
	listVoices.setEnabled(enabled);
	pitchSlide.setEnabled(enabled);
	pitchRangeSlide.setEnabled(enabled);
	stepByStepBox.setEnabled(enabled);
	listOutput.setEnabled(enabled);
	fileChooserButton.setEnabled(enabled);
    }

    /**
     * localize all the texts of the panel in the selected locale
     */
    public void relocalize() {
        ResourceBundle label = ResourceBundle.getBundle("label", Locale.getDefault());
        
        listEnginesLabel.setText(label.getString("javaspeaker.listEnginesLabel"));
        listVoicesLabel.setText(label.getString("javaspeaker.listVoicesLabel"));
        engineIconLabel.setText(label.getString("synthesizer.name"));
        pitchLabel.setText(label.getString("javaspeaker.pitchLabel"));
        pitchLabelMin.setText(label.getString("javaspeaker.pitchLabel.min"));
        pitchLabelMax.setText(label.getString("javaspeaker.pitchLabel.max"));
        pitchRangeLabel.setText(label.getString("javaspeaker.pitchRateLabel"));
        pitchRangeLabelMin.setText(label.getString("javaspeaker.pitchRateLabel.min"));
        pitchRangeLabelMax.setText(label.getString("javaspeaker.pitchRateLabel.max"));
        outputLabel.setText(label.getString("javaspeaker.output"));
        stepByStepLabel.setText(label.getString("javaspeaker.stepByStep"));
    }

    /**
     * @param voices
     */
    public void displayVoices(List<VoiceDesc> voices) {
	ItemListener[] listener = listVoices.getItemListeners();
	//remove all the listeners not to fire item changed events
	for (int i=0; i<listener.length; i++) {
	    listVoices.removeItemListener(listener[i]);
	}
        listVoices.removeAllItems();
        for (int i=0; i<voices.size(); i++) {
            VoiceDesc voice = voices.get(i);
            listVoices.addItem(voice);
        }
        //add the listeners previously removed
	for (int i=0; i<listener.length; i++) {
	    listVoices.addItemListener(listener[i]);
	}
    }

    /**
     * @param engines
     */
    public void displayEngines(List<SynthesizerModeDesc> engines) {
	ItemListener[] listener = listEngine.getItemListeners();
	//remove all the listeners not to fire item changed events
	for (int i=0; i<listener.length; i++) {
	    listEngine.removeItemListener(listener[i]);
	}
        listEngine.removeAllItems();
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
     * Display the pitch
     * @param pitch
     */
    public void displayPitch(float pitch) {
        pitchSlide.setValue((int)pitch);
    }

    /**
     * Display the pitch range
     * @param pitchRange
     */
    public void displayPitchRange(float pitchRange) {
        pitchRangeSlide.setValue((int)pitchRange);
    }
    
    /**
     * 
     * @see org.tramper.synthesizer.SynthesisListener#pitchChanged(org.tramper.synthesizer.SynthesisEvent)
     */
    public void pitchChanged(SynthesisEvent event) {
        float pitch = event.getPitch();
        this.displayPitch(pitch);
    }

    /**
     * 
     * @see org.tramper.synthesizer.SynthesisListener#pitchRangeChanged(org.tramper.synthesizer.SynthesisEvent)
     */
    public void pitchRangeChanged(SynthesisEvent event) {
        float pitchRange = event.getPitchRange();
        this.displayPitchRange(pitchRange);
    }
    
    /**
     * 
     * @see org.tramper.synthesizer.SynthesisListener#voicesListChanged(org.tramper.synthesizer.SynthesisEvent)
     */
    public void voicesListChanged(SynthesisEvent event) {
        List<VoiceDesc> voicesList = event.getVoices();
        this.displayVoices(voicesList);
    }
    
    /**
     * 
     * @see org.tramper.synthesizer.SynthesisListener#engineChanged(org.tramper.synthesizer.SynthesisEvent)
     */
    public void engineChanged(SynthesisEvent event) {
        EngineModeDesc engine = event.getEngine();
        selectEngine(engine);
    }

    /**
     * 
     * @see org.tramper.synthesizer.SynthesisListener#voiceChanged(org.tramper.synthesizer.SynthesisEvent)
     */
    public void voiceChanged(SynthesisEvent event) {
	VoiceDesc voice = event.getVoice();
	selectVoice(voice);
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
     * Select a voice in the list of voices
     * @param voice
     */
    public void selectVoice(VoiceDesc voice) {
	// if the desired engine is already selected, do nothing
	VoiceDesc selectedVoice = (VoiceDesc)listVoices.getSelectedItem();
	if (selectedVoice.getName().equalsIgnoreCase(voice.getName())) {
	    return;
	}
	int itemCount = listVoices.getItemCount();
	for (int i=0; i<itemCount; i++) {
	    VoiceDesc aVoice = (VoiceDesc)listVoices.getItemAt(i);
	    if (aVoice.getName().equalsIgnoreCase(voice.getName())) {
		listVoices.setSelectedIndex(i);
	    }
	}
    }
    
    /**
     * 
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent event) {
        if (UserInterfaceFactory.isAudioUserInterfaceInstanciated()) {
            Player currentPlayer = UserInterfaceFactory.getAudioUserInterface().getActiveRenderer();
            if (currentPlayer instanceof SpeechSynthesizer) {
        	SpeechSynthesizer synthesizer = (SpeechSynthesizer)currentPlayer;
        	Component source = (Component)event.getSource();
        	String name = source.getName();
        	if (name.equals("stepByStep")) {
                    JCheckBox checkbox = (JCheckBox) source;
                    boolean selected = checkbox.isSelected();
                    if (synthesizer != null) {
                	synthesizer.setStepByStep(selected);
                    }
        	} else if (name.equals("pitchSlide")) {
        	    JSlider slider = (JSlider) source;
                    if (!slider.getValueIsAdjusting()) {
                        int value = slider.getValue();
                	if (synthesizer != null) {
                	    synthesizer.setPitch(value);
                	}
                    }
        	} else if (name.equals("pitchRangeSlide")) {
        	    JSlider slider = (JSlider) source;
                    if (!slider.getValueIsAdjusting()) {
                        int value = slider.getValue();
                	if (synthesizer != null) {
                	    synthesizer.setPitchRange(value);
                	}
                    }
        	}
            }
        }
    }
    
    /**
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand();
	if (command.equals("chooseFile")) {
	    openFileDialog();
	}
    }

    /**
     * 
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    public void itemStateChanged(ItemEvent event) {
        if (UserInterfaceFactory.isAudioUserInterfaceInstanciated()) {
            Player currentPlayer = UserInterfaceFactory.getAudioUserInterface().getActiveRenderer();
            if (currentPlayer instanceof SpeechSynthesizer) {
        	SpeechSynthesizer synthesizer = (SpeechSynthesizer)currentPlayer;
        	Component source = (Component) event.getSource();
        	String name = source.getName();
        	if (name.equals("outputList")) {
        	    String selected = (String) event.getItem();
        	    int stateChange = event.getStateChange();
        	    if (stateChange == ItemEvent.SELECTED) {
        		if (selected.equals("file")) {
        		    fileChooserButton.setEnabled(true);
        		    openFileDialog();
        		} else if (selected.equals("speaker")) {
        		    fileChooserButton.setEnabled(false);
        		    if (synthesizer != null) {
        			synthesizer.setOutput();
        		    }
        		}
        	    }
        	} else if (name.equals("engineList")) {
        	    EngineModeDesc selected = (EngineModeDesc) event.getItem();
        	    int stateChange = event.getStateChange();
        	    if (stateChange == ItemEvent.SELECTED) {
                        try {
                            if (synthesizer != null) {
                        	synthesizer.loadEngine(selected.getEngineName(), selected.getModeName(), selected.getLocale());
                            }
                	} catch (SynthesisException e) {
                	    logger.error("engine "+selected.getEngineName()+" loading failed", e);
                	}
        	    } else if (stateChange == ItemEvent.DESELECTED) {
        		logger.debug("deselect " + selected.getEngineName() + " from " + name);
        	    }
        	} else if (name.equals("voiceList")) {
        	    VoiceDesc selected = (VoiceDesc) event.getItem();
        	    int stateChange = event.getStateChange();
        	    if (stateChange == ItemEvent.SELECTED) {
        		if (synthesizer != null) {
        		    synthesizer.loadVoice(selected);
        		}
        	    } else if (stateChange == ItemEvent.DESELECTED) {
        		logger.debug("deselect " + selected.getName() + " from " + name);
        	    }
        	}
            }
        }
    }

    /**
     * 
     */
    protected void openFileDialog() {
        ResourceBundle label = ResourceBundle.getBundle("label");
        // open a file dialog
        FileFilter fileFilter = new SaveAudioFileFilter();
        JFileChooser saveDialog = new JFileChooser();
        saveDialog.setMultiSelectionEnabled(false);
        saveDialog.addChoosableFileFilter(fileFilter);
        saveDialog.setLocale(Locale.getDefault());
        saveDialog.setDialogTitle(label.getString("audioTitle"));
        saveDialog.setDialogType(JFileChooser.SAVE_DIALOG);
        int returnValue = saveDialog.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File aFile = saveDialog.getSelectedFile();
            if (aFile != null) {
                boolean accepted = saveDialog.accept(aFile);
                if (accepted) {
                    OutputListCellRenderer renderer = (OutputListCellRenderer)listOutput.getRenderer();
                    renderer.setFile(aFile);
                    if (UserInterfaceFactory.isAudioUserInterfaceInstanciated()) {
                        Player currentPlayer = UserInterfaceFactory.getAudioUserInterface().getActiveRenderer();
                        if (currentPlayer instanceof SpeechSynthesizer) {
                            ((SpeechSynthesizer)currentPlayer).setOutput(aFile);
                        }
                    }
                } else {
                    List<UserInterface> ui = UserInterfaceFactory.getAllUserInterfaces();
                    for (UserInterface anUi : ui) {
                	anUi.raiseError("unsupportedAudio");
                    }
                    listOutput.setSelectedIndex(0);
                }
            } else {
        	listOutput.setSelectedIndex(0);
            }
        } else {
            listOutput.setSelectedIndex(0);
        }
    }
}
