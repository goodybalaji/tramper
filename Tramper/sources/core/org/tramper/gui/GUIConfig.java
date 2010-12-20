package org.tramper.gui;

import java.awt.Frame;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JSplitPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

/**
 * Graphical user interface configuration
 * @author Paul-Emile
 */
public class GUIConfig implements Runnable {
    /** logger */
    private Logger logger = Logger.getLogger(GUIConfig.class);
    /** GUI configuration properties */
    private Properties prop;
    /** Properties file pathname */
    private String propFile;
    
    /**
     * 
     */
    public GUIConfig() {
        super();
        prop = new Properties();
        String userDir = System.getProperty("user.home");
        String sep = System.getProperty("file.separator");
        propFile = userDir + sep + "GUIConfig.properties";
	this.load();
	
	Thread guiConfigSaver = new Thread(this, "GUI config saver");
	Runtime.getRuntime().addShutdownHook(guiConfigSaver);
    }
    
    /**
     * Load GUI configuration from a properties file
     */
    public void load() {
	FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(propFile);
            prop.load(inStream);
        } catch (IOException e) {
            logger.debug("unable to load the GUI config, use default values then");
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    logger.warn("load() : closing stream reading the config file failed");
                }
            }
        }
    }

    /**
     * get the window's extended state 
     * @return last extended state or default
     */
    public int getWindowExtendedState() {
	String windowState = prop.getProperty("window.extendedState");
	if (windowState == null) {
	    windowState = String.valueOf(Frame.NORMAL);
	}
        try {
            return Integer.parseInt(windowState);
        }
        catch (NumberFormatException e) {
            logger.error("the window.extendedState property is not an integer : "+e.getMessage());
            return Frame.NORMAL;
        }
    }
    
    /**
     * set the window's extended state
     * @param windowExtendedState
     */
    public void setWindowExtendedState(int windowExtendedState) {
        prop.setProperty("window.extendedState", String.valueOf(windowExtendedState));
    }

    /**
     * get the window's x coordinate 
     * @return last x or default
     */
    public int getWindowX() {
	String windowX = prop.getProperty("window.x");
	if (windowX == null) {
	    windowX = "0";
	}
        try {
            return Integer.parseInt(windowX);
        }
        catch (NumberFormatException e) {
            logger.error("the window.x property is not an integer : "+e.getMessage());
            return 0;
        }
    }
    
    /**
     * set the window's x coordinate
     * @param windowX
     */
    public void setWindowX(int windowX) {
        prop.setProperty("window.x", String.valueOf(windowX));
    }

    /**
     * get the window's y coordinate 
     * @return last y or default
     */
    public int getWindowY() {
	String windowY = prop.getProperty("window.y");
	if (windowY == null) {
	    windowY = "0";
	}
        try {
            return Integer.parseInt(windowY);
        }
        catch (NumberFormatException e) {
            logger.error("the window.y property is not an integer : "+e.getMessage());
            return 0;
        }
    }
    
    /**
     * set the window's y coordinate
     * @param windowY
     */
    public void setWindowY(int windowY) {
        prop.setProperty("window.y", String.valueOf(windowY));
    }

    /**
     * get the window's width
     * @return last width or default
     */
    public int getWindowWidth() {
	String windowWidth = prop.getProperty("window.width");
	if (windowWidth == null) {
	    windowWidth = "800";
	}
        try {
            return Integer.parseInt(windowWidth);
        }
        catch (NumberFormatException e) {
            logger.error("the window.width property is not an integer : "+e.getMessage());
            return 800;
        }
    }
    
    /**
     * set the window's width
     * @param windowWidth 
     */
    public void setWindowWidth(int windowWidth) {
        prop.setProperty("window.width", String.valueOf(windowWidth));
    }

    /**
     * get the window's height
     * @return last height or default
     */
    public int getWindowHeight() {
	String windowHeight = prop.getProperty("window.height");
	if (windowHeight == null) {
	    windowHeight = "600";
	}
        try {
            return Integer.parseInt(windowHeight);
        }
        catch (NumberFormatException e) {
            logger.error("the window.height property is not an integer : "+e.getMessage());
            return 600;
        }
    }
    
    /**
     * set the window's height
     * @param windowHeight 
     */
    public void setWindowHeight(int windowHeight) {
        prop.setProperty("window.height", String.valueOf(windowHeight));
    }

    /**
     * get the window's look and feel
     * @return last look and feel
     */
    public String getLookAndFeel() {
	String lookAndFeel = prop.getProperty("lookAndFeel");
	if (lookAndFeel == null) {
	    lookAndFeel = UIManager.getSystemLookAndFeelClassName();
	}
        return lookAndFeel;
    }
    
    /**
     * set the window's look and feel
     * @param windowLaF 
     */
    public void setLookAndFeel(String windowLaF) {
        prop.setProperty("lookAndFeel", windowLaF);
    }

    /**
     * get the window's address panel flag
     * @return last flag or default false
     */
    public boolean getAddressPanel() {
	String addressPanel = prop.getProperty("addressPanel");
	if (addressPanel == null) {
	    addressPanel = "true";
	}
        return Boolean.valueOf(addressPanel).booleanValue();
    }

    /**
     * set the window's address panel flag
     * @param windowAddressPanel 
     */
    public void setAddressPanel(boolean windowAddressPanel) {
        prop.setProperty("addressPanel", String.valueOf(windowAddressPanel));
    }

    /**
     * get the window's player panel flag
     * @return last flag or default false
     */
    public boolean getPlayerPanel() {
	String playerPanel = prop.getProperty("playerPanel");
	if (playerPanel == null) {
	    playerPanel = "true";
	}
        return Boolean.valueOf(playerPanel).booleanValue();
    }

    /**
     * set the window's player panel flag
     * @param windowReaderPanel 
     */
    public void setPlayerPanel(boolean windowReaderPanel) {
        prop.setProperty("playerPanel", String.valueOf(windowReaderPanel));
    }

    /**
     * get the window's recognizer panel flag
     * @return last flag or default false
     */
    public boolean getRecognizerPanel() {
	String recognizerPanel = prop.getProperty("recognizerPanel");
	if (recognizerPanel == null) {
	    recognizerPanel = "false";
	}
        return Boolean.valueOf(recognizerPanel).booleanValue();
    }
    
    /**
     * set the window's recognizer panel flag
     * @param windowRecognizerPanel 
     */
    public void setRecognizerPanel(boolean windowRecognizerPanel) {
        prop.setProperty("recognizerPanel", String.valueOf(windowRecognizerPanel));
    }

    /**
     * get the window's display panel flag
     * @return last flag or default false
     */
    public boolean getDisplayPanel() {
	String displayPanel = prop.getProperty("displayPanel");
	if (displayPanel == null) {
	    displayPanel = "false";
	}
        return Boolean.valueOf(displayPanel).booleanValue();
    }
    
    /**
     * set the window's display panel flag
     * @param windowDisplayPanel 
     */
    public void setDisplayPanel(boolean windowDisplayPanel) {
        prop.setProperty("displayPanel", String.valueOf(windowDisplayPanel));
    }

    /**
     * get the window's scale
     * @return current scale
     */
    public float getScale() {
	String scale = prop.getProperty("scale");
	if (scale == null) {
	    scale = "1";
	}
	try {
	    return Float.parseFloat(scale);
	} catch (NumberFormatException e) {
	    return 1;
	}
    }
    
    /**
     * set the window's scale
     * @param scale 
     */
    public void setScale(float scale) {
        prop.setProperty("scale", String.valueOf(scale));
    }

    /**
     * get the window's locale
     * @return current locale
     */
    public Locale getLocale() {
	String language = prop.getProperty("language");
	String country = prop.getProperty("country");
	if (language != null && country != null) {
	    return new Locale(language, country);
	} else {
	    return Locale.getDefault();
	}
    }
    
    /**
     * set the window's locale
     * @param windowLocale 
     */
    public void setLocale(Locale windowLocale) {
        prop.setProperty("language", windowLocale.getLanguage());
        prop.setProperty("country", windowLocale.getCountry());
    }

    /**
     * get the split pane orientation 
     * @return orientation
     */
    public int getOrientation() {
	String orientation = prop.getProperty("orientation");
	if (orientation == null) {
	    orientation = String.valueOf(JSplitPane.HORIZONTAL_SPLIT);
	}
        try {
            return Integer.parseInt(orientation);
        }
        catch (NumberFormatException e) {
            logger.error("the orientation property is not an integer : "+e.getMessage());
            return JSplitPane.HORIZONTAL_SPLIT;
        }
    }
    
    /**
     * set the split pane orientation
     * @param orientation
     */
    public void setOrientation(int orientation) {
        prop.setProperty("orientation", String.valueOf(orientation));
    }

    /**
     * 
     */
    public void save() {
	Thread saver = new Thread(this, "GUI config saver");
	saver.start();
    }

    public void run() {
	LookAndFeel laf = UIManager.getLookAndFeel();
	this.setLookAndFeel(laf.getClass().getName());

	float scale = (float)ScaleBoundedRangeModel.getInstance().getValue()/(float)100;
        this.setScale(scale);
	this.setLocale(Locale.getDefault());
        
	FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(propFile);
            prop.store(outStream, "Tramper configuration");
        } catch (IOException e) {
            logger.error("unable to save the GUI config", e);
        } finally {
            try {
        	outStream.close();
            } catch (IOException e) {
        	logger.warn("save() : closing stream writing config file failed");
            }
        }
    }
}
