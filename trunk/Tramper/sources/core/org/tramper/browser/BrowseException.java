package org.tramper.browser;

/**
 * Indicate a error when browsing
 * @author Paul-Emile
 */
public class BrowseException extends Exception {
    /**
     * BrowseException.java long
     */
    private static final long serialVersionUID = -3004611203736585763L;

    /**
     * 
     */
    public BrowseException() {
        super();
    }

    /**
     * @param arg0
     */
    public BrowseException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public BrowseException(Throwable arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public BrowseException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
