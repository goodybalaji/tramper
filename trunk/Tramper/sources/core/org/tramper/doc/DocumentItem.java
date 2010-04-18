package org.tramper.doc;

import java.util.List;

/**
 * A document item
 * @author Paul-Emile
 */
public abstract class DocumentItem extends SpeakableItem {
    /** links list */
    protected List<Link> links;

    /**
     * add a link to the item if it doesn't already exist
     * @param link 
     */
    public void addLink(Link link) {
        if (!this.links.contains(link))
            this.links.add(link);
    }
    
    /**
     * @param link link 
     */
    public void setLink(List<Link> link) {
        this.links = link;
    }

    /**
     * 
     * @return
     */
    public List<Link> getLinks() {
        return links;
    }
    
}
