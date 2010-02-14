package org.tramper.webPage.css;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

/**
 * Voice balance CSS property parser
 * @author Paul-Emile
 */
public class VoiceBalanceParser implements CSSPropertyParser {
    /** left value */
    protected final static String LEFT = "left";
    /** right value */
    protected final static String RIGHT = "right";
    /** center value */
    protected final static String CENTER = "center";
    /** leftwards value */
    protected final static String LEFTWARDS = "leftwards";
    /** right value */
    protected final static String RIGHTWARDS = "rightwards";

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
            	case CSSPrimitiveValue.CSS_NUMBER: 
            	    fValue = primitiveValue.getFloatValue(primitiveType);
            	    break;
            	case CSSPrimitiveValue.CSS_IDENT: 
            	    String strValue = primitiveValue.getStringValue().toLowerCase();
                    if (strValue.equals(LEFT)) {
                        fValue = -100;
                    } else if (strValue.equals(RIGHT)) {
                	fValue = 100;
                    } else if (strValue.equals(CENTER)) {
                	fValue = 0;
                    } else if (strValue.equals(LEFTWARDS)) {
                	// TODO take in account the parent's volume
                	fValue = -50;
                    } else if (strValue.equals(RIGHTWARDS)) {
                	fValue = 50;
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
