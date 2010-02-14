package org.tramper.doc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.tramper.loader.Loader;
import org.tramper.loader.LoaderFactory;
import org.tramper.loader.LoadingEvent;
import org.tramper.loader.LoadingListener;
import org.tramper.ui.UserInterfaceFactory;

/**
 * Load and save the history 
 * @author Paul-Emile
 */
public class History implements LoadingListener {
    /** logger */
    private Logger logger = Logger.getLogger(History.class);
    /** the singleton */
    private static History instance;
    /** the history */
    private Feed history;
    /** Favorites file name */
    private static final String FILENAME = "history.atom";
    /** history fil url */
    private URL historyUrl;
    /** current position in the history */
    private int currentPosition;
    /** maximum number of items in the history */
    protected final static int MAX_ITEM = 100;
    
    /**
     * private constructor
     */
    private History() {
        currentPosition = 0;
        String userHome = System.getProperty("user.home");
        String sep = System.getProperty("file.separator");
        String filepath = "file://" + userHome + sep + FILENAME;
        try {
            historyUrl = new URL(filepath);
        } catch (MalformedURLException e) {
            logger.error("wrong history url: "+filepath);
            throw new RuntimeException("wrong history url: "+filepath);
        }
        load();
    }
    
    /**
     * Load the singleton from a file, or instanciate new one if failed
     * @return
     */
    public static History getInstance() {
        if (instance == null) {
            instance = new History();
        }
        return instance;
    }
    
    /**
     * load the history from a file
     */
    public void load() {
        try {
            Loader loader = LoaderFactory.getLoader();
            loader.addLoadingListener(this);
            loader.download(historyUrl.toString(), new Target(Library.SECONDARY_FRAME, null));
        } catch (Exception e) {
            logger.error("can't access the history file "+historyUrl+", create empty list", e);
            newHistory();
        }
    }
    
    /**
     * instanciate a new history feed with some default informations
     */
    public void newHistory() {
        ResourceBundle label = ResourceBundle.getBundle("label");
        history = new Feed();
        history.setMimeType("application/atom+xml");
        history.setTitle(label.getString("history.name"));
        history.setDescription(label.getString("javaspeaker.productTitle"));
        String username = System.getProperty("user.name");
        history.setAuthor(username);
        history.setCreationDate(new Date());
        history.setLanguage(Locale.getDefault());
        history.setUrl(historyUrl);
    }
    
    /**
     * add a document in the list of history
     * @param document
     */
    public void addHistory(SimpleDocument document) {
	if (history == null) {
	    return;
	}
        String url = document.getUrl().toString();

        //if this is the favorites or the history, don't add it
	Outline fav = Favorites.getInstance().getFavorites();
	String historyUrl = history.getUrl().toString();
	String favoriteUrl = fav.getUrl().toString();
	if (url.equals(historyUrl) || url.equals(favoriteUrl)) {
	    return;
	}
	
        //if the open document is equals to the current one in the history, don't add it
        List<DocumentItem> items = history.getItems();
        int itemNumber = items.size();
        if (itemNumber > 0) {
            FeedItem anHistory = (FeedItem)items.get(currentPosition);
            List<Link> links = anHistory.getLinks();
            for (int j=0; j<links.size(); j++) {
                Link aLink = links.get(j);
                if (aLink.getRelation().equals("via")) {
                    SimpleDocument aDocument = aLink.getLinkedDocument();
                    URL favUrl = aDocument.getUrl();
                    if (url.equals(favUrl.toString())) {
                        return;
                    }
                }
            }
        }
        
        //if there is too much items in the history, delete the last oldest one before adding the new one
        if (itemNumber >= MAX_ITEM) {
            removeHistory(itemNumber-1);
        }
        
        //create a feed item and add it to the feed
        FeedItem newItem = new FeedItem();
        String title = document.getTitle();
        newItem.setTitle(title);
        newItem.setPublicationDate(new Date());
        newItem.setDescription(document.getDescription());
        Link aLink = new Link();
        aLink.setRelation("via");
        aLink.setLinkedDocument(document);
        aLink.setLinkingDocument(history);
        newItem.addLink(aLink);
        history.addItem(newItem, currentPosition);
    }
    
    /**
     * remove a document by it's index in the list 
     * @param i
     */
    public void removeHistory(int i) {
        history.removeItem(i);
    }
    
    /**
     * remove the document from the list
     * @param document
     */
    public void removeHistory(SimpleDocument document) {
        String url = document.getUrl().toString();
        List<DocumentItem> items = history.getItems();
        for (int i=0; i<items.size(); i++) {
            FeedItem anHistory = (FeedItem)items.get(i);
            List<Link> links = anHistory.getLinks();
            for (int j=0; j<links.size(); j++) {
                Link aLink = links.get(j);
                if (aLink.getRelation().equals("via")) {
                    SimpleDocument aDocument = aLink.getLinkedDocument();
                    String favUrl = aDocument.getUrl().toString();
                    if (url.equals(favUrl.toString())) {
                        history.removeItem(i);
                        return;
                    }
                }
            }
        }
    }
    
    /**
     * return all the history
     * @return
     */
    public Feed getHistory() {
        return this.history;
    }
    
    /**
     * return one of the history
     * @param index index of the requested document
     * @return the item
     */
    public FeedItem getHistory(int index) {
        FeedItem item = history.getItem(index);
        return item;
    }
    
    /**
     * Go back on the history
     */
    public void back() {
        List<DocumentItem> items = history.getItems();
        if (currentPosition < items.size()-1) {
            currentPosition++;
            loadCurrent();
        }
    }
    
    /**
     * Go forward in the history
     */
    public void forward() {
        if (currentPosition > 0) {
            currentPosition--;
            loadCurrent();
        }
    }
    
    /**
     * 
     */
    public void loadCurrent() {
	if (history.getSize() > 0) {
            FeedItem currentItem = this.getHistory(currentPosition);
            List<Link> links = currentItem.getLinks();
            for (int i=0; i<links.size(); i++) {
                Link aLink = links.get(i);
                String relation = aLink.getRelation();
                if ("via".equals(relation)) {
                    SimpleDocument aDocument = aLink.getLinkedDocument();
                    URL url = aDocument.getUrl();
                    Loader loader = LoaderFactory.getLoader();
                    loader.download(url.toString(), new Target(Library.PRIMARY_FRAME, null));
                    break;
                }
            }
	}
    }
    
    /**
     * 
     * @param doc
     * @return
     */
    public boolean contains(SimpleDocument doc) {
        String url = doc.getUrl().toString();
        return this.contains(url);
    }
    
    /**
     * 
     * @param url
     * @return
     */
    public boolean contains(String url) {
        List<DocumentItem> items = history.getItems();
        for (int i=0; i<items.size(); i++) {
            FeedItem anHistory = (FeedItem)items.get(i);
            List<Link> links = anHistory.getLinks();
            for (int j=0; j<links.size(); j++) {
                Link aLink = links.get(j);
                if (aLink.getRelation().equals("via")) {
                    SimpleDocument aDocument = aLink.getLinkedDocument();
                    String favUrl = aDocument.getUrl().toString();
                    if (url.equals(favUrl.toString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Save the history in a file and wait for the completion of the upload
     */
    public void save() {
	if (isLoaded()) {
            history.setLastBuildDate(new Date());
            try {
                Loader loader = LoaderFactory.getLoader();
                loader.uploadAndWait(history, historyUrl.toString());
            } catch (Exception e) {
                logger.error("error saving history "+historyUrl, e);
            }
	}
    }

    /**
     * Specify if the history is loaded
     * @return true if the loading is in terminated, false otherwise
     */
    public boolean isLoaded() {
	return (history != null);
    }
    
    /**
     * the loading has started, wait.
     * @see org.tramper.loader.LoadingListener#loadingStarted(org.tramper.loader.LoadingEvent)
     */
    public void loadingStarted(LoadingEvent event) {
        //ok, wait
    }
    
    /**
     * get the loaded document 
     * @see org.tramper.loader.LoadingListener#loadingCompleted(org.tramper.loader.LoadingEvent)
     */
    public void loadingCompleted(LoadingEvent event) {
        history = (Feed)event.getLoadedDocument();
        if (UserInterfaceFactory.isGraphicalUserInterfaceInstanciated()) {
            UserInterfaceFactory.getGraphicalUserInterface().clarify();
        }
    }
    
    /**
     * the loading has failed, instanciate a new empty history document 
     * @see org.tramper.loader.LoadingListener#loadingFailed(org.tramper.loader.LoadingEvent)
     */
    public void loadingFailed(LoadingEvent event) {
	logger.warn("history loading failed, make a new one");
        newHistory();
        if (UserInterfaceFactory.isGraphicalUserInterfaceInstanciated()) {
            UserInterfaceFactory.getGraphicalUserInterface().clarify();
        }
    }
    
    /**
     * If this event occurs, someone has stopped the loading. It should be forbidden.
     * @see org.tramper.loader.LoadingListener#loadingStopped(org.tramper.loader.LoadingEvent)
     */
    public void loadingStopped(LoadingEvent event) {
        //it should not occur !
	logger.error("history loading stopped !");
    }
}
