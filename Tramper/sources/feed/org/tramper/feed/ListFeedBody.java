package org.tramper.feed;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.StyleSheet;

import org.tramper.doc.DocumentItem;
import org.tramper.doc.Feed;
import org.tramper.doc.FeedItem;
import org.tramper.doc.Library;
import org.tramper.doc.Link;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.gui.IconFactory;
import org.tramper.gui.viewer.Body;
import org.tramper.loader.Loader;
import org.tramper.loader.LoaderFactory;
import org.tramper.ui.Renderer;

/**
 * 
 * @author Paul-Emile
 */
public class ListFeedBody extends JSplitPane implements Body, MouseListener, HyperlinkListener, ListSelectionListener {
    /** ListFeedBody.java long */
    private static final long serialVersionUID = 1L;
    /** List of feed items */
    protected JList feedList;
    /** Current feed item details */
    protected JEditorPane feedItemDetail;
    /** speakable document */
    protected Feed document;
    /** target */
    private Target target;

    /**
     * 
     */
    public ListFeedBody() {
        this.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.setOneTouchExpandable(false);
	this.setContinuousLayout(true);
        //the top component (the list) takes the extra space
        this.setResizeWeight(1);

        feedList = new JList();
        //feedList.setFixedCellWidth(700);
        feedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        feedList.setEnabled(true);
        feedList.addListSelectionListener(this);
        feedList.addMouseListener(this);
        
        JScrollPane listScroll = new JScrollPane();
        Dimension minListSize = listScroll.getMinimumSize();
        minListSize.height = 100;
        listScroll.setMinimumSize(minListSize);
        Dimension prefListSize = listScroll.getPreferredSize();
        prefListSize.height = 500;
        listScroll.setPreferredSize(prefListSize);
        listScroll.setViewportView(feedList);
        
        this.setTopComponent(listScroll);
        
        feedItemDetail = new JEditorPane();
        feedItemDetail.setContentType("text/html");
        feedItemDetail.setEditable(false);
        feedItemDetail.setBackground(Color.WHITE);
        feedItemDetail.addHyperlinkListener(this);
        HTMLEditorKit kit = (HTMLEditorKit)feedItemDetail.getEditorKit();
        StyleSheet sheet = kit.getStyleSheet();
	Font font = UIManager.getFont("Label.font");
        sheet.addRule("body {font-family: "+font.getFamily()+"; font-size: "+font.getSize()+"pt;}");

        JScrollPane detailScroll = new JScrollPane();
        Dimension minDetailSize = detailScroll.getMinimumSize();
        minDetailSize.height = 100;
        detailScroll.setMinimumSize(minDetailSize);
        Dimension prefDetailSize = detailScroll.getPreferredSize();
        prefDetailSize.height = 200;
        detailScroll.setPreferredSize(prefDetailSize);
        detailScroll.setViewportView(feedItemDetail);
        
        this.setBottomComponent(detailScroll);

        this.setDividerSize(2);
        this.setDividerLocation(-1);
    }

    public void displayDocument(SimpleDocument doc, Target target, int documentPart) {
	if (!(doc instanceof Feed)) {
	    throw new RuntimeException(doc.getTitle()+" is not a Feed");
	}
	document = (Feed)doc;
	this.target = target;
	if (documentPart == Renderer.ALL_PART) {
            FeedItemListCellRenderer itemSpeakableRenderer = new FeedItemListCellRenderer();
            feedList.setCellRenderer(itemSpeakableRenderer);
            feedList.setModel(document);
	} else if (documentPart == Renderer.LINK_PART) {
	    List<Link> documentLink = new ArrayList<Link>();
            List<DocumentItem> items = document.getItems();
            for (int i=0; i<items.size(); i++) {
                DocumentItem item = items.get(i);
                List<Link> links = item.getLinks();
                documentLink.addAll(links);
            }
            
            ListCellRenderer linkRenderer = new LinkListCellRenderer();
            feedList.setCellRenderer(linkRenderer);
            feedList.setListData(documentLink.toArray());
	} else {
	    return;
	}
        
        long index = document.getIndex();
        feedList.setSelectedIndex((int)index);
    }
    
    /**
     * 
     * @see org.tramper.gui.viewer.Viewer#first()
     */
    public void first() {
        feedList.setSelectedIndex(0);
        feedList.ensureIndexIsVisible(0);
    }
    
    /**
     * @see org.tramper.gui.viewer.Viewer#next()
     */
    public void next() {
        int maxSpeakable = feedList.getModel().getSize();
        int selectedIndex = feedList.getSelectedIndex();
        if (selectedIndex < maxSpeakable - 1) {
            feedList.setSelectedIndex(selectedIndex + 1);
            feedList.ensureIndexIsVisible(selectedIndex + 1);
        }
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#previous()
     */
    public void previous() {
        int selectedIndex = feedList.getSelectedIndex();
        if (selectedIndex > 0) {
            feedList.setSelectedIndex(selectedIndex - 1);
            feedList.ensureIndexIsVisible(selectedIndex - 1);
        }
    }

    /**
     * 
     * @see org.tramper.gui.viewer.Viewer#last()
     */
    public void last() {
        int feedListCount = feedList.getModel().getSize()-1;
        feedList.setSelectedIndex(feedListCount);
        feedList.ensureIndexIsVisible(feedListCount);
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent event) {
	int clickCount = event.getClickCount();
	int clickedButton = event.getButton();
	if (clickedButton == MouseEvent.BUTTON1) {
	    if (clickCount == 2) {
		FeedItem selectedItem = (FeedItem)feedList.getSelectedValue();
		if (selectedItem != null) {
		    List<Link> links = selectedItem.getLinks();
		    for (int i=0; i<links.size(); i++) {
			Link link = links.get(i);
			String relation = link.getRelation();
			if ("via".equals(relation) || "enclosure".equals(relation)) {
			    URL url = link.getLinkedDocument().getUrl();
			    Loader loader = LoaderFactory.getLoader();
			    loader.download(url.toString(), new Target(Library.PRIMARY_FRAME, null));
			}
		    }
		}
	    }
	}
    }
    
    /**
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent arg0) {
    }
    
    /**
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent arg0) {
    }
    
    /**
     * 
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent event) {
	int clickedButton = event.getButton();
	if (clickedButton == MouseEvent.BUTTON1) {
	    Library.getInstance().setActiveDocument(target);
	}
    }
    
    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent event) {
    }

    /**
     * when click a link, launch a loader
     * @param e
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
	if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            JEditorPane pane = (JEditorPane) e.getSource();
            if (e instanceof HTMLFrameHyperlinkEvent) {
                HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
                HTMLDocument doc = (HTMLDocument)pane.getDocument();
                doc.processHTMLFrameHyperlinkEvent(evt);
            } else {
        	URL url = e.getURL();
        	if (url != null) {
                    String urlStr = url.toString();
                    Loader loader = LoaderFactory.getLoader();
                    loader.download(urlStr, new Target(Library.PRIMARY_FRAME, null));
        	}
            }
        }
    }

    /**
     * 
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e) {
	boolean valueIsAdjusting = e.getValueIsAdjusting();
	int currentIndex = feedList.getSelectedIndex();
	if (!valueIsAdjusting) {
	    if (currentIndex != -1) {
                document.setIndex(currentIndex);
                FeedItem selectedItem = document.getItem(currentIndex);
                String displayableItem = renderFeedItem(selectedItem);
                feedItemDetail.setText(displayableItem);
	    }
	}
    }

    /**
     * Build the HTML code to render the feed item.
     * @param selectedItem the feed item to render
     * @return the HTML code builded
     */
    private String renderFeedItem(FeedItem selectedItem) {
        ResourceBundle label = ResourceBundle.getBundle("label");
        StringBuffer summarize = new StringBuffer();
        summarize.append("<html><head></head><body>");
        
        String description = selectedItem.getDescription();
        if (description != null) {
            summarize.append("<p style='margin-left: 15px'>");
            summarize.append(description);
            summarize.append("</p>");
        }
        
        List<Link> links = selectedItem.getLinks();
        for (int i=0; i<links.size(); i++) {
            Link aLink = links.get(i);
            SimpleDocument aDocument = aLink.getLinkedDocument();
            String mimeType = aDocument.getMimeType();
            summarize.append("<a href='");
            URL url = aDocument.getUrl();
            summarize.append(url.toString());
            summarize.append("'><img src='");
            URL urlImg = IconFactory.getIconUrlByMimeType(mimeType);
            if (urlImg == null) {
        	urlImg = getClass().getResource("images/File.png");
            }
            summarize.append(urlImg.toString());
            summarize.append("' border='0'>&nbsp;");
            summarize.append(aDocument.getTitle());
            summarize.append("</a>");
            summarize.append("<br>");
            summarize.append("<div style='color: #888888; margin-left: 15px'>");
            String relation = aLink.getRelation();
            String displayableRelation = label.getString("javaspeaker.menu."+relation);
            summarize.append(displayableRelation);
            if (mimeType != null) {
                summarize.append(" - ");
                summarize.append(mimeType);
            }
            long length = aDocument.getLength();
            if (length > 0) {
                NumberFormat nbFormat = NumberFormat.getIntegerInstance();
                summarize.append(nbFormat.format(length/1000));
                summarize.append(" - ");
                summarize.append(label.getString("javaspeaker.unit.kilobyte"));
            }
            summarize.append("</div>");
        }
        
        summarize.append("</body></html>");
        String summarizeItem = summarize.toString();
        
	return summarizeItem;
    }

    /**
     * @see javax.swing.JSplitPane#updateUI()
     */
    @Override
    public void updateUI() {
	super.updateUI();
	if (feedItemDetail != null) {
	    Font font = UIManager.getFont("Label.font");
    	
            HTMLEditorKit kit = (HTMLEditorKit)feedItemDetail.getEditorKit();
            StyleSheet sheet = kit.getStyleSheet();
            sheet.addRule("body {font-family: "+font.getFamily()+"; font-size: "+font.getSize()+"pt;}");
	}
    }

    public void paintMiniature(Graphics2D g2d, Dimension miniatureSize, boolean mouseOver) {
	Image feedIcon = document.getIcon();
	if (feedIcon != null) {
	    int iconWidth = feedIcon.getWidth(this);
	    int iconHeight = feedIcon.getHeight(this);
	    
	    double scale = (double)miniatureSize.width/(double)iconWidth;
	    if (scale > 1) {
		scale = 1.0;
	    }
	    g2d.scale(scale, scale);
	    int x = (miniatureSize.width - iconWidth)/2;
	    if (x < 0) {
		x = 0;
	    }
	    int y = (miniatureSize.height - iconHeight)/2;
	    if (y < 0) {
		y = 0;
	    }
	    g2d.drawImage(feedIcon, x, y, this);

	    // reset scale
	    g2d.scale(1/scale, 1/scale);
	} else {
	    double scale = 0.8;
        	
	    g2d.scale(scale, scale);
        	
	    feedList.paint(g2d);
        
	    // reset scale
	    g2d.scale(1/scale, 1/scale);
	}
    }
}
