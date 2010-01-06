package org.tramper.gui;

/**
 * @author Paul-Emile
 * 
 */
public class DisplayEvent {
    /** Full screen display */
    public static final int FULL_SCREEN = 1;
    /** Window display */
    public static final int WINDOW = 0;
    /** Mini display */
    public static final int MINI = 2;
    /** Full screen, window or mini */
    private int display;
    
    /**
     * 
     */
    public DisplayEvent() {
	super();
    }

    /**
     * @return display.
     */
    public int getDisplay() {
        return this.display;
    }

    /**
     * @param display display 
     */
    public void setDisplay(int display) {
        this.display = display;
    }
}
