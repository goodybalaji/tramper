package org.tramper.webPage.css;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

/**
 * @author Paul-Emile
 * 
 */
public class VoicePitchRangeParser implements CSSPropertyParser {
    /** x-low value */
    protected final static String X_LOW = "x-low";
    /** low value */
    protected final static String LOW = "low";
    /** medium value */
    protected final static String MEDIUM = "medium";
    /** high value */
    protected final static String HIGH = "high";
    /** x-high value */
    protected final static String X_HIGH = "x-high";

    /**
     * @see org.tramper.parser.css.CSSPropertyParser#parse(org.w3c.dom.css.CSSValue)
     */
    public Object parse(CSSValue value) {
	float fValue = 0;

        short valueType = value.getCssValueType();
        if (valueType == CSSValue.CSS_PRIMITIVE_VALUE) {
            CSSPrimitiveValue primitiveValue = (CSSPrimitiveValue) value;
            short primitiveType = primitiveValue.getPrimitiveType();
            switch (primitiveType) {
            	case CSSPrimitiveValue.CSS_HZ: 
            	    fValue = primitiveValue.getFloatValue(primitiveType);
            	    break;
            	case CSSPrimitiveValue.CSS_NUMBER: 
            	    fValue = primitiveValue.getFloatValue(primitiveType);
            	    break;
            	case CSSPrimitiveValue.CSS_PERCENTAGE: 
            	    fValue = primitiveValue.getFloatValue(primitiveType);
            	    break;
            	case CSSPrimitiveValue.CSS_IDENT: 
            	    String strValue = primitiveValue.getStringValue().toLowerCase();
                    if (strValue.equals(X_LOW)) {
                        fValue = 1;
                    } else if (strValue.equals(LOW)) {
                	fValue = 25;
                    } else if (strValue.equals(MEDIUM)) {
                	fValue = 50;
                    } else if (strValue.equals(HIGH)) {
                	fValue = 75;
                    } else if (strValue.equals(X_HIGH)) {
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
