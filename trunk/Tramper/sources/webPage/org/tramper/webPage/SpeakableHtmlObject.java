package org.tramper.webPage;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;

import org.lobobrowser.html.HtmlObject;
import org.lobobrowser.html.gui.HtmlPanel;
import org.tramper.gui.IconFactory;
import org.w3c.dom.NodeList;
import org.w3c.dom.html2.HTMLElement;

/**
 * @author Paul-Emile
 * 
 */
public class SpeakableHtmlObject implements HtmlObject {
    /** SpeakableHtmlObject.java long */
    private static final long serialVersionUID = -4801296616820995674L;
    /** component */
    private JComponent component;

    /**
     * 
     */
    public SpeakableHtmlObject(HTMLElement element) {
        //parameters
        NodeList paramElem = element.getElementsByTagName("param");
        for (int i=0; i<paramElem.getLength(); i++) {
            //Element paramNode = (Element)paramElem.item(i);
            //String paramName = paramNode.getAttribute("name");
            //String paramValue = paramNode.getAttribute("value");
            
        }
        
        //source of the document to embed
        //String srcUrl = element.getAttribute("data");
        
        //choose the right component from the given mime type
        String type = element.getAttribute("type");
        Icon typeIcon = IconFactory.getIconByMimeType(type);
        if (type == null) {
            type = "unknown";
        }
        if (type.startsWith("image")) {
            
        }
        else if (type.startsWith("audio")) {
            component = new JLabel(typeIcon);
        }
        else if (type.equals("text/html")) {
            component = new HtmlPanel();
            //((HtmlPanel)component).setHtml(hmtlText, srcUrl, rendererContext);
        }
        else {
            component = new JEditorPane();
            ((JEditorPane)component).setContentType("text/html");
            NodeList children = element.getChildNodes();
            for (int i=0; i<children.getLength(); i++) {
                //Element paramNode = (Element)paramElem.item(i);
                
            }
            //((JEditorPane)component).setText();
        }

        //for tooltip
        String title = element.getTitle();
        if (title != null) {
            component.setToolTipText(title);
        }
        
        //size
        String widthAttr = element.getAttribute("width");
        int width = component.getWidth();
        if (widthAttr != null) {
            try {
                width = Integer.parseInt(widthAttr);
            }
            catch (NumberFormatException e) {}
        }
        
        String heightAttr = element.getAttribute("height");
        int height = component.getHeight();
        if (heightAttr != null) {
            try {
                height = Integer.parseInt(heightAttr);
            }
            catch (NumberFormatException e) {}
        }

        component.setSize(width, height);
    }

    /**
     * @see org.lobobrowser.html.HtmlObject#destroy()
     */
    public void destroy() {
        component = null;
    }

    /**
     * @see org.lobobrowser.html.HtmlObject#getComponent()
     */
    public Component getComponent() {
        return component;
    }

    /**
     * @see org.lobobrowser.html.HtmlObject#reset(int, int)
     */
    public void reset(int availableWidth, int availableHeight) {
        component.setSize(availableWidth, availableHeight);
        component.validate();
    }

    /**
     * @see org.lobobrowser.html.HtmlObject#resume()
     */
    public void resume() {
    }

    /**
     * @see org.lobobrowser.html.HtmlObject#suspend()
     */
    public void suspend() {
    }

}
