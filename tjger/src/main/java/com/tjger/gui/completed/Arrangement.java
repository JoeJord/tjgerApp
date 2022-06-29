package com.tjger.gui.completed;

import java.util.LinkedHashMap;
import java.util.Map;

import com.tjger.game.completed.GameConfig;
import com.tjger.lib.ConstantValue;

import at.hagru.hgbase.android.awt.Color;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * Contains a standard arrangement with the defined parts.
 *
 * @author hagru
 */
public class Arrangement extends Part {

    final private Color backgroundColor;
    final private String background;
    final private String board;
    final private String cover;
    final private String pieceSet;
    final private Map<String,String> mapCardSets = new LinkedHashMap<>();
    final private Map<String,String> mapParts = new LinkedHashMap<>();
    final private Map<String,String> mapPartSets = new LinkedHashMap<>();
    final private Map<String,Color> mapColors = new LinkedHashMap<>();

    public Arrangement(String name, Color defaultBackColor, String defaultBack, String defaultBoard, String defaultPieceSet, String defaultCover, String cardSet) {
        super(ConstantValue.CONFIG_ARRANGEMENT, name, null, false);
        this.backgroundColor = defaultBackColor;
        this.background = defaultBack;
        this.board = defaultBoard;
        this.pieceSet = defaultPieceSet;
        this.cover = defaultCover;
        if (HGBaseTools.hasContent(cardSet)) {
            setCardSet(ConstantValue.CONFIG_CARDSET, cardSet);
        }
    }

    /* (non-Javadoc)
     * @see tjger.gui.completed.Part#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o2) {
        return (HGBaseTools.equalClass(this, o2) && this.toString().equals(o2.toString()));
    }
    
    /**
     * @return the background color defined for the arrangement, or the default background color or null
     */
    public Color getBackgroundColor() {
        if (backgroundColor == null) {
        	BackgroundColor backColor = GameConfig.getInstance().getBackgroundColor();
        	return (backColor == null) ? null : backColor.getDefaultColor();
        } else {
            return backgroundColor;
        }
    }
    
    /**
     * @return The default background or null.
     */
    public Background getBackground() {
        return GameConfig.getInstance().getBackground(this.background);
    }

    /**
     * @return The default baord or null.
     */
    public Board getBoard() {
        return GameConfig.getInstance().getBoard(this.board);
    }

    /**
     * @return The default piece set or null.
     */
    public PieceSet getPieceSet() {
        return GameConfig.getInstance().getPieceSet(this.pieceSet);
    }

    /**
     * @return The default cover or null.
     */
    public Cover getCover() {
        return GameConfig.getInstance().getCover(this.cover);
    }

    /**
     * @return The default card set or null.
     */
    public CardSet getCardSet() {
        return GameConfig.getInstance().getCardSet(ConstantValue.CONFIG_CARDSET);
    }

    /**
     * @param type The type name of the card set.
     * @return The card set or null.
     */
    public CardSet getCardSet(String type) {
        return GameConfig.getInstance().getCardSet(type, mapCardSets.get(type));
    }

    /**
     * Sets a arrangement for a card set.
     *
     * @param type The card set type.
     * @param name Name of the card set for this arrangement.
     */
    public void setCardSet(String type, String name) {
        mapCardSets.put(type, name);
    }

    /**
     * Sets a arrangement for a user defined part.
     *
     * @param type The user defined part type.
     * @param name Name of the part for this arrangement.
     */
    public void setPart(String type, String name) {
        mapParts.put(type, name);
    }

    /**
     * @param type A user defined part.
     * @return The part of this arrangement for the given type or null.
     */
    public Part getPart(String type) {
        return GameConfig.getInstance().getPart(type, mapParts.get(type));
    }

    /**
     * Sets a arrangement for a user defined part set.
     *
     * @param type The user defined part set type.
     * @param name Name of the part set for this arrangement.
     */
    public void setPartSet(String type, String name) {
        mapPartSets.put(type, name);
    }

    /**
     * @param type A user defined part set.
     * @return The part set of this arrangement for the given type or null.
     */
    public PartSet getPartSet(String type) {
        return GameConfig.getInstance().getPartSet(type, mapPartSets.get(type));
    }

    /**
     * @param colorType A color type.
     * @param color The color for this arrangement.
     */
    public void setColor(String colorType, Color color) {
        mapColors.put(colorType, color);
    }

    /**
     * @param colorType
     * @return The color for this arrangement or null if not defined.
     */
    public Color getColor(String colorType) {
        return mapColors.get(colorType);
    }
}
