package org.tramper.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.LookAndFeel;
import javax.swing.EnhancedIcon;
import javax.swing.SpringLayout;
import javax.swing.SpringUtilities;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.Logger;
import org.tramper.action.DecreaseScaleAction;
import org.tramper.action.IncreaseScaleAction;
import org.tramper.action.FullScreenAction;
import org.tramper.action.WindowAction;
import org.tramper.ui.UserInterfaceFactory;

/**
 * Manage the display of the GUI
 * @author Paul-Emile
 */
public class DisplayControlPanel extends JPanel implements ItemListener, DisplayListener {
    /** DisplayControlPanel.java long */
    private static final long serialVersionUID = -8731941207459221053L;
    /** logger */
    private Logger logger = Logger.getLogger(DisplayControlPanel.class);
    /** language list */
    private JComboBox languageList;
    /** appearence list */
    private JComboBox appearenceList;
    /** language label */
    private JLabel languageLabel;
    /** Appareance label */
    private JLabel appearenceLabel;
    /** Font size label */
    private JLabel sizeLabel;
    /** close control panel button */
    private JButton closeButton;
    /** title */
    private JLabel displayIconLabel;
    /** display mode label */
    private JLabel displayModeLabel;
    /** enlarge button */
    private JButton plusButton;
    /** enlargement slider */
    private JSlider enlargementSlider;
    /** reduce button */
    private JButton minusButton;
    /** window mode display button */
    private JToggleButton windowModeButton;
    /** full screen mode display button */
    private JToggleButton fullScreenModeButton;
    
    /**
     * 
     */
    public DisplayControlPanel(GraphicalUserInterface main) {
        ResourceBundle label = ResourceBundle.getBundle("label");
        BorderLayout displayControlPanelLayout = new BorderLayout();
        this.setLayout(displayControlPanelLayout);
        
        //Title bar
        JPanel displayTitlePanel = new JPanel();
        Color bgColor = displayTitlePanel.getBackground();
        int newRed = bgColor.getRed()+10 > 255 ? 255 : bgColor.getRed()+10;
        int newGreen = bgColor.getGreen()+10 > 255 ? 255 : bgColor.getGreen()+10;
        int newBlue = bgColor.getBlue()+10 > 255 ? 255 : bgColor.getBlue()+10;
        Color newBgColor = new Color(newRed, newGreen, newBlue);
        displayTitlePanel.setBackground(newBgColor);
        displayTitlePanel.setOpaque(false);
        BoxLayout engineTitleLayout = new BoxLayout(displayTitlePanel, BoxLayout.X_AXIS);
        displayTitlePanel.setLayout(engineTitleLayout);

        Icon displayIcon = new EnhancedIcon(getClass().getResource("images/Display.png"));
        displayIconLabel = new JLabel(displayIcon, JLabel.LEFT);
        displayIconLabel.setText(label.getString("display.name"));
        displayTitlePanel.add(displayIconLabel);
        
        displayTitlePanel.add(Box.createHorizontalGlue());

        Icon closeIcon = new EnhancedIcon(getClass().getResource("images/Error.png"));
        closeButton = new JButton(closeIcon);
        closeButton.setActionCommand("closeDisplay");
        Insets marginCloseButton = new Insets(0, 0, 0, 0);
        closeButton.setMargin(marginCloseButton);
        closeButton.addActionListener(main);
        displayTitlePanel.add(closeButton);
        
        this.add(displayTitlePanel, BorderLayout.NORTH);
        
        //display properties panel
        JPanel displayPropPanel = new JPanel();
        displayPropPanel.setOpaque(false);
        SpringLayout displayPropLayout = new SpringLayout();
        displayPropPanel.setLayout(displayPropLayout);

        displayPropPanel.add(Box.createVerticalStrut(20));
        displayPropPanel.add(Box.createVerticalStrut(20));
        
        languageLabel = new JLabel();
        languageLabel.setText(label.getString("javaspeaker.listEnginesLabel"));
        displayPropPanel.add(languageLabel);
        
        Vector<Locale> languageVector = getAvailableLanguage();
        languageList = new JComboBox(languageVector);
        languageList.setEditable(false);
        languageList.setMaximumSize(languageList.getPreferredSize());
        LanguageListCellRenderer langRenderer = new LanguageListCellRenderer();
        languageList.setRenderer(langRenderer);
        languageList.setSelectedItem(Locale.getDefault());
        languageList.addItemListener(this);
        displayPropPanel.add(languageList);

        appearenceLabel = new JLabel();
        appearenceLabel.setText(label.getString("javaspeaker.appearance"));
        displayPropPanel.add(appearenceLabel);

        LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        appearenceList = new JComboBox(lafs);
        appearenceList.setEditable(false);
        appearenceList.setMaximumSize(appearenceList.getPreferredSize());
        AppearenceListCellRenderer appearenceRenderer = new AppearenceListCellRenderer();
        appearenceList.setRenderer(appearenceRenderer);
        LookAndFeel laf = UIManager.getLookAndFeel();
        for (int i=0; i<lafs.length; i++) {
            if (lafs[i].getClassName().equals(laf.getClass().getName())) {
                appearenceList.setSelectedItem(lafs[i]);
                break;
            }
        }
        appearenceList.addItemListener(this);
        displayPropPanel.add(appearenceList);

        sizeLabel = new JLabel();
        sizeLabel.setText(label.getString("javaspeaker.enlargement"));
        displayPropPanel.add(sizeLabel);

        JPanel enlargmentPanel = new JPanel();
        enlargmentPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        enlargmentPanel.setMaximumSize(enlargmentPanel.getPreferredSize());

	BoundedRangeModel enlargementModel = ScaleBoundedRangeModel.getInstance();
	
        minusButton = new JButton(new DecreaseScaleAction(enlargementModel));
	URL iconMinusUrl = getClass().getResource("images/Minus.png");
	Icon minusIcon = new EnhancedIcon(iconMinusUrl);
	minusButton.setIcon(minusIcon);
	minusButton.setToolTipText(TooltipManager.createTooltip("reduce"));
	enlargmentPanel.add(minusButton);

	enlargementSlider = new JSlider(enlargementModel);
	enlargementSlider.setPreferredSize(new Dimension(130, 30));
	enlargmentPanel.add(enlargementSlider);
	
        plusButton = new JButton(new IncreaseScaleAction(enlargementModel));
	URL iconPlusUrl = getClass().getResource("images/Plus.png");
	Icon plusIcon = new EnhancedIcon(iconPlusUrl);
	plusButton.setIcon(plusIcon);
	plusButton.setToolTipText(TooltipManager.createTooltip("enlarge"));
	enlargmentPanel.add(plusButton);

        displayPropPanel.add(enlargmentPanel);
        
        displayModeLabel = new JLabel();
        displayModeLabel.setText(label.getString("javaspeaker.displayMode"));
        displayPropPanel.add(displayModeLabel);

        JPanel displayModePanel = new JPanel();
        displayModePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 3));
        displayModePanel.setMaximumSize(displayModePanel.getPreferredSize());
        
        windowModeButton = new JToggleButton(WindowAction.getInstance());
        windowModeButton.setIcon(new EnhancedIcon(getClass().getResource("images/window.png")));
        windowModeButton.setSelected(!main.isFullScreenMode());
        windowModeButton.setToolTipText(label.getString("javaspeaker.window"));
        displayModePanel.add(windowModeButton);

        fullScreenModeButton = new JToggleButton(FullScreenAction.getInstance());
        fullScreenModeButton.setIcon(new EnhancedIcon(getClass().getResource("images/fullscreen.png")));
        fullScreenModeButton.setSelected(main.isFullScreenMode());
        fullScreenModeButton.setToolTipText(label.getString("javaspeaker.fullScreen"));
        displayModePanel.add(fullScreenModeButton);
        
        ButtonGroup bGroup = new ButtonGroup();
        bGroup.add(windowModeButton);
        bGroup.add(fullScreenModeButton);
        
        displayPropPanel.add(displayModePanel);
        
        displayPropPanel.add(Box.createGlue());
        displayPropPanel.add(Box.createGlue());
        
        SpringUtilities.makeCompactGrid(displayPropPanel, //parent
                6, 2, //rows, cols
                3, 3,  //initX, initY
                5, 5); //xPad, yPad
        
        this.add(displayPropPanel, BorderLayout.CENTER);
    }

    /**
     * return the supported languages for display
     * @return
     */
    public Vector<Locale> getAvailableLanguage() {
        Vector<Locale> languageVector = new Vector<Locale>();
        languageVector.add(Locale.US);
        languageVector.add(Locale.FRANCE);
        return languageVector;
    }
    
    /**
     * localize all the texts of the panel in the selected locale
     */
    public void relocalize() {
        ResourceBundle label = ResourceBundle.getBundle("label", Locale.getDefault());
        
        displayIconLabel.setText(label.getString("display.name"));
        languageLabel.setText(label.getString("javaspeaker.listEnginesLabel"));
        appearenceLabel.setText(label.getString("javaspeaker.appearance"));
        sizeLabel.setText(label.getString("javaspeaker.enlargement"));
        plusButton.setToolTipText(TooltipManager.createTooltip("enlarge"));
        minusButton.setToolTipText(TooltipManager.createTooltip("reduce"));
        displayModeLabel.setText(label.getString("javaspeaker.displayMode"));
        fullScreenModeButton.setToolTipText(label.getString("javaspeaker.fullScreen"));
        windowModeButton.setToolTipText(label.getString("javaspeaker.window"));
    }

    /**
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getSource();
        int stateChange = e.getStateChange();

        GraphicalUserInterface main = UserInterfaceFactory.getGraphicalUserInterface();
        
        if (source == languageList) {
            if (stateChange == ItemEvent.SELECTED) {
                Locale selectedLocale = (Locale)languageList.getSelectedItem();
                Locale.setDefault(selectedLocale);
                main.relocalize();
            }
        }
        else if (source == appearenceList) {
            if (stateChange == ItemEvent.SELECTED) {
                LookAndFeelInfo selectedAppearence = (LookAndFeelInfo)appearenceList.getSelectedItem();
                String lafClassName = selectedAppearence.getClassName();
                try {
                    UIManager.setLookAndFeel(lafClassName);
                    SwingUtilities.updateComponentTreeUI(main);
                }
                catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                } finally {
                    main.restart();
                }
            }
        }
    }

    /**
     * @see javax.swing.JPanel#updateUI()
     */
    @Override
    public void updateUI() {
	super.updateUI();
    }

    /**
     * 
     * @see org.tramper.gui.DisplayListener#displayChanged(org.tramper.gui.DisplayEvent)
     */
    public void displayChanged(DisplayEvent e) {
	int display = e.getDisplay();
	if (display == DisplayEvent.FULL_SCREEN) {
	    fullScreenModeButton.setSelected(true);
	} else if (display == DisplayEvent.WINDOW) {
	    windowModeButton.setSelected(true);
	}
    }
}
