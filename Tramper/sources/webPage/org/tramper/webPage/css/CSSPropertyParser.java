package org.tramper.webPage.css;

import org.w3c.dom.css.CSSValue;

/**
 * CSS property parser
 * @author Paul-Emile
 */
public interface CSSPropertyParser {
    /**
     * Parse a CSS property into an object
     * @param value 
     * @return
     */
    public Object parse(CSSValue value);
}
