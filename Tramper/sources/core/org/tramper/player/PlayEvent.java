package org.tramper.player;

import java.util.EventObject;

/**
 * Event fired when a player changes its state
 * @author Paul-Emile
 */
public class PlayEvent extends EventObject {
    /** PlayEvent.java long */
    private static final long serialVersionUID = 5044950678552606580L;
    /** is the first item selected ?*/
    private boolean firstSelected;
    /** is the last item selected ? */
    private boolean lastSelected;
    /** new value */
    private long newValue;

    /**
     * @param arg0
     */
    public PlayEvent(Object arg0) {
        super(arg0);
    }

    /**
     * @return firstSelected.
     */
    public boolean isFirstSelected() {
        return this.firstSelected;
    }

    /**
     * @param firstSelected firstSelected 
     */
    public void setFirstSelected(boolean firstSelected) {
        this.firstSelected = firstSelected;
    }

    /**
     * @return lastSelected.
     */
    public boolean isLastSelected() {
        return this.lastSelected;
    }

    /**
     * @param lastSelected lastSelected 
     */
    public void setLastSelected(boolean lastSelected) {
        this.lastSelected = lastSelected;
    }
    
    /**
     * @return newValue.
     */
    public long getNewValue() {
        return this.newValue;
    }

    /**
     * @param newValue newValue 
     */
    public void setNewValue(long newValue) {
        this.newValue = newValue;
    }
}
