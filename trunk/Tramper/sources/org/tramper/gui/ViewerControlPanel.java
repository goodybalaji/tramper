package org.tramper.gui;

import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.tramper.gui.viewer.Viewer;

/**
 * 
 * @author Paul-Emile
 */
public class ViewerControlPanel extends JPanel {
    /** ViewerControlPanel.java long */
    private static final long serialVersionUID = 1L;

    public ViewerControlPanel(GraphicalUserInterface main) {
	BoxLayout panelLayout = new BoxLayout(this, BoxLayout.X_AXIS);
	this.setLayout(panelLayout);
	
	List<Viewer> viewers = main.getRenderers();
	for (Viewer viewer : viewers) {
	    addMiniature(viewer);
	}
	this.add(Box.createGlue());
    }
    
    public void addMiniature(Viewer viewer) {
        ViewerMiniature mini = new ViewerMiniature(viewer);
        this.add(mini, 0);
    }
    
    public void modifyMiniature(Viewer oldViewer, Viewer newViewer) {
	int componentCount = this.getComponentCount();
	// we don't want to test the last component, the glue
	for (int i=0; i<componentCount-1; i++) {
	    ViewerMiniature miniature = (ViewerMiniature)this.getComponent(i);
	    Viewer currentViewer = miniature.getViewer();
	    if (currentViewer.equals(oldViewer)) {
		miniature.setViewer(newViewer);
		break;
	    }
	}
    }
    
    public void removeMiniature(Viewer viewer) {
	int componentCount = this.getComponentCount();
	// we don't want to test the last component, the glue
	for (int i=0; i<componentCount-1; i++) {
	    ViewerMiniature mini = (ViewerMiniature)this.getComponent(i);
	    Viewer aViewer = mini.getViewer();
	    if (aViewer.equals(viewer)) {
		this.remove(mini);
		this.validate();
		break;
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
}
