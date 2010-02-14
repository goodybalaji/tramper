package org.tramper.synthesizer;

/**
 * Speech exception
 * @author Paul-Emile
 */
public class SynthesisException extends Exception {
    /**
     * SynthesisException.java long
     */
    private static final long serialVersionUID = -4863339007606582879L;

    /**
     * 
     */
    public SynthesisException() {
        super();
    }

    /**
     * @param arg0
     */
    public SynthesisException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public SynthesisException(Throwable arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public SynthesisException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
