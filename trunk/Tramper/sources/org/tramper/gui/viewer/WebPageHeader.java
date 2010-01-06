package org.tramper.gui.viewer;

import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.EnhancedIcon;
import javax.swing.Icon;
import javax.swing.JToggleButton;

import org.tramper.action.RenderURLAction;
import org.tramper.doc.Link;
import org.tramper.doc.MarkupDocument;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.gui.IconFactory;
import org.tramper.gui.JDropDownButton;

/**
 * 
 * @author Paul-Emile
 */
public class WebPageHeader extends FeedHeader {
    /** WebPageHeader.java long */
    private static final long serialVersionUID = -2257886870464654532L;
    /** help document links */
    private JDropDownButton documentHelp;
    /** home document links */
    private JDropDownButton documentHome;
    /** index document links */
    private JDropDownButton documentIndex;
    /** glossary document links */
    private JDropDownButton documentGlossary;
    /** native browser viewer button */
    private JToggleButton nativeBrowserButton;
    /** Cobra browser viewer button */
    private JToggleButton cobraBrowserButton;

    /**
     * 
     */
    public WebPageHeader() {
	super();
	
        ResourceBundle label = ResourceBundle.getBundle("label");
        Insets marginFavButton = new Insets(2, 2, 2, 10);

        // remove the feed viewer switch before adding webpage ones
        titlePanel.remove(titlePanel.getComponentCount()-3);
        titlePanel.remove(titlePanel.getComponentCount()-3);
        
	String engine = NativeWebPageBody.getCurrentEngine();
        nativeBrowserButton = new JToggleButton();
        nativeBrowserButton.setActionCommand("native");
        nativeBrowserButton.addActionListener(this);
        EnhancedIcon nativeBrowserIcon = null;
        if ("Mozilla".equals(engine)) {
            nativeBrowserIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/Firefox.png"));
        } else if ("Internet Explorer".equals(engine)) {
            nativeBrowserIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/IE.png"));
        } else if ("WebKit".equals(engine)) {
            nativeBrowserIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/Webkit.png"));
        }
        nativeBrowserButton.setIcon(nativeBrowserIcon);
        nativeBrowserButton.setSelected(true);
        nativeBrowserButton.setMargin(marginButton);
        nativeBrowserButton.setToolTipText(engine);
        titlePanel.add(nativeBrowserButton, titlePanel.getComponentCount()-2);
        
        cobraBrowserButton = new JToggleButton();
        cobraBrowserButton.setActionCommand("cobra");
        cobraBrowserButton.addActionListener(this);
        cobraBrowserButton.setMargin(marginButton);
        cobraBrowserButton.setIcon(new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/Lobo.png")));
        cobraBrowserButton.setToolTipText(label.getString("javaspeaker.portable"));
        titlePanel.add(cobraBrowserButton, titlePanel.getComponentCount()-2);
        
        ButtonGroup webPageViewerGroup = new ButtonGroup();
        webPageViewerGroup.add(nativeBrowserButton);
        webPageViewerGroup.add(cobraBrowserButton);

        
        List<Action> listButtonActions = new ArrayList<Action>();
        documentHome = new JDropDownButton(listButtonActions, false, false);
        documentHome.setText(label.getString("javaspeaker.menu.home"));
        documentHome.setVisible(false);
        documentHome.setMargin(marginFavButton);
        EnhancedIcon anIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/home.png"));
        documentHome.setIcon(anIcon);
        documentHome.setDisabledIcon(anIcon.toGray());
        linksPanel.add(documentHome);

        documentIndex = new JDropDownButton(listButtonActions, false, false);
        documentIndex.setText(label.getString("javaspeaker.menu.index"));
        documentIndex.setVisible(false);
        documentIndex.setMargin(marginFavButton);
        anIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/enumList.png"));
        documentIndex.setIcon(anIcon);
        documentIndex.setDisabledIcon(anIcon.toGray());
        linksPanel.add(documentIndex);

        documentGlossary = new JDropDownButton(listButtonActions, false, false);
        documentGlossary.setText(label.getString("javaspeaker.menu.glossary"));
        documentGlossary.setVisible(false);
        documentGlossary.setMargin(marginFavButton);
        anIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/unsortedList.png"));
        documentGlossary.setIcon(anIcon);
        documentGlossary.setDisabledIcon(anIcon.toGray());
        linksPanel.add(documentGlossary);

        documentHelp = new JDropDownButton(listButtonActions, false, false);
        documentHelp.setText(label.getString("help.name"));
        documentHelp.setVisible(false);
        documentHelp.setMargin(marginFavButton);
        anIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/help.png"));
        documentHelp.setIcon(anIcon);
        documentHelp.setDisabledIcon(anIcon.toGray());
        linksPanel.add(documentHelp);
    }

    /**
     * @see org.tramper.gui.viewer.FeedHeader#displayDocument(org.tramper.doc.SimpleDocument)
     */
    @Override
    public void displayDocument(SimpleDocument document, Target target) {
	super.displayDocument(document, target);
	List<Action> linkHelpPopup = new ArrayList<Action>();
	List<Action> linkHomePopup = new ArrayList<Action>();
	List<Action> linkIndexPopup = new ArrayList<Action>();
	List<Action> linkGlossaryPopup = new ArrayList<Action>();

        List<Link> links = ((MarkupDocument)document).getLinks();
        for (int i=0; i<links.size(); i++) {
            Link aLink = links.get(i);
            SimpleDocument aDocument = aLink.getLinkedDocument();
            String relation = aLink.getRelation();
            String linkTitle = aDocument.getTitle();
            String linkMimeType = aDocument.getMimeType();
            URL url = aDocument.getUrl();
            Action anAction = new RenderURLAction(url.toString(), target);
            String text = "";
            if (linkTitle != null && !linkTitle.equals("")) {
                text = text.concat(linkTitle);
            } else {
        	text = url.toString();
            }
            anAction.putValue(Action.NAME, text);
            if (linkMimeType != null) {
                Icon mimeTypeIcon = IconFactory.getIconByMimeType(linkMimeType);
                anAction.putValue(Action.SMALL_ICON, mimeTypeIcon);
            }
            
            if ("help".equals(relation)) {
        	linkHelpPopup.add(anAction);
            } else if ("home".equals(relation)) {
        	linkHomePopup.add(anAction);
            } else if ("index".equals(relation)) {
        	linkIndexPopup.add(anAction);
            } else if ("glossary".equals(relation)) {
        	linkGlossaryPopup.add(anAction);
            }
        }
        
        documentHelp.setActions(linkHelpPopup);
        documentHome.setActions(linkHomePopup);
        documentIndex.setActions(linkIndexPopup);
        documentGlossary.setActions(linkGlossaryPopup);

        if (linkHelpPopup.size() > 0) {
            documentHelp.setVisible(true);
        } else {
            documentHelp.setVisible(false);
        }
        if (linkHomePopup.size() > 0) {
            documentHome.setVisible(true);
        } else {
            documentHome.setVisible(false);
        }
        if (linkIndexPopup.size() > 0) {
            documentIndex.setVisible(true);
        } else {
            documentIndex.setVisible(false);
        }
        if (linkGlossaryPopup.size() > 0) {
            documentGlossary.setVisible(true);
        } else {
            documentGlossary.setVisible(false);
        }
    }

    /**
     * @see org.tramper.gui.viewer.FeedHeader#relocalize()
     */
    @Override
    public void relocalize() {
	super.relocalize();
        ResourceBundle label = ResourceBundle.getBundle("label");
        documentHelp.setText(label.getString("help.name"));
        documentHome.setText(label.getString("javaspeaker.menu.home"));
        documentIndex.setText(label.getString("javaspeaker.menu.index"));
        documentGlossary.setText(label.getString("javaspeaker.menu.glossary"));
        cobraBrowserButton.setToolTipText(label.getString("javaspeaker.portable"));
    }

    /**
     * @see org.tramper.gui.viewer.FeedHeader#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
	String actionCommand = e.getActionCommand();
	Container parent = this.getParent();
	if (actionCommand.equals("cobra")) {
	    CobraWebPageBody body = new CobraWebPageBody();
	    ((Viewer)parent).setBody(body);
	} else {
	    try {
		NativeWebPageBody body = new NativeWebPageBody();
		((Viewer)parent).setBody(body);
	    } catch (Exception e1) {
	    }
	}
    }
}
