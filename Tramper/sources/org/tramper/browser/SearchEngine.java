package org.tramper.browser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Locale;

import org.apache.log4j.Logger;


/**
 * A feed search engine
 * @author Paul-Emile
 */
public class SearchEngine {
    /** logger */
    private Logger logger = Logger.getLogger(SearchEngine.class);
    /** pattern of the url used for research */
    private String researchUrlPattern;
    /** search engine name */
    private String name;
    /** search engine logo */
    private String logo;
    /** keyword character separator */
    private String keywordSeparator;
    
    /**
     * 
     */
    public SearchEngine() {
        super();
    }
    
    /**
     * format the research url for that research engine
     * the keywords array must not be null or zero-length
     * @param keywords
     * @return
     */
    public String makeResearchUrl(String[] keywords) {
        MessageFormat researchUrlFormat = new MessageFormat(researchUrlPattern);
        Object[] var = new Object[2];
        //make the keywords list in the url
        StringBuilder keywordConcatenator = new StringBuilder();
        for (int i=0; i<keywords.length-1; i++) {
            keywordConcatenator.append(keywords[i]);
            keywordConcatenator.append(keywordSeparator);
        }
        keywordConcatenator.append(keywords[keywords.length-1]);
        String formatedKeywords = keywordConcatenator.toString();
        try {
            formatedKeywords = URLEncoder.encode(formatedKeywords, "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            //should never appear since I fixed the encoding to utf-8
        }
        var[0] = formatedKeywords;
        Locale locale = Locale.getDefault();
        var[1] = locale.getLanguage();
        
        //replace "en" by "news" for Yahoo news search engine :
        if (var[1].equals("en") && name.equals("yahoo news")) {
            var[1] = "news";
        }
        //replace "en" by "" for Yahoo web search engine :
        else if (name.equals("yahoo web")) {
            if (var[1].equals("en")) {
                var[1] = "";
            } else {
                var[1] = var[1] + ".";
            }
        }
        
        String formatedUrl = researchUrlFormat.format(var);
        logger.debug("makeResearchUrl() : "+formatedUrl);
        return formatedUrl;
    }

    /**
     * @return logo.
     */
    public String getLogo() {
        return this.logo;
    }

    /**
     * @param logo logo 
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }

    /**
     * @return name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name name 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return keywordSeparator.
     */
    protected String getKeywordSeparator() {
        return this.keywordSeparator;
    }

    /**
     * @param keywordSeparator keywordSeparator 
     */
    protected void setKeywordSeparator(String keywordSeparator) {
        this.keywordSeparator = keywordSeparator;
    }

    /**
     * @return researchUrlPattern.
     */
    protected String getResearchUrlPattern() {
        return this.researchUrlPattern;
    }

    /**
     * @param researchUrlPattern researchUrlPattern 
     */
    protected void setResearchUrlPattern(String researchUrlPattern) {
        this.researchUrlPattern = researchUrlPattern;
    }
}
