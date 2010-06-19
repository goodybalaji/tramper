package org.tramper.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.EnhancedIcon;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.tramper.action.LoadCurrentPrimaryTargetAction;
import org.tramper.action.LoadCurrentSecondaryTargetAction;
import org.tramper.action.LoadFavoritesAction;
import org.tramper.action.LoadHistoryAction;
import org.tramper.action.LoadTargetAction;
import org.tramper.browser.SearchEngine;
import org.tramper.browser.SearchEngineFactory;
import org.tramper.doc.Library;
import org.tramper.doc.Target;
import org.tramper.loader.Loader;
import org.tramper.loader.LoaderFactory;
import org.tramper.parser.ParserFactory;
import org.tramper.ui.UserInterface;
import org.tramper.ui.UserInterfaceFactory;

/**
 * Address bar
 * @author Paul-Emile
 */
public class AddressControlPanel extends JPanel implements ActionListener {
    /** logger */
    private Logger logger = Logger.getLogger(AddressControlPanel.class);
    /** AddressControlPanel.java long */
    private static final long serialVersionUID = 5406083060040975667L;
    /** Address label */
    private JLabel urlLabel;
    /** textfield where typing address */
    private JTextField urlTextField;
    /** button launching loading */
    private JDropDownButton urlButton;
    /** open button */
    private JButton openButton;
    /** search engine list */ 
    private JComboBox searchEngineList;
    /** Action loading the typed url in the primary target */
    private Action primaryAction;
    /** Action loading the typed url in the secondary target */
    private Action secondaryAction;
    /** favorites button */
    private JButton favoritesButton;
    /** historic button */
    private JButton historicButton;
    /** help button */
    //private JButton helpButton;
    /** last selected directory from the file chooser component */
    private File lastSelectedDir;

    /**
     * layout components
     */
    public AddressControlPanel() {
        this.setOpaque(true);
        GridBagLayout addressLayout = new GridBagLayout();
        this.setLayout(addressLayout);
        GridBagConstraints constraint = new GridBagConstraints();
        
        ResourceBundle label = ResourceBundle.getBundle("label");
        
        urlLabel = new JLabel(label.getString("Iwant"));
        constraint.anchor = GridBagConstraints.WEST;
        constraint.weightx = 0;
        constraint.insets = new Insets(2, 2, 2, 2);
        addressLayout.setConstraints(urlLabel, constraint);
        this.add(urlLabel);

        primaryAction = new LoadTargetAction(new Target(Library.PRIMARY_FRAME, null));
        
        urlTextField = new JTextField(40);
        urlTextField.setAction(primaryAction);
        urlTextField.setMaximumSize(urlTextField.getPreferredSize());
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.anchor = GridBagConstraints.CENTER;
        constraint.weightx = 1;
        constraint.insets = new Insets(2, 2, 2, 2);
        addressLayout.setConstraints(urlTextField, constraint);
        this.add(urlTextField);
        urlLabel.setLabelFor(urlTextField);

        EnhancedIcon newTabIcon = new EnhancedIcon(getClass().getResource("images/new.png"));
        
        List<Action> listButtonActions = new ArrayList<Action>();
        primaryAction.putValue(Action.NAME, label.getString("onNewRight"));
        EnhancedIcon newTabRightIcon = new EnhancedIcon(getClass().getResource("images/Arrow Right.png"));
        newTabRightIcon.addDecorationIcon(newTabIcon, SwingConstants.SOUTH_EAST);
        primaryAction.putValue(Action.SMALL_ICON, newTabRightIcon);
	listButtonActions.add(primaryAction);
	
        Action currentPrimaryAction = new LoadCurrentPrimaryTargetAction();
        currentPrimaryAction.putValue(Action.NAME, label.getString("onCurrentRight"));
        EnhancedIcon currentTabRightIcon = new EnhancedIcon(getClass().getResource("images/Arrow Right.png"));
        currentPrimaryAction.putValue(Action.SMALL_ICON, currentTabRightIcon);
	listButtonActions.add(currentPrimaryAction);
	
        secondaryAction = new LoadTargetAction(new Target(Library.SECONDARY_FRAME, null));
        secondaryAction.putValue(Action.NAME, label.getString("onNewLeft"));
        EnhancedIcon newTabLeftIcon = new EnhancedIcon(getClass().getResource("images/Arrow Left.png"));
        newTabLeftIcon.addDecorationIcon(newTabIcon, SwingConstants.SOUTH_WEST);
        secondaryAction.putValue(Action.SMALL_ICON, newTabLeftIcon);
	listButtonActions.add(secondaryAction);
	
        Action currentSecondaryAction = new LoadCurrentSecondaryTargetAction();
        currentSecondaryAction.putValue(Action.NAME, label.getString("onCurrentLeft"));
        EnhancedIcon currentTabLeftIcon = new EnhancedIcon(getClass().getResource("images/Arrow Left.png"));
        currentSecondaryAction.putValue(Action.SMALL_ICON, currentTabLeftIcon);
	listButtonActions.add(currentSecondaryAction);
	
        SearchEngineFactory engineFactory = SearchEngineFactory.getInstance();
        Vector<SearchEngine> searchEngines = engineFactory.getSearchEngines();
        searchEngineList = new JComboBox(searchEngines);
        searchEngineList.setEditable(false);
        searchEngineList.addItemListener(engineFactory);
        searchEngineList.setName("searchEngineList");
        SearchEngineListCellRenderer searchEngineRenderer = new SearchEngineListCellRenderer();
        searchEngineList.setRenderer(searchEngineRenderer);
        constraint.anchor = GridBagConstraints.EAST;
        constraint.weightx = 0;
        constraint.insets = new Insets(2, 2, 2, 2);
        addressLayout.setConstraints(searchEngineList, constraint);
        this.add(searchEngineList);

	urlButton = new JDropDownButton(listButtonActions, false, true);
        urlButton.setToolTipText(label.getString("javaspeaker.browseButton"));
        constraint.anchor = GridBagConstraints.EAST;
        constraint.weightx = 0;
        constraint.insets = new Insets(2, 2, 2, 2);
        addressLayout.setConstraints(urlButton, constraint);
        this.add(urlButton);

        this.add(Box.createHorizontalStrut(10));

        openButton = new JButton();
        openButton.setActionCommand("openButton");
        openButton.addActionListener(this);
        Icon fileIcon = new EnhancedIcon(getClass().getResource("images/Folder.png"));
        openButton.setIcon(fileIcon);
        openButton.setToolTipText(label.getString("javaspeaker.menu.file.open"));
        constraint.anchor = GridBagConstraints.EAST;
        constraint.weightx = 0;
        constraint.insets = new Insets(2, 2, 2, 2);
        addressLayout.setConstraints(openButton, constraint);
        this.add(openButton);

        favoritesButton = new JButton();
        Icon buttonIcon = new EnhancedIcon(getClass().getResource("images/Favorites.png"));
        favoritesButton.setIcon(buttonIcon);
        String tooltip = TooltipManager.createTooltip("favorites");
        favoritesButton.setToolTipText(tooltip);
        favoritesButton.setActionCommand("favorites");
        favoritesButton.setHorizontalTextPosition(SwingConstants.CENTER);
        favoritesButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        favoritesButton.addActionListener(LoadFavoritesAction.getInstance());
        constraint.anchor = GridBagConstraints.EAST;
        constraint.weightx = 0;
        constraint.insets = new Insets(2, 2, 2, 2);
        addressLayout.setConstraints(favoritesButton, constraint);
        this.add(favoritesButton);
        
        historicButton = new JButton();
        buttonIcon = new EnhancedIcon(getClass().getResource("images/Calendar.png"));
        historicButton.setIcon(buttonIcon);
        tooltip = TooltipManager.createTooltip("history");
        historicButton.setToolTipText(tooltip);
        historicButton.setActionCommand("historic");
        historicButton.setHorizontalTextPosition(SwingConstants.CENTER);
        historicButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        historicButton.addActionListener(LoadHistoryAction.getInstance());
        constraint.anchor = GridBagConstraints.EAST;
        constraint.weightx = 0;
        constraint.insets = new Insets(2, 2, 2, 2);
        addressLayout.setConstraints(historicButton, constraint);
        this.add(historicButton);

        /*helpButton = new JButton();
        buttonIcon = new EnhancedIcon(getClass().getResource("images/help.png"));
        helpButton.setIcon(buttonIcon);
        tooltip = TooltipManager.createTooltip("help");
        helpButton.setToolTipText(tooltip);
        helpButton.setActionCommand("help");
        helpButton.setHorizontalTextPosition(SwingConstants.CENTER);
        helpButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        helpButton.addActionListener(LoadHelpAction.getInstance());
        constraint.anchor = GridBagConstraints.EAST;
        constraint.weightx = 0;
        constraint.insets = new Insets(2, 2, 2, 2);
        addressLayout.setConstraints(helpButton, constraint);
        this.add(helpButton);*/
    }

    /**
     * localize all the texts of the panel in the selected locale
     */
    public void relocalize() {
        ResourceBundle label = ResourceBundle.getBundle("label");
        urlLabel.setText(label.getString("Iwant"));
        urlButton.setToolTipText(label.getString("javaspeaker.browseButton"));
        openButton.setToolTipText(label.getString("javaspeaker.menu.file.open"));
        String formated = TooltipManager.createTooltip("favorites");
        favoritesButton.setToolTipText(formated);
        formated = TooltipManager.createTooltip("history");
        historicButton.setToolTipText(formated);
    }
    
    /**
     * set a new url in the textfield
     * @param newUrl
     */
    public void setUrl(final String newUrl) {
	Runnable r = new Runnable() {
	    public void run() {
	        urlTextField.setText(newUrl);
	    }
	};
	if (SwingUtilities.isEventDispatchThread()) {
	    r.run();
	} else {
	    SwingUtilities.invokeLater(r);
	}
    }

    /**
     * The the url currently in the textfield
     * @return
     */
    public String getUrl() {
	return urlTextField.getText();
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("openButton")) {
	    ResourceBundle label = ResourceBundle.getBundle("label");
	    // open a file dialog
	    JFileChooser fileDialog = new JFileChooser();
	    fileDialog.setMultiSelectionEnabled(false);

	    Iterator<FileFilterByExtension> fileFilters = ParserFactory.getFileFiltersByExtension();
	    while (fileFilters.hasNext()) {
		FileFilterByExtension aFileFilter = fileFilters.next();
		fileDialog.addChoosableFileFilter(aFileFilter);
	    }
	    
	    fileDialog.setAcceptAllFileFilterUsed(true);
	    
	    fileDialog.setLocale(Locale.getDefault());
	    fileDialog.setDialogTitle(label.getString("feedTitle"));
	    fileDialog.setDialogType(JFileChooser.OPEN_DIALOG);
	    fileDialog.setCurrentDirectory(lastSelectedDir);
	    
	    int returnValue = fileDialog.showOpenDialog(this);
	    if (returnValue == JFileChooser.APPROVE_OPTION) {
		File aFile = fileDialog.getSelectedFile();
		if (aFile != null) {
		    boolean accepted = fileDialog.accept(aFile);
		    List<UserInterface> ui = UserInterfaceFactory.getAllUserInterfaces();
		    if (accepted) {
			lastSelectedDir = aFile.getParentFile();
			try {
			    URL url = aFile.toURI().toURL();
			    String urlRef = url.toString();
			    Loader aLoader = LoaderFactory.getLoader();
			    aLoader.download(urlRef, new Target(Library.PRIMARY_FRAME, null));
			} catch (Exception e) {
			    logger.error("bad url", e);
			    for (UserInterface anUi : ui) {
				anUi.raiseError("displayFailed");
			    }
			}
		    } else {
                        for (UserInterface anUi : ui) {
                            anUi.raiseError("displayFailed");
                        }
		    }
		}
	    }
        }
    }
}
