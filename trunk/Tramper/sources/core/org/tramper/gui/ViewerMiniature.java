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
import java.awt.Insets;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.EnhancedIcon;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.tramper.action.RemoveDocumentAction;
import org.tramper.doc.DocumentEvent;
import org.tramper.doc.DocumentListener;
import org.tramper.doc.Favorites;
import org.tramper.doc.Library;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.gui.viewer.Body;
import org.tramper.gui.viewer.Viewer;
import org.tramper.ui.UserInterfaceFactory;

/**
 * Displays a miniature of a viewer.
 * @author Paul-Emile
 */
public class ViewerMiniature extends JPanel implements MouseListener, DocumentListener, ItemListener {
    /** ViewerMiniature.java long */
    private static final long serialVersionUID = 1L;
    /** Miniaturized component */
    private Viewer miniaturised;
    /** title */
    private SimpleDocument document;
    /** margin width */
    private int marginWidth = 4;
    /** border width */
    private int borderWidth = 1;
    /** mouse is on the component */
    private boolean mouseOn;
    /** target */
    private Target target;
    /** close control panel button */
    private JButton closeButton;
    /** favorites button */
    private JToggleButton favoriteButton;
    /** details button */
    private JToggleButton detailButton;

    /**
     * Displays a miniature of the viewer in parameter 
     * with a few buttons to control the viewer: 
     * a close button, an add/remove from favorites, a show/hide details.
     * @param aViewer a viewer to miniaturize
     */
    public ViewerMiniature(Viewer aViewer) {
	SpringLayout layoutMgr = new SpringLayout();
	this.setLayout(layoutMgr);
	
	this.setOpaque(false);
	
        ResourceBundle label = ResourceBundle.getBundle("label");
        Insets marginButton = new Insets(0, 0, 0, 0);

        favoriteButton = new JToggleButton();
        Icon selectedFavIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/Favorites.png"));
        favoriteButton.setSelectedIcon(selectedFavIcon);
        Icon favIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/Favorites_inverted.png"));
        favoriteButton.setIcon(favIcon);
        favoriteButton.addItemListener(this);
        favoriteButton.setMargin(marginButton);
        this.add(favoriteButton);
        layoutMgr.putConstraint(SpringLayout.WEST, favoriteButton, marginWidth + borderWidth, SpringLayout.WEST, this);
        layoutMgr.putConstraint(SpringLayout.NORTH, favoriteButton, marginWidth + borderWidth, SpringLayout.NORTH, this);

        detailButton = new JToggleButton();
        // do not use "../" in the resource's path because it won't work inside of a jar
        URL iconUrl = getClass().getResource("/org/tramper/gui/images/remove.png");
        Icon selectedDetailIcon = new EnhancedIcon(iconUrl);
	detailButton.setSelectedIcon(selectedDetailIcon);
        Icon detailIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/add.png"));
        detailButton.setIcon(detailIcon);
        detailButton.addItemListener(this);
        detailButton.setMargin(marginButton);
	detailButton.setToolTipText(label.getString("javaspeaker.showDetails"));
        this.add(detailButton);
        layoutMgr.putConstraint(SpringLayout.WEST, detailButton, 2, SpringLayout.EAST, favoriteButton);
        layoutMgr.putConstraint(SpringLayout.NORTH, detailButton, marginWidth + borderWidth, SpringLayout.NORTH, this);
        
        Icon closeIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/Error.png"));
        closeButton = new JButton(closeIcon);
        closeButton.setMargin(marginButton);
        this.add(closeButton);
        layoutMgr.putConstraint(SpringLayout.EAST, closeButton, -marginWidth - borderWidth, SpringLayout.EAST, this);
        layoutMgr.putConstraint(SpringLayout.NORTH, closeButton, marginWidth + borderWidth, SpringLayout.NORTH, this);
        
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
	
        closeButton.setAction(new RemoveDocumentAction(target));

        ResourceBundle label = ResourceBundle.getBundle("label");
        Favorites fav = Favorites.getInstance();
        if (fav.isFavorite(document)) {
            favoriteButton.setSelected(true);
            favoriteButton.setToolTipText(label.getString("javaspeaker.removeFavorite"));
        } else {
            favoriteButton.setSelected(false);
            favoriteButton.setToolTipText(label.getString("javaspeaker.addFavorite"));
        }
    }
    
    /**
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    @Override
    public void paint(Graphics g) {
	Graphics2D g2d = (Graphics2D)g;
	Dimension miniatureSize = getSize();
	marginWidth = 2;

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
	Color outerBorder = titleForeground;

	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	
	float arcWidth = 20;
	float arcHeight = 20;
	RoundRectangle2D.Float roundRect = new RoundRectangle2D.Float(marginWidth, marginWidth, miniatureSize.width - 2*marginWidth, miniatureSize.height - 2*marginWidth, arcWidth, arcHeight);
	g2d.setClip(roundRect);
	
	// paint the background, if the viewer is not wide enough for the miniature
	g2d.setPaint(getBackground());
	g2d.fillRect(0, 0, getWidth(), getHeight());
	
	// paint he miniature itself
	Body body = miniaturised.getBody();
	body.paintMiniature(g2d, miniatureSize, mouseOn);

	// draw a halo around the miniature if the mouse is over the miniature
	if (mouseOn) {
	    Color extenalColor = new Color(230, 230, 230, 100);
	    Point2D center = new Point2D.Float(miniatureSize.width/2f, miniatureSize.height/2f);
	    float radius = miniatureSize.width/2f;
	    float[] dist = {0.5f, 1.0f};
	    Color internalColor = new Color(255, 255, 255, 0);
	    Color[] colors = {internalColor, extenalColor};
	    RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors, CycleMethod.NO_CYCLE);
	    g2d.setPaint(p);
	    g2d.fillRect(0, 0, miniatureSize.width, miniatureSize.height);
	}
	
	// apply transparency for title
	AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
        g2d.setComposite(composite);
        
	// get title metrics
	String title = document.getTitle();
	if (title == null) {
	    title = "";
	}
	FontRenderContext frc = g2d.getFontRenderContext();
	Font labelFont = UIManager.getFont("Label.font");
	labelFont = labelFont.deriveFont(Font.BOLD);
	LineMetrics metrics = labelFont.getLineMetrics(title, frc);
	
	// fill title background
	g2d.setColor(titleBackground);
        int titleBackgroundX = marginWidth + borderWidth;
        int titleBackgroundY = miniatureSize.height - ((int)metrics.getHeight() + marginWidth + borderWidth);
        int titleBackgroundWidth = miniatureSize.width - 2*(marginWidth + borderWidth);
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
	int titleX = marginWidth + borderWidth + (int)(arcWidth/2);
	int titleY = miniatureSize.height - (int)(metrics.getDescent() + metrics.getLeading() + marginWidth + borderWidth);
	g2d.drawString(title, titleX, titleY);
	
	// draw double-lines border
        g2d.setStroke(new BasicStroke((float)borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        roundRect.width -= 1;
        roundRect.height -= 1;
	g2d.setColor(outerBorder);
        g2d.draw(roundRect);

	g2d.setClip(null);
	
	composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f);
        g2d.setComposite(composite);
	super.paint(g);
    }

    /**
     * @see javax.swing.JComponent#getMinimumSize()
     */
    @Override
    public Dimension getMinimumSize() {
	return new Dimension(70, 45);
    }

    /**
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize() {
	return new Dimension(200, 150);
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
	Component body = (Component)miniaturised.getBody();
	if (body.getWidth() > 0 && body.getHeight() > 0) {
	    this.repaint();
	}
    }

    public void mouseExited(MouseEvent e) {
	this.mouseOn = false;
	Component body = (Component)miniaturised.getBody();
	if (body.getWidth() > 0 && body.getHeight() > 0) {
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
	Runnable r = new Runnable() {
	    public void run() {
		detailButton.setVisible(true);
	    }
	};
	if (SwingUtilities.isEventDispatchThread()) {
	    r.run();
	} else {
	    SwingUtilities.invokeLater(r);
	}
	this.repaint();
    }

    public void documentDeactivated(DocumentEvent event) {
	Runnable r = new Runnable() {
	    public void run() {
		detailButton.setVisible(false);
	    }
	};
	if (SwingUtilities.isEventDispatchThread()) {
	    r.run();
	} else {
	    SwingUtilities.invokeLater(r);
	}
	this.repaint();
    }

    /**
     * 
     */
    public void relocalize() {
        ResourceBundle label = ResourceBundle.getBundle("label");
        
        if (favoriteButton.isSelected()) {
            favoriteButton.setToolTipText(label.getString("javaspeaker.removeFavorite"));
        } else {
            favoriteButton.setToolTipText(label.getString("javaspeaker.addFavorite"));
        }

        if (detailButton.isSelected()) {
            detailButton.setToolTipText(label.getString("javaspeaker.hideDetails"));
        } else {
            detailButton.setToolTipText(label.getString("javaspeaker.showDetails"));
        }
    }
    
    /**
     * manage the favorites button
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getSource();
        int stateChange = e.getStateChange();
        if (source.equals(favoriteButton)) {
            if (document != null) {
                Favorites fav = Favorites.getInstance();
                ResourceBundle label = ResourceBundle.getBundle("label");
                
                if (stateChange == ItemEvent.SELECTED) {
                    fav.addFavorite(document);
                    favoriteButton.setToolTipText(label.getString("javaspeaker.removeFavorite"));
                } else {
                    fav.removeFavorite(document);
                    favoriteButton.setToolTipText(label.getString("javaspeaker.addFavorite"));
                }
            }
        } else if (source.equals(detailButton)) {
            ResourceBundle label = ResourceBundle.getBundle("label");
            
            if (stateChange == ItemEvent.SELECTED) {
        	miniaturised.setHeaderVisible(true);
                detailButton.setToolTipText(label.getString("javaspeaker.hideDetails"));
        	UserInterfaceFactory.getGraphicalUserInterface().validate();
            } else {
        	miniaturised.setHeaderVisible(false);
        	detailButton.setToolTipText(label.getString("javaspeaker.showDetails"));
        	UserInterfaceFactory.getGraphicalUserInterface().validate();
            }
        }
    }
}
