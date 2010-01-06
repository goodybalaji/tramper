package org.tramper.parser.css;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

/**
 * 
 * @author Paul-Emile
 */
public class SpeakParser implements CSSPropertyParser {
    /** normal value */
    protected final static String NORMAL = "normal";
    /** spell-out value */
    protected final static String SPELL_OUT = "spell-out";
    /** digits value */
    protected final static String DIGITS = "digits";
    /** literal-punctuation value */
    protected final static String LITERAL_PONCTUATION = "literal-punctuation";
    /** no-punctuation value */
    protected final static String NO_PONCTUATION = "no-punctuation";

    /**
     * @see org.tramper.parser.css.CSSPropertyParser#parse(org.w3c.dom.css.CSSValue)
     */
    public Object parse(CSSValue value) {
	String sValue = null;

        short valueType = value.getCssValueType();
        if (valueType == CSSValue.CSS_PRIMITIVE_VALUE) {
            CSSPrimitiveValue primitiveValue = (CSSPrimitiveValue) value;
            short primitiveType = primitiveValue.getPrimitiveType();
            switch (primitiveType) {
            	case CSSPrimitiveValue.CSS_IDENT: 
            	    sValue = primitiveValue.getStringValue().toLowerCase();
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
        
	return sValue;
    }

}
