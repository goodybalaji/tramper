package org.tramper.ui;

/**
 * @author Paul-Emile
 * 
 */
public class RenderingException extends Exception {
    /** RenderingException.java long */
    private static final long serialVersionUID = 7733677202194202882L;

    /**
     * 
     */
    public RenderingException() {
    }

    /**
     * @param message
     */
    public RenderingException(String message) {
	super(message);
    }

    /**
     * @param cause
     */
    public RenderingException(Throwable cause) {
	super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public RenderingException(String message, Throwable cause) {
	super(message, cause);
    }

}
