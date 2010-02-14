package org.tramper.doc;

import java.util.List;

/**
 * A document item
 * @author Paul-Emile
 */
public abstract class DocumentItem extends SpeakableItem {
    /** media list */
    protected List<Sound> media;
    /** links list */
    protected List<Link> links;

    /**
     * add a media to the item if it doesn't already exist
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
    
    /**
     * 
     * @return
     */
    public List<Sound> getMedia() {
        return media;
    }

    /**
     * 
     * @param media
     */
    public void setMedia(List<Sound> media) {
        this.media = media;
    }

    /**
     * add a media to the item if it doesn't already exist
     * @param mediaDocument
     */
    public void addMedia(Sound mediaDocument) {
        if (!this.media.contains(mediaDocument))
            this.media.add(mediaDocument);
    }
}
