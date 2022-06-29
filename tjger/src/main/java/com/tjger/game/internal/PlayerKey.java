package com.tjger.game.internal;

import com.tjger.game.GamePlayer;


/**
 * Player information to be used as keys.
 * 
 * @author hagru
 */
public class PlayerKey implements Comparable<PlayerKey> {
	
    private String playerName;
    private String playerType;
    
    public PlayerKey(GamePlayer player) {
        this(player.getName(), player.getType().getId());
    }

    public PlayerKey(String playerName, String playerType) {
        this.playerName = playerName;
        this.playerType = playerType;
    }
    /**
     * @return The player's name.
     */
    public String getPlayerName() {
        return playerName;
    }
    /**
     * @return The player's type.
     */
    public String getPlayerType() {
        return playerType;
    }
    @Override
	public String toString() {
        return getPlayerName()+"-"+getPlayerType();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
	public int compareTo(PlayerKey o2) {
        return toString().compareToIgnoreCase(o2.toString());
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
	public boolean equals(Object o2) {
        if (o2 instanceof PlayerKey) {
            return toString().equals(o2.toString());            
        } else return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
	public int hashCode() {
        return toString().hashCode();
    }

}