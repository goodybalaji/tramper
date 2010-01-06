package org.tramper.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.tramper.doc.DocumentEvent;
import org.tramper.doc.DocumentListener;
import org.tramper.doc.Favorites;
import org.tramper.doc.Library;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.gui.viewer.Body;
import org.tramper.gui.viewer.Viewer;

/**
 * 
 * @author Paul-Emile
 */
public class ViewerMiniature extends JPanel implements MouseListener, DocumentListener {
    /** ViewerMiniature.java long */
    private static final long serialVersionUID = 1L;
    /** Miniaturized component */
    private Viewer miniaturised;
    /** title */
    private SimpleDocument document;
    /** margin width */
    private int marginWidth = 4;
    /** border width */
    private int borderWidth = 2;
    /** mouse is on the component */
    private boolean mouseOn;
    /** target */
    private Target target;

    /**
     * 
     * @param miniaturised
     */
    public ViewerMiniature(Viewer aViewer) {
	this.setViewer(aViewer);
	this.addMouseListener(this);
	this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public Viewer getViewer() {
	return miniaturised;
    }
    
    public void setViewer(Viewer newViewer) {
	this.miniaturised = newViewer;
	this.target = newViewer.getTarget();
	this.document = newViewer.getDocument();
	document.addDocumentListener(this);
	this.setToolTipText(document.getTitle());
    }
    
    /**
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    @Override
    public void paint(Graphics g) {
	super.paint(g);
	Graphics2D g2d = (Graphics2D)g;
	Dimension miniatureSize = getSize();

	// choose the colors of the title, its background and the borders
	Color titleBackground = null;
	Color titleForeground = null;
	if (document.isActive()) {
	    titleBackground = UIManager.getColor("TextField.selectionBackground");
	    if (titleBackground == null) {
		titleBackground = UIManager.getColor("textHighlight");
	    }
	    titleForeground = UIManager.getColor("TextField.selectionForeground");
	    if (titleForeground == null) {
		titleForeground = UIManager.getColor("textHighlightText");
	    }
	} else {
	    titleBackground = UIManager.getColor("TextField.background");
	    if (titleBackground == null) {
		titleBackground = UIManager.getColor("text");
	    }
	    titleForeground = UIManager.getColor("TextField.foreground");
	    if (titleForeground == null) {
		titleForeground = UIManager.getColor("textText");
	    }
	}
	Color borderColor = titleBackground;
        
	Body body = miniaturised.getBody();
	g2d.setClip(marginWidth, marginWidth, miniatureSize.width - 2*marginWidth, miniatureSize.height - 2*marginWidth);
	body.paintMiniature(g2d, miniatureSize, mouseOn);
	g2d.setClip(null);

	// draw a spot light to the top left corner if the mouse is over the miniature
	/*if (mouseOn) {
	    int blue = UIManager.getColor("TextField.selectionBackground").getBlue();
	    int red = UIManager.getColor("TextField.selectionBackground").getRed();
	    int green = UIManager.getColor("TextField.selectionBackground").getGreen();
	    Color spotLightColor = new Color(red, green, blue, 75);
	    
	    Point2D center = new Point2D.Float(miniatureSize.height/3, miniatureSize.height/3);
	    float radius = miniatureSize.height/3;
	    Point2D focus = new Point2D.Float(miniatureSize.height/4, miniatureSize.height/4);
	    float[] dist = {0.1f, 0.7f, 0.9f};
	    Color bgColor = new Color(255, 255, 255, 0);
	    Color coreColor = new Color(255, 255, 255, 200);
	    Color[] colors = {coreColor, spotLightColor, bgColor};
	    RadialGradientPaint p = new RadialGradientPaint(center, radius, focus, dist, colors, CycleMethod.NO_CYCLE);
	    g2d.setPaint(p);
	    g2d.fillRect(0, 0, miniatureSize.height*2/3, miniatureSize.height*2/3);
	}*/
	
	// apply transparency for favorite icon and title
	AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
        g2d.setComposite(composite);
        
	// favorite icon
	boolean isFavorite = Favorites.getInstance().isFavorite(document);
	if (isFavorite) {
	    URL imgUrl = this.getClass().getResource("images/Favorites.png");
	    Icon icon = new ImageIcon(imgUrl);
	    icon.paintIcon(this, g2d, marginWidth + borderWidth, marginWidth + borderWidth);
	}
	
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	// get title metrics
	String title = document.getTitle();
	if (title == null) {
	    title = "";
	}
	FontRenderContext frc = g2d.getFontRenderContext();
	Font labelFont = UIManager.getFont("Label.font");
	if (document.isActive()) {
	    labelFont = labelFont.deriveFont(Font.BOLD);
	}
	LineMetrics metrics = labelFont.getLineMetrics(title, frc);
	
	// fill title background
	g2d.setColor(titleBackground);
        int titleBackgroundX = marginWidth;
        int titleBackgroundY = miniatureSize.height - ((int)metrics.getHeight() + marginWidth);
        int titleBackgroundWidth = miniatureSize.width - 2*marginWidth;
        int titleBackgroundHeight = (int)metrics.getHeight();
	g2d.fillRect(titleBackgroundX, titleBackgroundY, titleBackgroundWidth, titleBackgroundHeight);
	
	// write title
	composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
        g2d.setComposite(composite);
	g2d.setColor(titleForeground);
	g2d.setFont(labelFont);
	FontMetrics fontMetrics = g2d.getFontMetrics();
	int titleWidth = fontMetrics.stringWidth(title);
	// cut the title if it is too width for the miniature
	if (titleWidth > titleBackgroundWidth) {
	    int titleCharLength = title.length();
	    int visiblePart = (titleCharLength*titleBackgroundWidth)/titleWidth - 2;
	    title = title.substring(0, visiblePart) + "...";
	}
	int titleX = marginWidth + borderWidth;
	int titleY = miniatureSize.height - (int)(metrics.getDescent() + metrics.getLeading() + marginWidth);
	g2d.drawString(title, titleX, titleY);
	
	// draw border
	g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        int borderX = marginWidth;
        int borderY = marginWidth;
        int borderShapeWidth = miniatureSize.width - 2*marginWidth;
        int borderShapeHeight = miniatureSize.height - 2*marginWidth;
	g2d.drawRect(borderX, borderY, borderShapeWidth, borderShapeHeight);
	Color darkerColor = borderColor.darker();
	g2d.setColor(darkerColor);
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        borderX = marginWidth;
        borderY = marginWidth;
        borderShapeWidth = miniatureSize.width - 2*marginWidth;
        borderShapeHeight = miniatureSize.height - 2*marginWidth;
	g2d.drawRect(borderX, borderY, borderShapeWidth, borderShapeHeight);
    }

    /**
     * @see javax.swing.JComponent#getMinimumSize()
     */
    @Override
    public Dimension getMinimumSize() {
	return new Dimension(50, 38);
    }

    /**
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize() {
	return new Dimension(100, 75);
    }

    /**
     * @see javax.swing.JComponent#getMaximumSize()
     */
    @Override
    public Dimension getMaximumSize() {
	return new Dimension(200, 150);
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
	this.mouseOn = true;
	marginWidth = 1;
	Body body = miniaturised.getBody();
	if (((Component)body).getWidth() > 0 && ((Component)body).getHeight() > 0) {
	    this.repaint();
	}
    }

    public void mouseExited(MouseEvent e) {
	this.mouseOn = false;
	marginWidth = 4;
	Body body = miniaturised.getBody();
	if (((Component)body).getWidth() > 0 && ((Component)body).getHeight() > 0) {
	    this.repaint();
	}
    }

    public void mousePressed(MouseEvent e) {
	int button = e.getButton();
	if (button == MouseEvent.BUTTON1) {
	    Library.getInstance().setActiveDocument(target);
	}
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void documentActivated(DocumentEvent event) {
	this.repaint();
    }

    public void documentDeactivated(DocumentEvent event) {
	this.repaint();
    }
}
