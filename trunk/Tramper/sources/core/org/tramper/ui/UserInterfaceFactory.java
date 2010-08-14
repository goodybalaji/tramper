package org.tramper.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.Logger;
import org.tramper.aui.AudioUserInterface;
import org.tramper.gui.GUIConfig;
import org.tramper.gui.GraphicalUserInterface;


/**
 * 
 * @author Paul-Emile
 */
public class UserInterfaceFactory {
    /** logger */
    private static Logger logger = Logger.getLogger(UserInterfaceFactory.class);
    /** audio user interface */
    private static AudioUserInterface aui;
    /** graphical user interface */
    private static GraphicalUserInterface gui;
    /** auxiliary look and feel */
    private static LookAndFeel auxiliaryLaF;
    
    static {
	// add the auxiliary look and feel for assistive technology
	try {
	    Class<?> auxiliaryLaFClass = Class.forName("org.fingon.FingonLookAndFeel");
	    auxiliaryLaF = (LookAndFeel)auxiliaryLaFClass.newInstance();
	    UIManager.addAuxiliaryLookAndFeel(auxiliaryLaF);
	} catch (ClassNotFoundException e1) {
	    logger.info("no Fingon auxiliary look and feel class in classpath");
	} catch (InstantiationException e) {
	    logger.error(e);
	} catch (IllegalAccessException e) {
	    logger.error(e);
	}
	
	// for Quaqua look and feel only
	System.setProperty("Quaqua.sizeStyle","small");
	
	List<LookAndFeelInfo> lookAndFeels = new ArrayList<LookAndFeelInfo>();
	
	// remove the ugly look and feels (metal, Windows classic and motifs)
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
	/*LookAndFeelInfo substanceDustLaF = new UIManager.LookAndFeelInfo("Dust", "org.jvnet.substance.skin.SubstanceDustLookAndFeel");
	lookAndFeels.add(substanceDustLaF);
	LookAndFeelInfo substanceBusinessLaF = new UIManager.LookAndFeelInfo("Business Black Steel", "org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel");
	lookAndFeels.add(substanceBusinessLaF);
	LookAndFeelInfo substanceTwilightLaF = new UIManager.LookAndFeelInfo("Twilight", "org.jvnet.substance.skin.SubstanceTwilightLookAndFeel");
	lookAndFeels.add(substanceTwilightLaF);
	LookAndFeelInfo substanceRavenLaF = new UIManager.LookAndFeelInfo("Raven", "org.jvnet.substance.skin.SubstanceRavenLookAndFeel");
	lookAndFeels.add(substanceRavenLaF);
	LookAndFeelInfo substanceRavenGraphiteLaF = new UIManager.LookAndFeelInfo("Raven Graphite", "org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel");
	lookAndFeels.add(substanceRavenGraphiteLaF);
	LookAndFeelInfo substanceRavenGraphiteGlassLaF = new UIManager.LookAndFeelInfo("Raven Graphite Glass", "org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel");
	lookAndFeels.add(substanceRavenGraphiteGlassLaF);
	LookAndFeelInfo substanceOfficeBlue2007LaF = new UIManager.LookAndFeelInfo("Office Silver 2007", "org.jvnet.substance.skin.SubstanceOfficeSilver2007LookAndFeel");
	lookAndFeels.add(substanceOfficeBlue2007LaF);
	LookAndFeelInfo substanceMistAquaLaF = new UIManager.LookAndFeelInfo("Mist Aqua", "org.jvnet.substance.skin.SubstanceMistAquaLookAndFeel");
	lookAndFeels.add(substanceMistAquaLaF);
	LookAndFeelInfo substanceMistSilverLaF = new UIManager.LookAndFeelInfo("Mist Silver", "org.jvnet.substance.skin.SubstanceMistSilverLookAndFeel");
	lookAndFeels.add(substanceMistSilverLaF);
	LookAndFeelInfo substanceMagmaLaF = new UIManager.LookAndFeelInfo("Magma", "org.jvnet.substance.skin.SubstanceMagmaLookAndFeel");
	lookAndFeels.add(substanceMagmaLaF);
	LookAndFeelInfo substanceEmeraldDuskLaF = new UIManager.LookAndFeelInfo("Emerald dusk", "org.jvnet.substance.skin.SubstanceEmeraldDuskLookAndFeel");
	lookAndFeels.add(substanceEmeraldDuskLaF);
	LookAndFeelInfo substanceGeminiLaF = new UIManager.LookAndFeelInfo("Gemini", "org.jvnet.substance.api.skin.SubstanceGeminiLookAndFeel");
	lookAndFeels.add(substanceGeminiLaF);
	LookAndFeelInfo substanceGraphiteAquaLaF = new UIManager.LookAndFeelInfo("Graphite Aqua", "org.jvnet.substance.api.skin.SubstanceGraphiteAquaLookAndFeel");
	lookAndFeels.add(substanceGraphiteAquaLaF);
	LookAndFeelInfo substanceMagellanAquaLaF = new UIManager.LookAndFeelInfo("Magellan", "org.jvnet.substance.api.skin.SubstanceMagellanLookAndFeel");
	lookAndFeels.add(substanceMagellanAquaLaF);
	LookAndFeelInfo substanceChallengerLaF = new UIManager.LookAndFeelInfo("Challenger Deep", "org.jvnet.substance.skin.SubstanceChallengerDeepLookAndFeel");
	lookAndFeels.add(substanceChallengerLaF);
	LookAndFeelInfo substanceCremeLaF = new UIManager.LookAndFeelInfo("Creme", "org.jvnet.substance.skin.SubstanceCremeLookAndFeel");
	lookAndFeels.add(substanceCremeLaF);
	LookAndFeelInfo substanceSaharaLaF = new UIManager.LookAndFeelInfo("Sahara", "org.jvnet.substance.skin.SubstanceSaharaLookAndFeel");
	lookAndFeels.add(substanceSaharaLaF);
	LookAndFeelInfo substanceModerateLaF = new UIManager.LookAndFeelInfo("Moderate", "org.jvnet.substance.skin.SubstanceModerateLookAndFeel");
	lookAndFeels.add(substanceModerateLaF);
	LookAndFeelInfo substanceNebulaLaF = new UIManager.LookAndFeelInfo("Nebula", "org.jvnet.substance.skin.SubstanceNebulaLookAndFeel");
	lookAndFeels.add(substanceNebulaLaF);
	LookAndFeelInfo substanceAutumnLaF = new UIManager.LookAndFeelInfo("Autumn", "org.jvnet.substance.skin.SubstanceAutumnLookAndFeel");
	lookAndFeels.add(substanceAutumnLaF);*/
	
	// install the new look and feels
	UIManager.setInstalledLookAndFeels(lookAndFeels.toArray(new UIManager.LookAndFeelInfo[lookAndFeels.size()]));

	GUIConfig guiConfig = new GUIConfig();

	//try to load the user chosen look and feel
	final String lookAndFeel = guiConfig.getLookAndFeel();
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
        	try {
        	    UIManager.setLookAndFeel(lookAndFeel);
        	} catch (Exception e) {
        	    logger.warn("unable to load the look and feel at startup", e);
        	}
        	UIManager.put("ProgressBarUI", "org.tramper.gui.CompactProgressBarUI");
	    }
	});
    }
    
    /**
     * 
     */
    private UserInterfaceFactory() {
    }
    
    /**
     * 
     * @return
     */
    public static GraphicalUserInterface getGraphicalUserInterface() {
	if (gui == null) {
	    Runnable r = new Runnable() {
		public void run() {
		    gui = new GraphicalUserInterface();
		}
	    };
	    if (SwingUtilities.isEventDispatchThread()) {
		r.run();
	    } else {
		try {
		    SwingUtilities.invokeAndWait(r);
		} catch (Exception e) {
		    logger.error(e.getMessage(), e);
		}
	    }
	}
	return gui;
    }

    /**
     * 
     * @return
     */
    public static AudioUserInterface getAudioUserInterface() {
	if (aui == null) {
	    aui = new AudioUserInterface();
	}
	return aui;
    }
    
    /**
     * 
     * @return
     */
    public static List<UserInterface> getAllUserInterfaces() {
	List<UserInterface> ui = new ArrayList<UserInterface>();
	if (aui != null) {
	    ui.add(aui);
	}
	if (gui != null) {
	    ui.add(gui);
	}
	return ui;
    }
    
    /**
     * 
     */
    public static void removeGraphicalUserInterface() {
	if (gui != null) {
	    gui.dispose();
	    gui.unregister();
	    gui = null;
	}
    }
    
    /**
     * 
     */
    public static void removeAudioUserInterface() {
	if (aui != null) {
	    UIManager.removeAuxiliaryLookAndFeel(auxiliaryLaF);
	    SwingUtilities.updateComponentTreeUI(gui);
	    aui.unregister();
	    aui = null;
	}
    }

    /**
     * 
     */
    public static void restoreAudioUserInterface() {
	if (aui == null) {
	    UIManager.addAuxiliaryLookAndFeel(auxiliaryLaF);
	    SwingUtilities.updateComponentTreeUI(gui);
	    aui = new AudioUserInterface();
	}
    }
    
    /**
     * 
     * @return
     */
    public static boolean isGraphicalUserInterfaceInstanciated() {
	return (gui != null);
    }
    
    /**
     * 
     * @return
     */
    public static boolean isAudioUserInterfaceInstanciated() {
	return (aui != null);
    }
}
