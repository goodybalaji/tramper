package org.tramper.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tramper.parser.css.CSSPropertyParser;
import org.tramper.parser.css.CueAfterParser;
import org.tramper.parser.css.CueBeforeParser;
import org.tramper.parser.css.CueParser;
import org.tramper.parser.css.PauseAfterParser;
import org.tramper.parser.css.PauseBeforeParser;
import org.tramper.parser.css.PauseParser;
import org.tramper.parser.css.RestAfterParser;
import org.tramper.parser.css.RestBeforeParser;
import org.tramper.parser.css.RestParser;
import org.tramper.parser.css.SpeakParser;
import org.tramper.parser.css.VoiceBalanceParser;
import org.tramper.parser.css.VoiceFamilyParser;
import org.tramper.parser.css.VoicePitchParser;
import org.tramper.parser.css.VoicePitchRangeParser;
import org.tramper.parser.css.VoiceRateParser;
import org.tramper.parser.css.VoiceVolumeParser;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSValue;

import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.ParseException;

/**
 * @author Paul-Emile
 */
public class SpeechCSSParser {
    /** logger */
    private Logger logger = Logger.getLogger(SpeechCSSParser.class);
    /** CSS parser */
    protected CSSOMParser cssParser;
    /** url */
    protected String url;
    /** voice volume properties */
    protected final static String VOICE_VOLUME = "voice-volume";
    /** voice balance properties */
    protected final static String VOICE_BALANCE = "voice-balance";
    /** speak properties */
    protected final static String SPEAK = "speak";
    /** pause before properties */
    protected final static String PAUSE_BEFORE = "pause-before";
    /** pause after properties */
    protected final static String PAUSE_AFTER = "pause-after";
    /** pause properties */
    protected final static String PAUSE = "pause";
    /** rest before properties */
    protected final static String REST_BEFORE = "rest-before";
    /** rest after properties */
    protected final static String REST_AFTER = "rest-after";
    /** rest properties */
    protected final static String REST = "rest";
    /** cue before properties */
    protected final static String CUE_BEFORE = "cue-before";
    /** cue after properties */
    protected final static String CUE_AFTER = "cue-after";
    /** cue properties */
    protected final static String CUE = "cue";
    /** mark before properties */
    protected final static String MARK_BEFORE = "mark-before";
    /** mark after properties */
    protected final static String MARK_AFTER = "mark-after";
    /** mark properties */
    protected final static String MARK = "mark";
    /** voice family properties */
    protected final static String VOICE_FAMILY = "voice-family";
    /** voice rate properties */
    protected final static String VOICE_RATE = "voice-rate";
    /** voice pitch properties */
    protected final static String VOICE_PITCH = "voice-pitch";
    /** voice pitch range properties */
    protected final static String VOICE_PITCH_RANGE = "voice-pitch-range";
    /** voice stress properties */
    protected final static String VOICE_STRESS = "voice-stress";
    /** voice duration properties */
    protected final static String VOICE_DURATION = "voice-duration";
    /** phonemes properties */
    protected final static String PHONEMES = "phonemes";

    /**
     * 
     */
    public SpeechCSSParser() {
	cssParser = new CSSOMParser();
    }

    /**
     * Parse a style sheet pointed by the url.
     * @param url url of the style sheet
     * @return a hash
     */
    public Map<String, Map<String, Object>> parse(URL url) {
	this.url = url.toString();
	Map<String, Map<String, Object>> rules = new HashMap<String, Map<String, Object>>();
	InputStream s = null;
	try {
	    s = url.openStream();
	    InputStreamReader isr = new InputStreamReader(s);
	    // do not use new InputSource(url), it doesn't work with CSSOMParser!
	    InputSource source = new InputSource(isr);
	    CSSStyleSheet sheet = cssParser.parseStyleSheet(source);
	    rules = parseStyleSheet(sheet);
	} catch (Exception e) {
	    logger.warn("Error when parsing the style sheet at " + url);
	} finally {
	    if (s != null) {
		try {
		    s.close();
		} catch (IOException e) {}
	    }
	}
	return rules;
    }
    
    /**
     * Parse a block of styles.
     * @param text
     * @return
     */
    public Map<String, Map<String, Object>> parse(String text, String url) {
	this.url = url;
	Map<String, Map<String, Object>> rules = new HashMap<String, Map<String, Object>>();
        StringReader strReader = new StringReader(text);
	InputSource source = new InputSource(strReader);
	try {
	    CSSStyleSheet sheet = cssParser.parseStyleSheet(source);
	    rules = parseStyleSheet(sheet);
	} catch (IOException e) {
	    logger.warn("Error when parsing the block of styles "+text);
	}
	
	return rules;
    }

    /**
     * Parse an inline style.
     * @param text
     * @return
     */
    public Map<String, Object> parseStyleDeclaration(String text, String url) {
	this.url = url;
	Map<String, Object> rules = new HashMap<String, Object>();
        StringReader strReader = new StringReader(text);
	InputSource source = new InputSource(strReader);
	try {
	    CSSStyleDeclaration decl = cssParser.parseStyleDeclaration(source);
	    parseStyleDeclaration(decl, rules);
	} catch (IOException e) {
	    logger.error("Error when parsing the style "+text, e);
	} catch (Exception e) {
	    logger.error("CSS parse exception: "+e.getMessage());
	}
	
	return rules;
    }
    
    /**
     * Parses a CSS style sheet and returns a hash. selectors (element,
     * .class or #id) are keys, styles are values. Styles are hash of properties and values (with unit).
     * @param sheet
     * @return
     */
    protected Map<String, Map<String, Object>> parseStyleSheet(CSSStyleSheet sheet) {
	Map<String, Map<String, Object>> rules = new HashMap<String, Map<String, Object>>();
	
        //if no "speech" media found, skip the style sheet
        /*boolean speechFound = false;
        MediaList medias = sheet.getMedia();
        for (int i=0; i<medias.getLength(); i++) {
            String media = medias.item(i);
            if (media.equalsIgnoreCase("speech")) {
                speechFound = true;
                break;
            }
        }
        if (!speechFound) {
            return rules;
        }*/
        
        CSSRuleList ruleList = sheet.getCssRules();
        for (int j = 0; j < ruleList.getLength(); j++) {
            CSSRule rule = ruleList.item(j);
            short ruleType = rule.getType();
            
            if (ruleType == CSSRule.STYLE_RULE) {
        	CSSStyleRule styleRule = (CSSStyleRule)rule; 
                String selectors = styleRule.getSelectorText().toLowerCase();
                String[] selector = selectors.split(",");
                for (int k=0; k<selector.length; k++) {
                    String aSelector = selector[k].trim();
                    Map<String, Object> declMap = rules.get(aSelector);
                    if (declMap == null) {
                        declMap = new HashMap<String, Object>();
                        rules.put(aSelector, declMap);
                    }
    
                    CSSStyleDeclaration declarations = styleRule.getStyle();
                    parseStyleDeclaration(declarations, declMap);
                }
            } else if (ruleType == CSSRule.IMPORT_RULE) {
        	//TODO should try to parse the style sheet in import rule
            } else if (ruleType == CSSRule.MEDIA_RULE) {
        	//TODO should try to take in account the media rule
            }
        }
        return rules;
    }

    /**
     * 
     * @param declarations
     * @param declMap
     * @return
     */
    protected Map<String, Object> parseStyleDeclaration(CSSStyleDeclaration declarations, Map<String, Object> declMap) {
        for (int l=0; l<declarations.getLength(); l++) {
            String propertyName = declarations.item(l).toLowerCase();
            CSSValue value = declarations.getPropertyCSSValue(propertyName);
            CSSPropertyParser parser = null;
            if (propertyName.equals(VOICE_VOLUME)) {
        	parser = new VoiceVolumeParser();
                Object objValue = parser.parse(value);
                declMap.put(propertyName, objValue);
            } else if (propertyName.equals(VOICE_BALANCE)) {
        	parser = new VoiceBalanceParser();
                Object objValue = parser.parse(value);
                declMap.put(propertyName, objValue);
            } else if (propertyName.equals(SPEAK)) {
        	parser = new SpeakParser();
                Object objValue = parser.parse(value);
                declMap.put(propertyName, objValue);
            } else if (propertyName.equals(PAUSE_BEFORE)) {
        	parser = new PauseBeforeParser();
                Object objValue = parser.parse(value);
                declMap.put(propertyName, objValue);
            } else if (propertyName.equals(PAUSE_AFTER)) {
        	parser = new PauseAfterParser();
                Object objValue = parser.parse(value);
                declMap.put(propertyName, objValue);
            } else if (propertyName.equals(PAUSE)) {
        	parser = new PauseParser();
                Float[] objValue = (Float[])parser.parse(value);
                declMap.put(PAUSE_BEFORE, objValue[0]);
                declMap.put(PAUSE_AFTER, objValue[1]);
            } else if (propertyName.equals(REST_BEFORE)) {
        	parser = new RestBeforeParser();
                Object objValue = parser.parse(value);
                declMap.put(propertyName, objValue);
            } else if (propertyName.equals(REST_AFTER)) {
        	parser = new RestAfterParser();
                Object objValue = parser.parse(value);
                declMap.put(propertyName, objValue);
            } else if (propertyName.equals(REST)) {
        	parser = new RestParser();
                Float[] objValue = (Float[])parser.parse(value);
                declMap.put(REST_BEFORE, objValue[0]);
                declMap.put(REST_AFTER, objValue[1]);
            } else if (propertyName.equals(CUE_BEFORE)) {
        	parser = new CueBeforeParser(url);
                Object objValue = parser.parse(value);
                declMap.put(propertyName, objValue);
            } else if (propertyName.equals(CUE_AFTER)) {
        	parser = new CueAfterParser(url);
                Object objValue = parser.parse(value);
                declMap.put(propertyName, objValue);
            } else if (propertyName.equals(CUE)) {
        	parser = new CueParser(url);
                URL[] objValue = (URL[])parser.parse(value);
                declMap.put(CUE_BEFORE, objValue[0]);
                declMap.put(CUE_AFTER, objValue[1]);
            } else if (propertyName.equals(VOICE_RATE)) {
        	parser = new VoiceRateParser();
                Object objValue = parser.parse(value);
                declMap.put(propertyName, objValue);
            } else if (propertyName.equals(VOICE_PITCH)) {
        	parser = new VoicePitchParser();
                Object objValue = parser.parse(value);
                declMap.put(propertyName, objValue);
            } else if (propertyName.equals(VOICE_PITCH_RANGE)) {
        	parser = new VoicePitchRangeParser();
                Object objValue = parser.parse(value);
                declMap.put(propertyName, objValue);
            } else if (propertyName.equals(VOICE_FAMILY)) {
        	parser = new VoiceFamilyParser();
                Object objValue = parser.parse(value);
                declMap.put(propertyName, objValue);
            }
        }
        return declMap;
    }
}
