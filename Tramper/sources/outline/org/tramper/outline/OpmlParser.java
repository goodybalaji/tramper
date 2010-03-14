package org.tramper.outline;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.tramper.browser.MimeTypeMapper;
import org.tramper.doc.Link;
import org.tramper.doc.Outline;
import org.tramper.doc.OutlineItem;
import org.tramper.doc.SimpleDocument;
import org.tramper.parser.Parser;
import org.tramper.parser.ParsingException;
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
    /** logger */
    private Logger logger = Logger.getLogger(OpmlParser.class);
    /** date format */
    private SimpleDateFormat dateFormat;

    /**
     * Initialize the XML document builder 
     */
    public OpmlParser() {
        dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
    }

    /**
     * @see org.tramper.parser.Parser#parse(java.io.InputStream, URL)
     */
    public SimpleDocument parse(InputStream inStream, URL url) throws ParsingException {
	int linkNumber = 1;
        Document aDoc = null;
        DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
        docBuildFactory.setIgnoringComments(true);
        docBuildFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder aDocBuilder = null;
        try {
            aDocBuilder = docBuildFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error("XML document builder instanciation failed", e);
            throw new ParsingException("Impossible to get a document builder", e);
        }
        try {
            aDoc = aDocBuilder.parse(inStream);
        } catch (IOException e) {
            throw new ParsingException("Impossible to read the document", e);
        } catch (SAXException e) {
            throw new ParsingException("Impossible to parse the document", e);
        }
        Element docRoot = aDoc.getDocumentElement();

        Outline outline = new Outline();
        try {
            outline.setMimeType("text/x-opml");
            
            Node aNode = XPathAPI.selectSingleNode(docRoot, "/opml/head/title/text()");
            if (aNode != null) {
                String aText = aNode.getNodeValue();
                if (aText != null) {
                    outline.setTitle(aText);
                }
            }
            aNode = XPathAPI.selectSingleNode(docRoot, "/opml/head/ownerEmail/text()");
            if (aNode != null) {
                String anEmail = aNode.getNodeValue();
                if (anEmail != null) {   
                }
            }
            aNode = XPathAPI.selectSingleNode(docRoot, "/opml/head/ownerName/text()");
            if (aNode != null) {
                String aName = aNode.getNodeValue();
                if (aName != null) {
                    outline.setAuthor(aName);
                }
            }
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
            OutlineItem aRootItem = new OutlineItem(outline);
            this.depthFirstReading(aRootItem, body, linkNumber);
            outline.setRoot(aRootItem);
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
    protected void depthFirstReading(OutlineItem anItem, Node aNode, int linkNumber) {
        NodeList childNodes = aNode.getChildNodes();
        
        for (int i=0; i<childNodes.getLength(); i++) {
            Node aChild = childNodes.item(i);
            short aChildType = aChild.getNodeType();
            if (aChildType != Node.ELEMENT_NODE) {
                continue;
            }
            Element aChildNode = (Element)aChild;
            OutlineItem aChildItem = new OutlineItem(anItem.getTree());

            String url = null;
            if (aChildNode.hasAttribute("url")) {
                url = aChildNode.getAttribute("url");
            } else if (aChildNode.hasAttribute("xmlUrl")) {
                //URL of a RSS feed 
                url = aChildNode.getAttribute("xmlUrl");
            } else if (aChildNode.hasAttribute("htmlUrl")) {
                //URL of the link element of RSS feed 
                url = aChildNode.getAttribute("htmlUrl");
            }
            
            String mimeType = null;
            if (aChildNode.hasAttribute("type")) {
                String type = aChildNode.getAttribute("type");
                if (type.equalsIgnoreCase("rss")) {
                    //must be a feed
                    mimeType = "application/rss+xml";
                } else if (type.equalsIgnoreCase("link")) {
                    //could be any document viewable in a browser
                    mimeType = MimeTypeMapper.getInstance().getMimeType(url);
                } else if (type.equalsIgnoreCase("include")) {
                    //must be an outline
                    mimeType = "text/x-opml";
                } else {
                    mimeType = MimeTypeMapper.getInstance().getMimeType(url);
                }
            }
            
            String title = null;
            if (aChildNode.hasAttribute("text")) {
                title = aChildNode.getAttribute("text");
            } else if (aChildNode.hasAttribute("title")) {
                title = aChildNode.getAttribute("title");
            }
            aChildItem.setTitle(title);

            String description = null;
            if (aChildNode.hasAttribute("description")) {
                description = aChildNode.getAttribute("description");
            }
            
            if (url != null) {
                try {
                    URL linkUrl = new URL(url);
                    SimpleDocument aDocument = new SimpleDocument();
                    aDocument.setUrl(linkUrl);
                    aDocument.setMimeType(mimeType);
                    aDocument.setDescription(description);
                    Link aLink = new Link();
                    aLink.setLinkedDocument(aDocument);
                    aLink.setRelation("related");
                    aLink.setNumber(linkNumber++);
                    //aLink.setLinkingDocument(outline);
                    aChildItem.addLink(aLink);
                } catch (MalformedURLException e) {
                    logger.warn(e.getMessage());
                }
            }
            
            anItem.addChild(aChildItem);
            depthFirstReading(aChildItem, aChildNode, linkNumber);
        }
    }

    public void unparse(OutputStream outStream, SimpleDocument document, URL url) throws ParsingException {
	if (!(document instanceof Outline)) {
	    throw new RuntimeException(document.getTitle()+" is not an outline");
	}
	Outline doc = (Outline)document;

        DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
        //there is a trouble with namespace aware to true when reading the feeds
        docBuildFactory.setIgnoringComments(true);
        docBuildFactory.setIgnoringElementContentWhitespace(true);
        
        DocumentBuilder aDocBuilder = null;
        try {
            aDocBuilder = docBuildFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error("XML document builder instanciation failed", e);
        }
        Document docSource = aDocBuilder.newDocument();
        DOMSource source = new DOMSource(docSource);
        
        Locale locale = doc.getLanguage();
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String lang = locale.getLanguage();
        Element opmlElem = docSource.createElement("opml");
        opmlElem.setAttribute("xml:lang", lang);
        opmlElem.setAttribute("version", "2.0");
        docSource.appendChild(opmlElem);

        Element headElem = docSource.createElement("head");
        opmlElem.appendChild(headElem);
        
        String title = doc.getTitle();
        if (title != null) {
            Element titleElem = docSource.createElement("title");
            titleElem.appendChild(docSource.createTextNode(title));
            headElem.appendChild(titleElem);
        }

        Date creationDate = doc.getCreationDate();
        if (creationDate != null) {
            Element creationElem = docSource.createElement("dateCreated");
            creationElem.appendChild(docSource.createTextNode(dateFormat.format(creationDate)));
            headElem.appendChild(creationElem);
        }
        
        Date modificationDate = doc.getModificationDate();
        if (modificationDate != null) {
            Element modificationElem = docSource.createElement("dateModified");
            modificationElem.appendChild(docSource.createTextNode(dateFormat.format(modificationDate)));
            headElem.appendChild(modificationElem);
        }

        String author = doc.getAuthor();
        if (author != null) {
            Element ownerElem = docSource.createElement("ownerName");
            ownerElem.appendChild(docSource.createTextNode(author));
            headElem.appendChild(ownerElem);
        }

        Element bodyElem = docSource.createElement("body");
        opmlElem.appendChild(bodyElem);
        
        OutlineItem root = (OutlineItem)doc.getRoot();
        List<OutlineItem> children = root.getChildren();
        for (OutlineItem child : children) {
            depthFirstWriting(child, bodyElem, docSource);
        }
        
        //write the created source document into the output stream
        TransformerFactory transFact = TransformerFactory.newInstance();
        StreamResult result = new StreamResult(outStream);
        try {
            Transformer isoTransformer = transFact.newTransformer();
            isoTransformer.transform(source, result);
        } catch (TransformerConfigurationException e2) {
            logger.error(e2.getMessageAndLocation());
        } catch (TransformerException e) {
            logger.error(e.getMessageAndLocation());
        }
    }
    
    public void depthFirstWriting(OutlineItem item, Element elem, Document docSource) {
        Element outlineElem = docSource.createElement("outline");
        outlineElem.setAttribute("text", item.getTitle());
        List<Link> links = item.getLinks();
        if (links != null && links.size() > 0) {
            SimpleDocument doc = links.get(0).getLinkedDocument();
            String desc = doc.getDescription();
            outlineElem.setAttribute("description", desc);
            URL url = doc.getUrl();
            String mimeType = doc.getMimeType();
            if (mimeType.equals("application/rss+xml")) {
        	outlineElem.setAttribute("type", "rss");
                outlineElem.setAttribute("xmlUrl", url.toString());
            } else if (mimeType.equals("text/x-opml")) {
        	outlineElem.setAttribute("type", "include");
                outlineElem.setAttribute("url", url.toString());
            } else {
        	outlineElem.setAttribute("type", "link");
                outlineElem.setAttribute("url", url.toString());
            }
        }
        elem.appendChild(outlineElem);
        List<OutlineItem> children = item.getChildren();
        for (OutlineItem child : children) {
            depthFirstWriting(child, outlineElem, docSource);
        }
    }

    public boolean isExtensionSupported(String extension) {
	if (extension.equalsIgnoreCase("opml")) {
	    return true;
	}
	return false;
    }

    public boolean isMimeTypeSupported(String mimeType) {
	if (mimeType.equalsIgnoreCase("text/x-opml")) {
	    return true;
	} else if (mimeType.equalsIgnoreCase("text/opml")) {
	    return true;
	}
	return false;
    }

    public List<String> getSupportedExtensions() {
	List<String> extensions = new ArrayList<String>();
	extensions.add("opml");
	return extensions;
    }
    
    /**
     * 
     * @return
     */
    public SimpleDocument getSupportedDocument() {
	return new Outline();
    }
}
