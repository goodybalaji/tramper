package org.tramper.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

/**
 * Obfuscates the window to block the user inputs. Implements MouseListener to catch the mouse event.
 * (Java doesn't bubble the events to the components beneath)
 * @author Paul-Emile
 */
public class Obfuscator extends JComponent implements MouseListener {
    /** Obfuscator.java long */
    private static final long serialVersionUID = 8056144400202482727L;

    /**
     * 
     */
    public Obfuscator() {
	this.addMouseListener(this);
    }
    
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	Graphics2D g2d = (Graphics2D)g;
	Color obfuscatorPaint = new Color(30, 30, 30);
	g2d.setPaint(obfuscatorPaint);
	AlphaComposite obfuscatorComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
	g2d.setComposite(obfuscatorComposite);
	g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }
}
