package org.tramper.gui;

import java.awt.Dimension;

import javax.swing.JSplitPane;

/**
 * This class is necessary to redefine the getMinimumSize() method of this
 * SplitPane, which will allow to prevent the parent SplitPane to give too much
 * space to the miniature panel.
 * @author Paul-Emile
 */
public class ViewerSplitPane extends JSplitPane {
    /**
     * ViewerSplitPane.java long
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public ViewerSplitPane() {
	super();
	this.setOneTouchExpandable(true);
	this.setContinuousLayout(true);
	this.setDividerSize(6);
	this.setDividerLocation(0);
    }

    /**
     * @see javax.swing.JComponent#getMinimumSize()
     */
    @Override
    public Dimension getMinimumSize() {
	JSplitPane parent = (JSplitPane)this.getParent();
	Dimension parentSize = parent.getSize();
	Dimension miniatureMaximumSize = parent.getLeftComponent().getMaximumSize();
	Dimension minimumSize = new Dimension(parentSize.width - miniatureMaximumSize.width, parentSize.height - miniatureMaximumSize.height);
	return minimumSize;
    }
}
