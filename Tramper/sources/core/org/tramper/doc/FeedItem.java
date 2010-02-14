package org.tramper.doc;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.html.dom.HTMLDocumentImpl;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.InputSource;

/**
 * the item of a feed
 * @author Paul-Emile
 */
public class FeedItem extends DocumentItem {
    /** unique identifier */
    private String id;
    /** title */
    private String title;
    /** description */
    private String description;
    /** category */
    private String category;
    /** publication date */
    private Date publicationDate;
    /** update date */
    private Date updateDate;

    /**
     * 
     */
    public FeedItem() {
        super();
        links = new ArrayList<Link>();
        media = new ArrayList<Sound>();
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
     * @return title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * @param title title 
     */
    public void setTitle(String title) {
        this.title = title;
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
     * @return publicationDate.
     */
    public Date getPublicationDate() {
        return this.publicationDate;
    }

    /**
     * @param publicationDate publicationDate 
     */
    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    /**
     * @return updateDate.
     */
    public Date getUpdateDate() {
        return this.updateDate;
    }

    /**
     * @param updateDate updateDate 
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * 
     * @param locale 
     * @return the text to speak
     */
    public String getText(Locale locale) {
        StringBuffer text = new StringBuffer();
        if (title != null) {
            text.append(title);
            text.append(".\n");
        }
        if (category != null) {
            text.append(category);
            text.append(".\n");
        }
        for (int i=0; i<links.size(); i++) {
    	Link aLink = links.get(i);
    	String linkText = aLink.getText(locale);
    	text.append(linkText);
            text.append(".\n");
        }
        if (description != null) {
            DOMFragmentParser parser = new DOMFragmentParser();
            HTMLDocument document = new HTMLDocumentImpl();
            DocumentFragment fragment = document.createDocumentFragment();
            
            StringReader inputString = new StringReader(description);
            InputSource source = new InputSource(inputString);
            try {
                parser.parse(source, fragment);
                depthFirstSearch(text, fragment);
            }
            catch (Exception e) {
               text.append(description);
            }

            text.append(".\n");
        }
        return text.toString();
    }
    
    /**
     * Depth first search of a tree node for collecting text
     * @param text buffer for text node
     * @param node root of the tree
     */
    protected void depthFirstSearch(StringBuffer text, Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int i=0; i<childNodes.getLength(); i++) {
            Node aChild = childNodes.item(i);
            depthFirstSearch(text, aChild);
        }
        short nodeType = node.getNodeType();
        
        if (nodeType == Node.ELEMENT_NODE) {
            NamedNodeMap attributes = node.getAttributes();
            for (int j=0; j<attributes.getLength(); j++) {
                Node attr = attributes.item(j);
                String attrName = attr.getNodeName();
                //we read the alternatives texts to images 
                if (attrName.equalsIgnoreCase("alt")) {
                    String attrValue = attr.getNodeValue();
                    attrValue = "image : ".concat(attrValue);
                    attrValue = attrValue.concat(" ");
                    text.append(attrValue);
                }
            }
        }
        else if (nodeType == Node.TEXT_NODE) {
            String nodeValue = node.getNodeValue();
            nodeValue = nodeValue.trim();
            nodeValue = nodeValue.concat(" ");
            text.append(nodeValue);
        }
    }

    /**
     * return the title of the item
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return title;
    }
}
