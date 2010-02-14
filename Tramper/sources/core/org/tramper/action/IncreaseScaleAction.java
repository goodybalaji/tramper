package org.tramper.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BoundedRangeModel;

/**
 * 
 * @author Paul-Emile
 */
public class IncreaseScaleAction extends AbstractAction {
    /** EnlargementPlusAction.java long */
    private static final long serialVersionUID = -5802662502245843389L;
    /** slider model */
    private BoundedRangeModel model;
    
    /**
     * 
     */
    public IncreaseScaleAction(BoundedRangeModel model) {
	super();
	this.model = model;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
	int currentValue = model.getValue();
	int extent = model.getExtent();
	int newValue = currentValue + extent;
	model.setValue(newValue);
    }
}
