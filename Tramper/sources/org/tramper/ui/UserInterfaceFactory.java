package org.tramper.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.tramper.aui.AudioUserInterface;
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
	    try {
		SwingUtilities.invokeAndWait(new Runnable() {
		    public void run() {
    		    	gui = new GraphicalUserInterface();
		    }
		});
	    } catch (InterruptedException e) {
		logger.error(e.getMessage(), e);
	    } catch (InvocationTargetException e) {
		logger.error(e.getMessage(), e);
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
	    aui.unregister();
	    aui = null;
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
