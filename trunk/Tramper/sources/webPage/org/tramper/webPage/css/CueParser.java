package org.tramper.webPage.css;

import java.net.URL;

import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

/**
 * 
 * @author Paul-Emile
 */
public class CueParser implements CSSPropertyParser {
    /** url */
    protected String url;
    
    /**
     * 
     * @param url
     */
    public CueParser(String url) {
	this.url = url;
    }
    
    /**
     * 
     * @see org.tramper.parser.css.CSSPropertyParser#parse(org.w3c.dom.css.CSSValue)
     */
    public Object parse(CSSValue value) {
	URL[] uValue = new URL[2];
	
        short valueType = value.getCssValueType();
        if (valueType == CSSValue.CSS_PRIMITIVE_VALUE) {
            CSSPropertyParser parser = new CueBeforeParser(url);
            uValue[0] = (URL)parser.parse(value);
            parser = new CueAfterParser(url);
            uValue[1] = (URL)parser.parse(value);
        } else if (valueType == CSSValue.CSS_VALUE_LIST) {
            CSSValueList valueList = (CSSValueList) value;
            // 2 values at most, the rest is ignored
            CSSValue aValue = valueList.item(0);
            CSSPropertyParser parser = new CueBeforeParser(url);
            uValue[0] = (URL)parser.parse(aValue);
            aValue = valueList.item(1);
            parser = new CueAfterParser(url);
            uValue[1] = (URL)parser.parse(aValue);
        } else if (valueType == CSSValue.CSS_INHERIT) {
            // should be the value of the parent element
        } else if (valueType == CSSValue.CSS_CUSTOM) {
            // 
        }
        
	return uValue;
    }

}
