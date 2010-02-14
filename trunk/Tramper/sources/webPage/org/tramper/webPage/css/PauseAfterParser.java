package org.tramper.webPage.css;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

/**
 * 
 * @author Paul-Emile
 */
public class PauseAfterParser implements CSSPropertyParser {
    /** none value */
    protected final static String NONE = "none";
    /** x-weak value */
    protected final static String X_WEAK = "x-weak";
    /** weak value */
    protected final static String WEAK = "weak";
    /** medium value */
    protected final static String MEDIUM = "medium";
    /** strong value */
    protected final static String STRONG = "strong";
    /** x-strong value */
    protected final static String X_STRONG = "x-strong";

    /**
     * A time in ms
     * @see org.tramper.parser.css.CSSPropertyParser#parse(org.w3c.dom.css.CSSValue)
     */
    public Object parse(CSSValue value) {
	float fValue = 0;

        short valueType = value.getCssValueType();
        if (valueType == CSSValue.CSS_PRIMITIVE_VALUE) {
            CSSPrimitiveValue primitiveValue = (CSSPrimitiveValue) value;
            short primitiveType = primitiveValue.getPrimitiveType();
            switch (primitiveType) {
            	case CSSPrimitiveValue.CSS_MS: 
            	    fValue = primitiveValue.getFloatValue(primitiveType);
            	    break;
            	case CSSPrimitiveValue.CSS_S: 
            	    fValue = primitiveValue.getFloatValue(primitiveType)*1000;
            	    break;
            	case CSSPrimitiveValue.CSS_IDENT: 
            	    String strValue = primitiveValue.getStringValue().toLowerCase();
                    if (strValue.equals(NONE)) {
                        fValue = 0;
                    } else if (strValue.equals(X_WEAK)) {
                	fValue = 250;
                    } else if (strValue.equals(WEAK)) {
                	fValue = 500;
                    } else if (strValue.equals(MEDIUM)) {
                	fValue = 1000;
                    } else if (strValue.equals(STRONG)) {
                	fValue = 2000;
                    } else if (strValue.equals(X_STRONG)) {
                	fValue = 4000;
                    }
                    break;
            }
        } else if (valueType == CSSValue.CSS_VALUE_LIST) {
            CSSValueList valueList = (CSSValueList) value;
            //there should not be a list of values, so take the first one only
            CSSValue aValue = valueList.item(0);
            return this.parse(aValue);
        } else if (valueType == CSSValue.CSS_INHERIT) {
            // should be the value of the parent element
        } else if (valueType == CSSValue.CSS_CUSTOM) {
            // 
        }
        
	return Float.valueOf(fValue);
    }

}
