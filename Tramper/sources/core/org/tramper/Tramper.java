package org.tramper;

import org.tramper.aui.AudioUserInterface;
import org.tramper.doc.Favorites;
import org.tramper.doc.History;
import org.tramper.doc.Library;
import org.tramper.doc.Outline;
import org.tramper.doc.Target;
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
        Favorites favorite = Favorites.getInstance();

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
        Outline fav = favorite.getFavorites();
        Library.getInstance().addDocument(fav, new Target(Library.SECONDARY_FRAME, null));
    }
}
