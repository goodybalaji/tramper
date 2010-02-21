package org.tramper.webPage;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.lobobrowser.html.FormInput;
import org.lobobrowser.html.HtmlObject;
import org.lobobrowser.html.HtmlRendererContext;
import org.lobobrowser.html.UserAgentContext;
import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.test.SimpleHtmlRendererContext;
import org.tramper.action.QuitAction;
import org.tramper.doc.History;
import org.tramper.doc.Library;
import org.tramper.doc.Target;
import org.tramper.gui.GraphicalUserInterface;
import org.tramper.loader.Loader;
import org.tramper.loader.LoaderFactory;
import org.tramper.ui.UserInterface;
import org.tramper.ui.UserInterfaceFactory;
import org.w3c.dom.html2.HTMLElement;
import org.w3c.dom.html2.HTMLLinkElement;

/**
 * 
 * @author Paul-Emile
 */
public class SpeakableHtmlRendererContext extends SimpleHtmlRendererContext {
    /** rendering panel */
    protected HtmlPanel contextComponent;
    /** user agent */
    protected UserAgentContext userAgent;
    
    /**
     * 
     * @param contextComponent
     */
    public SpeakableHtmlRendererContext(HtmlPanel contextComponent) {
        this(contextComponent, null);
    }
    
    /**
     * 
     * @see org.lobobrowser.html.test.SimpleHtmlRendererContext#SimpleHtmlRendererContext(org.lobobrowser.html.gui.HtmlPanel, org.lobobrowser.html.HtmlRendererContext)
     */
    public SpeakableHtmlRendererContext(HtmlPanel contextComponent, HtmlRendererContext parentRcontext) {
        super(contextComponent, parentRcontext);
        this.contextComponent = contextComponent;
        userAgent = new SpeakableUserAgentContext();
    }
    
    /**
     * @see org.lobobrowser.html.HtmlRendererContext#back()
     */
    public void back() {
        History history = History.getInstance();
        history.back();
    }

    /**
     * The window loses the focus: what can I do?
     * @see org.lobobrowser.html.HtmlRendererContext#blur()
     */
    public void blur() {
        
    }

    /**
     * Confirms the closure of the window
     * @see org.lobobrowser.html.HtmlRendererContext#close()
     */
    public void close() {
	List<UserInterface> ui = UserInterfaceFactory.getAllUserInterfaces();
	boolean confirmed = false;
	for (UserInterface anUi : ui) {
	    anUi.confirmMessage("confirmQuit");
	}
        if (confirmed) {
            ActionEvent actionEvent = new ActionEvent(this, 0, "quit");
            QuitAction.getInstance().actionPerformed(actionEvent);
        }
    }

    /**
     * @see org.lobobrowser.html.HtmlRendererContext#focus()
     */
    public void focus() {
	GraphicalUserInterface gui = UserInterfaceFactory.getGraphicalUserInterface();
	gui.requestFocus();
    }

    /**
     * @see org.lobobrowser.html.HtmlRendererContext#getDefaultStatus()
     */
    public String getDefaultStatus() {
        ResourceBundle label = ResourceBundle.getBundle("label");
        return label.getString("javaspeaker.ready");
    }

    /**
     * @see org.lobobrowser.html.HtmlRendererContext#getHtmlObject(org.w3c.dom.html2.HTMLElement)
     */
    public HtmlObject getHtmlObject(HTMLElement arg0) {
        return new SpeakableHtmlObject(arg0);
    }

    /**
     * @see org.lobobrowser.html.HtmlRendererContext#getName()
     */
    public String getName() {
	GraphicalUserInterface gui = UserInterfaceFactory.getGraphicalUserInterface();
        return gui.getTitle();
    }

    /**
     * @see org.lobobrowser.html.HtmlRendererContext#getParent()
     */
    public HtmlRendererContext getParent() {
        return super.getParent();
    }

    /**
     * TODO return the current loader status
     * @see org.lobobrowser.html.HtmlRendererContext#getStatus()
     */
    public String getStatus() {
        ResourceBundle label = ResourceBundle.getBundle("label");
        return label.getString("javaspeaker.ready");
    }

    /**
     * @see org.lobobrowser.html.HtmlRendererContext#getTop()
     */
    public HtmlRendererContext getTop() {
        return super.getTop();
    }

    /**
     * @see org.lobobrowser.html.HtmlRendererContext#getUserAgentContext()
     */
    public UserAgentContext getUserAgentContext() {
        return userAgent;
    }

    /**
     * ???
     * @see org.lobobrowser.html.HtmlRendererContext#isClosed()
     */
    public boolean isClosed() {
        return false;
    }

    /**
     * @see org.lobobrowser.html.HtmlRendererContext#isVisitedLink(org.w3c.dom.html2.HTMLLinkElement)
     */
    public boolean isVisitedLink(HTMLLinkElement linkElem) {
        String href = linkElem.getHref();
        boolean visited = History.getInstance().contains(href);
        return visited;
    }

    /**
     * @see org.lobobrowser.html.HtmlRendererContext#linkClicked(org.w3c.dom.html2.HTMLElement, java.net.URL, java.lang.String)
     */
    public void linkClicked(HTMLElement arg0, URL url, String arg2) {
        Loader loader = LoaderFactory.getLoader();
        loader.download(url.toString(), new Target(Library.PRIMARY_FRAME, null));
    }

    /**
     * popup a context menu over the web page or some elements ?
     * @see org.lobobrowser.html.test.SimpleHtmlRendererContext#onContextMenu(org.w3c.dom.html2.HTMLElement, java.awt.event.MouseEvent)
     */
    public boolean onContextMenu(HTMLElement element, MouseEvent event) {
	return false;
    }

    /**
     * Reset the mouse cursor when moving out of a link.
     * @see org.lobobrowser.html.HtmlRendererContext#onMouseOut(org.w3c.dom.html2.HTMLElement, java.awt.event.MouseEvent)
     */
    public void onMouseOut(HTMLElement element, MouseEvent arg1) {
        String tagName = element.getTagName();
        if (tagName.equalsIgnoreCase("A")) {
            Cursor handCursor = new Cursor(Cursor.DEFAULT_CURSOR);
            contextComponent.setCursor(handCursor);
        }
    }

    /**
     * Set the mouse cursor to hand when moving over a link.
     * @see org.lobobrowser.html.HtmlRendererContext#onMouseOver(org.w3c.dom.html2.HTMLElement, java.awt.event.MouseEvent)
     */
    public void onMouseOver(HTMLElement element, MouseEvent mouseEvent) {
        String tagName = element.getTagName();
        if (tagName.equalsIgnoreCase("A")) {
            Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
            contextComponent.setCursor(handCursor);
        }
    }

    /**
     * open in the same window
     * @see org.lobobrowser.html.HtmlRendererContext#open(java.net.URL, java.lang.String, java.lang.String, boolean)
     */
    public HtmlRendererContext open(URL url, String arg1, String arg2, boolean arg3) {
        Loader loader = LoaderFactory.getLoader();
        loader.download(url.toString(), new Target(Library.PRIMARY_FRAME, null));
        return null;
    }

    /**
     * @see org.lobobrowser.html.HtmlRendererContext#reload()
     */
    public void reload() {
        History history = History.getInstance();
        history.loadCurrent();
    }

    /**
     * @see org.lobobrowser.html.HtmlRendererContext#scroll(int, int)
     */
    public void scroll(int x, int y) {
        contextComponent.scroll(x, y);
    }

    /**
     * @see org.lobobrowser.html.HtmlRendererContext#submitForm(java.lang.String, java.net.URL, java.lang.String, java.lang.String, org.lobobrowser.html.FormInput[])
     */
    public void submitForm(String method, URL url, String target, String enctype, FormInput[] formInput) {
        Loader loader = LoaderFactory.getLoader();
        
        if ("post".equalsIgnoreCase(method)) {
            //construct the HTTP request body with the form inputs
            Map<String, String> param = new HashMap<String, String>();
            for (int i=0; i<formInput.length; i++) {
                String paramName = formInput[i].getName();
                String paramValue = formInput[i].getTextValue();
                param.put(paramName, paramValue);
            }
            loader.call(url.toString(), param, new Target(Library.PRIMARY_FRAME, null));
        } else {
            String urlWithParam = url.toString();
            //if there is no '?' in the url, add one at the end
            if (urlWithParam.indexOf("?") == -1) {
                urlWithParam += "?";
            }
            //else, if there is no '&' at the end of the url, add one
            else if (!urlWithParam.endsWith("&")) {
                urlWithParam += "&";
            }
            
            //concatene the parameters at the end of the url
            if (enctype == null || enctype.equals("")) {
                enctype = "ISO-8859-1";
            }
            if (formInput != null) {
        	StringBuilder paramBuilder = new StringBuilder();
                for (int i=0; i<formInput.length; i++) {
                    String paramName = formInput[i].getName();
                    String paramValue = formInput[i].getTextValue();
                    try {
                	paramBuilder.append(paramName);
                	paramBuilder.append("=");
                	paramBuilder.append(URLEncoder.encode(paramValue, enctype));
                	paramBuilder.append("&");
                    }
                    catch (UnsupportedEncodingException e) {
                    }
                }
                urlWithParam += paramBuilder.toString();
            }
            loader.download(urlWithParam, new Target(Library.PRIMARY_FRAME, null));
        }
    }
}
