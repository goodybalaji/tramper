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
	    // for Quaqua look and feel
	    auxiliaryLaF.getDefaults().remove("ComboBoxUI");
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
	try {
	    Class.forName("ch.randelshofer.quaqua.QuaquaLookAndFeel");
	    LookAndFeelInfo quaquaLaF = new UIManager.LookAndFeelInfo("Quaqua", "ch.randelshofer.quaqua.QuaquaLookAndFeel");
	    lookAndFeels.add(quaquaLaF);
	} catch (ClassNotFoundException e) {}

	try {
	    Class.forName("org.pushingpixels.substance.api.skin.SubstanceMistAquaLookAndFeel");
	    LookAndFeelInfo substanceLaF = new UIManager.LookAndFeelInfo("Substance", "org.pushingpixels.substance.api.skin.SubstanceMistAquaLookAndFeel");
	    lookAndFeels.add(substanceLaF);
	} catch (ClassNotFoundException e) {}
	
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
     */
    public static void removeGraphicalUserInterface() {
	if (gui != null) {
	    gui.unregister();
	    gui = null;
	}
    }
    
    /**
     * 
     */
    public static void removeAudioUserInterface() {
	if (aui != null) {
	    if (gui != null) {
		UIManager.removeAuxiliaryLookAndFeel(auxiliaryLaF);
		SwingUtilities.updateComponentTreeUI(gui);
	    }
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
