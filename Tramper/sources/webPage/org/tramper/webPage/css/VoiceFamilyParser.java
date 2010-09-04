package org.tramper.webPage.css;

import java.util.ArrayList;
import java.util.List;

import org.fingon.synthesizer.VoiceDesc;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

/**
 * @author Paul-Emile
 * 
 */
public class VoiceFamilyParser implements CSSPropertyParser {
    /** child age */
    protected static final String CHILD = "child";
    /** young age */
    protected static final String YOUNG = "young";
    /** old age */
    protected static final String OLD = "old";
    /** female gender */
    protected static final String FEMALE = "female";
    /** male gender */
    protected static final String MALE = "male";
    /** neutral gender */
    protected static final String NEUTRAL = "neutral";

    /**
     * @see org.tramper.parser.css.CSSPropertyParser#parse(org.w3c.dom.css.CSSValue)
     */
    @SuppressWarnings("unchecked")
    public Object parse(CSSValue value) {
	List<VoiceDesc> lValue = new ArrayList<VoiceDesc>();
	
        short valueType = value.getCssValueType();
        if (valueType == CSSValue.CSS_PRIMITIVE_VALUE) {
            VoiceDesc voiceDesc = new VoiceDesc();
            lValue.add(voiceDesc);
            CSSPrimitiveValue primitiveValue = (CSSPrimitiveValue) value;
            short primitiveType = primitiveValue.getPrimitiveType();
            switch (primitiveType) {
            	case CSSPrimitiveValue.CSS_NUMBER: 
            	    // TODO store the voice number
            	    break;
            	case CSSPrimitiveValue.CSS_STRING:
            	    String strValue = primitiveValue.getStringValue().toLowerCase();
            	    voiceDesc.setName(strValue);
                    break;
            	case CSSPrimitiveValue.CSS_IDENT: 
            	    String idValue = primitiveValue.getStringValue().toLowerCase();
                    if (idValue.equals(CHILD)) {
                        voiceDesc.setAge(1);
                    } else if (idValue.equals(YOUNG)) {
                	voiceDesc.setAge(3);
                    } else if (idValue.equals(OLD)) {
                	voiceDesc.setAge(5);
                    } else if (idValue.equals(FEMALE)) {
                	voiceDesc.setGender(1);
                    } else if (idValue.equals(MALE)) {
                	voiceDesc.setGender(2);
                    } else if (idValue.equals(NEUTRAL)) {
                	voiceDesc.setGender(3);
                    }
                    break;
            }
        } else if (valueType == CSSValue.CSS_VALUE_LIST) {
            CSSValueList valueList = (CSSValueList) value;
            for (int i=0; i<valueList.getLength(); i++) {
                CSSValue aValue = valueList.item(i);
                List<VoiceDesc> aValueList = (List<VoiceDesc>)this.parse(aValue);
                lValue.addAll(aValueList);
            }
        } else if (valueType == CSSValue.CSS_INHERIT) {
            // should be the value of the parent element
        } else if (valueType == CSSValue.CSS_CUSTOM) {
            // 
        }
        
	return lValue;
    }

}
