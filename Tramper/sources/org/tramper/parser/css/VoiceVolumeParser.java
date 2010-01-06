package org.tramper.parser.css;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

/**
 * Voice volume CSS property parser
 * @author Paul-Emile
 */
public class VoiceVolumeParser implements CSSPropertyParser {
    /** silent value */
    protected final static String SILENT = "silent";
    /** x-soft value */
    protected final static String X_SOFT = "x-soft";
    /** soft value */
    protected final static String SOFT = "soft";
    /** medium value */
    protected final static String MEDIUM = "medium";
    /** loud value */
    protected final static String LOUD = "loud";
    /** x-loud value */
    protected final static String X_LOUD = "x-loud";
    
    /**
     * 
     */
    public VoiceVolumeParser() {
    }
    
    /**
     * Returns a value between 0 and 100.
     * @param value a CSS property value
     * @return 
     */
    public Object parse(CSSValue value) {
	float fValue = 100;

        short valueType = value.getCssValueType();
        if (valueType == CSSValue.CSS_PRIMITIVE_VALUE) {
            CSSPrimitiveValue primitiveValue = (CSSPrimitiveValue) value;
            short primitiveType = primitiveValue.getPrimitiveType();
            switch (primitiveType) {
            	case CSSPrimitiveValue.CSS_NUMBER: 
            	    fValue = primitiveValue.getFloatValue(primitiveType);
            	    break;
            	case CSSPrimitiveValue.CSS_PERCENTAGE: 
            	    fValue = primitiveValue.getFloatValue(primitiveType);
            	    break;
            	case CSSPrimitiveValue.CSS_IDENT: 
            	    String strValue = primitiveValue.getStringValue().toLowerCase();
                    if (strValue.equals(SILENT)) {
                        fValue = 0;
                    } else if (strValue.equals(X_SOFT)) {
                	fValue = 20;
                    } else if (strValue.equals(SOFT)) {
                	fValue = 40;
                    } else if (strValue.equals(MEDIUM)) {
                	fValue = 60;
                    } else if (strValue.equals(LOUD)) {
                	fValue = 80;
                    } else if (strValue.equals(X_LOUD)) {
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
