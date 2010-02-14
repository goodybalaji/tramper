package org.tramper.doc;

import java.util.Date;

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
    
}
