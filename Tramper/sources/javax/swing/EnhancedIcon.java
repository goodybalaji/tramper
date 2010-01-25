package javax.swing;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

/**
 * Scalable, animated and decorated icon.
 * @author Paul-Emile 
 */
public class EnhancedIcon extends ImageIcon implements Runnable {
    /** EnhancedIcon.java long */
    private static final long serialVersionUID = -7744881033405044029L;
    /** logger */
    private Logger logger = Logger.getLogger(EnhancedIcon.class);
    /** image ratio */
    protected float ratio;
    /** flag indicating the icon is animated or not */
    protected boolean animated;
    /** a counter for the animation */
    protected float counter = 0;
    /** thread for the animation */
    protected Thread aRunning;
    /** North-west decoration icon */
    protected ImageIcon northWestDecorationIcon;
    /** North-east decoration icon */
    protected ImageIcon northEastDecorationIcon;
    /** South-west decoration icon */
    protected ImageIcon southWestDecorationIcon;
    /** South-east decoration icon */
    protected ImageIcon southEastDecorationIcon;

    /**
     * 
     */
    public EnhancedIcon() {
	super();
    }

    /**
     * @param imageData
     * @param description
     */
    public EnhancedIcon(byte[] imageData, String description) {
	super(imageData, description);
    }

    /**
     * @param imageData
     */
    public EnhancedIcon(byte[] imageData) {
	super(imageData);
    }

    /**
     * @param image
     * @param description
     */
    public EnhancedIcon(Image image, String description) {
	super(image, description);
    }

    /**
     * @param image
     */
    public EnhancedIcon(Image image) {
	super(image);
    }

    /**
     * @param filename
     * @param description
     */
    public EnhancedIcon(String filename, String description) {
	super(filename, description);
    }

    /**
     * @param filename
     */
    public EnhancedIcon(String filename) {
	super(filename);
    }

    /**
     * @param location
     * @param description
     */
    public EnhancedIcon(URL location, String description) {
	super(location, description);
    }

    /**
     * @param location
     */
    public EnhancedIcon(URL location) {
	super(location);
    }

    /**
     * @see javax.swing.ImageIcon#paintIcon(java.awt.Component,
     * java.awt.Graphics, int, int)
     */
    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
	Float iconScale = (Float)UIManager.get("EnhancedIcon.scale");
	if (iconScale == null) {
	    iconScale = Float.valueOf(1);
	}
	int w = super.getIconWidth();
	int h = super.getIconHeight();
	Image i = this.getImage();
	ImageObserver imgObs = this.getImageObserver();
	ratio = (float)w / (float)h;
	Graphics2D g2d = (Graphics2D) g;
	
	if (animated) {
	    AlphaComposite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, counter);
	    g2d.setComposite(comp);
	}
	
	if (imgObs == null) {
	    this.setImageObserver(c);
	    imgObs = c;
	}
	
	int iconWidth = (int)(w*iconScale);
	int iconHeight = (int)(h*iconScale);
	g2d.drawImage(i, x, y, iconWidth, iconHeight, imgObs);
	
	if (northWestDecorationIcon != null) {
	    g2d.drawImage(northWestDecorationIcon.getImage(), x, y, iconWidth/2, iconHeight/2, imgObs);
	}
	if (northEastDecorationIcon != null) {
	    g2d.drawImage(northEastDecorationIcon.getImage(), x + iconWidth/2, y, iconWidth/2, iconHeight/2, imgObs);
	}
	if (southWestDecorationIcon != null) {
	    g2d.drawImage(southWestDecorationIcon.getImage(), x, y + iconHeight/2, iconWidth/2, iconHeight/2, imgObs);
	}
	if (southEastDecorationIcon != null) {
	    g2d.drawImage(southEastDecorationIcon.getImage(), x + iconWidth/2, y + iconHeight/2, iconWidth/2, iconHeight/2, imgObs);
	}
    }

    /**
     * @see javax.swing.ImageIcon#getIconHeight()
     */
    @Override
    public int getIconHeight() {
	Float iconScale = (Float)UIManager.get("EnhancedIcon.scale");
	if (iconScale == null) {
	    iconScale = Float.valueOf(1);
	}
	int h = super.getIconHeight();
	return (int)(h*iconScale);
    }

    /**
     * @see javax.swing.ImageIcon#getIconWidth()
     */
    @Override
    public int getIconWidth() {
	Float iconScale = (Float)UIManager.get("EnhancedIcon.scale");
	if (iconScale == null) {
	    iconScale = Float.valueOf(1);
	}
	int w = super.getIconWidth();
	return (int)(w*iconScale);
    }

    /**
     * draw an animation when running
     */
    public void run() {
	boolean increment = true;
	while (animated) {
	    try {
		Thread.sleep(50);
	    } catch (InterruptedException e) {
		logger.warn("slepping thread interrupted");
	    }

	    if (increment) {
		counter += 0.1;
	    } else {
		counter -= 0.1;
	    }
	    if (counter >= 1) {
		increment = false;
		counter = 1;
	    } else if (counter <= 0.2) {
		increment = true;
		counter = 0.2f;
	    }
	    this.getImageObserver().imageUpdate(getImage(), ImageObserver.FRAMEBITS, 0, 0, getIconWidth(), getIconHeight());
	}
    }

    /**
     * return the flag indicating if the icon is animated or not
     * @return animated.
     */
    public boolean isAnimated() {
	return animated;
    }

    /**
     * Set if the icon is animated or not
     * @param animated
     */
    public void setAnimated(boolean animated) {
	this.animated = animated;

	if (animated) {
	    if (aRunning == null || !aRunning.isAlive()) {
		aRunning = new Thread(this);
		aRunning.start();
	    }
	}
    }

    /**
     * Add a decoration icon upon this icon to one of the 4 corners. 
     * You can set the icon to null to remove the decoration at this place.
     * @param decorationIcon the decoration icon
     * @param location one of the following constants from SwingConstants:
     * NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST
     */
    public void addDecorationIcon(ImageIcon decorationIcon, int location) {
	if (location == SwingConstants.NORTH_WEST) {
	    northWestDecorationIcon = decorationIcon;
	} else if (location == SwingConstants.NORTH_EAST) {
	    northEastDecorationIcon = decorationIcon;
	} else if (location == SwingConstants.SOUTH_WEST) {
	    southWestDecorationIcon = decorationIcon;
	} else if (location == SwingConstants.SOUTH_EAST) {
	    southEastDecorationIcon = decorationIcon;
	}
	ImageObserver imgObs = this.getImageObserver();
	if (imgObs != null) {
	    imgObs.imageUpdate(getImage(), ImageObserver.FRAMEBITS, 0, 0, getIconWidth(), getIconHeight());
	}
    }
    
    /**
     * 
     */
    public void removeAllDecorationIcons() {
	northWestDecorationIcon = null;
	northEastDecorationIcon = null;
	southWestDecorationIcon = null;
	southEastDecorationIcon = null;

	ImageObserver imgObs = this.getImageObserver();
	if (imgObs != null) {
	    imgObs.imageUpdate(getImage(), ImageObserver.FRAMEBITS, 0, 0, getIconWidth(), getIconHeight());
	}
    }
    
    /**
     * 
     * @return
     */
    public Icon toGray() {
	Image grayImage = GrayFilter.createDisabledImage(this.getImage());
        return new EnhancedIcon(grayImage);
    }
}
