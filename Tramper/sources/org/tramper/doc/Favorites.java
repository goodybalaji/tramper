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

/**
 * Load and save the favorite documents
 * @author Paul-Emile
 */
public class Favorites implements LoadingListener {
    /** logger */
    private Logger logger = Logger.getLogger(Favorites.class);
    /** the singleton */
    private static Favorites instance;
    /** the favorites */
    private Feed favorites;
    /** Favorites file name */
    private static final String FILENAME = "favorites.atom";
    /** favorites file url */
    private URL favoritesUrl;
    
    /**
     * private constructor
     */
    private Favorites() {
        String userHome = System.getProperty("user.home");
        String sep = System.getProperty("file.separator");
        String filepath = "file://" + userHome + sep + FILENAME;
        try {
            favoritesUrl = new URL(filepath);
        } catch (MalformedURLException e) {
            logger.error("wrong favorites url: "+filepath);
            throw new RuntimeException("wrong favorites url: "+filepath);
        }
        load();
    }
    
    /**
     * Load the singleton from a file, or instanciate new one if failed
     * @return
     */
    public static Favorites getInstance() {
        if (instance == null) {
            instance = new Favorites();
        }
        return instance;
    }
    
    /**
     * load the favorites from a file and start their loading
     */
    public void load() {
        try {
            Loader loader = LoaderFactory.getLoader();
            loader.addLoadingListener(this);
            loader.download(favoritesUrl.toString(), new Target(Library.SECONDARY_FRAME, null));
        }
        catch (Exception e) {
            logger.error("can't access the favorites file "+favoritesUrl+", create empty list", e);
            newFavorites();
        }
    }
    
    /**
     * instanciate a new favorites feed with some default informations
     */
    public void newFavorites() {
        ResourceBundle label = ResourceBundle.getBundle("label");
        favorites = new Feed();
        favorites.setMimeType("application/atom+xml");
        favorites.setTitle(label.getString("favorites.name"));
        favorites.setDescription(label.getString("javaspeaker.productTitle"));
        String username = System.getProperty("user.name");
        favorites.setAuthor(username);
        favorites.setCreationDate(new Date());
        favorites.setLanguage(Locale.getDefault());
        favorites.setUrl(favoritesUrl);
    }
    
    /**
     * add a document in the list of favorites
     * @param document
     */
    public void addFavorite(SimpleDocument document) {
        if (!isFavorite(document)) {
            FeedItem newItem = new FeedItem();
            String title = document.getTitle();
            newItem.setTitle(title);
            newItem.setDescription(document.getDescription());
            Link aLink = new Link();
            aLink.setRelation("via");
            aLink.setLinkingDocument(favorites);
            aLink.setLinkedDocument(document);
            newItem.addLink(aLink);
            favorites.addItem(newItem);
        }
    }
    
    /**
     * remove a favorite by it's index in the list 
     * @param i
     */
    public void removeFavorite(int i) {
        favorites.removeItem(i);
    }
    
    /**
     * remove the favorite from the list
     * @param document
     */
    public void removeFavorite(SimpleDocument document) {
        String url = document.getUrl().toString();
        List<DocumentItem> items = favorites.getItems();
        for (int i=0; i<items.size(); i++) {
            FeedItem aFavorite = (FeedItem)items.get(i);
            List<Link> links = aFavorite.getLinks();
            for (int j=0; j<links.size(); j++) {
                Link aLink = links.get(j);
                if (aLink.getRelation().equals("via")) {
                    SimpleDocument aDocument = aLink.getLinkedDocument();
                    URL favUrl = aDocument.getUrl();
                    if (url.equals(favUrl.toString())) {
                        favorites.removeItem(i);
                        return;
                    }
                }
            }
        }
    }
    
    /**
     * return all the favorites
     * @return
     */
    public Feed getFavorites() {
        return this.favorites;
    }
    
    /**
     * return one of the favorites
     * @param index index of the requested favorite
     * @return the item
     */
    public FeedItem getFavorite(int index) {
        FeedItem item = favorites.getItem(index);
        return item;
    }
    
    /**
     * 
     * @param doc
     * @return
     */
    public boolean isFavorite(SimpleDocument doc) {
        String url = doc.getUrl().toString();
        List<DocumentItem> items = favorites.getItems();
        for (int i=0; i<items.size(); i++) {
            FeedItem aFavorite = (FeedItem)items.get(i);
            List<Link> links = aFavorite.getLinks();
            for (int j=0; j<links.size(); j++) {
                Link aLink = links.get(j);
                if (aLink.getRelation().equals("via")) {
                    SimpleDocument aDocument = aLink.getLinkedDocument();
                    URL favUrl = aDocument.getUrl();
                    if (url.equals(favUrl.toString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Save the favorites in a file
     */
    public void save() {
        favorites.setLastBuildDate(new Date());
        try {
            Loader loader = LoaderFactory.getLoader();
            loader.upload(favorites, favoritesUrl.toString());
        }
        catch (Exception e) {
            logger.error("error saving favorites "+favoritesUrl, e);
        }
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
        favorites = (Feed)event.getLoadedDocument();
    }
    
    /**
     * the loading has failed, instanciate a new empty favorites document 
     * @see org.tramper.loader.LoadingListener#loadingFailed(org.tramper.loader.LoadingEvent)
     */
    public void loadingFailed(LoadingEvent event) {
	logger.warn("favorites loading failed, make a new one");
        newFavorites();
    }
    
    /**
     * If this event occurs, someone has stopped the loading. It should be forbidden.
     * @see org.tramper.loader.LoadingListener#loadingStopped(org.tramper.loader.LoadingEvent)
     */
    public void loadingStopped(LoadingEvent event) {
        //it should not occur !
	logger.error("favorites loading stopped !");
    }
}
