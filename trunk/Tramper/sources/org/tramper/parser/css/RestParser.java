package org.tramper.parser.css;

import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

/**
 * 
 * @author Paul-Emile
 */
public class RestParser implements CSSPropertyParser {

    /**
     * 
     * @see org.tramper.parser.css.CSSPropertyParser#parse(org.w3c.dom.css.CSSValue)
     */
    public Object parse(CSSValue value) {
	Float[] fValue = new Float[2];
	
        short valueType = value.getCssValueType();
        if (valueType == CSSValue.CSS_PRIMITIVE_VALUE) {
            CSSPropertyParser parser = new RestBeforeParser();
            fValue[0] = (Float)parser.parse(value);
            parser = new RestAfterParser();
            fValue[1] = (Float)parser.parse(value);
        } else if (valueType == CSSValue.CSS_VALUE_LIST) {
            CSSValueList valueList = (CSSValueList) value;
            // 2 values at most, the rest is ignored
            CSSValue aValue = valueList.item(0);
            CSSPropertyParser parser = new RestBeforeParser();
            fValue[0] = (Float)parser.parse(aValue);
            
            aValue = valueList.item(1);
            parser = new RestAfterParser();
            fValue[1] = (Float)parser.parse(aValue);
        } else if (valueType == CSSValue.CSS_INHERIT) {
            // should be the value of the parent element
        } else if (valueType == CSSValue.CSS_CUSTOM) {
            // 
        }
        
	return fValue;
    }

}
