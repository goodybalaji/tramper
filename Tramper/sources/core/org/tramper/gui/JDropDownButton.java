package org.tramper.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Display the "menu arrow icon" on the right of the button. When the user clicks it, a popup menu appears.
 * Selecting a menu item triggers an action and replace the button's current action with this one.
 * The icon and/or the text of the button can be replaced by those of the selected menu item.
 * @author Paul-Emile
 */
public class JDropDownButton extends JButton implements MouseListener, ActionListener {
    /** JDropDownButton.java long */
    private static final long serialVersionUID = 5667829365682195924L;
    /** list zone width */
    private int listZoneWidth;
    /** button's popup menu */
    private JPopupMenu innerPopup;
    /** arrow icon */
    private Icon arrowIcon;
    /** current action */
    private Action currentAction;
    /** replace text flag */
    private boolean replaceText = false;
    /** replace icon flag */
    private boolean replaceIcon = true;

    /**
     * N.B: do not add any subcomponent to this listbutton or the Substance look and feel
     * will crash when calling getPreferredSize().
     * @param actions
     * @param replaceText
     * @param replaceIcon
     */
    public JDropDownButton(List<Action> actions, boolean replaceText, boolean replaceIcon) {
	super();
	this.replaceText = replaceText;
	this.replaceIcon = replaceIcon;
		
	innerPopup = new JPopupMenu();
	setActions(actions);
	arrowIcon = UIManager.getIcon("Menu.arrowIcon");
	listZoneWidth = arrowIcon.getIconWidth()*2;
		
	addMouseListener(this);
    }
    
    /**
     * Regenerates the popup menu and refresh the button
     * @param actions
     */
    public void setActions(List<Action> actions) {
	innerPopup.removeAll();
	if (actions != null) {
            for (int i=0; i<actions.size(); i++) {
                Action action = actions.get(i);
                JMenuItem aMenu = new JMenuItem((String)action.getValue(Action.NAME));
                aMenu.setIcon((Icon)action.getValue(Action.SMALL_ICON));
                aMenu.setActionCommand((String)action.getValue(Action.ACTION_COMMAND_KEY));
                aMenu.setAction(action);
                aMenu.addActionListener(this);
                innerPopup.add(aMenu);
            }
            if (actions.size() > 0) {
                Action firstAction = actions.get(0);
                setCurrentAction(firstAction);
            }
	}
	innerPopup.validate();
    }
    
    /**
     * 
     * @param action
     */
    protected void setCurrentAction(Action action) {
	currentAction = action;
	setActionCommand((String)action.getValue(Action.ACTION_COMMAND_KEY));
	if (replaceIcon) {
	    setIcon((Icon)action.getValue(Action.SMALL_ICON));
	}
	if (replaceText) {
	    setText((String)action.getValue(Action.NAME));
	}
    }
    
    @Override
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	Graphics2D g2d = (Graphics2D)g;
		
	// draw a separator  between the button itself and the added arrow
	Color shadow = UIManager.getColor("Separator.shadow");
	Color highlight = UIManager.getColor("Separator.highlight");
	Stroke stroke = new BasicStroke(1);
	Stroke previousStroke = g2d.getStroke();
	g2d.setStroke(stroke);
	Color previousColor = g2d.getColor();
	g2d.setColor(shadow);
	g2d.drawLine(getWidth() - listZoneWidth, 5, getWidth() - listZoneWidth, getHeight() - 5);
	g2d.setColor(highlight);
	g2d.drawLine(getWidth() - listZoneWidth + 1, 5, getWidth() - listZoneWidth + 1, getHeight() - 5);
	//reset the color and stroke not to disturb the next drawings
	g2d.setColor(previousColor);
	g2d.setStroke(previousStroke);
		
	// pass a JMenuItem because we are painting a menu icon and a few look and feels require that
	try {
	    arrowIcon.paintIcon(new JMenuItem(), g, getWidth() - (listZoneWidth + arrowIcon.getIconWidth())/2, (getHeight()-arrowIcon.getIconHeight())/2);
	} catch (Exception e) {
	    // fail under Linux 
	}
    }

    @Override
    public Dimension getPreferredSize() {
	Dimension jButtonSize = super.getPreferredSize();
	jButtonSize.width += listZoneWidth;
	return jButtonSize;
    }

    @Override
    public void updateUI() {
	super.updateUI();
	arrowIcon = UIManager.getIcon("Menu.arrowIcon");
	listZoneWidth = arrowIcon.getIconWidth()*2;
	if (innerPopup != null) {
	    SwingUtilities.updateComponentTreeUI(innerPopup);
	}
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void mousePressed(MouseEvent event) {
    }

    /**
     * Button released: if on right side, popup the menu, else current action.
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent event) {
	if (isEnabled()) {
	    int x = event.getX();
            Component source = (Component)event.getSource();
            int width = source.getWidth();
            if (x > width - listZoneWidth) {
            	innerPopup.show(source, 0, getHeight());
            } else {
                ActionEvent e = new ActionEvent(this, 0, this.getActionCommand());
                currentAction.actionPerformed(e);
            }
	}
    }

    /**
     * Menu item actioned: set the button's action with the selected menu item's action 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	JMenuItem source = (JMenuItem)e.getSource();
	Action action = source.getAction();
	setCurrentAction(action);
    }
}
