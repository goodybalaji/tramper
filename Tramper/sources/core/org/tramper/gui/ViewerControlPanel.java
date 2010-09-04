package org.tramper.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        LoaderFactory.getInstance().addLoaderFactoryListener(this);
	
	BoxLayout panelLayout = new BoxLayout(this, BoxLayout.X_AXIS);
	this.setLayout(panelLayout);

	this.add(Box.createHorizontalStrut(15));
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
	Dimension dimPanel = this.getSize();
	Graphics2D g2d = (Graphics2D)g;

	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	Color thisBackground = this.getBackground();
        int lighterRed = thisBackground.getRed()+40 > 255 ? 255 : thisBackground.getRed()+40;
        int lighterGreen = thisBackground.getGreen()+40 > 255 ? 255 : thisBackground.getGreen()+40;
        int lighterBlue = thisBackground.getBlue()+40 > 255 ? 255 : thisBackground.getBlue()+40;
        Color lighterBgColor = new Color(lighterRed, lighterGreen, lighterBlue);
        int darkerRed = thisBackground.getRed()-10 < 0 ? 0 : thisBackground.getRed()-10;
        int darkerGreen = thisBackground.getGreen()-10 < 0 ? 0 : thisBackground.getGreen()-10;
        int darkerBlue = thisBackground.getBlue()-10 < 0 ? 0 : thisBackground.getBlue()-10;
        Color darkerBgColor = new Color(darkerRed, darkerGreen, darkerBlue);
        int evenMoreDarkerRed = thisBackground.getRed()-40 < 0 ? 0 : thisBackground.getRed()-40;
        int evenMoreDarkerGreen = thisBackground.getGreen()-40 < 0 ? 0 : thisBackground.getGreen()-40;
        int evenMoreDarkerBlue = thisBackground.getBlue()-40 < 0 ? 0 : thisBackground.getBlue()-40;
        Color evenMoreDarkerBgColor = new Color(evenMoreDarkerRed, evenMoreDarkerGreen, evenMoreDarkerBlue);

	Point2D startPoint = new Point2D.Float(0, 0);
	Point2D endPoint = new Point2D.Float(0, dimPanel.height);
	float[] dist = {0.0f, 0.49f, 0.51f, 1.0f};
	Color[] colors = {lighterBgColor, thisBackground, darkerBgColor, evenMoreDarkerBgColor};
	LinearGradientPaint p = new LinearGradientPaint(startPoint, endPoint, dist, colors, CycleMethod.NO_CYCLE);
	g2d.setPaint(p);
	g2d.fillRect(0, 0, dimPanel.width, dimPanel.height);
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
	            GraphicalUserInterface gui = UserInterfaceFactory.getGraphicalUserInterface();
	            gui.raiseError("loadingFailed");
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
