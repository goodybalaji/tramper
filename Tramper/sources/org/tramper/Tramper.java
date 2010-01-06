package org.tramper;

import javax.swing.UIManager;

import org.fingon.FingonLookAnfFeel;
import org.tramper.aui.AudioUserInterface;
import org.tramper.doc.Favorites;
import org.tramper.doc.Feed;
import org.tramper.doc.History;
import org.tramper.doc.Library;
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
        if (JavaSystem.isJava5OrMore() == false) {
            System.err.println("You must launch this software with Java 1.5 or higher");
            System.exit(1);
        }

        //Load the favorites and historic feeds at startup
        History history = History.getInstance();
        Favorites favorite = Favorites.getInstance();

	UIManager.addAuxiliaryLookAndFeel(new FingonLookAnfFeel());
        //launch the GUI
        GraphicalUserInterface gui = UserInterfaceFactory.getGraphicalUserInterface();
        //launch the AUI
        AudioUserInterface aui = UserInterfaceFactory.getAudioUserInterface();
        aui.addAUIListener(gui);

        // Set an icon in the system tray
        new SystemTrayMgr();
        
        // load last document in the history
        while (!history.isLoaded()) {
            try {
		Thread.sleep(100);
	    } catch (InterruptedException e) {
	    }
        }
        history.loadCurrent();
        Feed fav = favorite.getFavorites();
        Library.getInstance().addDocument(fav, new Target(Library.SECONDARY_FRAME, null));
    }
}
