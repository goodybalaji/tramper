package org.tramper.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class CompactProgressBarUI extends BasicProgressBarUI {

    private Color backgroundHighlight;
    private Color foregroundHighlight;
    
    public static ComponentUI createUI(JComponent x) {
	return new CompactProgressBarUI();
    }

    /**
     * @see javax.swing.plaf.basic.BasicProgressBarUI#installDefaults()
     */
    @Override
    protected void installDefaults() {
        LookAndFeel.installProperty(progressBar, "opaque", Boolean.FALSE);
	LookAndFeel.installColorsAndFont(progressBar,
					 "ProgressBar.background",
					 "ProgressBar.foreground",
					 "ProgressBar.font");
        backgroundHighlight = UIManager.getColor("ProgressBar.backgroundHighlight");
        foregroundHighlight = UIManager.getColor("ProgressBar.foregroundHighlight");
    }

    /**
     * @see javax.swing.plaf.basic.BasicProgressBarUI#uninstallDefaults()
     */
    @Override
    protected void uninstallDefaults() {
    }

    /**
     * @see javax.swing.plaf.basic.BasicProgressBarUI#getMaximumSize(javax.swing.JComponent)
     */
    @Override
    public Dimension getMaximumSize(JComponent c) {
	return new Dimension(45, 45);
    }

    /**
     * @see javax.swing.plaf.basic.BasicProgressBarUI#getMinimumSize(javax.swing.JComponent)
     */
    @Override
    public Dimension getMinimumSize(JComponent c) {
	return new Dimension(15, 15);
    }

    /**
     * @see javax.swing.plaf.basic.BasicProgressBarUI#getPreferredSize(javax.swing.JComponent)
     */
    @Override
    public Dimension getPreferredSize(JComponent c) {
	return new Dimension(30, 30);
    }

    /**
     * @see javax.swing.plaf.basic.BasicProgressBarUI#getBox(java.awt.Rectangle)
     */
    @Override
    protected Rectangle getBox(Rectangle r) {
	return new Rectangle(progressBar.getSize());
    }

    /**
     * @see javax.swing.plaf.basic.BasicProgressBarUI#paintDeterminate(java.awt.Graphics, javax.swing.JComponent)
     */
    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
	Graphics2D g2d = (Graphics2D)g;
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	JProgressBar progressBar = (JProgressBar)c;
	Insets insets = progressBar.getInsets(); // area for border
	int barRectWidth = progressBar.getWidth() - (insets.right + insets.left);
	int barRectHeight = progressBar.getHeight() - (insets.top + insets.bottom);
        
	Stroke stroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	g2d.setStroke(stroke);
	Rectangle2D.Float ellipseBoundary = new Rectangle2D.Float(insets.left, insets.top, barRectWidth, barRectHeight);

	double percentComplete = progressBar.getPercentComplete();
	int frameCount = 8;
	double animationIndex = frameCount*percentComplete;
	float startingAngle = 0;
	float angularExtent = 360/frameCount;
	for (int i=0; i<frameCount; i++) {
	    startingAngle += angularExtent;
	    if (i < animationIndex) {
		g2d.setColor(backgroundHighlight);
	    } else {
		g2d.setColor(foregroundHighlight);
	    }
	    Arc2D.Float anArc = new Arc2D.Float(ellipseBoundary, startingAngle, angularExtent-10, Arc2D.PIE);
	    g2d.fill(anArc);
	    /*if (i < animationIndex) {
		g2d.setColor(backgroundHighlight.brighter());
	    } else {
		g2d.setColor(foregroundHighlight.darker());
	    }
	    g2d.draw(anArc);*/
	}

	g2d.setColor(progressBar.getParent().getBackground());
	Ellipse2D.Float center = new Ellipse2D.Float(progressBar.getWidth()/4, progressBar.getHeight()/4, progressBar.getWidth()/2, progressBar.getHeight()/2);
	g2d.fill(center);
    }

    /**
     * @see javax.swing.plaf.basic.BasicProgressBarUI#paintIndeterminate(java.awt.Graphics, javax.swing.JComponent)
     */
    @Override
    protected void paintIndeterminate(Graphics g, JComponent c) {
	Graphics2D g2d = (Graphics2D)g;
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	JProgressBar progressBar = (JProgressBar)c;
	int frameCount = getFrameCount();
	int animationIndex = getAnimationIndex();
	
	Insets insets = progressBar.getInsets(); // area for border
	int barRectWidth = progressBar.getWidth() - (insets.right + insets.left);
	int barRectHeight = progressBar.getHeight() - (insets.top + insets.bottom);
        
	Stroke stroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	g2d.setStroke(stroke);
	Rectangle2D.Float ellipseBoundary = new Rectangle2D.Float(insets.left, insets.top, barRectWidth, barRectHeight);

	float startingAngle = 0;
	float angularExtent = 360/frameCount;
	for (int i=0; i<frameCount; i++) {
	    startingAngle += angularExtent;
	    if (i == animationIndex) {
		g2d.setColor(backgroundHighlight);
	    } else {
		g2d.setColor(foregroundHighlight);
	    }
	    Arc2D.Float anArc = new Arc2D.Float(ellipseBoundary, startingAngle, angularExtent-10, Arc2D.PIE);
	    g2d.fill(anArc);
	}
	
	g2d.setColor(progressBar.getParent().getBackground());
	Ellipse2D.Float center = new Ellipse2D.Float(progressBar.getWidth()/4, progressBar.getHeight()/4, progressBar.getWidth()/2, progressBar.getHeight()/2);
	g2d.fill(center);
    }
}