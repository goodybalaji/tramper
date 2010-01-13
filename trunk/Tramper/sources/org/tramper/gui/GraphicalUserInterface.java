package org.tramper.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.FontUIResource;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Locale;

import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.tramper.action.AddFavoriteAction;
import org.tramper.action.BackHistoryAction;
import org.tramper.action.EnlargementMinusAction;
import org.tramper.action.EnlargementPlusAction;
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
import org.tramper.action.WindowAction;
import org.tramper.action.ResumePlayAction;
import org.tramper.action.StartPlayAction;
import org.tramper.action.SwitchViewersOrientationAction;
import org.tramper.action.StartRecordingAction;
import org.tramper.action.StopPlayAction;
import org.tramper.action.StopRecordingAction;
import org.tramper.action.TogglePausePlayAction;
import org.tramper.action.TogglePlayAction;
import org.tramper.aui.AUIEvent;
import org.tramper.aui.AUIListener;
import org.tramper.doc.History;
import org.tramper.doc.Library;
import org.tramper.doc.LibraryEvent;
import org.tramper.doc.LibraryListener;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.gui.viewer.Viewer;
import org.tramper.gui.viewer.ViewerFactory;
import org.tramper.loader.Loader;
import org.tramper.loader.LoaderFactory;
import org.tramper.loader.LoaderFactoryEvent;
import org.tramper.loader.LoaderFactoryListener;
import org.tramper.loader.LoadingEvent;
import org.tramper.loader.LoadingListener;
import org.tramper.player.Player;
import org.tramper.synthesizer.SpeechSynthesizer;
import org.tramper.ui.RenderingException;
import org.tramper.ui.UserInterface;
import org.tramper.ui.UserInterfaceFactory;

/**
 * Standard window for a common GUI
 * @author Paul-Emile
 */
public class GraphicalUserInterface extends JFrame implements UserInterface, ActionListener, WindowListener, LoadingListener, LoaderFactoryListener, LibraryListener, AUIListener {
    /**  */
    private static final long serialVersionUID = 3478510518434269631L;
    /** logger */
    private Logger logger = Logger.getLogger(GraphicalUserInterface.class);
    /** spliter between viewers */
    private JSplitPane viewersArea;
    /** spliter between miniatures and viewers */
    private JSplitPane miniaturesViewersArea;
    /** primary (right or down) panel */
    private JPanel primaryPanel;
    /** currently showed tab in primary panel */
    private Target currentPrimaryTarget;
    /** secondary (left or up) panel */
    private JPanel secondaryPanel;
    /** currently showed tab in primary panel */
    private Target currentSecondaryTarget;
    /** bars panel */
    private JPanel barPanel;
    /** GUI configuration */
    private GUIConfig guiConfig;
    /** link id typed by user when control key pressed */
    //private int typedLinkId;
    /**  */
    private DisplayControlPanel displayPanel;
    /**  */
    private PlayerControlPanel playerPanel;
    /**  */
    private SynthesizerControlPanel speakerPanel;
    /**  */
    private AddressControlPanel addressPanel;
    /**  */
    private RecognizerControlPanel recorderPanel;
    /**  */
    private ViewerControlPanel miniaturePanel;
    /**  */
    private Map<Loader, LoadingViewer> loadingViewers;
    /** enlargement */
    private int enlargement;
    /** mouse pointer image */
    //private Image cursorImage;
    /** display listeners */
    private List<DisplayListener> displayListeners;
    
    /**
     * initialize the GUI
     * @throws java.awt.HeadlessException
     */
    public GraphicalUserInterface() throws HeadlessException {
	super();
	displayListeners = new ArrayList<DisplayListener>();

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        
	//set the window's icon
	Image logoTramper = toolkit.createImage(getClass().getResource("images/Tramper.png"));
	this.setIconImage(logoTramper);

	//set the window's mouse pointer
        //cursorImage = toolkit.createImage(getClass().getResource("images/mouse_pointer.png"));
	
	// for Quaqua look and feel only
	System.setProperty("Quaqua.sizeStyle","small");
	
	List<LookAndFeelInfo> lookAndFeels = new ArrayList<LookAndFeelInfo>();
	// remove the metal and motifs look and feels
	LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
	for (int i=0; i<lafs.length; i++) {
	    String className = lafs[i].getClassName();
	    if (!className.equals("javax.swing.plaf.metal.MetalLookAndFeel")) {
		if (!className.equals("com.sun.java.swing.plaf.motif.MotifLookAndFeel")) {
		    if (!className.equals("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel")) {
			lookAndFeels.add(lafs[i]);
		    }
		}
	    }
	}
	// add nicer look and feels
	LookAndFeelInfo quaquaLaF = new UIManager.LookAndFeelInfo("Quaqua", "ch.randelshofer.quaqua.QuaquaLookAndFeel");
	lookAndFeels.add(quaquaLaF);
	LookAndFeelInfo substanceDustLaF = new UIManager.LookAndFeelInfo("Dust", "org.jvnet.substance.skin.SubstanceDustLookAndFeel");
	lookAndFeels.add(substanceDustLaF);
	LookAndFeelInfo substanceBusinessLaF = new UIManager.LookAndFeelInfo("Business Black Steel", "org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel");
	lookAndFeels.add(substanceBusinessLaF);
	LookAndFeelInfo substanceTwilightLaF = new UIManager.LookAndFeelInfo("Twilight", "org.jvnet.substance.skin.SubstanceTwilightLookAndFeel");
	lookAndFeels.add(substanceTwilightLaF);
	LookAndFeelInfo substanceRavenLaF = new UIManager.LookAndFeelInfo("Raven Graphite Glass", "org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel");
	lookAndFeels.add(substanceRavenLaF);
	LookAndFeelInfo substanceOfficeBlue2007LaF = new UIManager.LookAndFeelInfo("Office Silver 2007", "org.jvnet.substance.skin.SubstanceOfficeSilver2007LookAndFeel");
	lookAndFeels.add(substanceOfficeBlue2007LaF);
	LookAndFeelInfo substanceMistAquaLaF = new UIManager.LookAndFeelInfo("Mist Aqua", "org.jvnet.substance.skin.SubstanceMistAquaLookAndFeel");
	lookAndFeels.add(substanceMistAquaLaF);
	LookAndFeelInfo substanceMagmaLaF = new UIManager.LookAndFeelInfo("Magma", "org.jvnet.substance.skin.SubstanceMagmaLookAndFeel");
	lookAndFeels.add(substanceMagmaLaF);
	LookAndFeelInfo substanceEmeraldDuskLaF = new UIManager.LookAndFeelInfo("Emerald dusk", "org.jvnet.substance.skin.SubstanceEmeraldDuskLookAndFeel");
	lookAndFeels.add(substanceEmeraldDuskLaF);
	LookAndFeelInfo substanceGeminiLaF = new UIManager.LookAndFeelInfo("Gemini", "org.jvnet.substance.api.skin.SubstanceGeminiLookAndFeel");
	lookAndFeels.add(substanceGeminiLaF);
	LookAndFeelInfo substanceGraphiteAquaLaF = new UIManager.LookAndFeelInfo("Graphite aqua", "org.jvnet.substance.api.skin.SubstanceGraphiteAquaLookAndFeel");
	lookAndFeels.add(substanceGraphiteAquaLaF);
	LookAndFeelInfo substanceMagellanAquaLaF = new UIManager.LookAndFeelInfo("Magellan", "org.jvnet.substance.api.skin.SubstanceMagellanLookAndFeel");
	lookAndFeels.add(substanceMagellanAquaLaF);
	LookAndFeelInfo substanceChallengerLaF = new UIManager.LookAndFeelInfo("Challenger Deep", "org.jvnet.substance.skin.SubstanceChallengerDeepLookAndFeel");
	lookAndFeels.add(substanceChallengerLaF);
	
	// install the new look and feels
	UIManager.setInstalledLookAndFeels(lookAndFeels.toArray(new UIManager.LookAndFeelInfo[lookAndFeels.size()]));

	guiConfig = new GUIConfig();
	guiConfig.load();
	Locale.setDefault(guiConfig.getLocale());
	
	//try to load the look and feel
	String lookAndFeel = guiConfig.getLookAndFeel();
	try {
	    UIManager.setLookAndFeel(lookAndFeel);
	} catch (Exception e) {
	    logger.warn("unable to load the look and feel and its theme at startup", e);
	}

	// the look and feel decorates the window itself if possible
	if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
	    this.setUndecorated(true);
	    this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
	} else {
	    this.setUndecorated(false);
	}
	
	ResourceBundle label = ResourceBundle.getBundle("label", Locale.getDefault());

	this.setTitle(label.getString("javaspeaker.productTitle"));
	this.addWindowListener(this);
	
	boolean addressPanelFlag = guiConfig.getAddressPanel();
	boolean displayPanelFlag = guiConfig.getDisplayPanel();
	boolean synthesizerPanelFlag = guiConfig.getSynthesizerPanel();
	boolean recognizerPanelFlag = guiConfig.getRecognizerPanel();
	boolean readerPanelFlag = guiConfig.getPlayerPanel();

	// JMenus must be "heavy" to render in front of JDIC WebBrowser component
	JPopupMenu.setDefaultLightWeightPopupEnabled(false);

	// Main window
	BorderLayout frameLayout = new BorderLayout(2, 2);
	this.getContentPane().setLayout(frameLayout);
	
	miniaturesViewersArea = new JSplitPane();
	miniaturesViewersArea.setOneTouchExpandable(false);
	miniaturesViewersArea.setContinuousLayout(true);
	miniaturesViewersArea.setOrientation(JSplitPane.VERTICAL_SPLIT);
	miniaturesViewersArea.setDividerSize(2);
	miniaturesViewersArea.setDividerLocation(75);
        //the bottom component (the viewers) takes the extra space
	miniaturesViewersArea.setResizeWeight(0);
	this.getContentPane().add(miniaturesViewersArea, BorderLayout.CENTER);

	viewersArea = new JSplitPane();
	viewersArea.setDividerLocation(0);
	viewersArea.setOneTouchExpandable(true);
	viewersArea.setContinuousLayout(true);
	viewersArea.setOrientation(guiConfig.getOrientation());
	miniaturesViewersArea.setRightComponent(viewersArea);
	
	primaryPanel = new JPanel();
	LayoutManager primaryLayout = new CardLayout();
	primaryPanel.setLayout(primaryLayout);
	viewersArea.setRightComponent(primaryPanel);

	secondaryPanel = new JPanel();
	LayoutManager secondaryLayout = new CardLayout();
	secondaryPanel.setLayout(secondaryLayout);
	viewersArea.setLeftComponent(secondaryPanel);

	miniaturePanel = new ViewerControlPanel(this);
	miniaturesViewersArea.setLeftComponent(miniaturePanel);
	
	
	barPanel = new JPanel();
	BoxLayout barLayout = new BoxLayout(barPanel, BoxLayout.Y_AXIS);
	barPanel.setLayout(barLayout);
	this.getContentPane().add(barPanel, BorderLayout.NORTH);

	// loading progress bar
	loadingViewers = new HashMap<Loader, LoadingViewer>();
        
	// Address bar
	if (addressPanelFlag) {
	    addressPanel = new AddressControlPanel();
	    barPanel.add(addressPanel, 0);
	}

	// display control panel
	if (displayPanelFlag) {
	    displayPanel = new DisplayControlPanel(this);
	    this.addDisplayListener(displayPanel);
	    this.getContentPane().add(displayPanel, BorderLayout.WEST);
	}

	if (synthesizerPanelFlag) {
	    speakerPanel = new SynthesizerControlPanel(this, null);
	    this.getContentPane().add(speakerPanel, BorderLayout.WEST);
	}

	if (recognizerPanelFlag) {
	    recorderPanel = new RecognizerControlPanel(this);
	    this.getContentPane().add(recorderPanel, BorderLayout.WEST);
	}
	
	if (readerPanelFlag) {
	    playerPanel = new PlayerControlPanel(this);
	    this.getContentPane().add(playerPanel, BorderLayout.SOUTH);
	}
	
        LoaderFactory.addLoaderFactoryListener(this);
        Library library = Library.getInstance();
        
	InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	ActionMap actionMap = this.getRootPane().getActionMap();
	inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "stop");
	actionMap.put("stop", StopPlayAction.getInstance());
	inputMap.put(KeyStroke.getKeyStroke("pressed ALT_GRAPH"), "start listening");
	actionMap.put("start listening", StartRecordingAction.getInstance());
	inputMap.put(KeyStroke.getKeyStroke("released ALT_GRAPH"), "stop listening");
	actionMap.put("stop listening", StopRecordingAction.getInstance());
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_DOWN_MASK), "previous");
	actionMap.put("previous", PreviousPlayAction.getInstance());
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_DOWN_MASK), "next");
	actionMap.put("next", NextPlayAction.getInstance());
	inputMap.put(KeyStroke.getKeyStroke("PAUSE"), "pause/resume");
	actionMap.put("pause/resume", TogglePausePlayAction.getInstance());
	actionMap.put("pause", PausePlayAction.getInstance());
	actionMap.put("resume", ResumePlayAction.getInstance());
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "help");
	actionMap.put("help", LoadHelpAction.getInstance());
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "about");
	actionMap.put("about", LoadAboutAction.getInstance());
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "favorites");
	actionMap.put("favorites", LoadFavoritesAction.getInstance());
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "history");
	actionMap.put("history", LoadHistoryAction.getInstance());
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "switch");
	actionMap.put("switch", new SwitchViewersOrientationAction());
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK), "play/stop");
	actionMap.put("play/stop", TogglePlayAction.getInstance());
	actionMap.put("play", StartPlayAction.getInstance());
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), "addFavorite");
	actionMap.put("addFavorite", AddFavoriteAction.getInstance());
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK & InputEvent.SHIFT_DOWN_MASK), "removeFavorite");
	actionMap.put("removeFavorite", AddFavoriteAction.getInstance());
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.ALT_DOWN_MASK), "backHistory");
	actionMap.put("backHistory", BackHistoryAction.getInstance());
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.ALT_DOWN_MASK), "forwardHistory");
	actionMap.put("forwardHistory", ForwardHistoryAction.getInstance());
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), "openSynthesizer");
	actionMap.put("openSynthesizer", new OpenSynthesizerAction(library.getActiveTarget()));
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), "openRecognizer");
	actionMap.put("openRecognizer", OpenRecognizerAction.getInstance());
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "openDisplay");
	actionMap.put("openDisplay", OpenDisplayAction.getInstance());
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MULTIPLY, InputEvent.CTRL_DOWN_MASK), "enlargementPlus");
	actionMap.put("enlargementPlus", EnlargementPlusAction.getInstance());
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DIVIDE, InputEvent.CTRL_DOWN_MASK), "enlargementMinus");
	actionMap.put("enlargementMinus", EnlargementMinusAction.getInstance());
	/*inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, 0, true), "stop typing number");
	actionMap.put("stop typing number", new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    public void actionPerformed(ActionEvent ae) {
		if (typedLinkId > 0) {
		    SimpleDocument document = Library.getInstance().getActiveDocument();
		    if (document instanceof MarkupDocument) {
			MarkupDocument docWithLinks = (MarkupDocument)document;
                        String link = docWithLinks.getLink(typedLinkId);
                        if (link != null) {
                            Loader aLoader = LoaderFactory.getLoader();
                            aLoader.download(link, new Target(Library.PRIMARY_FRAME, null));
                        }
    		    	typedLinkId = 0;
		    }
		}
	    }
	});
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK), "action 1");
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1, InputEvent.CTRL_DOWN_MASK), "action 1");
	actionMap.put("action 1", new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    public void actionPerformed(ActionEvent ae) {
		typedLinkId = Integer.parseInt(String.valueOf(typedLinkId) + String.valueOf(1));
	    }
	});
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK), "action 2");
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD2, InputEvent.CTRL_DOWN_MASK), "action 2");
	actionMap.put("action 2", new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    public void actionPerformed(ActionEvent ae) {
		typedLinkId = Integer.parseInt(String.valueOf(typedLinkId) + String.valueOf(2));
	    }
	});
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK), "action 3");
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD3, InputEvent.CTRL_DOWN_MASK), "action 3");
	actionMap.put("action 3", new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    public void actionPerformed(ActionEvent ae) {
		typedLinkId = Integer.parseInt(String.valueOf(typedLinkId) + String.valueOf(3));
	    }
	});
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK), "action 4");
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD4, InputEvent.CTRL_DOWN_MASK), "action 4");
	actionMap.put("action 4", new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    public void actionPerformed(ActionEvent ae) {
		typedLinkId = Integer.parseInt(String.valueOf(typedLinkId) + String.valueOf(4));
	    }
	});
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_5, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK), "action 5");
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD5, InputEvent.CTRL_DOWN_MASK), "action 5");
	actionMap.put("action 5", new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    public void actionPerformed(ActionEvent ae) {
		typedLinkId = Integer.parseInt(String.valueOf(typedLinkId) + String.valueOf(5));
	    }
	});
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_6, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK), "action 6");
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD6, InputEvent.CTRL_DOWN_MASK), "action 6");
	actionMap.put("action 6", new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    public void actionPerformed(ActionEvent ae) {
		typedLinkId = Integer.parseInt(String.valueOf(typedLinkId) + String.valueOf(6));
	    }
	});
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_7, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK), "action 7");
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD7, InputEvent.CTRL_DOWN_MASK), "action 7");
	actionMap.put("action 7", new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    public void actionPerformed(ActionEvent ae) {
		typedLinkId = Integer.parseInt(String.valueOf(typedLinkId) + String.valueOf(7));
	    }
	});
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_8, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK), "action 8");
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD8, InputEvent.CTRL_DOWN_MASK), "action 8");
	actionMap.put("action 8", new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    public void actionPerformed(ActionEvent ae) {
		typedLinkId = Integer.parseInt(String.valueOf(typedLinkId) + String.valueOf(8));
	    }
	});
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_9, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK), "action 9");
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD9, InputEvent.CTRL_DOWN_MASK), "action 9");
	actionMap.put("action 9", new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    public void actionPerformed(ActionEvent ae) {
		typedLinkId = Integer.parseInt(String.valueOf(typedLinkId) + String.valueOf(9));
	    }
	});
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK), "action 0");
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, InputEvent.CTRL_DOWN_MASK), "action 0");
	actionMap.put("action 0", new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    public void actionPerformed(ActionEvent ae) {
		typedLinkId = Integer.parseInt(String.valueOf(typedLinkId) + String.valueOf(0));
	    }
	});*/

	changeEnlargement(guiConfig.getEnlargement());

	this.pack();

	int windowExtendedState = guiConfig.getWindowExtendedState();
	this.setExtendedState(windowExtendedState);
	if (windowExtendedState == Frame.NORMAL) {
	    int windowX = guiConfig.getWindowX();
	    int windowY = guiConfig.getWindowY();
	    int windowWidth = guiConfig.getWindowWidth();
	    int windowHeight = guiConfig.getWindowHeight();
	    this.setBounds(windowX, windowY, windowWidth, windowHeight);
	}
	
	// Obfuscate the window as long as the history is not ready
	if (!History.getInstance().isLoaded()) {
	    this.obfuscate();
	}
	
	library.addLibraryListener(this);
        setVisible(true);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent event) {
	String command = event.getActionCommand();
	 if (command.equals("closeEngine")) {
	    this.getContentPane().remove(speakerPanel);
	    this.getContentPane().validate();
	    speakerPanel = null;
	} else if (command.equals("closeRecognizer")) {
	    this.getContentPane().remove(recorderPanel);
	    this.getContentPane().validate();
	    recorderPanel = null;
	} else if (command.equals("closeDisplay")) {
	    this.getContentPane().remove(displayPanel);
	    this.getContentPane().validate();
	    displayPanel = null;
	}
    }

    /**
     * display a document in the right document viewer
     * @param document
     */
    public void renderDocument(SimpleDocument document, Target target) {
	String frame = target.getFrame();
	String tab = target.getTab();
	
	// we instanciate a new viewer for this document at this target
	Viewer docViewer = ViewerFactory.getViewerByDocument(document);
	((Component) docViewer).setName(tab);
        if (frame.equals(Library.SECONDARY_FRAME)) {
	    secondaryPanel.add((Component) docViewer, tab);
            CardLayout panelLayout = (CardLayout)secondaryPanel.getLayout();
            panelLayout.show(secondaryPanel, tab);
            currentSecondaryTarget = target;
            if (viewersArea.getDividerLocation() < 0.2) {
        	viewersArea.setDividerLocation(0.4);
            }
        } else {
            primaryPanel.add((Component) docViewer, tab);
            CardLayout panelLayout = (CardLayout)primaryPanel.getLayout();
            panelLayout.show(primaryPanel, tab);
            currentPrimaryTarget = target;
            if (viewersArea.getDividerLocation() > 0.8) {
        	viewersArea.setDividerLocation(0.4);
            }
        }
        
        try {
            // display the document in the viewer
	    docViewer.render(document, target);
	    
	    // give the focus to the document
	    ((Component) docViewer).requestFocusInWindow();
	    
	    // add the viewer's miniature
	    miniaturePanel.addMiniature(docViewer);
	} catch (RenderingException e) {
	    logger.error("error when rendering " + document.getTitle());
	}
        
        // display the document's url in the address bar
        if (addressPanel != null) {
            addressPanel.setUrl(document.getUrl().toString());
        }
        
        this.validate();
    }
    
    public void modifyRenderer(SimpleDocument document, Target target) {
	String frame = target.getFrame();
	String tab = target.getTab();
	Viewer docViewer = getViewer(target);
	if (docViewer != null && docViewer.canRender(document)) {
	    // there is already a viewer for this document at this target, let's keep it
	    if (frame.equals(Library.SECONDARY_FRAME)) {
	        CardLayout panelLayout = (CardLayout)secondaryPanel.getLayout();
	        panelLayout.show(secondaryPanel, tab);
	        currentSecondaryTarget = target;
	        if (viewersArea.getDividerLocation() < 0.2) {
	            viewersArea.setDividerLocation(0.4);
	        }
	    } else {
	        CardLayout panelLayout = (CardLayout)primaryPanel.getLayout();
	        panelLayout.show(primaryPanel, tab);
	        currentPrimaryTarget = target;
	        if (viewersArea.getDividerLocation() > 0.8) {
	            viewersArea.setDividerLocation(0.4);
	        }
	    }
	    
	    try {
	        // display the document in the viewer
		docViewer.render(document, target);
		
		// give the focus to the document
		((Component) docViewer).requestFocusInWindow();

		// modify the viewer's miniature
		miniaturePanel.modifyMiniature(docViewer, docViewer);
	    } catch (RenderingException e) {
		logger.error("error when rendering " + document.getTitle());
	    }
	    
	    // display the document's url in the address bar
	    if (addressPanel != null) {
	        addressPanel.setUrl(document.getUrl().toString());
	    }
	    
	    this.validate();
	} else {
	    // it is necessary to remove the former viewer first, 
	    // because you don't remove the former component when adding the new one at the same place. 
	    removeRenderer(target);
	    // create a proper viewer and replace the former one
	    renderDocument(document, target);
	}
    }
    
    /**
     * 
     * @param target
     */
    public void removeRenderer(Target target) {
	String frame = target.getFrame();
	String tab = target.getTab();
	Component docViewer = null;
	// since we can't get the viewer associated to the tab directly,
	// we loop on all viewers and test their name, which equals tab.
	if (frame.equals(Library.PRIMARY_FRAME)) {
	    Component[] components = primaryPanel.getComponents();
	    for (Component comp : components) {
		String name = comp.getName();
		if (name.equals(tab)) {
		    docViewer = comp;
        	    primaryPanel.remove(comp);
        	    if (primaryPanel.getComponentCount() == 0) {
        		viewersArea.setDividerLocation(1.0);
        	    }
        	    break;
		}
	    }
	} else {
	    Component[] components = secondaryPanel.getComponents();
	    for (Component comp : components) {
		String name = comp.getName();
		if (name.equals(tab)) {
		    docViewer = comp;
		    secondaryPanel.remove(comp);
        	    if (secondaryPanel.getComponentCount() == 0) {
        		viewersArea.setDividerLocation(0.0);
        	    }
        	    break;
		}
	    }
	}
	miniaturePanel.removeMiniature((Viewer)docViewer);
    }

    /**
     * 
     * @return
     */
    public Viewer getActiveRenderer() {
	Component[] viewers = primaryPanel.getComponents();
	for (Component viewer : viewers) {
	    if (((Viewer) viewer).isActive()) {
		return (Viewer) viewer;
	    }
	}
	viewers = secondaryPanel.getComponents();
	for (Component viewer : viewers) {
	    if (((Viewer) viewer).isActive()) {
		return (Viewer) viewer;
	    }
	}
	return null;
    }

    /**
     * 
     */
    public void openSynthesizer(SpeechSynthesizer synthesizer) {
	if (recorderPanel != null) {
	    this.getContentPane().remove(recorderPanel);
	    recorderPanel = null;
	} else if (displayPanel != null) {
	    this.getContentPane().remove(displayPanel);
	    displayPanel = null;
	}
	if (speakerPanel == null) {
            speakerPanel = new SynthesizerControlPanel(this, synthesizer);
            this.getContentPane().add(speakerPanel, BorderLayout.WEST);
            // transfer the focus to the next component
            speakerPanel.transferFocus();
            this.validate();
	}
    }

    /**
     * 
     */
    public void openRecognizer() {
	if (speakerPanel != null) {
	    this.getContentPane().remove(speakerPanel);
	    speakerPanel = null;
	} else if (displayPanel != null) {
	    this.getContentPane().remove(displayPanel);
	    displayPanel = null;
	}
	if (recorderPanel == null) {
            recorderPanel = new RecognizerControlPanel(this);
            this.getContentPane().add(recorderPanel, BorderLayout.WEST);
            // transfer the focus to the next component
            recorderPanel.transferFocus();
            this.validate();
	}
    }

    /**
     * 
     */
    public void openDisplay() {
	if (speakerPanel != null) {
	    this.getContentPane().remove(speakerPanel);
	    speakerPanel = null;
	} else if (recorderPanel != null) {
	    this.getContentPane().remove(recorderPanel);
	    recorderPanel = null;
	}
	if (displayPanel == null) {
            displayPanel = new DisplayControlPanel(this);
            this.addDisplayListener(displayPanel);
            this.getContentPane().add(displayPanel, BorderLayout.WEST);
            // transfer the focus to the next component
            displayPanel.transferFocus();
            this.validate();
	}
    }
    
    /**
     * 
     */
    public void openPlayer() {
	if (playerPanel == null) {
            playerPanel = new PlayerControlPanel(this);
            this.getContentPane().add(playerPanel, BorderLayout.SOUTH);
            playerPanel.requestFocusInWindow();
            // transfer the focus to the next component : the keyword textfield
            playerPanel.transferFocus();
            this.validate();
	}
    }

    /**
     * 
     */
    public void openAddress() {
	if (addressPanel == null) {
            addressPanel = new AddressControlPanel();
            Viewer docViewer = getActiveRenderer();
            if (docViewer != null) {
                SimpleDocument document = docViewer.getDocument();
                addressPanel.setUrl(document.getUrl().toString());
            }
            barPanel.add(addressPanel);
            addressPanel.requestFocusInWindow();
            // transfer the focus to the next component: the textfield
            addressPanel.transferFocus();
            this.validate();
	}
    }
    
    /**
     * 
     * @return
     */
    public String getAddress() {
	if (addressPanel != null) {
	    String address = addressPanel.getUrl();
	    return address;
	}
	return null;
    }

    /**
     * localize all the texts of the window in the selected locale
     */
    public void relocalize() {
	ResourceBundle label = ResourceBundle.getBundle("label");

	this.setTitle(label.getString("javaspeaker.productTitle"));

	Component[] viewers = primaryPanel.getComponents();
	for (Component viewer : viewers) {
	    ((Viewer)viewer).relocalize();
	}
	viewers = secondaryPanel.getComponents();
	for (Component viewer : viewers) {
	    ((Viewer)viewer).relocalize();
	}
	if (playerPanel != null) {
	    playerPanel.relocalize();
	}
	if (speakerPanel != null) {
	    speakerPanel.relocalize();
	}
	if (recorderPanel != null) {
	    recorderPanel.relocalize();
	}
	if (addressPanel != null) {
	    addressPanel.relocalize();
	}
	if (displayPanel != null) {
	    displayPanel.relocalize();
	}
	this.getContentPane().validate();
    }

    /**
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    public void windowOpened(WindowEvent arg0) {
    }

    /**
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    public void windowIconified(WindowEvent arg0) {
    }

    /**
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    public void windowDeiconified(WindowEvent arg0) {
    }

    /**
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    public void windowActivated(WindowEvent arg0) {
	this.clarify();
    }

    /**
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    public void windowDeactivated(WindowEvent arg0) {
	this.obfuscate();
    }

    /**
     * Before closing the window, we save the favorites.
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    public void windowClosing(WindowEvent arg0) {
	ActionEvent actionEvent = new ActionEvent(this, 0, "quit");
	QuitAction.getInstance().actionPerformed(actionEvent);
    }

    /**
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    public void windowClosed(WindowEvent arg0) {
    }

    /**
     * 
     * @see org.tramper.loader.LoaderFactoryListener#newLoader(org.tramper.loader.LoaderFactoryEvent)
     */
    public void newLoader(LoaderFactoryEvent event) {
	final Loader loader = event.getLoader();
	loader.addLoadingListener(this);
	Runnable thread = new Runnable() {
	    public void run() {
	    	LoadingViewer loadingViewer = new LoadingViewer();
	    	loadingViewer.setLoader(loader);
	    	loadingViewers.put(loader, loadingViewer);
	    	barPanel.add(loadingViewer, -1);
	    	UserInterfaceFactory.getGraphicalUserInterface().validate();
	    }
	};
	if (SwingUtilities.isEventDispatchThread()) {
	    thread.run();
	} else {
	    try {
		SwingUtilities.invokeAndWait(thread);
	    } catch (Exception e) {
		logger.error("Error when creating a loading viewer in the EDT", e);
	    }
	}
    }

    /**
     * 
     * @see org.tramper.loader.LoadingListener#loadingStarted(org.tramper.loader.LoadingEvent)
     */
    public void loadingStarted(LoadingEvent event) {
	final LoadingViewer loadingViewer = loadingViewers.get(event.getSource());
	if (loadingViewer != null) {
	    SwingUtilities.invokeLater(new Runnable() {
                public void run() {
            	    loadingViewer.start();
            	}
            });
    	}
    }

    /**
     * 
     * @see org.tramper.loader.LoadingListener#loadingCompleted(org.tramper.loader.LoadingEvent)
     */
    public void loadingCompleted(LoadingEvent event) {
	final LoadingViewer loadingViewer = loadingViewers.remove(event.getSource());
	if (loadingViewer != null) {
	    SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    loadingViewer.stop();
        	    barPanel.remove(loadingViewer);
        	    UserInterfaceFactory.getGraphicalUserInterface().validate();
        	}
            });
        }
    }

    /**
     * 
     * @see org.tramper.loader.LoadingListener#loadingFailed(org.tramper.loader.LoadingEvent)
     */
    public void loadingFailed(LoadingEvent event) {
	final LoadingViewer loadingViewer = loadingViewers.remove(event.getSource());
	if (loadingViewer != null) {
	    SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    loadingViewer.stop();
		    barPanel.remove(loadingViewer);
		    UserInterfaceFactory.getGraphicalUserInterface().validate();
		    raiseError("loadingFailed");
		}
	    });
	}
    }

    /**
     * 
     * @see org.tramper.loader.LoadingListener#loadingStopped(org.tramper.loader.LoadingEvent)
     */
    public void loadingStopped(LoadingEvent event) {
	final LoadingViewer loadingViewer = loadingViewers.remove(event.getSource());
	if (loadingViewer != null) {
	    SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    loadingViewer.stop();
        	    barPanel.remove(loadingViewer);
        	    UserInterfaceFactory.getGraphicalUserInterface().validate();
        	}
	    });
        }
    }
    
    /**
     * display a confirmation message. 
     * @param msgKey
     * @return
     */
    public boolean confirmMessage(String msgKey) {
	return confirmMessage(msgKey, new Object[0]);
    }

    /**
     * display a confirmation message. 
     * @param msgKey
     * @param params 
     * @return
     */
    public boolean confirmMessage(String msgKey, Object[] params) {
        ResourceBundle bundle = ResourceBundle.getBundle("label");
        String message = bundle.getString("javaspeaker.message." + msgKey);
        MessageFormat msgFormat = new MessageFormat(message);
        String formatedMsg = msgFormat.format(params);
        String title = bundle.getString("javaspeaker.info");
        int chosenOption = JOptionPane.showConfirmDialog(this, formatedMsg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (chosenOption == JOptionPane.YES_OPTION) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * display a info message. 
     * @param msgKey
     */
    public void raiseInfo(String msgKey) {
        ResourceBundle bundle = ResourceBundle.getBundle("label");
        String message = bundle.getString("javaspeaker.message." + msgKey);
        String title = bundle.getString("javaspeaker.info");
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * display a warning message. 
     * @param msgKey
     */
    public void raiseWarning(String msgKey) {
        ResourceBundle bundle = ResourceBundle.getBundle("label");
        String message = bundle.getString("javaspeaker.message." + msgKey);
        String title = bundle.getString("javaspeaker.warn");
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * display an error message. 
     * @param msgKey
     */
    public void raiseError(String msgKey) {
        ResourceBundle bundle = ResourceBundle.getBundle("label");
        String message = bundle.getString("javaspeaker.message." + msgKey);
        String title = bundle.getString("javaspeaker.error");
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Obfuscate the window to trap the mouse events.
     */
    public void obfuscate() {
	Component glassPane = getGlassPane();
	if (!(glassPane instanceof Obfuscator)) {
	    glassPane = new Obfuscator();
            setGlassPane(glassPane);
	}
	glassPane.setVisible(true);
    }
    
    /**
     * Clarify the window to be able to use it.
     */
    public void clarify() {
	final Component glassPane = getGlassPane();
	if (glassPane instanceof Obfuscator) {
            SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	            glassPane.setVisible(false);
	        }
            });
	}
    }

    /**
     * 
     * @return
     */
    public int getEnlargement() {
	return enlargement;
    }
    
    /**
     * Change the general size of fonts and images
     * @param value
     */
    public void changeEnlargement(int value) {
        enlargement += value;
        
        Font font = UIManager.getFont("Button.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("Button.font", font);

        font = UIManager.getFont("Label.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("Label.font", font);

        font = UIManager.getFont("CheckBox.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("CheckBox.font", font);

        font = UIManager.getFont("ComboBox.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("ComboBox.font", font);

        font = UIManager.getFont("EditorPane.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("EditorPane.font", font);

        font = UIManager.getFont("FormattedTextField.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("FormattedTextField.font", font);

        font = UIManager.getFont("List.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("List.font", font);

        font = UIManager.getFont("Menu.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("Menu.font", font);

        font = UIManager.getFont("MenuItem.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("MenuItem.font", font);

        font = UIManager.getFont("Panel.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("Panel.font", font);

        font = UIManager.getFont("RadioButton.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("RadioButton.font", font);

        font = UIManager.getFont("ScrollPane.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("ScrollPane.font", font);

        font = UIManager.getFont("Spinner.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("Spinner.font", font);

        font = UIManager.getFont("Table.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("Table.font", font);

        font = UIManager.getFont("TableHeader.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("TableHeader.font", font);

        font = UIManager.getFont("TextArea.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("TextArea.font", font);

        font = UIManager.getFont("TextField.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("TextField.font", font);

        font = UIManager.getFont("TextPane.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("TextPane.font", font);

        font = UIManager.getFont("ToggleButton.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("ToggleButton.font", font);

        font = UIManager.getFont("ToolTip.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("ToolTip.font", font);

        font = UIManager.getFont("Tree.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("Tree.font", font);

        font = UIManager.getFont("ProgressBar.font");
        font = new FontUIResource(font.deriveFont((float)(font.getSize()+value)));
        UIManager.put("ProgressBar.font", font);
        
        int iconScale = UIManager.getInt("Icon.scale");
        iconScale = iconScale + value;
        UIManager.put("Icon.scale", iconScale);

        /*int cursorWidth = cursorImage.getWidth(this);
        int cursorHeight = cursorImage.getHeight(this);
        int desiredCursorWidth = cursorWidth + value;
        int desiredCursorHeight = cursorHeight + value;
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension authorizedCursorDim = toolkit.getBestCursorSize(desiredCursorWidth, desiredCursorHeight);
        cursorImage = cursorImage.getScaledInstance(authorizedCursorDim.width, authorizedCursorDim.height, Image.SCALE_DEFAULT);
        Cursor cursor = toolkit.createCustomCursor(cursorImage, new Point(authorizedCursorDim.width-1, 0), "paper_plane");
        this.setCursor(cursor);*/
        
        SwingUtilities.updateComponentTreeUI(this);
    }
    
    /**
     * Display the GUI in full screen mode
     */
    public void displayFullScreenMode() {
        this.getContentPane().remove(playerPanel);
        playerPanel = null;
        
        barPanel.remove(addressPanel);
        addressPanel = null;

	miniaturesViewersArea.remove(miniaturePanel);
        miniaturePanel = null;
	
        if (displayPanel != null) {
            this.getContentPane().remove(displayPanel);
            displayPanel = null;
        }

        if (speakerPanel != null) {
            this.getContentPane().remove(speakerPanel);
            speakerPanel = null;
        }

        if (recorderPanel != null) {
            this.getContentPane().remove(recorderPanel);
            recorderPanel = null;
        }
        
	this.dispose();
        this.setUndecorated(true);
	this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
	this.pack();
	GraphicsEnvironment graphicsEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice defaultScreen = graphicsEnv.getDefaultScreenDevice();
	defaultScreen.setFullScreenWindow(this);

	// associates the escape button with the "quit full screen" action
	ActionMap actionMap = this.getRootPane().getActionMap();
	actionMap.put("stop", WindowAction.getInstance());

	DisplayEvent event = new DisplayEvent();
	event.setDisplay(DisplayEvent.FULL_SCREEN);
	fireDisplayChanged(event);
    }
    
    /**
     * Display the GUI as a window
     */
    public void displayWindowMode() {
	GraphicsEnvironment graphicsEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice defaultScreen = graphicsEnv.getDefaultScreenDevice();
	defaultScreen.setFullScreenWindow(null);
	
	// display toolbars
        playerPanel = new PlayerControlPanel(this);
        this.getContentPane().add(playerPanel, BorderLayout.SOUTH);
        addressPanel = new AddressControlPanel();
        barPanel.add(addressPanel, 0);
        miniaturePanel = new ViewerControlPanel(this);
	miniaturesViewersArea.setLeftComponent(miniaturePanel);
        
        Viewer docViewer = getActiveRenderer();
        if (docViewer != null) {
            SimpleDocument document = docViewer.getDocument();
            addressPanel.setUrl(document.getUrl().toString());
        }
	
	this.dispose();
	if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
	    this.setUndecorated(true);
	    this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
	} else {
	    this.setUndecorated(false);
	}
	this.pack();
        this.setVisible(true);
        
        // reset the escape button default action
	ActionMap actionMap = this.getRootPane().getActionMap();
	actionMap.put("stop", StopPlayAction.getInstance());
	
	DisplayEvent event = new DisplayEvent();
	event.setDisplay(DisplayEvent.WINDOW);
	fireDisplayChanged(event);
    }
    
    /**
     * 
     * @return true if the GUI if in full screen mode, false otherwise
     */
    public boolean isFullScreenMode() {
	GraphicsEnvironment graphicsEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice defaultScreen = graphicsEnv.getDefaultScreenDevice();
	Window win = defaultScreen.getFullScreenWindow();
	if (win == null) {
	    return false;
	} else {
	    return true;
	}
    }
    
    /**
     * Fires a display changed event to all the display listener.
     * @param event
     */
    private void fireDisplayChanged(DisplayEvent event) {
	for (DisplayListener listener : displayListeners) {
	    listener.displayChanged(event);
	}
    }

    /**
     * Save the GUI configuration, dispose the window, set the singleton to null
     * and reinit the singleton.
     */
    public void restart() {
	// save the window location before disposing it, it doesn't work if the window is not displayed
	saveGuiConfig();
	try {
	    this.dispose();
	    if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
		this.setUndecorated(true);
		this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
	    } else {
		this.setUndecorated(false);
	    }
	    this.pack();
	    this.setVisible(true);
	} catch (Exception e) {
	    // thrown when switching from Substance to another look and feel
            List<UserInterface> ui = UserInterfaceFactory.getAllUserInterfaces();
            for (UserInterface anUi : ui) {
        	anUi.raiseWarning("badLaF");
            }
            System.exit(1);
	}
    }

    /**
     * Save the GUI configuration in a file
     */
    public void saveGuiConfig() {
	LookAndFeel laf = UIManager.getLookAndFeel();
        
        guiConfig.setEnlargement(enlargement);
	guiConfig.setWindowX(this.getLocationOnScreen().x);
	guiConfig.setWindowY(this.getLocationOnScreen().y);
	guiConfig.setWindowWidth(this.getWidth());
	guiConfig.setWindowHeight(this.getHeight());
	guiConfig.setLookAndFeel(laf.getClass().getName());
	guiConfig.setAddressPanel((addressPanel != null));
	guiConfig.setPlayerPanel((playerPanel != null));
	guiConfig.setSynthesizerPanel((speakerPanel != null));
	guiConfig.setRecognizerPanel((recorderPanel != null));
	guiConfig.setDisplayPanel((displayPanel != null));
	guiConfig.setWindowExtendedState(this.getExtendedState());
	guiConfig.setLocale(Locale.getDefault());
	guiConfig.setOrientation(viewersArea.getOrientation());
	guiConfig.save();
    }

    /**
     * Adds a listener to the list of display listener.
     * @param listener
     */
    public void addDisplayListener(DisplayListener listener) {
	if (!displayListeners.contains(listener)) {
	    displayListeners.add(listener);
	}
    }
    
    /**
     * 
     * @param listener
     */
    public void removeDisplayListener(DisplayListener listener) {
	displayListeners.remove(listener);
    }

    /**
     * 
     * @see org.tramper.doc.LibraryListener#documentAdded(org.tramper.doc.LibraryEvent)
     */
    public void documentAdded(LibraryEvent event) {
	final SimpleDocument document = event.getDocument();
	final Target target = event.getTarget();
	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
        	renderDocument(document, target);
            }
	});
    }

    /**
     * 
     * @see org.tramper.doc.LibraryListener#documentModified(org.tramper.doc.LibraryEvent)
     */
    public void documentModified(LibraryEvent event) {
	final SimpleDocument document = event.getDocument();
	final Target target = event.getTarget();
	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
        	modifyRenderer(document, target);
            }
	});
    }
    
    /**
     * Removes the document viewer and the miniature.
     * @see org.tramper.doc.LibraryListener#documentRemoved(org.tramper.doc.LibraryEvent)
     */
    public void documentRemoved(LibraryEvent event) {
	final Target target = event.getTarget();
	
	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
        	removeRenderer(target);
            }
	});
    }

    /**
     * Shows the renderer on top.
     * @see org.tramper.doc.LibraryListener#documentActivated(org.tramper.doc.LibraryEvent)
     */
    public void documentActivated(LibraryEvent event) {
	Target activatedTarget = event.getTarget();
	String frame = activatedTarget.getFrame();
	String tab = activatedTarget.getTab();
	
        if (frame.equals(Library.SECONDARY_FRAME)) {
            CardLayout panelLayout = (CardLayout)secondaryPanel.getLayout();
            panelLayout.show(secondaryPanel, tab);
            currentSecondaryTarget = activatedTarget;
            if (viewersArea.getDividerLocation() < 0.2) {
                viewersArea.setDividerLocation(0.4);
            }
        } else {
            CardLayout panelLayout = (CardLayout)primaryPanel.getLayout();
            panelLayout.show(primaryPanel, tab);
            currentPrimaryTarget = activatedTarget;
            if (viewersArea.getDividerLocation() > 0.8) {
                viewersArea.setDividerLocation(0.4);
            }
        }
        // display the document's url in the address bar
        if (addressPanel != null) {
            SimpleDocument document = event.getDocument();
            addressPanel.setUrl(document.getUrl().toString());
        }
    }

    /**
     * 
     * @see org.tramper.doc.LibraryListener#documentDeactivated(org.tramper.doc.LibraryEvent)
     */
    public void documentDeactivated(LibraryEvent event) {
    }
    
    /**
     * 
     * @param event
     */
    public void playerAdded(AUIEvent event) {
	Player addedPlayer = event.getPlayer();
	SimpleDocument addedPlayerDoc = addedPlayer.getDocument();
	
	Component[] viewers = primaryPanel.getComponents();
	for (Component viewer : viewers) {
            SimpleDocument doc = ((Viewer)viewer).getDocument();
            if (addedPlayerDoc.equals(doc)) {
                addedPlayer.addPlayListener((Viewer)viewer);
            }
	}
	viewers = secondaryPanel.getComponents();
	for (Component viewer : viewers) {
            SimpleDocument doc = ((Viewer)viewer).getDocument();
            if (addedPlayerDoc.equals(doc)) {
                addedPlayer.addPlayListener((Viewer)viewer);
            }
	}
    }

    /**
     * 
     * @param event
     */
    public void playerRemoved(AUIEvent event) {
	Player removedPlayer = event.getPlayer();
	SimpleDocument removedPlayerDoc = removedPlayer.getDocument();
	
	Component[] viewers = primaryPanel.getComponents();
	for (Component viewer : viewers) {
            SimpleDocument doc = ((Viewer)viewer).getDocument();
            if (removedPlayerDoc.equals(doc)) {
        	removedPlayer.removePlayListener((Viewer)viewer);
            }
	}
	viewers = secondaryPanel.getComponents();
	for (Component viewer : viewers) {
            SimpleDocument doc = ((Viewer)viewer).getDocument();
            if (removedPlayerDoc.equals(doc)) {
        	removedPlayer.removePlayListener((Viewer)viewer);
            }
	}
    }

    /**
     * 
     * @see org.tramper.aui.AUIListener#playerActivated(org.tramper.aui.AUIEvent)
     */
    public void playerActivated(AUIEvent event) {
	final Player activatedPlayer = event.getPlayer();
        if (playerPanel != null) {
            SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	            playerPanel.setControlValues(activatedPlayer);
	        }
	    });
        }
        if (activatedPlayer instanceof SpeechSynthesizer) {
            if (speakerPanel != null) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
    	            	speakerPanel.setControlValues((SpeechSynthesizer)activatedPlayer);
                    }
                });
            }
        }
    }

    /**
     * 
     * @see org.tramper.aui.AUIListener#playerDeactivated(org.tramper.aui.AUIEvent)
     */
    public void playerDeactivated(AUIEvent event) {
	Player deactivatedPlayer = event.getPlayer();
	if (deactivatedPlayer != null) {
            if (playerPanel != null) {
                deactivatedPlayer.removePlayListener(playerPanel);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                	playerPanel.setEnabled(false);
                    }
                });
            }
            if (deactivatedPlayer instanceof SpeechSynthesizer) {
                if (speakerPanel != null) {
                    ((SpeechSynthesizer)deactivatedPlayer).removeSpeechListener(speakerPanel);
                }
            }
	}
    }
    
    /**
     * Returns the viewer in the target in parameter, if any.
     * @param target 
     * @return a Viewer, or null
     */
    public Viewer getViewer(Target target) {
	String frame = target.getFrame();
	String tab = target.getTab();
	
	// since we can't get the viewer associated to the tab directly,
	// we loop on all viewers and test their name, which equals tab.
	Component[] components = null;
	if (frame.equals(Library.PRIMARY_FRAME)) {
	    components = primaryPanel.getComponents();
	} else {
	    components = secondaryPanel.getComponents();
	}
	
	for (Component comp : components) {
	    String name = comp.getName();
	    if (name.equals(tab)) {
		return (Viewer)comp;
	    }
	}
	return null;
    }
    
    /**
     * 
     * @return
     */
    public List<Viewer> getRenderers() {
	List<Viewer> renderers = new ArrayList<Viewer>();

	Component[] viewers = primaryPanel.getComponents();
	for (Component viewer : viewers) {
	    renderers.add((Viewer)viewer);
	}
	viewers = secondaryPanel.getComponents();
	for (Component viewer : viewers) {
	    renderers.add((Viewer)viewer);
	}
	return renderers;
    }

    /**
     * @return currentPrimaryTarget.
     */
    public Target getCurrentPrimaryTarget() {
        return this.currentPrimaryTarget;
    }

    /**
     * @return currentSecondaryTarget.
     */
    public Target getCurrentSecondaryTarget() {
        return this.currentSecondaryTarget;
    }
    
    /**
     * 
     */
    public void switchSplitPaneOrientation() {
	int orientation = viewersArea.getOrientation();
	if (orientation == JSplitPane.VERTICAL_SPLIT) {
	    viewersArea.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
	} else {
	    viewersArea.setOrientation(JSplitPane.VERTICAL_SPLIT);
	}
	orientation = miniaturesViewersArea.getOrientation();
	if (orientation == JSplitPane.VERTICAL_SPLIT) {
	    miniaturesViewersArea.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
	    miniaturePanel.verticalLayout();
	} else {
	    miniaturesViewersArea.setOrientation(JSplitPane.VERTICAL_SPLIT);
	    miniaturePanel.horizontalLayout();
	}
    }

    /**
     * 
     * @see org.tramper.ui.UserInterface#unregister()
     */
    public void unregister() {
	LoaderFactory.removeLoaderFactoryListener(this);
	Library.getInstance().removeLibraryListener(this);
    }
}