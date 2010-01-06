package org.tramper.browser;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import org.apache.log4j.Logger;


/**
 * A search engine factory
 * @author Paul-Emile
 */
public class SearchEngineFactory implements ItemListener {
    /** logger */
    private static Logger logger = Logger.getLogger(SearchEngineFactory.class);
    /** list of the available search engine */
    private Vector<SearchEngine> searchEngines = new Vector<SearchEngine>();
    /** singleton */
    private static SearchEngineFactory instance;
    /** selected engine */
    private SearchEngine selectedSearchEngine;
    
    /**
     * 
     * @return
     */
    public static SearchEngineFactory getInstance() {
	if (instance == null) {
	    instance = new SearchEngineFactory();
	}
	return instance;
    }
    
    /**
     * 
     */
    private SearchEngineFactory() {
        super();
        ResourceBundle engineDefinitions = ResourceBundle.getBundle("org.tramper.browser.searchengine");
        String[] searchEngineName = engineDefinitions.getString("list").split(",");
        for (int i=0; i<searchEngineName.length; i++) {
            try {
                String name = engineDefinitions.getString(searchEngineName[i]+".name");
                String logo = engineDefinitions.getString(searchEngineName[i]+".logo");
                String researchUrl = engineDefinitions.getString(searchEngineName[i]+".researchurl");
                String keywordSeparator = engineDefinitions.getString(searchEngineName[i]+".keywordseparator");
                if (keywordSeparator == null || keywordSeparator.equals("")) {
                    keywordSeparator = " ";
                }
                SearchEngine engine = new SearchEngine();
                engine.setName(name);
                engine.setLogo(logo);
                engine.setResearchUrlPattern(researchUrl);
                engine.setKeywordSeparator(keywordSeparator);
                searchEngines.add(engine);
            } catch (MissingResourceException e) {
                logger.error("one of the resource is missing, we can't load the search engine : "+e.getMessage());
            }
        }
        if (searchEngines.size() > 0) {
            selectedSearchEngine = searchEngines.get(0);
        }
    }
    
    /**
     * Load the list of requestable search engines from a file
     * @return vector of SearchEngine objects
     */
    public Vector<SearchEngine> getSearchEngines() {
        return searchEngines;
    }

    /**
     * Returns the selected search engine in the address bar.
     * @return
     */
    public SearchEngine getSelectedSearchEngine() {
	return selectedSearchEngine;
    }
    
    /**
     * Listens to the search engine combobox in the address bar.
     * @param e 
     */
    public void itemStateChanged(ItemEvent e) {
	int stateChange = e.getStateChange();
	if (stateChange == ItemEvent.SELECTED) {
	    selectedSearchEngine = (SearchEngine)e.getItem();
	}
    }
}
