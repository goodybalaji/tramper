package org.tramper;

import org.tramper.aui.AudioUserInterface;
import org.tramper.doc.Favorites;
import org.tramper.doc.History;
import org.tramper.gui.GraphicalUserInterface;
import org.tramper.gui.SystemTrayMgr;
import org.tramper.ui.UserInterfaceFactory;

/**
 * Launch Tramper
 * @author Paul-Emile
 */
public class Tramper {

    /**
     * Launch the GUI
     * @param args
     */
    public static void main(String[] args) {
        if (JavaSystem.isJava6OrMore() == false) {
            System.err.println("You must launch this software with Java 1.6 or higher");
            System.exit(1);
        }

        //Load the favorites and historic feeds at startup
        History history = History.getInstance();
        Favorites.getInstance();

        //launch the GUI
        GraphicalUserInterface gui = UserInterfaceFactory.getGraphicalUserInterface();
        //launch the AUI
        AudioUserInterface aui = UserInterfaceFactory.getAudioUserInterface();
        aui.addAUIListener(gui);

        // Set an icon in the system tray
        new SystemTrayMgr();
        
        // load last document of the history
        while (!history.isLoaded()) {
            try {
		Thread.sleep(100);
	    } catch (InterruptedException e) {}
        }
        history.loadCurrent();
    }
}
