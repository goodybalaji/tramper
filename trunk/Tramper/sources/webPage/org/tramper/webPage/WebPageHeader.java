package org.tramper.webPage;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.EnhancedIcon;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.tramper.action.RenderURLAction;
import org.tramper.doc.Link;
import org.tramper.doc.MarkupDocument;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.gui.IconFactory;
import org.tramper.gui.JDropDownButton;
import org.tramper.gui.viewer.SimpleHeader;
import org.tramper.gui.viewer.Viewer;

/**
 * 
 * @author Paul-Emile
 */
public class WebPageHeader extends SimpleHeader implements ActionListener {
    /** WebPageHeader.java long */
    private static final long serialVersionUID = -2257886870464654532L;
    /** language label */
    private JLabel languageLabel;
    /** language */
    private JLabel documentLanguage;
    /** links panel */
    protected JPanel linksPanel;
    /** alternate document links */
    private JDropDownButton documentAlternate;
    /** previous document links */
    private JDropDownButton documentPrevious;
    /** next document links */
    private JDropDownButton documentNext;
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

        GridBagLayout layout = (GridBagLayout)this.getLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(3, 3, 3, 3);
        constraints.weighty = 1;
        
        languageLabel = new JLabel();
        languageLabel.setText(label.getString("javaspeaker.listEnginesLabel")+":");
        languageLabel.setForeground(labelColor);
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        layout.setConstraints(languageLabel, constraints);
        this.add(languageLabel);
        
        documentLanguage = new JLabel();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(documentLanguage, constraints);
        this.add(documentLanguage);
        

        linksPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weightx = 0;
        constraints.fill = GridBagConstraints.BOTH;
        layout.setConstraints(linksPanel, constraints);
        this.add(linksPanel);

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
        linksPanel.add(nativeBrowserButton);
        
        cobraBrowserButton = new JToggleButton();
        cobraBrowserButton.setActionCommand("cobra");
        cobraBrowserButton.addActionListener(this);
        cobraBrowserButton.setMargin(marginButton);
        cobraBrowserButton.setIcon(new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/Lobo.png")));
        cobraBrowserButton.setToolTipText(label.getString("javaspeaker.portable"));
        linksPanel.add(cobraBrowserButton);
        
        ButtonGroup webPageViewerGroup = new ButtonGroup();
        webPageViewerGroup.add(nativeBrowserButton);
        webPageViewerGroup.add(cobraBrowserButton);

        List<Action> listButtonActions = new ArrayList<Action>();
        documentAlternate = new JDropDownButton(listButtonActions, true, true);
        documentAlternate.setText(label.getString("javaspeaker.menu.alternate"));
        documentAlternate.setVisible(false);
        documentAlternate.setMargin(marginFavButton);
        EnhancedIcon anIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/about.png"));
        documentAlternate.setIcon(anIcon);
        documentAlternate.setDisabledIcon(anIcon.toGray());
        linksPanel.add(documentAlternate);

        documentPrevious = new JDropDownButton(listButtonActions, false, false);
        documentPrevious.setText(label.getString("javaspeaker.menu.previous"));
        documentPrevious.setVisible(false);
        documentPrevious.setMargin(marginFavButton);
        anIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/Arrow Left.png"));
        documentPrevious.setIcon(anIcon);
        documentPrevious.setDisabledIcon(anIcon.toGray());
        linksPanel.add(documentPrevious);

        documentNext = new JDropDownButton(listButtonActions, false, false);
        documentNext.setText(label.getString("javaspeaker.menu.next"));
        documentNext.setVisible(false);
        documentNext.setMargin(marginFavButton);
        anIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/Arrow Right.png"));
        documentNext.setIcon(anIcon);
        documentNext.setDisabledIcon(anIcon.toGray());
        linksPanel.add(documentNext);
        
        documentHome = new JDropDownButton(listButtonActions, false, false);
        documentHome.setText(label.getString("javaspeaker.menu.home"));
        documentHome.setVisible(false);
        documentHome.setMargin(marginFavButton);
        anIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/home.png"));
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
	
        Icon icon = ((MarkupDocument)document).getIcon();
        iconLabel.setIcon(icon);
        
        Locale language = ((MarkupDocument)document).getLanguage();
        if (language != null) {
            documentLanguage.setText(language.getDisplayName());
            Icon langIcon = IconFactory.getFlagIconByLocale(language);
            documentLanguage.setIcon(langIcon);
        } else {
            documentLanguage.setText(null);
            documentLanguage.setIcon(null);
        }

	List<Action> linkAlternatePopup = new ArrayList<Action>();
	List<Action> linkHelpPopup = new ArrayList<Action>();
	List<Action> linkHomePopup = new ArrayList<Action>();
	List<Action> linkIndexPopup = new ArrayList<Action>();
	List<Action> linkGlossaryPopup = new ArrayList<Action>();
	List<Action> linkPreviousPopup = new ArrayList<Action>();
	List<Action> linkNextPopup = new ArrayList<Action>();

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
            } else if ("alternate".equals(relation)) {
        	linkAlternatePopup.add(anAction);
            } else if ("home".equals(relation)) {
        	linkHomePopup.add(anAction);
            } else if ("index".equals(relation)) {
        	linkIndexPopup.add(anAction);
            } else if ("previous".equals(relation)) {
        	linkPreviousPopup.add(anAction);
            } else if ("next".equals(relation)) {
        	linkNextPopup.add(anAction);
            } else if ("glossary".equals(relation)) {
        	linkGlossaryPopup.add(anAction);
            }
        }
        
        documentHelp.setActions(linkHelpPopup);
        documentAlternate.setActions(linkAlternatePopup);
        documentHome.setActions(linkHomePopup);
        documentIndex.setActions(linkIndexPopup);
        documentGlossary.setActions(linkGlossaryPopup);
        documentPrevious.setActions(linkPreviousPopup);
        documentNext.setActions(linkNextPopup);

        ResourceBundle label = ResourceBundle.getBundle("label");
        if (linkAlternatePopup.size() > 0) {
            documentAlternate.setVisible(true);
        } else {
            documentAlternate.setText(label.getString("javaspeaker.menu.alternate"));
            Icon anIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/about.png"));
            documentAlternate.setIcon(anIcon);
            documentAlternate.setVisible(false);
        }
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
        if (linkPreviousPopup.size() > 0) {
            documentPrevious.setVisible(true);
        } else {
            documentPrevious.setVisible(false);
        }
        if (linkNextPopup.size() > 0) {
            documentNext.setVisible(true);
        } else {
            documentNext.setVisible(false);
        }
    }

    /**
     * @see org.tramper.gui.viewer.FeedHeader#relocalize()
     */
    @Override
    public void relocalize() {
	super.relocalize();
        ResourceBundle label = ResourceBundle.getBundle("label");
        
        languageLabel.setText(label.getString("javaspeaker.listEnginesLabel")+":");
        if (document != null) {
            Locale language = ((MarkupDocument)document).getLanguage();
            if (language != null) {
                documentLanguage.setText(language.getDisplayName());
            }
        }
        
        documentHelp.setText(label.getString("help.name"));
        documentAlternate.setText(label.getString("javaspeaker.menu.alternate"));
        documentHome.setText(label.getString("javaspeaker.menu.home"));
        documentIndex.setText(label.getString("javaspeaker.menu.index"));
        documentPrevious.setText(label.getString("javaspeaker.menu.previous"));
        documentNext.setText(label.getString("javaspeaker.menu.next"));
        documentGlossary.setText(label.getString("javaspeaker.menu.glossary"));
        cobraBrowserButton.setToolTipText(label.getString("javaspeaker.portable"));
    }

    /**
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
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
