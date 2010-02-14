package org.tramper.parser;

/**
 * Exception thrown when parsing a stream
 * @author Paul-Emile
 */
public class ParsingException extends Exception {
    /**
     * ParsingException.java long
     */
    private static final long serialVersionUID = 3507523321592069520L;

    /**
     * 
     */
    public ParsingException() {
        super();
    }

    /**
     * @param arg0
     */
    public ParsingException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public ParsingException(Throwable arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public ParsingException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
