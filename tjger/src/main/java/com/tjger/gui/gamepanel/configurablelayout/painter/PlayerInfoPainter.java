package com.tjger.gui.gamepanel.configurablelayout.painter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.tjger.game.GamePlayer;
import com.tjger.game.completed.GameEngine;
import com.tjger.gui.SimpleGamePanel;
import com.tjger.gui.completed.Part;
import com.tjger.gui.completed.configurablelayout.ScaleType;
import com.tjger.gui.completed.configurablelayout.layoutelement.PlayerInfoLayout;

import at.hagru.hgbase.android.awt.Color;
import at.hagru.hgbase.android.awt.Dimension;
import at.hagru.hgbase.gui.StringSizeSupplier;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * The painter for an element of the type "Player information" of a configurable layout.
 */
public class PlayerInfoPainter implements IndexedElementPainter<PlayerInfoLayout, Part> {
    /**
     * The game panel where to paint.
     */
    private final SimpleGamePanel gamePanel;

    /**
     * Constructs a new instance.
     *
     * @param gamePanel The game panel where to paint.
     */
    public PlayerInfoPainter(SimpleGamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public SimpleGamePanel getGamePanel() {
        return gamePanel;
    }

    @Override
    public Part getActiveElement(PlayerInfoLayout element) {
        throw new UnsupportedOperationException("There are no active gamefield elements for a player information!");
    }

    @Override
    public int getPercentSize(PlayerInfoLayout element) {
        return (int) element.getPercentFontSize(new StringSizeSupplier(getInformation(element), new Paint()), () -> getSurroundingAreaDimension(element));
    }

    @Override
    public Point getPaintPosition(PlayerInfoLayout element) {
        Point pos = IndexedElementPainter.super.getPaintPosition(element);
        // The y coordinate must be corrected by the height (above the baseline) of the font.
        pos.offset(0, getPaintDimension(element).height);
        return pos;
    }

    @Override
    public ScaleType getScale(PlayerInfoLayout element) {
        return element.getScale();
    }

    @Override
    public double getPaintAngle(PlayerInfoLayout element) {
        throw new UnsupportedOperationException("The angle is not supported for a player information!");
    }

    @Override
    public Dimension getPaintDimension(PlayerInfoLayout element) {
        Float fontSize = getFontSize(element);
        if (fontSize == null) {
            return null;
        }
        return new StringSizeSupplier(getInformation(element), new Paint()).get(fontSize);
    }

    /**
     * Returns the color in which the player information is painted.
     *
     * @param element The layout element to paint.
     * @return The color in which the player information is painted.
     */
    public Color getColor(PlayerInfoLayout element) {
        return element.getColor();
    }

    /**
     * Returns the player for which the information should be painted.
     *
     * @param engine  The game engine.
     * @param element The layout element to paint.
     * @return The player for which the information should be painted.
     */
    public GamePlayer getPlayer(GameEngine engine, PlayerInfoLayout element) {
        return engine.getPlayerWithIndex(getPlayerIndex(element));
    }

    /**
     * Returns the information which should be painted.
     *
     * @param element The layout element to paint.
     * @return The information which should be painted.
     */
    public String getInformation(PlayerInfoLayout element) {
        GamePlayer player = getPlayer(GameEngine.getInstance(), element);
        if (player == null) {
            return null;
        }
        switch (element.getType()) {
            case NAME:
                return player.getName();
            case GAME_SCORE:
                return String.valueOf(player.getScore(GamePlayer.SCORE_GAME));
            case ROUND_SCORE:
                return String.valueOf(player.getScore(GamePlayer.SCORE_ROUND));
            case TURN_SCORE:
                return String.valueOf(player.getScore(GamePlayer.SCORE_TURN));
            default:
                break;
        }
        return null;
    }

    /**
     * Returns the font size with which the information should be painted.
     *
     * @param element The layout element to paint.
     * @return The font size with which the information should be painted.
     */
    public Float getFontSize(PlayerInfoLayout element) {
        return element.getFontSize(new StringSizeSupplier(getInformation(element), new Paint()), () -> getSurroundingAreaDimension(element));
    }

    /**
     * Changes the current color if a color is configured for this element.
     *
     * @param element The layout element to paint.
     */
    protected void changeColor(PlayerInfoLayout element) {
        Color color = getColor(element);
        if (color != null) {
            getGamePanel().changeColor(color);
        }
    }

    /**
     * Changes the current font size if a font size is configured for this element.
     *
     * @param element The layout element to paint.
     */
    protected void changeFontSize(PlayerInfoLayout element) {
        Float fontSize = getFontSize(element);
        if (fontSize != null) {
            getGamePanel().changeFontSize(fontSize);
        }
    }

    @Override
    public void paint(PlayerInfoLayout element, Canvas g) {
        String info = getInformation(element);
        if (!HGBaseTools.hasContent(info)) {
            return;
        }

        changeColor(element);
        changeFontSize(element);

        Point pos = getPaintPosition(element);
        getGamePanel().drawString(info, pos.x, pos.y, g);
    }
}
