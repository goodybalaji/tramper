package org.tramper.doc;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A link between 2 documents defined by a relation type.
 * @author Paul-Emile
 */
public class Link extends SpeakableItem {
    /** linking document wearing the link to the linked document */
    protected SimpleDocument linkingDocument;
    /** linked document being pointed by the link weared by the linking document */
    protected SimpleDocument linkedDocument;
    /** relation type between the 2 documents */
    protected String relation;
    /** identifier */
    protected String id;
    /** link number in the document */
    protected int number;
    
    /**
     * 
     */
    public Link() {
    }

    /**
     * @return linkedDocument.
     */
    public SimpleDocument getLinkedDocument() {
        return this.linkedDocument;
    }

    /**
     * @param linkedDocument linkedDocument 
     */
    public void setLinkedDocument(SimpleDocument linkedDocument) {
        this.linkedDocument = linkedDocument;
    }

    /**
     * @return linkingDocument.
     */
    public SimpleDocument getLinkingDocument() {
        return this.linkingDocument;
    }

    /**
     * @param linkingDocument linkingDocument 
     */
    public void setLinkingDocument(SimpleDocument linkingDocument) {
        this.linkingDocument = linkingDocument;
    }

    /**
     * @return relation.
     */
    public String getRelation() {
        return this.relation;
    }

    /**
     * @param relation relation 
     */
    public void setRelation(String relation) {
        this.relation = relation;
    }

    /**
     * Returns a speakable text describing this link
     * @return
     */
    public String getText(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        ResourceBundle label = ResourceBundle.getBundle("label", locale);
        String docTitle = linkedDocument.getTitle();
        String link = label.getString("javaspeaker.link");
        String text = link;
        if (relation != null) {
            String relationDoc = label.getString("javaspeaker.menu."+relation);
            text += " " + relationDoc;
        }
        text += " " + number;
        if (docTitle != null) {
            text += " " + docTitle;
        }
        return text;
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

    /**
     * @return number.
     */
    public int getNumber() {
        return this.number;
    }

    /**
     * @param number number 
     */
    public void setNumber(int number) {
        this.number = number;
    }
}
