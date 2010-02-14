package org.tramper.webPage.css;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

/**
 * @author Paul-Emile
 * 
 */
public class CueBeforeParser implements CSSPropertyParser {
    /** logger */
    Logger logger = Logger.getLogger(CueBeforeParser.class);
    /** url base */
    protected String baseUrl = null;
    /** host part of the url */
    protected String hostUrl = null;
    /** url beginning pattern */
    protected Pattern urlPattern;
    /** none value */
    protected final static String NONE = "none";

    /**
     * 
     */
    public CueBeforeParser(String url) {
        urlPattern = Pattern.compile("^\\w+\\:/{0,2}[.[^/]]*");
        computeBaseUrl(url);
    }

    /**
     * @see org.tramper.parser.css.CSSPropertyParser#parse(org.w3c.dom.css.CSSValue)
     */
    public Object parse(CSSValue value) {
	URL uValue = null;

        short valueType = value.getCssValueType();
        if (valueType == CSSValue.CSS_PRIMITIVE_VALUE) {
            CSSPrimitiveValue primitiveValue = (CSSPrimitiveValue) value;
            short primitiveType = primitiveValue.getPrimitiveType();
            switch (primitiveType) {
            	case CSSPrimitiveValue.CSS_URI: 
            	    try {
			uValue = completeUrl(primitiveValue.getStringValue());
		    } catch (MalformedURLException e) {
			logger.error(primitiveValue.getStringValue(), e);
		    }
            	    break;
            	case CSSPrimitiveValue.CSS_STRING:
            	    try {
			uValue = completeUrl(primitiveValue.getStringValue());
		    } catch (MalformedURLException e) {
			logger.error(primitiveValue.getStringValue(), e);
		    }
                    break;
            	case CSSPrimitiveValue.CSS_IDENT: 
            	    String idValue = primitiveValue.getStringValue().toLowerCase();
                    if (idValue.equals(NONE)) {
                        uValue = null;
                    }
                    break;
            }
        } else if (valueType == CSSValue.CSS_VALUE_LIST) {
            CSSValueList valueList = (CSSValueList) value;
            //TODO don't know how "url() volume" is parsed
            CSSValue aValue = valueList.item(0);
            return this.parse(aValue);
        } else if (valueType == CSSValue.CSS_INHERIT) {
            // should be the value of the parent element
        } else if (valueType == CSSValue.CSS_CUSTOM) {
            // 
        }

	return uValue;
    }

    /**
     * Complete the relative and absolute urls before instantiate them.
     * @param uncompleteUrl
     * @return an URL
     * @throws MalformedURLException if unable to instantiate the url
     */
    public URL completeUrl(String uncompleteUrl) throws MalformedURLException {
	if (uncompleteUrl == null || uncompleteUrl.equals("")) {
	    throw new MalformedURLException();
	}
	
	// either this is an absolute path, concatenate the host part of the url
        if (uncompleteUrl.startsWith("/")) {
            uncompleteUrl = hostUrl.concat(uncompleteUrl);
        // or this is a relative path, concatenate the base part of the url
        } else if (uncompleteUrl.indexOf(":") == -1) {
            uncompleteUrl = baseUrl.concat(uncompleteUrl);
        }
        // otherwise this is a full url, nothing to concatenate
        
        URL anUrl = new URL(uncompleteUrl);
	return anUrl;
    }
    
    /**
     * 
     * @param url
     */
    public void computeBaseUrl(String url) {
        try {
            //first we determine the base url and the host part
            URL anUrl = new URL(url);
            hostUrl = anUrl.getProtocol()+"://"+anUrl.getHost();
            int port = anUrl.getPort();
            if (port != -1) {
                hostUrl += ":" + port;
            }
            String path = anUrl.getPath();
            int lastSlashIndex = path.lastIndexOf("/");
            if (lastSlashIndex != -1) {
                path = path.substring(0, lastSlashIndex+1);
            } else {
                path = "/";
            }
            baseUrl = hostUrl + path;
        }
        catch (MalformedURLException e) {
            logger.warn(e.getMessage(), e);
            
            Matcher urlMatcher = urlPattern.matcher(url);
            boolean urlMatched = urlMatcher.lookingAt();
            if (urlMatched) {
                hostUrl = urlMatcher.group();
            }
            
            int lastSlashIndex = url.lastIndexOf("/");
            baseUrl = url.substring(0, lastSlashIndex+1);
        }
    }
}
