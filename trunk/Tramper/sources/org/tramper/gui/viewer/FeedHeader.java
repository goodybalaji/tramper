package org.tramper.gui.viewer;

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
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.EnhancedIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.tramper.action.RenderURLAction;
import org.tramper.doc.Link;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.MarkupDocument;
import org.tramper.doc.Target;
import org.tramper.gui.IconFactory;
import org.tramper.gui.JDropDownButton;

/**
 * 
 * @author Paul-Emile
 */
public class FeedHeader extends OutlineHeader implements ActionListener {
    /** FeedHeader.java long */
    private static final long serialVersionUID = -2306601558648095594L;
    /** related document links */
    private JDropDownButton documentRelated;
    /** alternate document links */
    private JDropDownButton documentAlternate;
    /** source/via document links */
    private JDropDownButton documentSource;
    /** previous document links */
    private JDropDownButton documentPrevious;
    /** next document links */
    private JDropDownButton documentNext;
    /** list feed viewer button */
    private JToggleButton listFeedButton;
    /** table feed viewer button */
    private JToggleButton tableFeedButton;
    /** links panel */
    protected JPanel linksPanel;

    public FeedHeader() {
	super();

        ResourceBundle label = ResourceBundle.getBundle("label");
        Insets marginFavButton = new Insets(2, 2, 2, 10);

        titlePanel.add(Box.createHorizontalStrut(20), titlePanel.getComponentCount()-2);
        
        listFeedButton = new JToggleButton();
        listFeedButton.setActionCommand("listFeed");
        listFeedButton.addActionListener(this);
        listFeedButton.setIcon(new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/list.png")));
        listFeedButton.setSelected(true);
        listFeedButton.setMargin(marginButton);
        listFeedButton.setToolTipText(label.getString("javaspeaker.list"));
        titlePanel.add(listFeedButton, titlePanel.getComponentCount()-2);
        
        tableFeedButton = new JToggleButton();
        tableFeedButton.setActionCommand("tableFeed");
        tableFeedButton.addActionListener(this);
        tableFeedButton.setMargin(marginButton);
        tableFeedButton.setIcon(new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/table.png")));
        tableFeedButton.setToolTipText(label.getString("javaspeaker.table"));
        titlePanel.add(tableFeedButton, titlePanel.getComponentCount()-2);
        
        ButtonGroup feedViewerGroup = new ButtonGroup();
        feedViewerGroup.add(listFeedButton);
        feedViewerGroup.add(tableFeedButton);
        
        GridBagLayout layout = (GridBagLayout)detailsPanel.getLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(3, 3, 3, 3);
        constraints.weighty = 1;
        
        linksPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weightx = 0;
        constraints.fill = GridBagConstraints.BOTH;
        layout.setConstraints(linksPanel, constraints);
        detailsPanel.add(linksPanel);
        
        List<Action> listButtonActions = new ArrayList<Action>();
        documentSource = new JDropDownButton(listButtonActions, false, false);
        documentSource.setText(label.getString("javaspeaker.menu.via"));
        documentSource.setVisible(false);
        documentSource.setMargin(marginFavButton);
        EnhancedIcon anIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/Arrow Down.png"));
        documentSource.setIcon(anIcon);
        documentSource.setDisabledIcon(anIcon.toGray());
        linksPanel.add(documentSource);

        documentRelated = new JDropDownButton(listButtonActions, false, false);
        documentRelated.setText(label.getString("javaspeaker.menu.related"));
        documentRelated.setVisible(false);
        documentRelated.setMargin(marginFavButton);
        anIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/Arrow Up.png"));
        documentRelated.setIcon(anIcon);
        documentRelated.setDisabledIcon(anIcon.toGray());
        linksPanel.add(documentRelated);

        documentAlternate = new JDropDownButton(listButtonActions, true, true);
        documentAlternate.setText(label.getString("javaspeaker.menu.alternate"));
        documentAlternate.setVisible(false);
        documentAlternate.setMargin(marginFavButton);
        anIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/about.png"));
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
    }

    /**
     * 
     * @see org.tramper.gui.viewer.OutlineHeader#displayDocument(org.tramper.doc.SimpleDocument)
     */
    public void displayDocument(SimpleDocument document, Target target) {
	super.displayDocument(document, target);
	
	List<Action> linkRelatedPopup = new ArrayList<Action>();
	List<Action> linkAlternatePopup = new ArrayList<Action>();
	List<Action> linkSourcePopup = new ArrayList<Action>();
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
            
            if ("related".equals(relation)) {
        	linkRelatedPopup.add(anAction);
            } else if ("alternate".equals(relation)) {
        	linkAlternatePopup.add(anAction);
            } else if ("via".equals(relation)) {
        	linkSourcePopup.add(anAction);
            } else if ("previous".equals(relation)) {
        	linkPreviousPopup.add(anAction);
            } else if ("next".equals(relation)) {
        	linkNextPopup.add(anAction);
            }
        }
        
        documentRelated.setActions(linkRelatedPopup);
        documentAlternate.setActions(linkAlternatePopup);
        documentSource.setActions(linkSourcePopup);
        documentPrevious.setActions(linkPreviousPopup);
        documentNext.setActions(linkNextPopup);

        ResourceBundle label = ResourceBundle.getBundle("label");
        if (linkRelatedPopup.size() > 0) {
            documentRelated.setVisible(true);
        } else {
            documentRelated.setVisible(false);
        }
        if (linkAlternatePopup.size() > 0) {
            documentAlternate.setVisible(true);
        } else {
            documentAlternate.setText(label.getString("javaspeaker.menu.alternate"));
            Icon anIcon = new EnhancedIcon(getClass().getResource("/org/tramper/gui/images/about.png"));
            documentAlternate.setIcon(anIcon);
            documentAlternate.setVisible(false);
        }
        if (linkSourcePopup.size() > 0) {
            documentSource.setVisible(true);
        } else {
            documentSource.setVisible(false);
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
     * 
     * @see org.tramper.gui.viewer.OutlineHeader#relocalize()
     */
    public void relocalize() {
	super.relocalize();
        ResourceBundle label = ResourceBundle.getBundle("label");

        documentRelated.setText(label.getString("javaspeaker.menu.related"));
        documentAlternate.setText(label.getString("javaspeaker.menu.alternate"));
        documentSource.setText(label.getString("javaspeaker.menu.via"));
        documentPrevious.setText(label.getString("javaspeaker.menu.previous"));
        documentNext.setText(label.getString("javaspeaker.menu.next"));
        listFeedButton.setToolTipText(label.getString("javaspeaker.list"));
        tableFeedButton.setToolTipText(label.getString("javaspeaker.table"));
    }

    public void actionPerformed(ActionEvent e) {
	String actionCommand = e.getActionCommand();
	Container parent = this.getParent();
	if (actionCommand.equals("listFeed")) {
	    ListFeedBody body = new ListFeedBody();
	    ((Viewer)parent).setBody(body);
	} else {
	    TableFeedBody body = new TableFeedBody();
	    ((Viewer)parent).setBody(body);
	}
    }
}
