package org.tramper.feed;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.EnhancedIcon;
import javax.swing.Icon;
import javax.xml.parsers.*;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.tramper.doc.Feed;
import org.tramper.doc.FeedItem;
import org.tramper.doc.Link;
import org.tramper.doc.SimpleDocument;
import org.tramper.parser.Parser;
import org.tramper.parser.ParsingException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


/**
 * RSS 0.9X, 1.0 and 2.0 parser
 * @author Paul-Emile
 */
public class RssParser implements Parser {
    /** logger */
    private Logger logger = Logger.getLogger(RssParser.class);
    
    /**
     * 
     */
    public RssParser() {
	super();
    }

    /**
     * Read from the stream in parameter and make a speakable document
     * @param inStream stream on a RSS document
     * @return a speakable document
     * @throws ParsingException
     */
    public SimpleDocument parse(InputStream inStream, URL url) throws ParsingException {
        DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
        docBuildFactory.setIgnoringComments(true);
        docBuildFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder aDocBuilder = null;
        try {
            aDocBuilder = docBuildFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new ParsingException("XML document builder instanciation failed", e);
        }
        Document aDoc = null;
        try {
            aDoc = aDocBuilder.parse(inStream);
        } catch (IOException e) {
            throw new ParsingException("Impossible de lire le document", e);
        } catch (SAXException e) {
            throw new ParsingException("Impossible de parser le document", e);
        }
        Element docRoot = aDoc.getDocumentElement();

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        
        Feed feed = new Feed();
        try {
            Node aNode = XPathAPI.selectSingleNode(docRoot, "/rss/channel/language/text()");
            if (aNode != null) {
                String language = aNode.getNodeValue();
                if (language != null)
                    feed.parseLanguage(language);
            }
            
            feed.setMimeType("application/rss+xml");
            
            aNode = XPathAPI.selectSingleNode(docRoot, "/rss/channel/title/text()");
            if (aNode != null) {
                String aText = aNode.getNodeValue();
                if (aText != null)
                    feed.setTitle(aText);
            }
            aNode = XPathAPI.selectSingleNode(docRoot, "/rss/channel/description/text()");
            if (aNode != null) {
                String aText = aNode.getNodeValue();
                if (aText != null)
                    feed.setDescription(aText);
            }
            aNode = XPathAPI.selectSingleNode(docRoot, "/rss/channel/category/text()");
            if (aNode != null) {
                String aText = aNode.getNodeValue();
                if (aText != null)
                    feed.setCategory(aText);
            }
            aNode = XPathAPI.selectSingleNode(docRoot, "/rss/channel/copyright/text()");
            if (aNode != null) {
                String aText = aNode.getNodeValue();
                if (aText != null)
                    feed.setCopyright(aText);
            }
            aNode = XPathAPI.selectSingleNode(docRoot, "/rss/channel/managingEditor/text()");
            if (aNode != null) {
                String aText = aNode.getNodeValue();
                if (aText != null)
                    feed.setAuthor(aText);
            }
            aNode = XPathAPI.selectSingleNode(docRoot, "/rss/channel/image/url/text()");
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
            aNode = XPathAPI.selectSingleNode(docRoot, "/rss/channel/pubDate/text()");
            if (aNode != null) {
                String aText = aNode.getNodeValue();
                if (aText != null) {
                    try {
                        Date pubDate = dateFormat.parse(aText);
                        feed.setCreationDate(pubDate);
                    }
                    catch (ParseException e) {
                        logger.warn("error when parsing publication date : "+aText);
                    }
                }
            }
            aNode = XPathAPI.selectSingleNode(docRoot, "/rss/channel/lastBuildDate/text()");
            if (aNode != null) {
                String aText = aNode.getNodeValue();
                if (aText != null) {
                    try {
                        Date buildDate = dateFormat.parse(aText);
                        feed.setLastBuildDate(buildDate);
                    }
                    catch (ParseException e) {
                        logger.warn("error when parsing last build date : "+aText);
                    }
                }
            }

            Node linkNode =  XPathAPI.selectSingleNode(docRoot, "/rss/channel/link/text()");
            if (linkNode != null) {
                try {
                    URL anUrl = new URL(linkNode.getNodeValue());
                    SimpleDocument aDocument = new SimpleDocument();
                    aDocument.setUrl(anUrl);
                    aDocument.setMimeType("text/html");
                    String pathname = anUrl.getPath();
                    aDocument.setTitle(pathname);
                    Link aLink = new Link();
                    aLink.setLinkedDocument(aDocument);
                    aLink.setLinkingDocument(feed);
                    aLink.setRelation("alternate");
                    feed.addLink(aLink);
                }
                catch (MalformedURLException e) {
                    logger.warn("bad link url", e);
                }
            }
            
            int linkNumber = 1;
            NodeList itemNodeList =  XPathAPI.selectNodeList(docRoot, "/rss/channel/item");
            for (int i=0; i<itemNodeList.getLength(); i++) {
                Node itemNode = itemNodeList.item(i);
                FeedItem item = new FeedItem();
                aNode = XPathAPI.selectSingleNode(itemNode, "guid/text()");
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
                aNode = XPathAPI.selectSingleNode(itemNode, "description/text()");
                if (aNode != null) {
                    String aText = aNode.getNodeValue();
                    if (aText != null) {
                        item.setDescription(aText);
                    }
                }
                aNode = XPathAPI.selectSingleNode(itemNode, "category/text()");
                if (aNode != null) {
                    String aText = aNode.getNodeValue();
                    if (aText != null)
                        item.setCategory(aText);
                }
                
                Node enclosureNode =  XPathAPI.selectSingleNode(itemNode, "enclosure");
                if (enclosureNode != null) {
                    Node urlNode = XPathAPI.selectSingleNode(enclosureNode, "@url");
                    if (urlNode == null) {
                        //RDF attribute :
                        urlNode = XPathAPI.selectSingleNode(enclosureNode, "@resource");
                    }
                    String urlString = urlNode.getNodeValue();
                    try {
                        URL aUrl = new URL(urlString);
                        Link aLink = new Link();
                        SimpleDocument aMedia = new SimpleDocument();
                        aMedia.setUrl(aUrl);
                        String pathname = aUrl.getPath();
                        int slashIndex = pathname.lastIndexOf("/");
                        String filename = pathname.substring(slashIndex+1);
                        aMedia.setTitle(filename);
                        Node typeNode = XPathAPI.selectSingleNode(enclosureNode, "@type");
                        aMedia.setMimeType(typeNode.getNodeValue());
                        Node lengthNode = XPathAPI.selectSingleNode(enclosureNode, "@length");
                        if (lengthNode != null) {
                            String lengthValue = lengthNode.getNodeValue();
                            try {
                                long length = Long.parseLong(lengthValue.trim());
                                aMedia.setLength(length);
                            } catch (NumberFormatException e) {
                                logger.warn("bad media length : "+lengthValue);
                            }
                        }
                        aLink.setLinkedDocument(aMedia);
                        aLink.setLinkingDocument(feed);
                        aLink.setRelation("enclosure");
                        aLink.setNumber(linkNumber++);
                        item.addLink(aLink);
                    } catch (MalformedURLException e) {
                        logger.warn("bad enclosure url", e);
                    }
                }
                
                Node sourceNode =  XPathAPI.selectSingleNode(itemNode, "source");
                if (sourceNode != null) {
                    Node urlNode = XPathAPI.selectSingleNode(sourceNode, "@url");
                    if (urlNode != null) {
                	Feed aDocument = new Feed();
                        String encodedUrl = urlNode.getNodeValue();
                        try {
                            String decodedUrl = URLDecoder.decode(encodedUrl, "utf-8");
                            URL aUrl = new URL(decodedUrl);
                            aDocument.setUrl(aUrl);
                            aDocument.setMimeType("application/rss+xml");
                            Node titleNode = XPathAPI.selectSingleNode(sourceNode, "text()");
                            if (titleNode != null) {
                                aDocument.setTitle(titleNode.getNodeValue());
                            } else {
                                String pathname = aUrl.getPath();
                                int slashIndex = pathname.lastIndexOf("/");
                                String filename = pathname.substring(slashIndex+1);
                                aDocument.setTitle(filename);
                            }
                            Link aLink = new Link();
                            aLink.setLinkedDocument(aDocument);
                            aLink.setLinkingDocument(feed);
                            aLink.setRelation("via");
                            aLink.setNumber(linkNumber++);
                            item.addLink(aLink);
                        }
                        catch (MalformedURLException e) {
                            logger.warn("bad source url", e);
                        }
                        catch (UnsupportedEncodingException e) {
                            logger.warn("bad encoding source url", e);
                        }
                    }
                }

                linkNode =  XPathAPI.selectSingleNode(itemNode, "link/text()");
                if (linkNode != null) {
                    try {
                        URL aUrl = new URL(linkNode.getNodeValue());
                        SimpleDocument aMedia = new SimpleDocument();
                        aMedia.setUrl(aUrl);
                        aMedia.setMimeType("text/html");
                        String pathname = aUrl.getPath();
                        int slashIndex = pathname.lastIndexOf("/");
                        String filename = pathname.substring(slashIndex+1);
                        aMedia.setTitle(filename);
                        Link aLink = new Link();
                        aLink.setLinkedDocument(aMedia);
                        aLink.setLinkingDocument(feed);
                        aLink.setRelation("alternate");
                        aLink.setNumber(linkNumber++);
                        item.addLink(aLink);
                    } catch (MalformedURLException e) {
                        logger.warn("bad link url", e);
                    }
                }
                
                feed.addItem(item);
            }
        } catch (TransformerException e) {
            logger.error("XPath error", e);
            throw new ParsingException("Error when reading the RSS document");
        }
        return feed;
    }

    public void unparse(OutputStream outStream, SimpleDocument document, URL url) throws ParsingException {
    }

    public boolean isExtensionSupported(String extension) {
	if (extension.equalsIgnoreCase("rss")) {
	    return true;
	} else if (extension.equalsIgnoreCase("rdf")) {
	    return true;
	}
	return false;
    }

    public boolean isMimeTypeSupported(String mimeType) {
	if (mimeType.equalsIgnoreCase("application/rss+xml")) {
	    return true;
	} else if (mimeType.equalsIgnoreCase("application/rdf+xml")) {
	    return true;
	} else if (mimeType.equalsIgnoreCase("text/xml")) {
	    return true;
	}
	return false;
    }

    public List<String> getSupportedExtensions() {
	List<String> extensions = new ArrayList<String>();
	extensions.add("rss");
	extensions.add("rdf");
	extensions.add("xml");
	return extensions;
    }
    
    /**
     * 
     * @return
     */
    public SimpleDocument getSupportedDocument() {
	return new Feed();
    }
}
