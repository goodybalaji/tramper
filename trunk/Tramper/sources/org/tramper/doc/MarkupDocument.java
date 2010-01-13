package org.tramper.doc;

import java.net.URL;
import java.util.List;
import java.util.Locale;

import javax.swing.Icon;

/**
 * Abstract markup document
 * @author Paul-Emile
 */
public abstract class MarkupDocument extends SimpleDocument {
    /** charset */
    protected String charset;
    /** language/locale */
    protected Locale language;
    /** icon */
    protected Icon icon;
    /** links to related documents */
    protected transient List<Link> links;
    /** Speakable items list */
    protected transient List<DocumentItem> items;

    /**
     * 
     * @param charset
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * @return charset.
     */
    public String getCharset() {
        return this.charset;
    }
    
    /**
     * @return icon.
     */
    public Icon getIcon() {
        return this.icon;
    }

    /**
     * @param icon icon 
     */
    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    /**
     * @see org.tramper.doc.MarkupDocument#getLanguage()
     */
    public Locale getLanguage() {
        return language;
    }
    
    /**
     * 
     * @param language
     */
    public void setLanguage(Locale language) {
        this.language = language;
    }

    /**
     * 
     * @see org.tramper.doc.MarkupDocument#getLink(int)
     */
    public String getLink(int linkId) {
	Link aLink = null;
        for (int i=0; i<items.size(); i++) {
            DocumentItem item = items.get(i);
            List<Link> links = item.getLinks();
            for (int j=0; j<links.size(); j++) {
                aLink = (Link)links.get(j);
                if (aLink.getNumber() == linkId) {
                    SimpleDocument aDocument = aLink.getLinkedDocument();
                    URL url = aDocument.getUrl();
                    return url.toString();
                }
            }
        }
        return null;
    }
    
    /**
     * @return links.
     */
    public List<Link> getLinks() {
        return this.links;
    }

    /**
     * @param links links 
     */
    public void setLinks(List<Link> link) {
        this.links = link;
    }

    /**
     * add a links to the list
     * @param aLink
     */
    public void addLink(Link aLink) {
        if (this.links.contains(aLink) == false)
            this.links.add(aLink);
    }
    
    /**
     * Returns the items of the feed
     * @return
     */
    public List<DocumentItem> getItems() {
        return items;
    }
    
    /**
     * 
     * @see org.tramper.doc.SimpleDocument#hasLink()
     */
    public boolean hasLink() {
	return true;
    }
}
