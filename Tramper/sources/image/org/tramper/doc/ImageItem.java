package org.tramper.doc;

import java.awt.image.BufferedImage;
import java.util.Locale;

/**
 * 
 * @author Paul-Emile
 */
public class ImageItem extends DocumentItem {

    private BufferedImage image;
    
    /**
     * @see org.tramper.doc.SpeakableItem#getText(java.util.Locale)
     */
    @Override
    public String getText(Locale locale) {
	return "";
    }

    /**
     * @return image.
     */
    public BufferedImage getImage() {
        return this.image;
    }

    /**
     * @param image image 
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }

}
