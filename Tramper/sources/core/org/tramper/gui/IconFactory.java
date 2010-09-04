package org.tramper.gui;

import java.net.URL;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.EnhancedIcon;
import javax.swing.ImageIcon;

/**
 * Icon factory
 * @author Paul-Emile
 */
public class IconFactory {
    
    /**
     * 
     */
    public IconFactory() {
        super();
    }
    
    /**
     * return the icon's url from the document's mime type
     * @param mimeType document's mime type
     * @return url
     */
    public static URL getIconUrlByMimeType(String mimeType) {
        URL iconUrl = null;
        if (mimeType == null) {
            iconUrl = IconFactory.class.getResource("images/File.png");
        }
        else if (mimeType.equals("text/html")) {
            iconUrl = IconFactory.class.getResource("images/Webpage.png");
        }
        else if (mimeType.equals("text/xhtml")) {
            iconUrl = IconFactory.class.getResource("images/Webpage.png");
        }
        else if (mimeType.equals("application/xhtml+xml")) {
            iconUrl = IconFactory.class.getResource("images/Webpage.png");
        }
        else if (mimeType.equals("application/rss+xml")) {
            iconUrl = IconFactory.class.getResource("images/Feed.png");
        }
        else if (mimeType.equals("application/rdf+xml")) {
            iconUrl = IconFactory.class.getResource("images/Feed.png");
        }
        else if (mimeType.equals("application/atom+xml")) {
            iconUrl = IconFactory.class.getResource("images/Feed.png");
        }
        else if (mimeType.equals("text/x-opml")) {
            iconUrl = IconFactory.class.getResource("images/Outline.png");
        }
        else if (mimeType.startsWith("audio")) {
            iconUrl = IconFactory.class.getResource("images/music.png");
        }
        else if (mimeType.equals("application/ogg")) {
            iconUrl = IconFactory.class.getResource("images/music.png");
        }
        else if (mimeType.startsWith("video")) {
            iconUrl = IconFactory.class.getResource("images/video.png");
        }
        else if (mimeType.startsWith("image")) {
            iconUrl = IconFactory.class.getResource("images/image.png");
        } else {
            iconUrl = IconFactory.class.getResource("images/File.png");
        }
        return iconUrl;
    }
    
    /**
     * return the icon corresponding to the document's mime type
     * @param mimeType document's mime type
     * @return icon
     */
    public static Icon getIconByMimeType(String mimeType) {
	URL iconUrl = getIconUrlByMimeType(mimeType);
	Icon icon = new EnhancedIcon(iconUrl);
        return icon;
    }
    
    /**
     * return the flag's icon corresponding to the locale in parameter
     * @param locale
     * @return
     */
    public static ImageIcon getFlagIconByLocale(Locale locale) {
        URL iconUrl = null;
        if (locale.equals(Locale.FRENCH) || locale.equals(Locale.FRANCE)) {
            iconUrl = IconFactory.class.getResource("images/flags/France.png");
        } else if (locale.equals(Locale.ENGLISH) || locale.equals(Locale.US)) {
            iconUrl = IconFactory.class.getResource("images/flags/USA.png");
        } else if (locale.equals(Locale.UK)) {
            iconUrl = IconFactory.class.getResource("images/flags/UK.png");
        } else if (locale.equals(Locale.CANADA)) {
            iconUrl = IconFactory.class.getResource("images/flags/Canada.png");
        } else if (locale.equals(new Locale("en", "AU"))) {
            iconUrl = IconFactory.class.getResource("images/flags/Australia.png");
        } else if (locale.equals(new Locale("es")) || locale.equals(new Locale("es", "ES"))) {
            iconUrl = IconFactory.class.getResource("images/flags/Spain.png");
        } else if (locale.equals(Locale.GERMAN) || locale.equals(Locale.GERMANY)) {
            iconUrl = IconFactory.class.getResource("images/flags/Germany.png");
        } else if (locale.equals(Locale.ITALIAN) || locale.equals(Locale.ITALY)) {
            iconUrl = IconFactory.class.getResource("images/flags/Italy.png");
        } else if (locale.equals(new Locale("pt")) || locale.equals(new Locale("pt", "PT"))) {
            iconUrl = IconFactory.class.getResource("images/flags/Portugal.png");
        } else if (locale.equals(new Locale("pt", "BR"))) {
            iconUrl = IconFactory.class.getResource("images/flags/Brazil.png");
        } else if (locale.equals(Locale.JAPAN) || locale.equals(Locale.JAPANESE)) {
            iconUrl = IconFactory.class.getResource("images/flags/Japan.png");
        } else if (locale.equals(Locale.KOREA) || locale.equals(Locale.KOREAN)) {
            iconUrl = IconFactory.class.getResource("images/flags/South Korea.png");
        } else if (locale.equals(Locale.CHINA) || locale.equals(Locale.CHINESE)) {
            iconUrl = IconFactory.class.getResource("images/flags/China.png");
        } else if (locale.equals(new Locale("fi")) || locale.equals(new Locale("fi", "FI"))) {
            iconUrl = IconFactory.class.getResource("images/flags/Finland.png");
        } else if (locale.equals(new Locale("ru")) || locale.equals(new Locale("ru", "RU"))) {
            iconUrl = IconFactory.class.getResource("images/flags/Russian Federation.png");
        } else if (locale.equals(new Locale("hi")) || locale.equals(new Locale("hi", "IN"))) {
            iconUrl = IconFactory.class.getResource("images/flags/India.png");
        } else {
            return null;
        }
        ImageIcon icon = new EnhancedIcon(iconUrl);
        return icon;
    }
}
