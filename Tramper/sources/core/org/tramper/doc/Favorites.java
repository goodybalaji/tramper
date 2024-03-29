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
 * Manages the favorites in an outline.
 * @author Paul-Emile
 */
public class Favorites implements LoadingListener {
    /** logger */
    private Logger logger = Logger.getLogger(Favorites.class);
    /** the singleton */
    private static Favorites instance;
    /** the favorites */
    private Outline favorites;
    /** Favorites file name */
    private static final String FILENAME = "favorites.opml";
    /** favorites file URL */
    private URL favoritesUrl;
    
    /**
     * Builds the favorite's URL and start to load them.
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
     * Load the singleton from a file, or instantiate new one if failed
     * @return
     */
    public static Favorites getInstance() {
        if (instance == null) {
            instance = new Favorites();
        }
        return instance;
    }
    
    /**
     * Starts the loading of the favorites.
     */
    public void load() {
        try {
            Loader loader = LoaderFactory.getInstance().newLoader();
            loader.addLoadingListener(this);
            loader.download(favoritesUrl.toString(), new Target(Library.SECONDARY_FRAME, null));
        } catch (Exception e) {
            logger.error("can't access the favorites file "+favoritesUrl+", create empty list", e);
            newFavorites();
        }
    }
    
    /**
     * Instantiates a new favorites outline with some default informations.
     */
    public void newFavorites() {
        ResourceBundle label = ResourceBundle.getBundle("label");
        favorites = new Outline();
        favorites.setMimeType("text/x-opml");
        favorites.setTitle(label.getString("favorites.name"));
        String username = System.getProperty("user.name");
        favorites.setAuthor(username);
        favorites.setCreationDate(new Date());
        favorites.setLanguage(Locale.getDefault());
        favorites.setUrl(favoritesUrl);
        
        OutlineItem root = new OutlineItem(favorites);
        favorites.setRoot(root);

        Loader saver = LoaderFactory.getInstance().newLoader();
        saver.initUpload(favorites, favoritesUrl.toString());
        Thread aSavingThread = new Thread((Runnable)saver, "favorites saving");
        Runtime.getRuntime().addShutdownHook(aSavingThread);
    }
    
    /**
     * Adds a document in the favorites.
     * @param document the document to add
     */
    public void addFavorite(SimpleDocument document) {
        if (!isFavorite(document)) {
            OutlineItem newItem = new OutlineItem(favorites);
            String title = document.getTitle();
            newItem.setTitle(title);
            Link aLink = new Link();
            aLink.setRelation("related");
            aLink.setLinkingDocument(favorites);
            aLink.setLinkedDocument(document);
            newItem.addLink(aLink);
            
            String category = document.getFavoriteCategory();
            OutlineItem root = (OutlineItem)favorites.getRoot();
            List<OutlineItem> children = root.getChildren();
            for (OutlineItem child : children) {
        	String childTitle = child.getTitle();
        	if (category.equals(childTitle)) {
        	    child.addChild(newItem);
        	    return;
        	}
            }

            OutlineItem newCategoryItem = new OutlineItem(favorites);
            newCategoryItem.setTitle(document.getFavoriteCategory());
            newCategoryItem.addChild(newItem);
            root.addChild(newCategoryItem);
        }
    }
    
    /**
     * Removes the document in parameter from the favorites.
     * @param document the document to remove
     */
    public void removeFavorite(SimpleDocument document) {
        OutlineItem root = (OutlineItem)favorites.getRoot();
        List<OutlineItem> children = root.getChildren();
        for (OutlineItem child : children) {
            List<OutlineItem> subChildren = child.getChildren();
            for (OutlineItem subChild : subChildren) {
        	List<Link> links = subChild.getLinks();
        	if (links != null && links.size() > 0) {
        	    SimpleDocument linkedDoc = links.get(0).getLinkedDocument();
        	    if (linkedDoc.equals(document)) {
        		child.removeChild(subChild);
        		if (child.isLeaf()) {
        		    root.removeChild(child);
        		}
        		return;
        	    }
        	}
            }
        }
    }
    
    /**
     * Returns the favorites.
     * @return the favorites
     */
    public Outline getFavorites() {
        return this.favorites;
    }
    
    /**
     * Determines if the document in parameter is already a favorite.
     * @param doc the document to look for
     * @return true if it is a favorite, false otherwise
     */
    public boolean isFavorite(SimpleDocument document) {
        OutlineItem root = (OutlineItem)favorites.getRoot();
        List<OutlineItem> children = root.getChildren();
        for (OutlineItem child : children) {
            List<OutlineItem> subChildren = child.getChildren();
            for (OutlineItem subChild : subChildren) {
        	List<Link> links = subChild.getLinks();
        	if (links != null && links.size() > 0) {
        	    SimpleDocument linkedDoc = links.get(0).getLinkedDocument();
        	    if (linkedDoc.equals(document)) {
        		return true;
        	    }
        	}
            }
        }
        return false;
    }
    
    /**
     * Saves the favorites.
     */
    public void save() {
	if (favorites != null) {
            favorites.setModificationDate(new Date());
            try {
                Loader loader = LoaderFactory.getInstance().newLoader();
                loader.upload(favorites, favoritesUrl.toString());
            } catch (Exception e) {
                logger.error("error saving favorites "+favoritesUrl, e);
            }
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
        favorites = (Outline)event.getLoadedDocument();

        Loader saver = LoaderFactory.getInstance().newLoader();
        saver.initUpload(favorites, favoritesUrl.toString());
        Thread aSavingThread = new Thread((Runnable)saver, "favorites saving");
        Runtime.getRuntime().addShutdownHook(aSavingThread);
    }
    
    /**
     * the loading has failed, instantiate a new empty favorites document 
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
	logger.error("favorites loading stopped!");
        newFavorites();
    }
}
