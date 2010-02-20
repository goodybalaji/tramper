package org.tramper.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JPanel;

/**
 * Obfuscates the window to block the user inputs. Implements MouseListener to catch the mouse event.
 * (Java doesn't bubble the events to the components beneath)
 * @author Paul-Emile
 */
public class Obfuscator extends JPanel implements MouseListener {
    /** Obfuscator.java long */
    private static final long serialVersionUID = 8056144400202482727L;
    /**  */
    private Method componentMixingMethod;

    /**
     * 
     */
    public Obfuscator() {
	this.setLayout(new FlowLayout());
	this.addMouseListener(this);
	setOpaque(false);
	try {
	    Class awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
	    componentMixingMethod = awtUtilitiesClass.getMethod("setComponentMixingCutoutShape", Component.class, Shape.class);
	} catch (Exception e) {
	    System.err.println(e.toString() + " " + e.getMessage());
	}
    }
    
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	try {
	    componentMixingMethod.invoke(null, this, new Rectangle(0, 0, getWidth(), getHeight()));
	} catch (Exception e) {
	    System.err.println(e.toString() + " " + e.getMessage());
	}
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
