package org.tramper.webPage.css;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

/**
 * @author Paul-Emile
 * 
 */
public class VoiceRateParser implements CSSPropertyParser {
    /** x-slow value */
    protected final static String X_SLOW = "x-slow";
    /** slow value */
    protected final static String SLOW = "slow";
    /** medium value */
    protected final static String MEDIUM = "medium";
    /** fast value */
    protected final static String FAST = "fast";
    /** x-fast value */
    protected final static String X_FAST = "x-fast";

    /**
     * @see org.tramper.parser.css.CSSPropertyParser#parse(org.w3c.dom.css.CSSValue)
     */
    public Object parse(CSSValue value) {
	float fValue = 50;

        short valueType = value.getCssValueType();
        if (valueType == CSSValue.CSS_PRIMITIVE_VALUE) {
            CSSPrimitiveValue primitiveValue = (CSSPrimitiveValue) value;
            short primitiveType = primitiveValue.getPrimitiveType();
            switch (primitiveType) {
            	case CSSPrimitiveValue.CSS_PERCENTAGE: 
            	    fValue = primitiveValue.getFloatValue(primitiveType);
            	    break;
            	case CSSPrimitiveValue.CSS_IDENT: 
            	    String strValue = primitiveValue.getStringValue().toLowerCase();
                    if (strValue.equals(X_SLOW)) {
                        fValue = 1;
                    } else if (strValue.equals(SLOW)) {
                	fValue = 25;
                    } else if (strValue.equals(MEDIUM)) {
                	fValue = 50;
                    } else if (strValue.equals(FAST)) {
                	fValue = 75;
                    } else if (strValue.equals(X_FAST)) {
                	fValue = 100;
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
