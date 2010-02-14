package org.tramper.doc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A web page
 * @author Paul-Emile
 */
public class WebPage extends MarkupDocument implements Serializable {
    /** WebPage.java long */
    private static final long serialVersionUID = 1883723293964921614L;
    /** category */
    private String category;
    
    /**
     * 
     */
    public WebPage() {
        items = new ArrayList<DocumentItem>();
        links = new ArrayList<Link>();
    }
    
    /**
     * parse a string to instantiate a locale
     * @param lang
     */
    public void parseLanguage(String lang) {
        if (lang.length() == 2) {
            language = new Locale(lang);
        } else if (lang.length() == 5) {
            String[] localePart = new String[0];
            if (lang.indexOf("-") != -1) {
                localePart = lang.split("-");
            } else if (lang.indexOf("_") != -1) {
                localePart = lang.split("_");
            }
            
            if (localePart.length == 2) {
                language = new Locale(localePart[0], localePart[1]);
            }
        }
    }
    
    /**
     * @return category.
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * @param category category 
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * add an item 
     * @param item item to add
     */
    public void addItem(DocumentItem item) {
        this.items.add(item);
    }
    
    /**
     * remove an item
     * @param item item to remove
     */
    public void removeItem(DocumentItem item) {
        this.items.remove(item);
    }
}
