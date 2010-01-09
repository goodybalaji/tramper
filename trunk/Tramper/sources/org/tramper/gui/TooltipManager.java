package org.tramper.gui;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Paul-Emile
 * 
 */
public class TooltipManager {
    /**
     * Returns the tooltip associated to the action in parameter.
     * @param action
     * @return
     */
    public static String createTooltip(String action) {
	ResourceBundle label = ResourceBundle.getBundle("label");
        String tooltipPattern = label.getString("tooltip");
        MessageFormat tooltipFormat = new MessageFormat(tooltipPattern);
        
	String name = label.getString(action+".name");
	String description = label.getString(action+".desc");
	String keys = label.getString(action+".keys");
	
	Locale recognizerLocale = Locale.getDefault();
	
	label = ResourceBundle.getBundle("label", recognizerLocale);
	String speechCommand = label.getString(action+".speech");
	Object[] arguments = new Object[] {name, description, keys, speechCommand};
	String tooltip = tooltipFormat.format(arguments);
	return tooltip;
    }
}
