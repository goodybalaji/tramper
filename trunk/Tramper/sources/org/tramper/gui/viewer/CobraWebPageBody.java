package org.tramper.gui.viewer;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.SystemColor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import org.apache.log4j.Logger;
import org.lobobrowser.html.HtmlRendererContext;
import org.lobobrowser.html.UserAgentContext;
import org.lobobrowser.html.domimpl.HTMLDocumentImpl;
import org.lobobrowser.html.domimpl.NodeImpl;
import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.parser.DocumentBuilderImpl;
import org.lobobrowser.html.parser.InputSourceImpl;
import org.tramper.doc.DocumentItem;
import org.tramper.doc.Library;
import org.tramper.doc.Link;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.doc.WebPage;
import org.tramper.doc.WebPageItem;
import org.tramper.gui.MagnifyingGlass;
import org.tramper.gui.SpeakableHtmlRendererContext;
import org.tramper.ui.Renderer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * A Java web page body based on the Cobra project.
 * @author Paul-Emile
 */
public class CobraWebPageBody extends HtmlPanel implements Body, MouseListener {
    /** CobraWebPageBody.java long */
    private static final long serialVersionUID = -6151339938542198426L;
    /** logger */
    private static Logger logger = Logger.getLogger(CobraWebPageBody.class);
    /** speakable document */
    private WebPage document;
    /** selected item index */
    private int selectedIndex;
    /** List of HTML elements viewed */
    private List<String> elementId;
    /** HTML document builder */
    private DocumentBuilderImpl docBuilder;
    /** customized HTML renderer context */
    private HtmlRendererContext rendererContext;
    /** user agent */
    private UserAgentContext userAgentcontext;
    /** last element style */
    private String style;
    /** page section selected highlight color */
    private String highLightColor;
    /** javascript initialization */
    //private String initJavascript;
    /** target */
    private Target target;
    
    /**
     * 
     */
    public CobraWebPageBody() {
        elementId = new ArrayList<String>();
        this.addMouseListener(this);
        
        rendererContext = new SpeakableHtmlRendererContext(this);
        userAgentcontext = rendererContext.getUserAgentContext();
        docBuilder = new DocumentBuilderImpl(userAgentcontext, rendererContext);

        //load the javascript to modify the web page
        /*InputStream in = this.getClass().getResourceAsStream("webPageMagnifier.js");
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(in, "ISO-8859-1");
        } catch (UnsupportedEncodingException e1) {
            reader = new InputStreamReader(in);
        }
        StringWriter out = new StringWriter();
        char[] charBuffer = new char[1000];
        try {
            int read = 0;
            do {
                read = reader.read(charBuffer);
                if (read == -1) {
                    break;
                } else {
                    out.write(charBuffer, 0, read);
                }
            } while (read >= 0);
            initJavascript = out.toString();
        } catch (IOException e) {
            logger.debug(e.getMessage(), e);
        } finally {
            try {
                out.close();
                reader.close();
            } catch (IOException e) {}
        }*/
    }
    
    /**
     * Get the background and text colors for the selected item in the page.
     * Called each time a look and feel is selected.
     * @see javax.swing.JPanel#updateUI()
     */
    public void updateUI() {
        super.updateUI();
        UIDefaults defaults = UIManager.getDefaults();
        Object uiDefault = defaults.get("textHighlight");
        logger.debug("uiDefault="+uiDefault);
        if (uiDefault instanceof ColorUIResource) {
            ColorUIResource bgSystemColor = (ColorUIResource)uiDefault;
            highLightColor = "rgb("+bgSystemColor.getRed()+","+bgSystemColor.getGreen()+","+bgSystemColor.getBlue()+")";
        }
        else {
            SystemColor bgSystemColor = SystemColor.textHighlight;
            highLightColor = "rgb("+bgSystemColor.getRed()+","+bgSystemColor.getGreen()+","+bgSystemColor.getBlue()+")";
        }
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#getDocument()
     */
    public SimpleDocument getDocument() {
        return document;
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#render(org.tramper.doc.SimpleDocument)
     */
    public void displayDocument(SimpleDocument doc, Target target, int documentPart) {
	if (!(doc instanceof WebPage)) {
	    throw new RuntimeException(doc.getTitle()+" is not a WebPage");
	}
	this.target = target;
	
        document = (WebPage)doc;
        
        URL url = doc.getUrl();
        String uri = url.toString();
        
        //incremental loading
        try {
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", userAgentcontext.getUserAgent());
            InputStream in = connection.getInputStream();
            InputStream bin = new BufferedInputStream(in);
            InputSource inputSrc = new InputSourceImpl(bin, uri, document.getCharset());
            HTMLDocumentImpl htmlDoc = (HTMLDocumentImpl)docBuilder.createDocument(inputSrc);
            this.setDocument(htmlDoc, rendererContext);
            htmlDoc.load();
            //execute initialization javascript to generate the ids
            //AbstractView view = htmlDoc.getDefaultView();
            //Window window = (Window)view;
            //window.eval(initJavascript);
            //window.eval("initPage()");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        
        elementId.clear();
        List<DocumentItem> speakables = document.getItems();
        for (int i=0; i<speakables.size(); i++) {
            DocumentItem item = speakables.get(i);
            elementId.add(((WebPageItem)item).getId());
        }
        
        first();
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#first()
     */
    public void first() {
        NodeImpl root = this.getRootNode();
        Document doc = root.getOwnerDocument();
        
        //deselect the current item
        String id = elementId.get(selectedIndex);
        Element elem = doc.getElementById(id);
        if (elem != null) {
            elem.setAttribute("style", style);
        }
        
        selectedIndex = 0;
        
        //select the first item
        id = elementId.get(selectedIndex);
        elem = doc.getElementById(id);
        if (elem != null) {
            style = elem.getAttribute("style");
            elem.setAttribute("style", style+"; border: 1px dotted "+highLightColor);
        }
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#last()
     */
    public void last() {
        NodeImpl root = this.getRootNode();
        Document doc = root.getOwnerDocument();
        
        //deselect the current item
        String id = elementId.get(selectedIndex);
        Element elem = doc.getElementById(id);
        if (elem != null) {
            elem.setAttribute("style", style);
        }
        
        selectedIndex = elementId.size()-1;
        
        //select the first item
        id = elementId.get(selectedIndex);
        elem = doc.getElementById(id);
        if (elem != null) {
            style = elem.getAttribute("style");
            elem.setAttribute("style", style+"; border: 1px dotted "+highLightColor);
        }
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#next()
     */
    public void next() {
        NodeImpl root = this.getRootNode();
        Document doc = root.getOwnerDocument();
        
        //deselect the current item
        String id = elementId.get(selectedIndex);
        Element elem = doc.getElementById(id);
        if (elem != null) {
            elem.setAttribute("style", style);
        }
        
        selectedIndex++;
        
        //select the next item
        id = elementId.get(selectedIndex);
        elem = doc.getElementById(id);
        if (elem != null) {
            style = elem.getAttribute("style");
            elem.setAttribute("style", style+"; border: 1px dotted "+highLightColor);
        }
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#previous()
     */
    public void previous() {
        NodeImpl root = this.getRootNode();
        Document doc = root.getOwnerDocument();
        
        //deselect the current item
        String id = elementId.get(selectedIndex);
        Element elem = doc.getElementById(id);
        if (elem != null) {
            elem.setAttribute("style", style);
        }
        
        selectedIndex--;
        
        //select the next item
        id = elementId.get(selectedIndex);
        elem = doc.getElementById(id);
        if (elem != null) {
            style = elem.getAttribute("style");
            elem.setAttribute("style", style+"; border: 1px dotted "+highLightColor);
        }
    }

    /**
     * add recursively the magnifying glass as mouse listener on each sub component 
     * @param aComponent
     * @param glass
     */
    protected void addMouseListenerRecursively(Container aComponent, MagnifyingGlass glass) {
        Component[] subComponent = aComponent.getComponents();
        for (int i=0; i<subComponent.length; i++) {
            subComponent[i].addMouseListener(glass);
            subComponent[i].addMouseMotionListener(glass);
            if (subComponent[i] instanceof Container) {
                addMouseListenerRecursively((Container)subComponent[i], glass);
            }
        }
    }

    /**
     * remove recursively the magnifying glass as mouse listener from each sub component 
     * @param aComponent
     * @param glass
     */
    protected void removeMouseListenerRecursively(Container aComponent, MagnifyingGlass glass) {
        Component[] subComponent = aComponent.getComponents();
        for (int i=0; i<subComponent.length; i++) {
            subComponent[i].removeMouseListener(glass);
            subComponent[i].removeMouseMotionListener(glass);
            if (subComponent[i] instanceof Container) {
                removeMouseListenerRecursively((Container)subComponent[i], glass);
            }
        }
    }
    
    /**
     * @see org.tramper.gui.viewer.Viewer#render()
     */
    public void render(int documentPart) {
        NodeImpl root = this.getRootNode();
        Document doc = root.getOwnerDocument();
        if (selectedIndex >= 0 && elementId.size() > 0) {
            String id = elementId.get(selectedIndex);
            Element elem = doc.getElementById(id);
            elem.setAttribute("style", style);
        }
        
        elementId.clear();
        
        if (documentPart == Renderer.ALL_PART) {
            List<DocumentItem> speakables = document.getItems();
            for (int i=0; i<speakables.size(); i++) {
                Object item = speakables.get(i);
                elementId.add(((WebPageItem)item).getId());
            }
        } else if (documentPart == Renderer.LINK_PART) {
            List<Link> links = new ArrayList<Link>();
            List<DocumentItem> items = document.getItems();
            for (int i=0; i<items.size(); i++) {
                Object item = items.get(i);
                List<Link> itemLinks = ((WebPageItem)item).getLinks();
                links.addAll(itemLinks);
            }
            for (int i=0; i<links.size(); i++) {
                Link item = (Link)links.get(i);
                elementId.add(item.getId());
            }
        } else {
            return;
        }

        selectedIndex = 0;
        first();
    }

    public void mouseClicked(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent event) {
	int clickedButton = event.getButton();
	if (clickedButton == MouseEvent.BUTTON1) {
	    Library.getInstance().setActiveDocument(target);
	}
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void paintMiniature(Graphics2D g2d, Dimension miniatureSize, boolean mouseOver) {
	double scale = 0.4;
	g2d.scale(scale, scale);
	
	this.paint(g2d);

	// reset scale
	g2d.scale(1/scale, 1/scale);
    }
}
