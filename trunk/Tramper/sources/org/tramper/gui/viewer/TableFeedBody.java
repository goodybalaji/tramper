package org.tramper.gui.viewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.StyleSheet;

import org.tramper.doc.Feed;
import org.tramper.doc.FeedItem;
import org.tramper.doc.Library;
import org.tramper.doc.SimpleDocument;
import org.tramper.doc.Target;
import org.tramper.gui.DateTableCellRenderer;
import org.tramper.gui.ListTableCellEditor;
import org.tramper.gui.ListTableCellRenderer;
import org.tramper.loader.Loader;
import org.tramper.loader.LoaderFactory;

/**
 * Feed viewer using a table
 * @author Paul-Emile
 */
public class TableFeedBody extends JSplitPane implements Body, MouseListener, KeyListener, HyperlinkListener {
    /** TableFeedBody.java long  */
    private static final long serialVersionUID = -3059998222395276883L;
    /** table */
    private JTable feedTable;
    /** Current feed item details */
    protected JEditorPane feedItemDetail;
    /** speakable document */
    private Feed document;
    /** target */
    private Target target;
    
    /**
     * 
     */
    public TableFeedBody() {
        this.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.setOneTouchExpandable(false);
        //the top component (the list) takes the extra space
        this.setResizeWeight(1);

        JScrollPane scroll = new JScrollPane();
        feedTable = new JTable();
        feedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        feedTable.setRowSelectionAllowed(true);
        feedTable.changeSelection(0, 0, false, false);
        feedTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        feedTable.addMouseListener(this);
        feedTable.setRowHeight(25);
        feedTable.addKeyListener(this);
        scroll.setViewportView(feedTable);
        
        this.setTopComponent(scroll);

        feedItemDetail = new JEditorPane();
        feedItemDetail.setContentType("text/html");
        feedItemDetail.addHyperlinkListener(this);
        feedItemDetail.setBackground(Color.WHITE);
        feedItemDetail.setEditable(false);
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
    
    /**
     * This method picks good column sizes.
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit().
     */
    private void initColumnSizes(JTable table) {
        TableModel model = table.getModel();
        if (model.getRowCount() > 0) {
            TableColumn column = null;
            Component comp = null;
            int cellWidth = 0;
            TableColumnModel columnModel = table.getColumnModel();
            
            for (int i = 0; i < columnModel.getColumnCount(); i++) {
                column = columnModel.getColumn(i);
    
                for (int j=0; j<table.getRowCount(); j++) {
                    TableCellRenderer cellRenderer = table.getCellRenderer(j, i);
                    comp = cellRenderer.getTableCellRendererComponent(table, model.getValueAt(j, i), false, false, j, i);
                    cellWidth = comp.getPreferredSize().width;
                    column.setPreferredWidth(Math.max(column.getPreferredWidth(), cellWidth));
                }
            }
        }
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#getDocument()
     */
    public SimpleDocument getDocument() {
        return document;
    }

    /**
     * 
     * @see org.tramper.gui.viewer.Body#displayDocument(org.tramper.doc.SimpleDocument, org.tramper.doc.Target, int)
     */
    public void displayDocument(SimpleDocument doc, Target target, int documentPart) {
	if (!(doc instanceof Feed)) {
	    throw new RuntimeException(doc.getTitle()+" is not a Feed");
	}
        document = (Feed)doc;
        this.target = target;
	
        feedTable.setModel(document);
        TableRowSorter<TableModel> tableSorter = new TableRowSorter<TableModel>(document);
        feedTable.setRowSorter(tableSorter);
        
        long indexModel = document.getIndex();
        int indexView = feedTable.convertColumnIndexToView((int)indexModel);
        feedTable.changeSelection(indexView, 0, false, false);
        TableColumnModel model = feedTable.getColumnModel();
        
        ListTableCellRenderer linkCellRenderer = new ListTableCellRenderer();
        ListTableCellEditor linkCellEditor = new ListTableCellEditor();
        TableColumn descColumn = model.getColumn(2);
        descColumn.setCellRenderer(linkCellRenderer);
        descColumn.setCellEditor(linkCellEditor);
        descColumn = model.getColumn(3);
        descColumn.setCellRenderer(linkCellRenderer);
        descColumn.setCellEditor(linkCellEditor);
        
        DateTableCellRenderer dateCellRenderer = new DateTableCellRenderer();
        descColumn = model.getColumn(4);
        descColumn.setCellRenderer(dateCellRenderer);
        descColumn = model.getColumn(5);
        descColumn.setCellRenderer(dateCellRenderer);

        this.initColumnSizes(feedTable);
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#first()
     */
    public void first() {
	int minRowView = 0;
        feedTable.changeSelection(minRowView, 0, false, false);
	int minRowModel = feedTable.convertRowIndexToModel(minRowView);
        document.setIndex(minRowModel);
        FeedItem selectedItem = document.getItem(minRowModel);
        String displayableItem = renderFeedItem(selectedItem);
        feedItemDetail.setText(displayableItem);
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#last()
     */
    public void last() {
        int maxRowView = feedTable.getModel().getRowCount();
        feedTable.changeSelection(maxRowView-1, 0, false, false);
	int maxRowModel = feedTable.convertRowIndexToModel(maxRowView);
        document.setIndex(maxRowModel-1);
        FeedItem selectedItem = document.getItem(maxRowModel-1);
        String displayableItem = renderFeedItem(selectedItem);
        feedItemDetail.setText(displayableItem);
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#next()
     */
    public void next() {
        int selectedRowView = feedTable.getSelectedRow();
        feedTable.changeSelection(selectedRowView+1, 0, false, false);
	int selectedRowModel = feedTable.convertRowIndexToModel(selectedRowView);
        document.setIndex(selectedRowModel+1);
        FeedItem selectedItem = document.getItem(selectedRowModel+1);
        String displayableItem = renderFeedItem(selectedItem);
        feedItemDetail.setText(displayableItem);
    }

    /**
     * @see org.tramper.gui.viewer.Viewer#previous()
     */
    public void previous() {
        int selectedRowView = feedTable.getSelectedRow();
        feedTable.changeSelection(selectedRowView-1, 0, false, false);
	int selectedRowModel = feedTable.convertRowIndexToModel(selectedRowView);
        document.setIndex(selectedRowModel-1);
        FeedItem selectedItem = document.getItem(selectedRowModel-1);
        String displayableItem = renderFeedItem(selectedItem);
        feedItemDetail.setText(displayableItem);
    }

    /**
     * Build the HTML code to render the feed item.
     * @param selectedItem the feed item to render
     * @return the HTML code builded
     */
    private String renderFeedItem(FeedItem selectedItem) {
        StringBuffer summarize = new StringBuffer();
        summarize.append("<html><head></head><body>");
        String description = selectedItem.getDescription();
        if (description != null) {
            summarize.append(description);
        }
        
        summarize.append("</body></html>");
        String summarizeItem = summarize.toString();
        
	return summarizeItem;
    }

    /**
     * @see javax.swing.JPanel#updateUI()
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

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
	int clickedButton = e.getButton();
	if (clickedButton == MouseEvent.BUTTON1) {
	    Library.getInstance().setActiveDocument(target);
	    int selectedRowView = feedTable.getSelectedRow();
	    int selectedRowModel = feedTable.convertRowIndexToModel(selectedRowView);
	    document.setIndex(selectedRowModel);
            FeedItem selectedItem = document.getItem(selectedRowModel);
            String displayableItem = renderFeedItem(selectedItem);
            feedItemDetail.setText(displayableItem);
	}
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
	int keyCode = e.getKeyCode();
	if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_PAGE_DOWN || keyCode == KeyEvent.VK_PAGE_UP) {
	    int selectedRowView = feedTable.getSelectedRow();
	    int selectedRowModel = feedTable.convertRowIndexToModel(selectedRowView);
	    document.setIndex(selectedRowModel);
            FeedItem selectedItem = document.getItem(selectedRowModel);
            String displayableItem = renderFeedItem(selectedItem);
            feedItemDetail.setText(displayableItem);
	}
    }

    public void keyTyped(KeyEvent e) {
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
	if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            JEditorPane pane = (JEditorPane) e.getSource();
            if (e instanceof HTMLFrameHyperlinkEvent) {
                HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
                HTMLDocument doc = (HTMLDocument)pane.getDocument();
                doc.processHTMLFrameHyperlinkEvent(evt);
            } else {
                String url = e.getURL().toString();
                Loader loader = LoaderFactory.getLoader();
                loader.download(url, new Target(Library.PRIMARY_FRAME, null));
            }
        }
    }

    public void paintMiniature(Graphics2D g2d, Dimension miniatureSize, boolean mouseOver) {
	double scale = 0.8;
	g2d.scale(scale, scale);
	
	this.paint(g2d);

	// reset scale
	g2d.scale(1/scale, 1/scale);
    }
}
