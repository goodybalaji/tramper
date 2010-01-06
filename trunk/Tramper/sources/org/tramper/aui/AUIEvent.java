package org.tramper.aui;

import java.util.EventObject;

import org.tramper.player.Player;

/**
 * 
 * @author Paul-Emile
 */
public class AUIEvent extends EventObject {
    /** AUIEvent.java long */
    private static final long serialVersionUID = -6770708041346880960L;
    /** player */
    private Player player;

    /**
     * 
     * @param source
     */
    public AUIEvent(Object source) {
	super(source);
    }

    /**
     * @return player.
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * @param player player 
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

}
