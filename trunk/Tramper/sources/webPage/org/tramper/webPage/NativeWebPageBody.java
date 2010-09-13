package org.tramper.webPage;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.IllegalComponentStateException;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.jdesktop.jdic.browser.BrowserEngineManager;
import org.jdesktop.jdic.browser.IBrowserEngine;
import org.jdesktop.jdic.browser.IWebBrowser;
import org.jdesktop.jdic.browser.WebBrowserEvent;
import org.jdesktop.jdic.browser.WebBrowserListener;
import org.tramper.JavaSystem;
import org.tramper.action.QuitAction;
import org.tramper.doc.DocumentItem;
import org.tramper.doc.Link;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.doc.WebPage;
import org.tramper.doc.WebPageItem;
import org.tramper.gui.GraphicalUserInterface;
import org.tramper.gui.viewer.Body;
import org.tramper.loader.Loader;
import org.tramper.loader.LoaderFactory;
import org.tramper.ui.Renderer;
import org.tramper.ui.UserInterfaceFactory;

/**
 * Native web page body.
 * Embed the native default web browser in a panel.
 * @author Paul-Emile
 */
public class NativeWebPageBody extends JPanel implements Body, WebBrowserListener {
    /** NativeWebPageBody.java long */
    private static final long serialVersionUID = 1014294360172106907L;
    /** logger */
    private Logger logger = Logger.getLogger(NativeWebPageBody.class);
    /** speakable document */
    private WebPage document;
    /** selected item index */
    private int selectedIndex;
    /** List of HTML elements viewed */
    private List<String> elementId;
    /** start reading document after download start in browser */
    private boolean setUrlNotUsed = true;
    /** native browser component (AWT canvas) */
    private IWebBrowser webBrowser;
    /** Javascript initialization */
    //private String initJavascript;
    /** target */
    private Target target;
    /** last screen capture */
    private BufferedImage screenCapture;
    
    /**
     * @throws Exception if no suitable native browser
     */
    @SuppressWarnings("unchecked")
    public NativeWebPageBody() throws Exception {
        super();
        elementId = new ArrayList<String>();
        
        BrowserEngineManager browserEngineMgr = BrowserEngineManager.instance();
        
        //load Internet Explorer if under Windows (Firefox doesn't work)
        if (JavaSystem.isWindows()) {
            Map<String, IBrowserEngine> engines = browserEngineMgr.getEngines();
            Iterator<String> entries = engines.keySet().iterator();
            while (entries.hasNext()) {
                String engineName = entries.next();
                if (engineName.equals(BrowserEngineManager.IE)) {
                    browserEngineMgr.setActiveEngine(engineName);
                    IBrowserEngine browserEngine = browserEngineMgr.getActiveEngine();
                    webBrowser = browserEngine.getWebBrowser();
                }
            }
        } else {
            IBrowserEngine browserEngine = browserEngineMgr.getActiveEngine();
            //if there is no default known native browser, the browser engine is null
            if (browserEngine == null || !browserEngine.isEngineAvailable()) {
                //then try to load another one
                Map<String, IBrowserEngine> engines = browserEngineMgr.getEngines();
                Iterator<String> entries = engines.keySet().iterator();
                boolean engineLoaded = false;
                while (entries.hasNext()) {
                    String engineName = entries.next();
                    browserEngineMgr.setActiveEngine(engineName);
                    browserEngine = browserEngineMgr.getActiveEngine();
                    if (browserEngine.isEngineAvailable()) {
                        logger.debug(engineName+" available");
                        //too dirty to be kept
                        /*if (engineName.equals("Mozilla")) {
                            browserEngine.setEnginePath("C:\\Program Files\\Mozilla Firefox");
                        }*/
                        //try that here because the isEngineAvailable method doesn't seem to work
                        webBrowser = browserEngine.getWebBrowser();
                        //webBrowser can be null (ex: WebKit on Windows)
                        if (webBrowser != null) {
                            logger.debug(engineName+" loaded");
                            engineLoaded = true;
                            break;
                        }
                    }
                    else {
                        logger.debug(engineName+" unavailable");
                    }
                }
                if (engineLoaded == false) {
                    logger.warn("no available native browser on this system");
                    throw new Exception();
                }
            }
            else {
                webBrowser = browserEngine.getWebBrowser();
            }
        }
        
        //use a wrapping Jpanel in order not to break the layout mixing AWT and Swing components
        this.setLayout(new BorderLayout());
        this.add(webBrowser.asComponent(), BorderLayout.CENTER);
        webBrowser.addWebBrowserListener(this);
        //WebBrowser.setDebug(true);

        //load the javascript to modify the webpage
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
     * Returns the loaded native browser or null if none.
     * @return display name of the native browser
     */
    public static String getCurrentEngine() {
        if (JavaSystem.isWindows()) {
            return BrowserEngineManager.IE;
        }
        BrowserEngineManager browserEngineMgr = BrowserEngineManager.instance();
        IBrowserEngine browserEngine = browserEngineMgr.getActiveEngine();
        if (browserEngine != null) {
            return browserEngine.getBrowserName();
        } else {
            return null;
        }
    }
    
    /**
     * 
     * @see org.tramper.gui.viewer.Body#displayDocument(org.tramper.doc.SimpleDocument, org.tramper.doc.Target, int)
     */
    public void displayDocument(SimpleDocument doc, Target target, int documentPart) {
	if (!(doc instanceof WebPage)) {
	    throw new RuntimeException(doc.getTitle() + " is not a WebPage");
	}
        URL newUrl = doc.getUrl();
        URL currentUrl = webBrowser.getURL();
        boolean uriEqual = false;
	try {
	    URI newUri = newUrl.toURI();
	    URI currentUri = null;
	    if (currentUrl != null) {
		currentUri = currentUrl.toURI();
	    }
	    uriEqual = newUri.equals(currentUri);
	} catch (URISyntaxException e) {
	    uriEqual = false;
	}
        //do not reload the webpage if this is the same url (happens when loading document after a click on a link)
        if (!uriEqual) {
            this.target = target;
            document = (WebPage)doc;
            
            webBrowser.setURL(newUrl);
            setUrlNotUsed = false;
            elementId.clear();
            
            if (documentPart == Renderer.ALL_PART) {
                List<DocumentItem> speakables = document.getItems();
                for (int i=0; i<speakables.size(); i++) {
                    DocumentItem item = speakables.get(i);
                    elementId.add(((WebPageItem)item).getId());
                }
            } else if (documentPart == Renderer.LINK_PART) {
                List<Link> links = new ArrayList<Link>();
                List<DocumentItem> items = document.getItems();
                for (int i=0; i<items.size(); i++) {
                    DocumentItem item = items.get(i);
                    List<Link> itemLinks = item.getLinks();
                    links.addAll(itemLinks);
                }
                for (int i=0; i<links.size(); i++) {
                    Link item = links.get(i);
                    elementId.add(item.getId());
                }
            } else {
                return;
            }

            selectedIndex = 0;
            first();
        }
    }

    /**
     * 
     * @see org.tramper.gui.viewer.Viewer#first()
     */
    public void first() {
        selectedIndex = 0;
        
        //select the first item
        String id = elementId.get(selectedIndex);
        selectElement(id);
    }
    
    /**
     * @see org.tramper.gui.viewer.Viewer#next()
     */
    public void next() {
        if (selectedIndex < elementId.size()-1) {
            selectedIndex++;
        }
        
        //select the next item
        String id = elementId.get(selectedIndex);
        selectElement(id);
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#previous()
     */
    public void previous() {
        selectedIndex--;
        
        //select the previous item
        String id = elementId.get(selectedIndex);
        selectElement(id);
    }
    
    /**
     * 
     * @see org.tramper.gui.viewer.Viewer#last()
     */
    public void last() {
        selectedIndex = elementId.size()-1;
        
        //select the last item
        String id = elementId.get(selectedIndex);
        selectElement(id);
    }
    
    /**
     * Select an HTML element in the page
     * @param id id of the element
     */
    protected void selectElement(String id) {
        //webBrowser.executeScript("selectItem('"+id+"')");
    }
    
    /**
     * Frame an HTML element in the page
     * @param id id of the element
     */
    protected void frameElement(String id) {
        //webBrowser.executeScript("frameItem('"+id+"')");
    }

    public void initializationCompleted(WebBrowserEvent arg0) {
    }

    public void downloadStarted(WebBrowserEvent arg0) {
        URL url = webBrowser.getURL();
        if (url != null) {
            String urlString = url.toString();
            logger.debug("download started: url="+urlString);
        }
    }

    public void downloadProgress(WebBrowserEvent arg0) {
    }

    public void downloadCompleted(WebBrowserEvent arg0) {
    }

    public void documentCompleted(WebBrowserEvent arg0) {
        URL url = webBrowser.getURL();
        if (url != null) {
            String urlString = url.toString();
            logger.debug("document completed: url="+urlString);
            if (setUrlNotUsed) { // a click on a link, a javascript reload...
        	// we have to load the document because we don't have a player for it
                logger.debug("reload document after completed in browser");
                Loader loader = LoaderFactory.getInstance().newLoader();
                loader.download(urlString, target);
            }
        }
        
        // since setUrl() is called only in displayDocument, 
        // we can consider this flag is true the rest of the time 
        setUrlNotUsed = true;

        //webBrowser.executeScript(initJavascript);
        //webBrowser.executeScript("initPage()");
        first();
    }

    public void downloadError(WebBrowserEvent arg0) {
        logger.debug("download error : "+arg0.getID()+" "+arg0.getData());
    }

    /**
     * Processes the commands coming from the web page, set by javascript.
     * @see org.jdesktop.jdic.browser.WebBrowserListener#statusTextChange(org.jdesktop.jdic.browser.WebBrowserEvent)
     */
    public void statusTextChange(WebBrowserEvent browserEvent) {
	String statusText = browserEvent.getData();
	if (statusText != null) {
            if (statusText.startsWith("command:")) {
                //we have got a command, let us process it
        	String htmlId = statusText.substring(8);
        	selectedIndex = elementId.indexOf(htmlId);
            }
            //else ignore the status text
	}
    }

    public void titleChange(WebBrowserEvent arg0) {
    }

    public void windowClose(WebBrowserEvent arg0) {
	GraphicalUserInterface gui = UserInterfaceFactory.getGraphicalUserInterface();
	boolean confirmed = false;
	confirmed = gui.confirmMessage("confirmQuit");
        if (confirmed) {
            ActionEvent actionEvent = new ActionEvent(this, 0, "quit");
            QuitAction.getInstance().actionPerformed(actionEvent);
        }
    }

    /**
     * @see javax.swing.JPanel#updateUI()
     */
    /*@Override
    public void updateUI() {
	super.updateUI();
	
	if (webBrowser != null) {
	    int scale = UIManager.getInt("Icon.scale");
	    webBrowser.executeScript("zoomPage("+scale+")");
	}
    }*/
    
    /**
     * Returns the native web browser component, used by the miniature
     * @return
     */
    public Component getNativeWebBrowser() {
	return webBrowser.asComponent();
    }

    /**
     * Specify a minimum size to allow the splitpane to reduce 
     * the native browser.
     * @see javax.swing.JComponent#getMinimumSize()
     */
    @Override
    public Dimension getMinimumSize() {
	return new Dimension(50, 50);
    }

    public void paintMiniature(Graphics2D g2d, Dimension miniatureSize, boolean mouseOver) {
	// the miniature of the native web browser breaks the layout 
	// if we use the JPanel around the native component,
	// and we can't paint the native component itself either.

	Dimension screenCaptureSize = this.getSize();
	try {
	    Rectangle screenPart = new Rectangle(this.getLocationOnScreen(), screenCaptureSize);
	    Robot robot = new Robot();
	    screenCapture = robot.createScreenCapture(screenPart);
	} catch (AWTException e) {
	    logger.error("Screen capture not possible");
	} catch (IllegalComponentStateException e) {
	    // can't locate the browser on screen because it is not displayed
	}
	if (screenCapture != null) {
	    int captureWidth = screenCaptureSize.width;
	    int captureHeight = screenCaptureSize.height;
	
	    double scale = (double)miniatureSize.width/(double)captureWidth;
	    if (scale > 1) {
		scale = 1.0;
	    }
	    g2d.scale(scale, scale);
	    int x = (miniatureSize.width - captureWidth)/2;
	    if (x < 0) {
		x = 0;
	    }
	    int y = (miniatureSize.height - captureHeight)/2;
	    if (y < 0) {
		y = 0;
	    }
	    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2d.drawImage(screenCapture, null, x, y);

	    // reset scale
	    g2d.scale(1/scale, 1/scale);
	}
    }
}
