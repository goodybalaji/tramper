package org.tramper.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.tramper.doc.Library;
import org.tramper.gui.viewer.Viewer;
import org.tramper.loader.Loader;
import org.tramper.loader.LoaderFactory;
import org.tramper.loader.LoaderFactoryEvent;
import org.tramper.loader.LoaderFactoryListener;
import org.tramper.loader.LoadingEvent;
import org.tramper.loader.LoadingListener;
import org.tramper.ui.UserInterface;
import org.tramper.ui.UserInterfaceFactory;

/**
 * Displays the miniatures of the viewers and the loading viewers.
 * @author Paul-Emile
 */
public class ViewerControlPanel extends JPanel implements LoadingListener, LoaderFactoryListener {
    /** ViewerControlPanel.java long */
    private static final long serialVersionUID = 1L;
    /** logger */
    private Logger logger = Logger.getLogger(ViewerControlPanel.class);
    /**  */
    private Map<Loader, LoadingViewer> loadingViewers;

    public ViewerControlPanel(GraphicalUserInterface main) {
	loadingViewers = new HashMap<Loader, LoadingViewer>();
        LoaderFactory.addLoaderFactoryListener(this);
	
	BoxLayout panelLayout = new BoxLayout(this, BoxLayout.X_AXIS);
	this.setLayout(panelLayout);

	this.add(Box.createGlue());
	List<Viewer> viewers = main.getRenderers();
	for (Viewer viewer : viewers) {
	    addMiniature(viewer);
	}
    }
    
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	URL backgroundUrl = this.getClass().getResource("images/background-miniatures.png");
	Dimension dimPanel = this.getSize();
	Graphics2D g2d = (Graphics2D)g;
	try {
	    BufferedImage backgroundImage = ImageIO.read(backgroundUrl);
	    int imageWidth = backgroundImage.getWidth();
	    int imageHeight = backgroundImage.getHeight();
	    g2d.drawImage(backgroundImage, dimPanel.width - imageWidth, dimPanel.height - imageHeight, this);
	} catch (Exception e) {}
    }

    public void addMiniature(Viewer viewer) {
        ViewerMiniature mini = new ViewerMiniature(viewer);
        String frame = viewer.getTarget().getFrame();
        if (Library.PRIMARY_FRAME.equals(frame)) {
            this.add(mini, -1);
        } else {
            this.add(mini, 0);
        }
    }
    
    public void modifyMiniature(Viewer oldViewer, Viewer newViewer) {
	int componentCount = this.getComponentCount();
	for (int i=0; i<componentCount; i++) {
	    Component aComponent = this.getComponent(i);
	    if (aComponent instanceof ViewerMiniature) {
    	    	ViewerMiniature miniature = (ViewerMiniature)aComponent;
    	    	Viewer currentViewer = miniature.getViewer();
    	    	if (currentViewer.equals(oldViewer)) {
    	    	    miniature.setViewer(newViewer);
    	    	    break;
    	    	}
	    }
	}
    }
    
    public void removeMiniature(Viewer viewer) {
	int componentCount = this.getComponentCount();
	for (int i=0; i<componentCount; i++) {
	    Component aComponent = this.getComponent(i);
	    if (aComponent instanceof ViewerMiniature) {
    	    	ViewerMiniature miniature = (ViewerMiniature)aComponent;
    	    	Viewer aViewer = miniature.getViewer();
    	    	if (aViewer.equals(viewer)) {
    	    	    this.remove(miniature);
    	    	    this.validate();
    	    	    break;
    	    	}
	    }
	}
    }
    
    public void horizontalLayout() {
	BoxLayout panelLayout = new BoxLayout(this, BoxLayout.X_AXIS);
	this.setLayout(panelLayout);
    }
    
    public void verticalLayout() {
	BoxLayout panelLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
	this.setLayout(panelLayout);
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
		add(loadingViewer, 0);
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
		    remove(loadingViewer);
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
		    remove(loadingViewer);
		    UserInterfaceFactory.getGraphicalUserInterface().validate();
	            List<UserInterface> ui = UserInterfaceFactory.getAllUserInterfaces();
	            for (UserInterface anUi : ui) {
	        	anUi.raiseError("loadingFailed");
	            }
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
		    remove(loadingViewer);
        	    UserInterfaceFactory.getGraphicalUserInterface().validate();
        	}
	    });
        }
    }

    /**
     * 
     * @see javax.swing.JComponent#getMaximumSize()
     */
    @Override
    public Dimension getMaximumSize() {
	return new Dimension(200, 150);
    }

    public void relocalize() {
	int componentCount = this.getComponentCount();
	for (int i=0; i<componentCount; i++) {
	    Component aComponent = this.getComponent(i);
	    if (aComponent instanceof ViewerMiniature) {
    	    	ViewerMiniature miniature = (ViewerMiniature)aComponent;
    	    	miniature.relocalize();
	    }
	}
    }
}
