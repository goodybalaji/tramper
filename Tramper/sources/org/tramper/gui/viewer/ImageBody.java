package org.tramper.gui.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.EnhancedIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.tramper.doc.ImageDocument;
import org.tramper.doc.Library;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;

/**
 * @author Paul-Emile
 * 
 */
public class ImageBody extends JScrollPane implements Body, MouseListener {
    /** ImageBody.java long */
    private static final long serialVersionUID = 1L;
    /** progress icon */
    protected EnhancedIcon runningIcon;
    /** progress icon */
    private JLabel progress;
    /** target */
    private Target target;
    /** document */
    protected ImageDocument document;

    public ImageBody() {
	super();
	runningIcon = new EnhancedIcon();
	progress = new JLabel(runningIcon);
	progress.setBackground(Color.BLACK);
	progress.setOpaque(true);
	progress.addMouseListener(this);
	this.setViewportView(progress);
	this.setBackground(Color.BLACK);
	this.setOpaque(true);
    }
    
    /**
     * @see org.tramper.gui.viewer.Body#displayDocument(org.tramper.doc.SimpleDocument, org.tramper.doc.Target, int)
     */
    public void displayDocument(SimpleDocument doc, Target target, int documentPart) {
	if (!(doc instanceof ImageDocument)) {
	    throw new RuntimeException(doc.getTitle()+" is not an image");
	}
	this.target = target;
	document = (ImageDocument)doc;
        runningIcon.setImage(document.getImage());
    }

    /**
     * @see org.tramper.gui.viewer.Body#first()
     */
    public void first() {
    }

    /**
     * @see org.tramper.gui.viewer.Body#last()
     */
    public void last() {
    }

    /**
     * @see org.tramper.gui.viewer.Body#next()
     */
    public void next() {
    }

    /**
     * @see org.tramper.gui.viewer.Body#previous()
     */
    public void previous() {
    }

    public void mouseClicked(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent event) {
	int clickedButton = event.getButton();
	if (clickedButton == MouseEvent.BUTTON1) {
	    Library.getInstance().setActiveDocument(target);
	}
    }

    public void mouseReleased(MouseEvent e) {
    }

    /**
     * @see org.tramper.gui.viewer.Body#paintMiniature(java.awt.Graphics2D, java.awt.Dimension, boolean)
     */
    public void paintMiniature(Graphics2D g2d, Dimension miniatureSize, boolean mouseOver) {
	Dimension documentSize = progress.getSize();
	int imageWidth = document.getImage().getWidth();
	
	double scale = (double)miniatureSize.width/(double)imageWidth;
	if (scale > 1) {
	    scale = 1.0;
	}
	g2d.scale(scale, scale);
	g2d.translate(-(documentSize.width - miniatureSize.width)/2*scale, -(documentSize.height - miniatureSize.height)/2*scale);
	
	progress.paint(g2d);

	// reset scale, translation
	g2d.translate((documentSize.width - miniatureSize.width)/2*scale, (documentSize.height - miniatureSize.height)/2*scale);
	g2d.scale(1/scale, 1/scale);
    }
}
