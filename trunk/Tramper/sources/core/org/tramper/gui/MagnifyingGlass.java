package org.tramper.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * A magnifying glass enlarging the underlying component's graphics 
 * @author Paul-Emile
 */
public class MagnifyingGlass extends JComponent implements MouseListener, MouseMotionListener {
    /** MagnifyingGlass.java long */
    private static final long serialVersionUID = -4076930702028003930L;
    /** mouse location on window */
    private Point glassMouseLocation;
    /** magnifying glass width */
    private int viewWidth;
    /** magnifying glass height */
    private int viewHeight;
    /** magnification */
    private int magnification;
    /** magnified component */
    private Component magnifiedComponent;
    /** border color */
    private Paint glassBorderPaint;
    /** transparency */
    //private Composite glassComposite;
    /** magnifying glass state */
    private boolean active;
    
    /**
     * 
     */
    public MagnifyingGlass() {
        active = false;
        magnification = 1;
        viewWidth = 100;
        viewHeight = 75;
        glassBorderPaint = new Color(30, 30, 30);
        //glassComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
        this.setOpaque(false);
    }
    
    /**
     * Draws the magnifying glass over the document viewer
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g) {
        if (active && magnifiedComponent.isValid() && glassMouseLocation != null) {
            Point magnifiedOnScreen = magnifiedComponent.getLocationOnScreen();
            Point glassOnScreen = this.getLocationOnScreen();
            int xDeltaBetweenGlassAndMagnified = magnifiedOnScreen.x - glassOnScreen.x;
            int yDeltaBetweenGlassAndMagnified = magnifiedOnScreen.y - glassOnScreen.y;
            Graphics2D aGraphics = (Graphics2D)g;
            Shape viewPort = new Rectangle(glassMouseLocation.x-(magnification*viewWidth)/2, glassMouseLocation.y-(magnification*viewHeight)/2, (magnification*viewWidth), (magnification*viewHeight));
            Stroke glassBorder = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            aGraphics.setStroke(glassBorder);
            aGraphics.setPaint(glassBorderPaint);
            aGraphics.draw(viewPort);
            //aGraphics.setComposite(glassComposite);
            //aGraphics.fill(viewPort);
            aGraphics.setClip(viewPort);
            aGraphics.scale(magnification, magnification);
            int xtranslation = xDeltaBetweenGlassAndMagnified - (glassMouseLocation.x*(magnification-1))/magnification;
            int ytranslation = yDeltaBetweenGlassAndMagnified - (glassMouseLocation.y*(magnification-1))/magnification;
            aGraphics.translate(xtranslation, ytranslation);
            magnifiedComponent.paint(aGraphics);
        }
    }
    
    public void mouseDragged(MouseEvent e) {
    }
    
    public void mouseMoved(MouseEvent e) {
        Point docViewerMouseLocation = e.getPoint();
        Component source = e.getComponent();
        glassMouseLocation = SwingUtilities.convertPoint(source, docViewerMouseLocation, this);
        this.repaint();
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        this.setVisible(true);
    }

    public void mouseExited(MouseEvent e) {
        this.setVisible(false);
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }
    
    /**
     * @return magnifiedComponent.
     */
    public Component getMagnifiedComponent() {
        return this.magnifiedComponent;
    }

    /**
     * @param magnifiedComponent magnifiedComponent 
     */
    public void setMagnifiedComponent(Component magnifiedComponent) {
        this.magnifiedComponent = magnifiedComponent;
    }

    /**
     * @return magnification.
     */
    public int getMagnification() {
        return this.magnification;
    }

    /**
     * @param magnification magnification 
     */
    public void setMagnification(int magnification) {
        this.magnification = magnification;
    }
    
    /**
     * 
     */
    public void increaseMagnification() {
        if (magnification == 1) {
            this.magnification++;
            this.setActive(true);
        }
        else if (magnification < 6) {
            this.magnification++;
            this.repaint();
        }
    }
    
    /**
     * 
     */
    public void decreaseMagnification() {
        if (magnification > 2) {
            this.magnification--;
            this.repaint();
        }
        else if (magnification == 2) {
            this.magnification--;
            this.setActive(false);
        }
    }

    /**
     * @return active.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * @param active active 
     */
    public void setActive(boolean active) {
        this.active = active;
        //this.setVisible(active);
    }
}
