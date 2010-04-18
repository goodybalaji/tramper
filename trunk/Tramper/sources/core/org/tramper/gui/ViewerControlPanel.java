package org.tramper.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
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
    /** rescaled background image */
    private BufferedImage rescaledImage;
    /** background image */
    private BufferedImage backgroundImage;

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

	URL backgroundUrl = this.getClass().getResource("images/blue_ice.jpg");
	try {
	    backgroundImage = ImageIO.read(backgroundUrl);
	    rescaledImage = new BufferedImage(backgroundImage.getWidth(), backgroundImage.getHeight(), backgroundImage.getType());
	    rescaleImage();
	} catch (Exception e) {}
    }
    
    /**
     * @see javax.swing.JPanel#updateUI()
     */
    @Override
    public void updateUI() {
	super.updateUI();
	rescaleImage();
    }

    private void rescaleImage() {
	if (backgroundImage != null) {
	    Color backgroundColor = this.getBackground();
	    float redOffset = backgroundColor.getRed() - 128;
	    float greenOffset = backgroundColor.getGreen() - 128;
	    float blueOffset = backgroundColor.getBlue() - 128;
	    RescaleOp op = new RescaleOp(new float[]{1f, 1f, 1f}, new float[]{redOffset, greenOffset, blueOffset}, null);
	    op.filter(backgroundImage, rescaledImage);
	}
    }
    
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	Dimension dimPanel = this.getSize();
	Graphics2D g2d = (Graphics2D)g;
	Composite currentComposite = g2d.getComposite();
	g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
	if (rescaledImage != null) {
	    int imageWidth = rescaledImage.getWidth();
	    int imageHeight = rescaledImage.getHeight();
	    g2d.drawImage(rescaledImage, dimPanel.width - imageWidth, dimPanel.height - imageHeight, this);
	}
	g2d.setComposite(currentComposite);
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
