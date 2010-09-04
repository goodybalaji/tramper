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
import org.tramper.action.HorizontalViewersOrientationAction;
import org.tramper.action.IncreaseScaleAction;
import org.tramper.action.FullScreenAction;
import org.tramper.action.VerticalViewersOrientationAction;
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
    /** appearance list */
    private JComboBox appearanceList;
    /** language label */
    private JLabel languageLabel;
    /** Appearance label */
    private JLabel appearanceLabel;
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
    /** horizontal splitter button */
    private JToggleButton horizontalButton;
    /** vertical splitter button */
    private JToggleButton verticalButton;
    /** full screen mode display button */
    private JButton fullScreenModeButton;
    /** title panel */
    private JPanel displayTitlePanel;
    
    /**
     * 
     */
    public DisplayControlPanel(GraphicalUserInterface main) {
        this.setOpaque(true);
        ResourceBundle label = ResourceBundle.getBundle("label");
        BorderLayout displayControlPanelLayout = new BorderLayout();
        this.setLayout(displayControlPanelLayout);
        //this.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, this.getForeground()));
        
        //Title bar
        displayTitlePanel = new JPanel();
        displayTitlePanel.setOpaque(true);
        BoxLayout engineTitleLayout = new BoxLayout(displayTitlePanel, BoxLayout.X_AXIS);
        Color bgColor = displayTitlePanel.getBackground();
        int newRed = bgColor.getRed()+15 > 255 ? 255 : bgColor.getRed()+15;
        int newGreen = bgColor.getGreen()+15 > 255 ? 255 : bgColor.getGreen()+15;
        int newBlue = bgColor.getBlue()+15 > 255 ? 255 : bgColor.getBlue()+15;
        Color newBgColor = new Color(newRed, newGreen, newBlue);
        displayTitlePanel.setBackground(newBgColor);
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

        appearanceLabel = new JLabel();
        appearanceLabel.setText(label.getString("javaspeaker.appearance"));
        displayPropPanel.add(appearanceLabel);

        LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        appearanceList = new JComboBox(lafs);
        appearanceList.setEditable(false);
        appearanceList.setMaximumSize(appearanceList.getPreferredSize());
        AppearenceListCellRenderer appearenceRenderer = new AppearenceListCellRenderer();
        appearanceList.setRenderer(appearenceRenderer);
        LookAndFeel laf = UIManager.getLookAndFeel();
        for (int i=0; i<lafs.length; i++) {
            if (lafs[i].getClassName().equals(laf.getClass().getName())) {
                appearanceList.setSelectedItem(lafs[i]);
                break;
            }
        }
        appearanceList.addItemListener(this);
        displayPropPanel.add(appearanceList);

        sizeLabel = new JLabel();
        sizeLabel.setText(label.getString("javaspeaker.enlargement"));
        displayPropPanel.add(sizeLabel);

        JPanel enlargmentPanel = new JPanel();
        enlargmentPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        enlargmentPanel.setOpaque(false);
        enlargmentPanel.setMaximumSize(enlargmentPanel.getPreferredSize());

	BoundedRangeModel enlargementModel = ScaleBoundedRangeModel.getInstance();
	
        minusButton = new JButton(new DecreaseScaleAction(enlargementModel));
	URL iconMinusUrl = getClass().getResource("images/Minus.png");
	Icon minusIcon = new EnhancedIcon(iconMinusUrl);
	minusButton.setIcon(minusIcon);
	minusButton.setToolTipText(TooltipManager.createTooltip("reduce"));
	enlargmentPanel.add(minusButton);

	enlargementSlider = new JSlider(enlargementModel);
	enlargementSlider.setPreferredSize(new Dimension(100, 20));
	enlargementSlider.setEnabled(false);
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
        displayModePanel.setLayout(new BoxLayout(displayModePanel, BoxLayout.X_AXIS));
        displayModePanel.setMaximumSize(displayModePanel.getPreferredSize());
        displayModePanel.setOpaque(false);

        fullScreenModeButton = new JButton(FullScreenAction.getInstance());
        fullScreenModeButton.setIcon(new EnhancedIcon(getClass().getResource("images/fullscreen.png")));
        fullScreenModeButton.setToolTipText(label.getString("javaspeaker.fullScreen"));
        displayModePanel.add(fullScreenModeButton);
        
        displayModePanel.add(Box.createHorizontalStrut(10));
        
        horizontalButton = new JToggleButton(new HorizontalViewersOrientationAction());
        horizontalButton.setIcon(new EnhancedIcon(getClass().getResource("images/view_left_right.png")));
        horizontalButton.setSelected(main.isSplitPaneHorizontal());
        horizontalButton.setToolTipText(label.getString("horizontal"));
        displayModePanel.add(horizontalButton);

        verticalButton = new JToggleButton(new VerticalViewersOrientationAction());
        verticalButton.setIcon(new EnhancedIcon(getClass().getResource("images/view_top_bottom.png")));
        verticalButton.setSelected(!main.isSplitPaneHorizontal());
        verticalButton.setToolTipText(label.getString("vertical"));
        displayModePanel.add(verticalButton);

        ButtonGroup bGroup = new ButtonGroup();
        bGroup.add(horizontalButton);
        bGroup.add(verticalButton);
        
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
        appearanceLabel.setText(label.getString("javaspeaker.appearance"));
        sizeLabel.setText(label.getString("javaspeaker.enlargement"));
        plusButton.setToolTipText(TooltipManager.createTooltip("enlarge"));
        minusButton.setToolTipText(TooltipManager.createTooltip("reduce"));
        displayModeLabel.setText(label.getString("javaspeaker.displayMode"));
        fullScreenModeButton.setToolTipText(label.getString("javaspeaker.fullScreen"));
        horizontalButton.setToolTipText(label.getString("horizontal"));
        verticalButton.setToolTipText(label.getString("vertical"));
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
        } else if (source == appearanceList) {
            if (stateChange == ItemEvent.SELECTED) {
                LookAndFeelInfo selectedAppearence = (LookAndFeelInfo)appearanceList.getSelectedItem();
                String lafClassName = selectedAppearence.getClassName();
                try {
                    UIManager.setLookAndFeel(lafClassName);
                    SwingUtilities.updateComponentTreeUI(main);
                    // we can manage the window decoration only if the frame is not displayed
                    /*if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
                	main.setUndecorated(true);
                	main.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
                    } else {
                	main.setUndecorated(false);
                    }*/
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * @see javax.swing.JPanel#updateUI()
     */
    @Override
    public void updateUI() {
	if (displayTitlePanel != null) {
            Color bgColor = displayTitlePanel.getBackground();
            int newRed = bgColor.getRed()+15 > 255 ? 255 : bgColor.getRed()+15;
            int newGreen = bgColor.getGreen()+15 > 255 ? 255 : bgColor.getGreen()+15;
            int newBlue = bgColor.getBlue()+15 > 255 ? 255 : bgColor.getBlue()+15;
            Color newBgColor = new Color(newRed, newGreen, newBlue);
            displayTitlePanel.setBackground(newBgColor);
	}
	super.updateUI();
    }

    /**
     * 
     * @see org.tramper.gui.DisplayListener#displayChanged(org.tramper.gui.DisplayEvent)
     */
    public void displayChanged(DisplayEvent e) {
	int display = e.getDisplay();
	if (display == DisplayEvent.HORIZONTAL) {
	    horizontalButton.setSelected(true);
	} else if (display == DisplayEvent.VERTICAL) {
	    verticalButton.setSelected(true);
	}
    }
}
