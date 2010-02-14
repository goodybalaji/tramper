package org.tramper.doc;

import java.util.ArrayList;
import java.util.Locale;

//import org.apache.log4j.Logger;


/**
 * A web page item
 * @author Paul-Emile
 */
public class WebPageItem extends DocumentItem {
    /** unique identifier */
    private String id;
    /** speakable text from the item's content for speaker */
    private StringBuffer content;
    
    /**
     * 
     */
    public WebPageItem() {
        content = new StringBuffer();
        media = new ArrayList<Sound>();
        links = new ArrayList<Link>();
    }
    
    /**
     * @return content.
     */
    public String getContent() {
        return this.content.toString();
    }

    /**
     * @param content content 
     */
    public void setContent(String content) {
        this.content.replace(0, content.length()-1, content);
    }
    
    /**
     * append the specified string to the content
     * @param content
     */
    public void appendContent(String text) {
        text = text.trim();
        if (text.length() > 0) {
            text = text.concat(" ");
            this.content.append(text);
        }
    }
    
    /**
     * has some text or not
     * @return true if some text, false otherwise
     */
    public boolean hasContent() {
        return (this.content.length() > 0);
    }
    
    /**
     * @see org.tramper.doc.SpeakableItem#getSpeakableText()
     */
    public String getText(Locale locale) {
        return content.toString();
    }

    /**
     * return the content of the item
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return content.toString();
    }

    /**
     * @return id.
     */
    public String getId() {
        return this.id;
    }

    /**
     * @param id id 
     */
    public void setId(String id) {
        this.id = id;
    }
    
}
