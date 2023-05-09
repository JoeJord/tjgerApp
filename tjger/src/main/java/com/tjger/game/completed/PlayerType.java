package com.tjger.game.completed;

import com.tjger.game.internal.PlayerFactory;
import com.tjger.lib.ArrayUtil;

import android.graphics.Bitmap;
import at.hagru.hgbase.lib.HGBaseItem;
import at.hagru.hgbase.lib.HGBaseText;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * The player type.
 * 
 * @author hagru
 */
public class PlayerType implements HGBaseItem {

    final private String playerType;
    final private Bitmap typeImage;
    /**
     * Flag if this player type is only available in the pro version but should be shown in the free version as teaser for the pro version.
     */
    private final boolean proTeaser;

    public PlayerType(String playerType, Bitmap typeImage, boolean proTeaser) {
        super();
        this.playerType = playerType;
        this.typeImage = typeImage;
        this.proTeaser = proTeaser;
    }
    
    /**
     * @return The id of this player type.
     */
    @Override
	public String getId() {
        return playerType;
    }
    
    /**
     * @return The image for this player type, can be null.
     */
    public Bitmap getImage() {
        return typeImage;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        return HGBaseText.getText(getId());
    }
    
    /**
     * @return True, if it's a human player type.
     */
    public boolean isHuman() {
        String[] hp = ArrayUtil.toPlayerTypeIds(PlayerFactory.getInstance().getHumanPlayerTypes()); 
        return (HGBaseTools.getIndexOf(hp, getId())>=0);        
    }
    
    /**
     * @return True, if it's a network player type. 
     */
    public boolean isNetwork() {
        String[] np = ArrayUtil.toPlayerTypeIds(PlayerFactory.getInstance().getNetworkPlayerTypes()); 
        return (HGBaseTools.getIndexOf(np, getId())>=0);        
    }
    
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
	public boolean equals(Object o2) {
        if (o2 instanceof PlayerType) {
            return getId().equals(((PlayerType)o2).getId());            
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
	public int hashCode() {
        return getId().hashCode();
    }

    /**
     * Returns {@code true} if this player type is only available in the pro version but should be shown in the free version as teaser for the pro version.
     *
     * @return {@code true} if this player type is only available in the pro version but should be shown in the free version as teaser for the pro version.
     */
    public boolean isProTeaser() {
        return proTeaser;
    }

    /**
     * Returns {@code true} if the specified player type is only available in the pro version but should be shown in the free version as teaser for the pro version.
     *
     * @param playerType The player type to check.
     * @return {@code true} if the specified player type is only available in the pro version but should be shown in the free version as teaser for the pro version.
     */
    public static boolean isProTeaser(PlayerType playerType) {
        if (playerType == null) {
            return false;
        }
        return playerType.isProTeaser();
    }
}
