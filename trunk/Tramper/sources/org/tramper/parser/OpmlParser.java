package org.tramper.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.tramper.doc.Link;
import org.tramper.doc.Sound;
import org.tramper.doc.Outline;
import org.tramper.doc.OutlineItem;
import org.tramper.doc.SimpleDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * OPML 2.0 parser
 * @author Paul-Emile
 */
public class OpmlParser implements Parser {
    /** XML document builder */
    private DocumentBuilder aDocBuilder;
    /** logger */
    private Logger logger = Logger.getLogger(OpmlParser.class);
    /** document's url */
    protected URL url;
    /** link number */
    protected int linkNumber;

    /**
     * Initialize the XML document builder 
     */
    public OpmlParser() {
        DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
        docBuildFactory.setIgnoringComments(true);
        docBuildFactory.setValidating(false);
        docBuildFactory.setNamespaceAware(false);
        docBuildFactory.setIgnoringElementContentWhitespace(true);
        try {
            aDocBuilder = docBuildFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            logger.error("XML document builder instanciation failed", e);
        }
    }

    /**
     * @param url url 
     */
    public void setUrl(URL url) {
        this.url = url;
    }
    
    /**
     * @see org.tramper.parser.Parser#parse(java.io.InputStream)
     */
    public SimpleDocument parse(InputStream inStream) throws ParsingException {
	linkNumber = 1;
        Document aDoc = null;
        try {
            aDoc = aDocBuilder.parse(inStream);
        } catch (IOException e) {
            throw new ParsingException("Impossible to read the document", e);
        } catch (SAXException e) {
            throw new ParsingException("Impossible to parse the document", e);
        }
        Element docRoot = aDoc.getDocumentElement();

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        
        Outline outline = new Outline();
        try {
            outline.setMimeType("text/x-opml");
            
            Node aNode = XPathAPI.selectSingleNode(docRoot, "/opml/head/title/text()");
            if (aNode != null) {
                String aText = aNode.getNodeValue();
                if (aText != null)
                    outline.setTitle(aText);
            }
            String author = null;
            aNode = XPathAPI.selectSingleNode(docRoot, "/opml/head/ownerEmail/text()");
            if (aNode != null) {
                String anEmail = aNode.getNodeValue();
                if (anEmail != null)
                    author = anEmail;
            }
            aNode = XPathAPI.selectSingleNode(docRoot, "/opml/head/ownerName/text()");
            if (aNode != null) {
                String aName = aNode.getNodeValue();
                if (aName != null) {
                    if (author == null)
                        author = aName;
                    else
                        author += " ("+aName+")";
                }
            }
            outline.setAuthor(author);
            aNode = XPathAPI.selectSingleNode(docRoot, "/opml/head/dateCreated/text()");
            if (aNode != null) {
                String aText = aNode.getNodeValue();
                if (aText != null) {
                    try {
                        Date pubDate = dateFormat.parse(aText);
                        outline.setCreationDate(pubDate);
                    }
                    catch (ParseException e) {
                        logger.warn("error when parsing created date : "+aText);
                    }
                }
            }
            aNode = XPathAPI.selectSingleNode(docRoot, "/opml/head/dateModified/text()");
            if (aNode != null) {
                String aText = aNode.getNodeValue();
                if (aText != null) {
                    try {
                        Date buildDate = dateFormat.parse(aText);
                        outline.setModificationDate(buildDate);
                    }
                    catch (ParseException e) {
                        logger.warn("error when parsing modified date : "+aText);
                    }
                }
            }
            
            Node body = docRoot.getElementsByTagName("body").item(0);
            OutlineItem aRootItem = new OutlineItem();
            this.depthFirstSearch(aRootItem, body);
            outline.setRootItem(aRootItem);
        }
        catch (TransformerException e) {
            logger.error("XPath error", e);
            throw new ParsingException("Error when reading the opml document");
        }
        return outline;
    }

    /**
     * Depth first search of a tree node 
     * @param anItem outline 
     * @param aNode root of the tree
     */
    protected void depthFirstSearch(OutlineItem anItem, Node aNode) {
        NodeList childNodes = aNode.getChildNodes();
        
        for (int i=0; i<childNodes.getLength(); i++) {
            Node aChild = childNodes.item(i);
            short aChildType = aChild.getNodeType();
            if (aChildType != Node.ELEMENT_NODE)
                continue;
            Element aChildNode = (Element)aChild;
            OutlineItem aChildItem = new OutlineItem();
            
            String mimeType = null;
            if (aChildNode.hasAttribute("type")) {
                String type = aChildNode.getAttribute("type");
                //should a feed
                if (type.equalsIgnoreCase("rss")) {
                    mimeType = "application/rss+xml";
                }
                //could be any document viewable in browser
                else if (type.equalsIgnoreCase("link")) {
                    mimeType = "";
                }
                //must be an outline
                else if (type.equalsIgnoreCase("include")) {
                    mimeType = "text/x-opml";
                }
            }
            
            String title = null;
            if (aChildNode.hasAttribute("text"))
                title = aChildNode.getAttribute("text");
            else if (aChildNode.hasAttribute("title"))
                title = aChildNode.getAttribute("title");
            aChildItem.setTitle(title);
            
            String url = null;
            if (aChildNode.hasAttribute("url"))
                url = aChildNode.getAttribute("url");
            else if (aChildNode.hasAttribute("xmlUrl"))
                url = aChildNode.getAttribute("xmlUrl");
            else if (aChildNode.hasAttribute("htmlUrl"))
                url = aChildNode.getAttribute("htmlUrl");
            //htmlUrl attribute is used for the link attribute of rss type 
            if (url != null) {
                try {
                    URL linkUrl = new URL(url);
                    Sound aDocument = new Sound();
                    aDocument.setUrl(linkUrl);
                    aDocument.setMimeType(mimeType);
                    Link aLink = new Link();
                    aLink.setLinkedDocument(aDocument);
                    aLink.setRelation("related");
                    aLink.setNumber(linkNumber++);
                    //aLink.setLinkingDocument(outline);
                    aChildItem.addLink(aLink);
                }
                catch (MalformedURLException e) {
                    logger.warn(e.getMessage());
                }
            }
            
            anItem.addChild(aChildItem);
            aChildItem.setFather(anItem);
            depthFirstSearch(aChildItem, aChildNode);
        }
    }

    public void unparse(OutputStream outStream, SimpleDocument document) throws ParsingException {
    }
}
