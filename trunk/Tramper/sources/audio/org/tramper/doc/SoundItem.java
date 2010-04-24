package org.tramper.doc;

import java.net.URL;
import java.util.Locale;

/**
 * 
 * @author Paul-Emile
 */
public class SoundItem extends DocumentItem {

    private URL url;
    
    /**
     * @see org.tramper.doc.SpeakableItem#getText(java.util.Locale)
     */
    @Override
    public String getText(Locale locale) {
	return "";
    }

    /**
     * @return url.
     */
    public URL getUrl() {
        return this.url;
    }

    /**
     * @param url url 
     */
    public void setUrl(URL url) {
        this.url = url;
    }

}
