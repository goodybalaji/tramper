package org.tramper.gui;

import javax.swing.DefaultBoundedRangeModel;

/**
 * Model for slider managing GUI scale.
 * @author Paul-Emile
 */
public class ScaleBoundedRangeModel extends DefaultBoundedRangeModel {
    /** EnlargementBoundedRangeModel.java long */
    private static final long serialVersionUID = 1L;
    /** instance */
    private static ScaleBoundedRangeModel instance;
    
    public static ScaleBoundedRangeModel getInstance() {
	if (instance == null) {
	    instance = new ScaleBoundedRangeModel(100, 25, 75, 200);
	}
	return instance;
    }
    
    private ScaleBoundedRangeModel(int value, int extent, int min, int max) {
	super(value, extent, min, max);
    }
}
