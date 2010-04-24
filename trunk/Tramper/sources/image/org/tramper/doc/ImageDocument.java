package org.tramper.doc;

import java.awt.image.BufferedImage;

/**
 * an image
 * @author Paul-Emile
 */
public class ImageDocument extends SimpleDocument {
    /** image width */
    protected int width;
    /** image height */
    protected int height;
    /** image */
    protected BufferedImage image;
    
    /**
     * 
     */
    public ImageDocument() {
        super();
    }
    
    /**
     * @return height.
     */
    public int getHeight() {
        return this.height;
    }
    /**
     * @param height height 
     */
    public void setHeight(int height) {
        this.height = height;
    }
    /**
     * @return width.
     */
    public int getWidth() {
        return this.width;
    }
    /**
     * @param width width 
     */
    public void setWidth(int width) {
        this.width = width;
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

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "image";
    }
}
