package org.tramper.webPage;

import java.awt.Image;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.tramper.doc.Link;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.WebPage;
import org.tramper.doc.WebPageItem;
import org.tramper.parser.Parser;
import org.tramper.parser.ParsingException;
import org.fingon.synthesizer.VoiceDesc;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Abstract HTML parser.
 * @author Paul-Emile
 */
public abstract class AbstractHtmlParser implements Parser {
    /** logger */
    private Logger logger = Logger.getLogger(AbstractHtmlParser.class);
    /** labels bundle */
    protected ResourceBundle label;
    /** default CSS speech styles */
    protected static Map<String, Map<String, Object>> defaultStyleMap;
    
    //load the default CSS styles
    static {
        URL defaultCSSUrl = AbstractHtmlParser.class.getResource("css/default-speech-3.css");
        SpeechCSSParser cssParser = new SpeechCSSParser();
        defaultStyleMap = cssParser.parse(defaultCSSUrl);
    }
    
    /**
     * 
     */
    public AbstractHtmlParser() {
	super();
    }

    /**
     * make a DOM document from a input stream 
     * @param inStream input stream
     * @return DOM document
     * @throws ParsingException
     */
    protected abstract Document makeDocument(InputStream inStream) throws ParsingException;
    
    /**
     * @see org.tramper.parser.Parser#parse(java.io.InputStream, URL)
     */
    public SimpleDocument parse(InputStream inStream, URL url) throws ParsingException {
	Map<String, Map<String, Object>> allStyleMap = new HashMap<String, Map<String,Object>>(defaultStyleMap);
        Document docRoot = this.makeDocument(inStream);
        
        WebPage doc = new WebPage();
        doc.setMimeType("text/html");
        doc.setCharset("UTF-8");
        
        Element root = (Element)docRoot.getElementsByTagName("html").item(0);
        if (root == null) {
            logger.error("no html root");
            throw new ParsingException("no html root");
        } else {
            String lang = root.getAttribute("lang");
            if (lang != null) {
                doc.parseLanguage(lang);
            } else {
                lang = root.getAttribute("xml:lang");
                if (lang != null) {
                    doc.parseLanguage(lang);
                } else {
                    doc.setLanguage(Locale.getDefault());
                }
            }
        }
        
        String hostUrl = null, baseUrl = null;
        if (url != null) {
            //first we determine the base url and the host part
            hostUrl = url.getProtocol()+"://"+url.getHost();
            int port = url.getPort();
            if (port != -1) {
                hostUrl += ":" + port;
            }
            String path = url.getPath();
            int lastSlashIndex = path.lastIndexOf("/");
            if (lastSlashIndex != -1) {
                path = path.substring(0, lastSlashIndex+1);
            } else {
                path = "/";
            }
            baseUrl = hostUrl + path;
        }

        NodeList titleList = docRoot.getElementsByTagName("title");
        if (titleList.getLength() > 0) {
            Node title = titleList.item(0);
            Node titleText = title.getFirstChild();
            if (titleText != null) {
                doc.setTitle(titleText.getNodeValue());
            }
        }
        
        NodeList metaList = docRoot.getElementsByTagName("meta");
        for (int i=0; i<metaList.getLength(); i++) {
            Element meta = (Element)metaList.item(i);
            String metaName = meta.getAttribute("name");
            String metaHttpEquiv = meta.getAttribute("http-equiv");
            String metaContent = meta.getAttribute("content");
            if (metaName != null && !metaName.equals("")) {
                if (metaName.equalsIgnoreCase("description"))
                    doc.setDescription(metaContent);
                else if (metaName.equalsIgnoreCase("author"))
                    doc.setAuthor(metaContent);
                else if (metaName.equalsIgnoreCase("category"))
                    doc.setCategory(metaContent);
                else if (metaName.equalsIgnoreCase("copyright"))
                    doc.setCopyright(metaContent);
            }
            else if (metaHttpEquiv != null && !metaHttpEquiv.equals("")) {
                if (metaHttpEquiv.equalsIgnoreCase("content-type")) {
                    if (metaContent != null) {
                        //manage the charset if present
                        int semiColumnIndex = metaContent.indexOf(";");
                        if (semiColumnIndex != -1) {
                            int equalIndex = metaContent.indexOf("=");
                            if (equalIndex != -1) {
                                String metaCharset = metaContent.substring(equalIndex+1);
                                doc.setCharset(metaCharset);
                            }
                            metaContent = metaContent.substring(0, semiColumnIndex);
                        }
                        doc.setMimeType(metaContent);
                    }
                }
                else if (metaHttpEquiv.equalsIgnoreCase("content-language")) {
                    if (metaContent != null)
                        doc.parseLanguage(metaContent);
                }
            }
        }

	SpeechCSSParser cssParser = new SpeechCSSParser();
	    
        NodeList metaLink = docRoot.getElementsByTagName("link");
        for (int i=0; i<metaLink.getLength(); i++) {
            Element link = (Element)metaLink.item(i);
            String linkHref = link.getAttribute("href");
            try {
                URL anUrl = completeUrl(linkHref, baseUrl, hostUrl);
                if (anUrl != null) {
                    String linkTitle = link.getAttribute("title");
                    String linkRel = link.getAttribute("rel");
                    String linkType = link.getAttribute("type");
                    if (linkRel.equalsIgnoreCase("icon") || linkRel.equalsIgnoreCase("shortcut icon")) {
                        try {
                            Image img = ImageIO.read(anUrl);
        	            doc.setIcon(img);
        		} catch (Exception e) {
        		    logger.debug("unreadable icon: "+anUrl);
        		}
                    } else if (linkRel.equalsIgnoreCase("stylesheet")) {
                        if ("text/css".equals(linkType)) {
                            String media = link.getAttribute("media").toLowerCase();
                            if (media.equals("") || media.equals("speech") || media.equals("aural")) {
                                //load a CSS style sheet
                                Map<String, Map<String, Object>> styleMap = cssParser.parse(anUrl);
                                //don't overwrite a default style if not overloaded for an existing element
                                Iterator<Entry<String, Map<String, Object>>> entryIt = styleMap.entrySet().iterator();
                                while (entryIt.hasNext()) {
                                    Map.Entry<String, Map<String, Object>> entry = entryIt.next();
                                    String key = entry.getKey();
                                    Map<String, Object> value = entry.getValue();
                                    if (allStyleMap.containsKey(key)) {
                                	Map<String, Object> elementStyles = allStyleMap.get(key);
                                	elementStyles.putAll(value);
                                    } else {
                                	allStyleMap.put(key, value);
                                    }
                                }
                            }
                        }
                    } else {
                        SimpleDocument aDocument = new SimpleDocument();
                        aDocument.setUrl(anUrl);
                        aDocument.setTitle(linkTitle);
                        aDocument.setMimeType(linkType);
                        
                        Link aLink = new Link();
                        aLink.setLinkedDocument(aDocument);
                        aLink.setLinkingDocument(doc);
                        if (linkRel.equalsIgnoreCase("prev")) {
                            aLink.setRelation("previous");
                        } else {
                            aLink.setRelation(linkRel);
                        }
                        doc.addLink(aLink);
                    }
                }
            } catch (MalformedURLException e) {
                logger.debug(e.getMessage());
            }
        }

        NodeList styles = docRoot.getElementsByTagName("style");
        for (int i=0; i<styles.getLength(); i++) {
            Element style = (Element)styles.item(i);
            String text = style.getTextContent();
            Map<String, Map<String, Object>> styleMap = cssParser.parse(text, url.toString());
            //don't overwrite a default style if not overloaded for an existing element
            Iterator<Entry<String, Map<String, Object>>> entryIt = styleMap.entrySet().iterator();
            while (entryIt.hasNext()) {
        	Map.Entry<String, Map<String, Object>> entry = entryIt.next();
        	String key = entry.getKey();
        	Map<String, Object> value = entry.getValue();
        	if (allStyleMap.containsKey(key)) {
        	    Map<String, Object> elementStyles = allStyleMap.get(key);
        	    elementStyles.putAll(value);
        	} else {
        	    allStyleMap.put(key, value);
        	}
            }
        }
        
        
        Node body = docRoot.getElementsByTagName("body").item(0);
        if (body == null) {
            throw new ParsingException("no body element in HTML document");
        }
        
        int linkNumber = 1;
        try {
            label = ResourceBundle.getBundle("label", doc.getLanguage());
        } catch (Exception e) {
            //either the document language is null, or there is no properties file for it
            label = ResourceBundle.getBundle("label");
        }
        int idIndex = 1;
        this.depthFirstSearch(doc, null, null, body, url, baseUrl, hostUrl, allStyleMap, idIndex, linkNumber);
        return doc;
    }

    /**
     * Depth first search of a tree node for collecting web page item
     * @param doc HTML document
     * @param item current web page item
     * @param node current HTML document node
     * @param url URL of the document to parse
     */
    @SuppressWarnings("unchecked")
    protected void depthFirstSearch(WebPage doc, WebPageItem item, Link aLink, Node node, URL url, String baseUrl, String hostUrl, Map<String, Map<String, Object>> allStyleMap, int idIndex, int linkNumber) {
        boolean searchDepth = true;
        
        short nodeType = node.getNodeType();
        
        if (nodeType == Node.ELEMENT_NODE) {
            String nodeName = node.getNodeName().toLowerCase();
            //elements defining a new item
            if (nodeName.equals("p") || 
                nodeName.equals("div") || 
                nodeName.equals("td") || 
                nodeName.equals("th") || 
                nodeName.equals("li") || 
                nodeName.equals("body") || 
                nodeName.equals("form")) {
                item = new WebPageItem();
                Map<String, Object> itemStyles = new HashMap<String, Object>();
                Map<String, Object> propElemMap = allStyleMap.get(nodeName);
                if (propElemMap != null) {
                    itemStyles.putAll(propElemMap);
                }
                
                String itemClass = ((Element)node).getAttribute("class");
                if (!itemClass.equals("")) {
                    Map<String, Object> propClassMap = allStyleMap.get("*."+itemClass);
                    if (propClassMap != null) {
                	itemStyles.putAll(propClassMap);
                    }
                    propClassMap = allStyleMap.get(nodeName+"."+itemClass);
                    if (propClassMap != null) {
                	itemStyles.putAll(propClassMap);
                    }
                }

                String itemId = ((Element)node).getAttribute("id");
                if (itemId.equals("")) {
                    itemId = "tramper"+(idIndex++);
                } else {
                    Map<String, Object> propIdMap = allStyleMap.get("*#"+itemId);
                    if (propIdMap != null) {
                	itemStyles.putAll(propIdMap);
                    }
                    propIdMap = allStyleMap.get(nodeName+"#"+itemId);
                    if (propIdMap != null) {
                	itemStyles.putAll(propIdMap);
                    }
                }
                item.setId(itemId);

                String itemStyle = ((Element)node).getAttribute("style");
                if (!itemStyle.equals("")) {
                    SpeechCSSParser cssParser = new SpeechCSSParser();
                    // "{" are necessary for CSS parser to work properly:
                    Map<String, Object> propStyleMap = cssParser.parseStyleDeclaration("{"+itemStyle+"}", url.toString());
                    if (propStyleMap != null) {
                	itemStyles.putAll(propStyleMap);
                    }
                }
                
                Float volume = (Float)itemStyles.get(SpeechCSSParser.VOICE_VOLUME);
                item.setVolume(volume);
                Float balance = (Float)itemStyles.get(SpeechCSSParser.VOICE_BALANCE);
                item.setBalance(balance);
                String speak = (String)itemStyles.get(SpeechCSSParser.SPEAK);
                item.setSpeak(speak);
                Float pauseBefore = (Float)itemStyles.get(SpeechCSSParser.PAUSE_BEFORE);
                item.setPauseBefore(pauseBefore);
                Float pauseAfter = (Float)itemStyles.get(SpeechCSSParser.PAUSE_AFTER);
                item.setPauseAfter(pauseAfter);
                Float restBefore = (Float)itemStyles.get(SpeechCSSParser.REST_BEFORE);
                item.setRestBefore(restBefore);
                Float restAfter = (Float)itemStyles.get(SpeechCSSParser.REST_AFTER);
                item.setRestAfter(restAfter);
                URL cueBefore = (URL)itemStyles.get(SpeechCSSParser.CUE_BEFORE);
                item.setCueBefore(cueBefore);
                URL cueAfter = (URL)itemStyles.get(SpeechCSSParser.CUE_AFTER);
                item.setCueAfter(cueAfter);
                Float rate = (Float)itemStyles.get(SpeechCSSParser.VOICE_RATE);
                item.setRate(rate);
                Float pitch = (Float)itemStyles.get(SpeechCSSParser.VOICE_PITCH);
                item.setPitch(pitch);
                Float pitchRange = (Float)itemStyles.get(SpeechCSSParser.VOICE_PITCH_RANGE);
                item.setPitchRange(pitchRange);
                List<VoiceDesc> voiceFamily = (List<VoiceDesc>)itemStyles.get(SpeechCSSParser.VOICE_FAMILY);
                item.setVoiceFamily(voiceFamily);
                
                doc.addItem(item);
            }
            //elements to be replaced by an alternative text 
            else if (nodeName.equals("input")) {
                String inputType = ((Element)node).getAttribute("type");
                if (inputType != null && !inputType.equals("") && !inputType.equalsIgnoreCase("hidden")) {
                    inputType = inputType.toLowerCase();
                    try {
                        String inputText = label.getString("javaspeaker."+inputType);
                        //read the input value if neither an image nor a checkbox, radio and file input
                        if (!inputType.equals("image") && 
                            !inputType.equals("checkbox") && 
                            !inputType.equals("radio") && 
                            !inputType.equals("file")) {
                            String inputValue = ((Element)node).getAttribute("value");
                            if (inputValue != null && !inputValue.equals("")) {
                                inputText = inputText.concat(" " + inputValue);
                            }
                        }
                        item.appendContent(inputText);
                    } catch (MissingResourceException e) {
                        //bad type attribute on input
                    }
                }
            } else if (nodeName.equals("select")) {
                String selectString = label.getString("javaspeaker.select");
                item.appendContent(selectString);
            } else if (nodeName.equals("textarea")) {
                String textareaString = label.getString("javaspeaker.textarea");
                item.appendContent(textareaString);
            } else if (nodeName.equals("img")) {
                String imgAlt = ((Element)node).getAttribute("alt");
                if (imgAlt != null && !"".equals(imgAlt)) {
                    String imgString = label.getString("javaspeaker.image");
                    imgAlt = imgString.concat(" ").concat(imgAlt);
                    item.appendContent(imgAlt);
                }
            } else if (nodeName.equals("a")) {
                String linkString = label.getString("javaspeaker.link");
                String link = linkString.concat(" ");
                String linkTitle = ((Element)node).getAttribute("title");
                if (linkTitle != null && !linkTitle.equals("")) {
                    link = link.concat(linkTitle);
                }
                item.appendContent(link);
                
                String linkId = ((Element)node).getAttribute("id");
                if (linkId.equals("")) {
                    linkId = "tramper"+(idIndex++);
                }
                
                String linkHref = ((Element)node).getAttribute("href");
                SimpleDocument aDocument = new SimpleDocument();
                URL linkUrl = null;
                try {
                    linkUrl = completeUrl(linkHref, baseUrl, hostUrl);
                    aDocument.setUrl(linkUrl);
                } catch (MalformedURLException e) {
                    logger.debug("Bad A href: "+linkUrl);
                }

                aLink = new Link();
                String linkRelation = ((Element)node).getAttribute("rel");
                if (linkRelation != null && !linkRelation.equals("")) {
                    if (linkRelation.equalsIgnoreCase("prev")) {
                        aLink.setRelation("previous");
                    } else {
                        aLink.setRelation(linkRelation);
                    }
                }
                
                aDocument.setTitle(linkTitle);
                aLink.setId(linkId);
                aLink.setLinkedDocument(aDocument);
                aLink.setLinkingDocument(doc);
                aLink.setNumber(linkNumber++);
                item.addLink(aLink);
            } else if (nodeName.equals("area")) {
                String areaAlt = ((Element)node).getAttribute("alt");
                if (areaAlt != null && !areaAlt.equals("")) {
                    String areaString = label.getString("javaspeaker.imagearea");
                    areaAlt = areaString.concat(" ").concat(areaAlt);
                    item.appendContent(areaAlt);
                }
            } else if (nodeName.equals("frame")) {
                String frameTitle = ((Element)node).getAttribute("title");
                if (frameTitle != null && !frameTitle.equals("")) {
                    String frameString = label.getString("javaspeaker.frame");
                    frameTitle = frameString.concat(" ").concat(frameTitle);
                    item.appendContent(frameTitle);
                }
            }
            //don't want to parse content of script and style elements
            else if (nodeName.equals("script") || nodeName.equals("style")) {
                searchDepth = false;
            }
            //embedded media to play
            else if (nodeName.equals("embed")) {
                String mediaSrc = ((Element)node).getAttribute("src");
                try {
                    URL anUrl = completeUrl(mediaSrc, baseUrl, hostUrl);
                    SimpleDocument aMedia = new SimpleDocument();
                    aMedia.setUrl(anUrl);
                    String mediaTitle = ((Element)node).getAttribute("title");
                    if (mediaTitle != null) {
                        aMedia.setTitle(mediaTitle);
                    }
                    Link mediaLink = new Link();
                    mediaLink.setLinkedDocument(aMedia);
                    mediaLink.setLinkingDocument(doc);
                    mediaLink.setRelation("enclosure");
                    mediaLink.setNumber(linkNumber++);
                    item.addLink(mediaLink);
                } catch (MalformedURLException e) {
                    //skip the media creation if bad url
                    logger.debug(e.getMessage());
                }
            } else if (nodeName.equals("bgsound")) {
                String mediaSrc = ((Element)node).getAttribute("src");
                try {
                    URL anUrl = completeUrl(mediaSrc, baseUrl, hostUrl);
                    SimpleDocument aMedia = new SimpleDocument();
                    aMedia.setUrl(anUrl);
                    String mediaTitle = ((Element)node).getAttribute("title");
                    if (mediaTitle != null) {
                        aMedia.setTitle(mediaTitle);
                    }
                    Link mediaLink = new Link();
                    mediaLink.setLinkedDocument(aMedia);
                    mediaLink.setLinkingDocument(doc);
                    mediaLink.setRelation("enclosure");
                    mediaLink.setNumber(linkNumber++);
                    item.addLink(mediaLink);
                }
                catch (MalformedURLException e) {
                    //skip the media creation if bad url
                    logger.debug(e.getMessage());
                }
            } else if (nodeName.equals("object")) {
                String mediaSrc = ((Element)node).getAttribute("data");
                try {
                    URL anUrl = completeUrl(mediaSrc, baseUrl, hostUrl);
                    SimpleDocument aMedia = new SimpleDocument();
                    aMedia.setUrl(anUrl);
                    String mediaType = ((Element)node).getAttribute("type");
                    if (mediaType != null) {
                        aMedia.setMimeType(mediaType);
                    }
                    String mediaTitle = ((Element)node).getAttribute("title");
                    if (mediaTitle != null) {
                        aMedia.setTitle(mediaTitle);
                    }
                    Link mediaLink = new Link();
                    mediaLink.setLinkedDocument(aMedia);
                    mediaLink.setLinkingDocument(doc);
                    mediaLink.setRelation("enclosure");
                    mediaLink.setNumber(linkNumber++);
                    item.addLink(mediaLink);
                } catch (MalformedURLException e) {
                    //skip the media creation if bad url
                    logger.debug(e.getMessage());
                }
            }
            //parameters for objects elements
            else if (nodeName.equals("param")) {
                String paramName = ((Element)node).getAttribute("name");
                String paramValue = ((Element)node).getAttribute("value");
                List<Link> links = item.getLinks();
                if (links.size() > 0) {
                    Link lastLink = links.get(links.size()-1);
                    SimpleDocument lastMedia = lastLink.getLinkedDocument();
                    if (paramName.equalsIgnoreCase("src")) {
                        URL mediaUrl = lastMedia.getUrl();
                        if (mediaUrl == null) {
                            try {
                        	mediaUrl = completeUrl(paramValue, baseUrl, hostUrl);
                                lastMedia.setUrl(mediaUrl);
                            } catch (MalformedURLException e) {
                                logger.debug("Bad media url: "+mediaUrl);
                            }
                        }
                    } // do something for "autoplay" and "autoStart"?
                }
            }
            //others elements are ignored
        } else if (nodeType == Node.TEXT_NODE) {
            String nodeValue = node.getNodeValue();
            if (nodeValue != null && !nodeValue.equals("")) {
                item.appendContent(nodeValue);
            }
            if (aLink != null) {
                SimpleDocument aDocument = aLink.getLinkedDocument();
                String title = aDocument.getTitle();
                aDocument.setTitle(title+nodeValue);
            }
        }
        
        if (searchDepth) {
            NodeList childNodes = node.getChildNodes();
            for (int i=0; i<childNodes.getLength(); i++) {
                Node aChild = childNodes.item(i);
                depthFirstSearch(doc, item, aLink, aChild, url, baseUrl, hostUrl, allStyleMap, idIndex, linkNumber);
            }
        }
    }
    
    /**
     * Complete the relative and absolute urls before instantiate them.
     * @param uncompleteUrl
     * @return an URL
     * @throws MalformedURLException if unable to instantiate the url
     */
    public URL completeUrl(String uncompleteUrl, String baseUrl, String hostUrl) throws MalformedURLException {
	if (uncompleteUrl == null || uncompleteUrl.equals("")) {
	    throw new MalformedURLException();
	}
	
	// either this is an absolute path, concatenate the host part of the url
        if (uncompleteUrl.startsWith("/")) {
            uncompleteUrl = hostUrl.concat(uncompleteUrl);
        // or this is a relative path, concatenate the base part of the url
        } else if (uncompleteUrl.indexOf(":") == -1) {
            uncompleteUrl = baseUrl.concat(uncompleteUrl);
        }
        // otherwise this is a full url, nothing to concatenate
        
        URL anUrl = new URL(uncompleteUrl);
	return anUrl;
    }
    
    /**
     * 
     * @see org.tramper.parser.Parser#unparse(java.io.OutputStream, SimpleDocument, URL)
     */
    public void unparse(OutputStream outStream, SimpleDocument document, URL url) throws ParsingException {
        
    }
    
    /**
     * 
     * @return
     */
    public SimpleDocument getSupportedDocument() {
	return new WebPage();
    }
}
