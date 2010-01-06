package org.tramper.recognizer;

/**
 * A recording exception
 * @author Paul-Emile
 */
public class RecognitionException extends Exception {

    /**
     * RecognitionException.java long
     */
    private static final long serialVersionUID = 7219109005211186595L;

    /**
     * 
     */
    public RecognitionException() {
    }

    /**
     * @param message
     */
    public RecognitionException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public RecognitionException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public RecognitionException(String message, Throwable cause) {
        super(message, cause);
    }

}
