package org.tramper.feed;

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

import javax.swing.EnhancedIcon;
import javax.swing.Icon;
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
import org.tramper.doc.DocumentItem;
import org.tramper.doc.Feed;
import org.tramper.doc.FeedItem;
import org.tramper.doc.Link;
import org.tramper.doc.Sound;
import org.tramper.doc.SimpleDocument;
import org.tramper.parser.Parser;
import org.tramper.parser.ParsingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Atom 0.3 and 1.0 parser
 * @author Paul-Emile
 */
public class AtomParser implements Parser {
    /** logger */
    private Logger logger = Logger.getLogger(AtomParser.class);
    /**  */
    protected SimpleDateFormat dateFormat = null;
    /**  */
    protected SimpleDateFormat dateFormatMS = null;
    /**  */
    protected SimpleDateFormat dateFormatTZ = null;
    /**  */
    protected SimpleDateFormat dateFormatMSTZ = null;
    

    /**
     * Initialize the XML document builder 
     */
    public AtomParser() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        dateFormatMS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'", Locale.US);
        dateFormatTZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz", Locale.US);
        dateFormatMSTZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSz", Locale.US);
    }

    /**
     * Read from the stream in parameter and make a speakable document
     * @see org.tramper.parser.Parser#parse(java.io.InputStream, URL)
     */
    public SimpleDocument parse(InputStream inStream, URL url) throws ParsingException {
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
        Document aDoc = null;
        try {
            aDoc = aDocBuilder.parse(inStream);
        } catch (IOException e) {
            throw new ParsingException("Impossible to read the document", e);
        } catch (SAXException e) {
            throw new ParsingException("Impossible to parse the document", e);
        }
        Element docRoot = aDoc.getDocumentElement();

        Feed feed = new Feed();
        feed.setMimeType("application/atom+xml");
        
        try {
            Node aNode = XPathAPI.selectSingleNode(docRoot, "/feed/@xml:lang");
            /*if (aNode == null) {
                aNode = XPathAPI.selectSingleNode(docRoot, "/feed/@lang");
            }*/
            if (aNode != null) {
                String language = aNode.getNodeValue();
                if (language != null)
                    feed.parseLanguage(language);
            }
            
            aNode = XPathAPI.selectSingleNode(docRoot, "/feed/title/text()");
            if (aNode != null) {
                String aText = aNode.getNodeValue();
                if (aText != null)
                    feed.setTitle(aText);
            }
            aNode = XPathAPI.selectSingleNode(docRoot, "/feed/subtitle/text()");
            if (aNode != null) {
                String aText = aNode.getNodeValue();
                if (aText != null)
                    feed.setDescription(aText);
            }
            aNode = XPathAPI.selectSingleNode(docRoot, "/feed/category/@term");
            if (aNode != null) {
                String aText = aNode.getNodeValue();
                if (aText != null)
                    feed.setCategory(aText);
            }
            aNode = XPathAPI.selectSingleNode(docRoot, "/feed/icon/text()");
            if (aNode != null) {
                String aText = aNode.getNodeValue();
                if (aText != null) {
                    try {
                	URL iconUrl = new URL(aText);
                	Icon icon = new EnhancedIcon(iconUrl);
                        feed.setIcon(icon);
                    } catch (MalformedURLException e) {}
                }
            } else {
                aNode = XPathAPI.selectSingleNode(docRoot, "/feed/logo/text()");
                if (aNode != null) {
                    String aText = aNode.getNodeValue();
                    if (aText != null) {
                        try {
                            URL iconUrl = new URL(aText);
                            Icon icon = new EnhancedIcon(iconUrl);
                            feed.setIcon(icon);
                        } catch (MalformedURLException e) {}
                    }
                }
            }
            aNode = XPathAPI.selectSingleNode(docRoot, "/feed/author");
            if (aNode != null) {
                String author = null;
                Node anEmailNode = XPathAPI.selectSingleNode(aNode, "email/text()");
                if (anEmailNode != null) {
                    String anEmail = anEmailNode.getNodeValue();
                    if (anEmail != null)
                        author = anEmail;
                }
                Node aNameNode = XPathAPI.selectSingleNode(aNode, "name/text()");
                if (aNameNode != null) {
                    String aName = aNameNode.getNodeValue();
                    if (aName != null) {
                        if (author == null)
                            author = aName;
                        else
                            author += " ("+aName+")";
                    }
                }
                feed.setAuthor(author);
            }
            //Atom 1.0
            aNode = XPathAPI.selectSingleNode(docRoot, "/feed/rights/text()");
            if (aNode != null) {
                String aText = aNode.getNodeValue();
                if (aText != null)
                    feed.setCopyright(aText);
            }
            else {
                //Atom 0.3
                aNode = XPathAPI.selectSingleNode(docRoot, "/feed/copyright/text()");
                if (aNode != null) {
                    String aText = aNode.getNodeValue();
                    if (aText != null)
                        feed.setCopyright(aText);
                }
            }
            //Atom 1.0
            aNode = XPathAPI.selectSingleNode(docRoot, "/feed/published/text()");
            if (aNode != null) {
                String aText = aNode.getNodeValue();
                if (aText != null) {
                    Date pubDate = this.formatDate(aText);
                    feed.setCreationDate(pubDate);
                }
            }
            else {
                //Atom 0.3
                aNode = XPathAPI.selectSingleNode(docRoot, "/feed/issued/text()");
                if (aNode != null) {
                    String aText = aNode.getNodeValue();
                    if (aText != null) {
                        Date pubDate = this.formatDate(aText);
                        feed.setCreationDate(pubDate);
                    }
                }
            }
            //Atom 1.0
            aNode = XPathAPI.selectSingleNode(docRoot, "/feed/updated/text()");
            if (aNode != null) {
                String aText = aNode.getNodeValue();
                if (aText != null) {
                    Date buildDate = this.formatDate(aText);
                    feed.setLastBuildDate(buildDate);
                }
            }
            else {
                //Atom 0.3
                aNode = XPathAPI.selectSingleNode(docRoot, "/feed/modified/text()");
                if (aNode != null) {
                    String aText = aNode.getNodeValue();
                    if (aText != null) {
                        Date buildDate = this.formatDate(aText);
                        feed.setLastBuildDate(buildDate);
                    }
                }
            }

            NodeList linkList =  XPathAPI.selectNodeList(docRoot, "/feed/link[@rel='via' or @rel='next' or @rel='previous' or @rel='related' or @rel='alternate' or @rel='comments' or @rel='service.post' or @rel='service.edit']");
            if (linkList != null) {
                for (int j=0; j<linkList.getLength(); j++) {
                    Node linkNode = linkList.item(j);
                    SimpleDocument aDocument = new SimpleDocument();
                    Node urlNode = XPathAPI.selectSingleNode(linkNode, "@href");
                    if (urlNode != null) {
                        String encodedUrl = urlNode.getNodeValue();
                        try {
                            URL aUrl = new URL(encodedUrl);
                            aDocument.setUrl(aUrl);
                            Node typeNode = XPathAPI.selectSingleNode(linkNode, "@type");
                            if (typeNode != null) {
                                String type = typeNode.getNodeValue();
                                aDocument.setMimeType(type);
                            }
                            Node titleNode = XPathAPI.selectSingleNode(linkNode, "@title");
                            if (titleNode != null) {
                                aDocument.setTitle(titleNode.getNodeValue());
                            }
                            else {
                                String path = aUrl.getPath();
                                int slashIndex = path.lastIndexOf("/");
                                String filename = path.substring(slashIndex+1);
                                aDocument.setTitle(filename);
                            }
                            
                            Link aLink = new Link();
                            aLink.setLinkedDocument(aDocument);
                            aLink.setLinkingDocument(feed);
                            Node relationNode = XPathAPI.selectSingleNode(linkNode, "@rel");
                            if (relationNode != null) {
                                aLink.setRelation(relationNode.getNodeValue());
                            }
                            feed.addLink(aLink);
                        }
                        catch (MalformedURLException e) {
                            logger.warn("bad link url", e);
                        }
                    }
                }
            }
            
            int linkNumber = 1;
            NodeList itemNodeList =  XPathAPI.selectNodeList(docRoot, "/feed/entry");
            for (int i=0; i<itemNodeList.getLength(); i++) {
                Node itemNode = itemNodeList.item(i);
                FeedItem item = new FeedItem();
                
                aNode = XPathAPI.selectSingleNode(itemNode, "id/text()");
                if (aNode != null) {
                    String aText = aNode.getNodeValue();
                    if (aText != null)
                        item.setId(aText);
                }
                aNode = XPathAPI.selectSingleNode(itemNode, "title/text()");
                if (aNode != null) {
                    String aText = aNode.getNodeValue();
                    if (aText != null)
                        item.setTitle(aText);
                }

                //Atom 1.0
                aNode = XPathAPI.selectSingleNode(itemNode, "published/text()");
                if (aNode != null) {
                    String aText = aNode.getNodeValue();
                    if (aText != null) {
                        Date pubDate = this.formatDate(aText);
                        item.setPublicationDate(pubDate);
                    }
                }
                else {
                    //Atom 0.3
                    aNode = XPathAPI.selectSingleNode(itemNode, "issued/text()");
                    if (aNode != null) {
                        String aText = aNode.getNodeValue();
                        if (aText != null) {
                            Date pubDate = this.formatDate(aText);
                            item.setPublicationDate(pubDate);
                        }
                    }
                }
                //Atom 1.0
                aNode = XPathAPI.selectSingleNode(itemNode, "updated/text()");
                if (aNode != null) {
                    String aText = aNode.getNodeValue();
                    if (aText != null) {
                        Date buildDate = this.formatDate(aText);
                        item.setUpdateDate(buildDate);
                    }
                }
                else {
                    //Atom 0.3
                    aNode = XPathAPI.selectSingleNode(itemNode, "modified/text()");
                    if (aNode != null) {
                        String aText = aNode.getNodeValue();
                        if (aText != null) {
                            Date buildDate = this.formatDate(aText);
                            item.setUpdateDate(buildDate);
                        }
                    }
                }
                aNode = XPathAPI.selectSingleNode(itemNode, "content");
                if (aNode != null) {
                    String type = "text";
                    Node aTypeNode = XPathAPI.selectSingleNode(itemNode, "content/@type");
                    if (aTypeNode != null) {
                        type = aTypeNode.getNodeValue();
                    }
                    if (type.equals("text") || type.equals("text/plain") || type.equals("html") || type.equals("text/html")) {
                        Node aTextNode = XPathAPI.selectSingleNode(itemNode, "content/text()");
                        if (aTextNode != null) {
                            item.setDescription(aTextNode.getNodeValue());
                        }
                    }
                    else if (type.equals("xhtml")) {
                        Node aDivNode = XPathAPI.selectSingleNode(itemNode, "content/div");
                        if (aDivNode != null) {
                            StringBuffer descriptionBuffer = new StringBuffer();
                            this.depthFirstSearch(descriptionBuffer, aDivNode);
                            item.setDescription(descriptionBuffer.toString());
                        }
                    }
                    else {
                        
                    }
                }
                else {
                    aNode = XPathAPI.selectSingleNode(itemNode, "summary/text()");
                    if (aNode != null) {
                        String aText = aNode.getNodeValue();
                        if (aText != null) {
                            item.setDescription(aText);
                        }
                    }
                }
                
                aNode = XPathAPI.selectSingleNode(itemNode, "category/@term");
                if (aNode != null) {
                    String aText = aNode.getNodeValue();
                    if (aText != null)
                        item.setCategory(aText);
                }
                
                Node enclosureNode =  XPathAPI.selectSingleNode(itemNode, "link[@rel='enclosure']");
                if (enclosureNode != null) {
                    Sound aMedia = new Sound();
                    Node urlNode = XPathAPI.selectSingleNode(enclosureNode, "@href");
                    if (urlNode != null) {
                        String encodedUrl = urlNode.getNodeValue();
                        try {
                            URL aUrl = new URL(encodedUrl);
                            aMedia.setUrl(aUrl);
                            Node titleNode = XPathAPI.selectSingleNode(enclosureNode, "@title");
                            if (titleNode != null) {
                                aMedia.setTitle(titleNode.getNodeValue());
                            } else {
                                String path = aUrl.getPath();
                                int slashIndex = path.lastIndexOf("/");
                                String filename = path.substring(slashIndex+1);
                                aMedia.setTitle(filename);
                            }
                            Node typeNode = XPathAPI.selectSingleNode(enclosureNode, "@type");
                            if (typeNode != null) {
                                aMedia.setMimeType(typeNode.getNodeValue());
                            }
                            Node lengthNode = XPathAPI.selectSingleNode(enclosureNode, "@length");
                            if (lengthNode != null) {
                                try {
                                    int length = Integer.parseInt(lengthNode.getNodeValue().trim());
                                    aMedia.setLength(length);
                                } catch (NumberFormatException e) {
                                    logger.warn("bad enclosure length : "+lengthNode.getNodeValue(), e);
                                }
                            }
                            item.addMedia(aMedia);
                        }
                        catch (MalformedURLException e) {
                            logger.warn("bad enclosure url", e);
                        }
                    }
                }
                
                linkList =  XPathAPI.selectNodeList(itemNode, "link[@rel='via' or @rel='next' or @rel='previous' or @rel='related' or @rel='alternate' or @rel='comments' or @rel='service.post' or @rel='service.edit']");
                if (linkList != null) {
                    for (int j=0; j<linkList.getLength(); j++) {
                        Node linkNode = linkList.item(j);
                        SimpleDocument aDocument = new SimpleDocument();
                        Node urlNode = XPathAPI.selectSingleNode(linkNode, "@href");
                        if (urlNode != null) {
                            String encodedUrl = urlNode.getNodeValue();
                            try {
                                URL anUrl = new URL(encodedUrl);
                                aDocument.setUrl(anUrl);
                                
                                Node typeNode = XPathAPI.selectSingleNode(linkNode, "@type");
                                if (typeNode != null) {
                                    String type = typeNode.getNodeValue();
                                    aDocument.setMimeType(type);
                                }
                                Node titleNode = XPathAPI.selectSingleNode(linkNode, "@title");
                                if (titleNode != null) {
                                    aDocument.setTitle(titleNode.getNodeValue());
                                } else {
                                    String path = anUrl.getPath();
                                    int slashIndex = path.lastIndexOf("/");
                                    String filename = path.substring(slashIndex+1);
                                    aDocument.setTitle(filename);
                                }
                                
                                Link aLink = new Link();
                                aLink.setLinkedDocument(aDocument);
                                aLink.setLinkingDocument(feed);
                                aLink.setNumber(linkNumber++);
                                Node relationNode = XPathAPI.selectSingleNode(linkNode, "@rel");
                                if (relationNode != null) {
                                    aLink.setRelation(relationNode.getNodeValue());
                                }
                                
                                item.addLink(aLink);
                            }
                            catch (MalformedURLException e) {
                                logger.warn("bad link url", e);
                            }
                        }
                    }
                }
                feed.addItem(item);
            }
        }
        catch (TransformerException e) {
            logger.error("XPath error", e);
            throw new ParsingException("Error when reading the Atom document");
        }
        return feed;
    }
    
    /**
     * 
     * @param document
     * @param inStream
     * @throws ParsingException
     */
    public void unparse(OutputStream outStream, SimpleDocument document, URL url) throws ParsingException {
	if (!(document instanceof Feed)) {
	    throw new RuntimeException(document.getTitle()+" is not a Feed");
	}
        Feed doc = (Feed)document;

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
        Element feedElem = docSource.createElement("feed");
        feedElem.setAttribute("xml:lang", lang);
        feedElem.setAttribute("xmlns", "http://www.w3.org/2005/Atom");
        docSource.appendChild(feedElem);

        String title = doc.getTitle();
        if (title != null) {
            Element titleElem = docSource.createElement("title");
            titleElem.appendChild(docSource.createTextNode(title));
            feedElem.appendChild(titleElem);
        }
        
        String desc = doc.getDescription();
        if (desc != null) {
            Element subtitleElem = docSource.createElement("subtitle");
            subtitleElem.appendChild(docSource.createTextNode(desc));
            feedElem.appendChild(subtitleElem);
        }

        Date buildDate = doc.getLastBuildDate();
        if (buildDate != null) {
            Element updateElem = docSource.createElement("updated");
            updateElem.appendChild(docSource.createTextNode(dateFormat.format(buildDate)));
            feedElem.appendChild(updateElem);
        }

        Date pubDate = doc.getCreationDate();
        if (pubDate != null) {
            Element publishElem = docSource.createElement("published");
            publishElem.appendChild(docSource.createTextNode(dateFormat.format(pubDate)));
            feedElem.appendChild(publishElem);
        }
        
        String copyright = doc.getCopyright();
        if (copyright != null) {
            Element rightElem = docSource.createElement("rights");
            rightElem.appendChild(docSource.createTextNode(copyright));
            feedElem.appendChild(rightElem);
        }
        
        String category = doc.getCategory();
        if (category != null) {
            Element categoryElem = docSource.createElement("category");
            categoryElem.setAttribute("term", category);
            feedElem.appendChild(categoryElem);
        }

        String author = doc.getAuthor();
        if (author != null) {
            Element authorElem = docSource.createElement("author");
            feedElem.appendChild(authorElem);
            Element nameElem = docSource.createElement("name");
            nameElem.appendChild(docSource.createTextNode(author));
            authorElem.appendChild(nameElem);
        }
        
        List<Link> links = doc.getLinks();
        for (int i=0; i<links.size(); i++) {
            Link aLink = links.get(i);
            SimpleDocument mediaDocument = aLink.getLinkedDocument();
            Element linkElem = docSource.createElement("link");
            feedElem.appendChild(linkElem);
            
            String rel = aLink.getRelation();
            if (rel != null) {
                linkElem.setAttribute("rel", rel);
            }
            String type = mediaDocument.getMimeType();
            if (type != null) {
                linkElem.setAttribute("type", type);
            }
            URL mediaUrl = mediaDocument.getUrl();
            if (mediaUrl != null) {
                linkElem.setAttribute("href", mediaUrl.toString());
            }
            title = mediaDocument.getTitle();
            if (title != null) {
                linkElem.setAttribute("title", title);
            }
        }

        List<DocumentItem> items = doc.getItems();
        for (int i=0; i<items.size(); i++) {
            FeedItem item = (FeedItem)items.get(i);

            Element entryElem = docSource.createElement("entry");
            feedElem.appendChild(entryElem);
            
            title = item.getTitle();
            if (title != null) {
                Element titleElem = docSource.createElement("title");
                titleElem.appendChild(docSource.createTextNode(title));
                entryElem.appendChild(titleElem);
            }
            
            category = item.getCategory();
            if (category != null) {
                Element categoryElem = docSource.createElement("category");
                categoryElem.setAttribute("term", category);
                entryElem.appendChild(categoryElem);
            }
            
            desc = item.getDescription();
            if (desc != null) {
                Element summaryElem = docSource.createElement("summary");
                summaryElem.appendChild(docSource.createTextNode(desc));
                entryElem.appendChild(summaryElem);
            }
            
            String id = item.getId();
            if (id != null) {
                Element idElem = docSource.createElement("id");
                idElem.appendChild(docSource.createTextNode(id));
                entryElem.appendChild(idElem);
            }

            pubDate = item.getPublicationDate();
            if (pubDate != null) {
                Element updateElem = docSource.createElement("published");
                updateElem.appendChild(docSource.createTextNode(dateFormat.format(pubDate)));
                entryElem.appendChild(updateElem);
            }

            buildDate = item.getUpdateDate();
            if (buildDate != null) {
                Element publishElem = docSource.createElement("updated");
                publishElem.appendChild(docSource.createTextNode(dateFormat.format(buildDate)));
                entryElem.appendChild(publishElem);
            }

            links = item.getLinks();
            for (int j=0; j<links.size(); j++) {
                Link aLink = (Link)links.get(j);
                SimpleDocument aDocument = aLink.getLinkedDocument();
                Element linkElem = docSource.createElement("link");
                entryElem.appendChild(linkElem);
                
                String rel = aLink.getRelation();
                if (rel != null) {
                    linkElem.setAttribute("rel", rel);
                }
                String type = aDocument.getMimeType();
                if (type != null) {
                    linkElem.setAttribute("type", type);
                }
                URL linkUrl = aDocument.getUrl();
                if (linkUrl != null) {
                    linkElem.setAttribute("href", linkUrl.toString());
                }
                title = aDocument.getTitle();
                if (title != null) {
                    linkElem.setAttribute("title", title);
                }
            }
            
            List<Sound> medias = item.getMedia();
            for (int j=0; j<medias.size(); j++) {
                Sound mediaDocument = medias.get(j);
                Element linkElem = docSource.createElement("link");
                entryElem.appendChild(linkElem);
                
                linkElem.setAttribute("rel", "enclosure");

                String type = mediaDocument.getMimeType();
                if (type != null) {
                    linkElem.setAttribute("type", type);
                }
                URL mediaUrl = mediaDocument.getUrl();
                if (mediaUrl != null) {
                    linkElem.setAttribute("href", mediaUrl.toString());
                }
                long length = mediaDocument.getLength();
                if (length > 0) {
                    linkElem.setAttribute("length", String.valueOf(length));
                }
                title = mediaDocument.getTitle();
                if (title != null) {
                    linkElem.setAttribute("title", title);
                }
            }
        }
        
        //write the created source document into the output stream
        TransformerFactory transFact = TransformerFactory.newInstance();
        StreamResult result = new StreamResult(outStream);
        try {
            Transformer isoTransformer = transFact.newTransformer();
            isoTransformer.transform(source, result);
        }
        catch (TransformerConfigurationException e2) {
            logger.error(e2.getMessageAndLocation());
        }
        catch (TransformerException e) {
            logger.error(e.getMessageAndLocation());
        }
    }
    
    /**
     * try to format a text into a date 
     * @param aText
     * @return null if formating failed
     */
    protected Date formatDate(String aText) {
        Date aDate = null;
        try {
            aDate = dateFormat.parse(aText);
        }
        catch (ParseException pe) {
            try {
                aDate = dateFormatMS.parse(aText);
            }
            catch (ParseException pe1) {
                try {
                    aDate = dateFormatTZ.parse(aText);
                }
                catch (ParseException pe2) {
                    try {
                        aDate = dateFormatMSTZ.parse(aText);
                    }
                    catch (ParseException pe3) {
                        logger.warn("error when parsing date : "+aText);
                    }
                }
            }
        }
        return aDate;
    }
    
    /**
     * Depth first search of a tree node to 'serialize' in a string
     * @param text buffer for resulting string 
     * @param node root of the tree
     */
    protected void depthFirstSearch(StringBuffer text, Node node) {
        NodeList childNodes = node.getChildNodes();
        
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String nodeName = node.getNodeName();
            text.append("<"+nodeName);
            NamedNodeMap attributes = node.getAttributes();
            for (int j=0; j<attributes.getLength(); j++) {
                Node attr = attributes.item(j);
                String attrName = attr.getNodeName();
                String attrValue = attr.getNodeValue();
                text.append(" "+attrName+"='"+attrValue+"'");
            }
            text.append(">");
        }
        
        for (int i=0; i<childNodes.getLength(); i++) {
            Node aChild = childNodes.item(i);
            depthFirstSearch(text, aChild);
        }
        
        if (node.getNodeType() == Node.TEXT_NODE) {
            String nodeValue = node.getNodeValue();
            text.append(nodeValue);
        }
        else if (node.getNodeType() == Node.ELEMENT_NODE) {
            String nodeName = node.getNodeName();
            text.append("</"+nodeName+">");
        }
    }

    public boolean isExtensionSupported(String extension) {
	if (extension.equalsIgnoreCase("atom")) {
	    return true;
	}
	return false;
    }

    public boolean isMimeTypeSupported(String mimeType) {
	if (mimeType.equalsIgnoreCase("application/atom+xml")) {
	    return true;
	}
	return false;
    }

    public static List<String> getSupportedExtensions() {
	List<String> extensions = new ArrayList<String>();
	extensions.add("atom");
	return extensions;
    }
}
