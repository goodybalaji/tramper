package org.tramper.doc;

import java.util.Date;
import java.util.List;

/**
 * A simple document.
 * @author Paul-Emile
 */
public class SimpleDocument extends TextDocument {
    /** title */
    protected String title;
    /** description */
    protected String description;
    /** author */
    protected String author;
    /** Copyright */
    protected String copyright;
    /** creation date */
    protected Date creationDate;
    /** Speakable items list */
    protected List<DocumentItem> items;
    /** length in bytes */
    protected long length;
    
    /**
     * 
     */
    public SimpleDocument() {
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * 
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return author.
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * @param author author 
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description description 
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return copyright.
     */
    public String getCopyright() {
        return this.copyright;
    }

    /**
     * @param copyright copyright 
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    /**
     * @return creationDate.
     */
    public Date getCreationDate() {
        return this.creationDate;
    }

    /**
     * @param creationDate creationDate 
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Returns the items of the document
     * @return
     */
    public List<DocumentItem> getItems() {
        return items;
    }

    /**
     * @return length.
     */
    public long getLength() {
        return this.length;
    }

    /**
     * @param length length 
     */
    public void setLength(long length) {
        this.length = length;
    }

}
