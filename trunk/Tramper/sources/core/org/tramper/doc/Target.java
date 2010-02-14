package org.tramper.doc;

/**
 * A place where to load a document. Defines a frame and a tab. 
 * @author Paul-Emile
 */
public class Target implements Comparable<Target> {
    /** a frame in the GUI */
    private String frame;
    /** a tab in the frame */
    private String tab;
    
    /**
     * 
     * @param frame
     * @param tab
     */
    public Target(String frame, String tab) {
	this.frame = frame;
	this.tab = tab;
    }

    /**
     * @return frame.
     */
    public String getFrame() {
        return this.frame;
    }

    /**
     * @param frame frame 
     */
    public void setFrame(String frame) {
        this.frame = frame;
    }

    /**
     * @return tab.
     */
    public String getTab() {
        return this.tab;
    }

    /**
     * @param tab tab 
     */
    public void setTab(String tab) {
        this.tab = tab;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (obj instanceof Target) {
	    Target objTarget = (Target)obj;
	    String objFrame = objTarget.getFrame();
	    String objTab = objTarget.getTab();
	    if (frame == null) {
		if (objFrame == null) {
		    if (tab == null) {
			if (objTab == null) {
			    return true;
			} else {
			    return false;
			}
		    } else {
			if (tab.equals(objTab)) {
			    return true;
			} else {
			    return false;
			}
		    }
		} else {
		    return false;
		}
	    } else {
		if (frame.equals(objFrame)) {
		    if (tab == null) {
			if (objTab == null) {
			    return true;
			} else {
			    return false;
			}
		    } else {
			if (tab.equals(objTab)) {
			    return true;
			} else {
			    return false;
			}
		    }
		} else {
		    return false;
		}
	    }
	} else {
	    return false;
	}
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	return frame.hashCode() + tab.hashCode();
    }

    /**
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Target toCompare) {
	if (toCompare == null) {
	    return 1;
	}
	String compFrame = toCompare.getFrame();
	int compFrameId = Integer.valueOf(compFrame);
	int frameId = Integer.valueOf(frame);
	if (frameId > compFrameId) {
	    return 1;
	} else if (frameId < compFrameId) {
	    return -1;
	}
	
	String compTab = toCompare.getTab();
	int compTabId = Integer.valueOf(compTab);
	int tabId = Integer.valueOf(tab);
	if (tabId > compTabId) {
	    return 1;
	} else if (tabId < compTabId) {
	    return -1;
	} else {
	    return 0;
	}
    } 
}
