package com.tjger.gui.internal;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.tjger.game.completed.GameConfig;
import com.tjger.game.completed.GameManager;
import com.tjger.gui.SimpleGamePanel;
import com.tjger.gui.completed.Background;
import com.tjger.gui.completed.Board;
import com.tjger.gui.completed.CardSet;
import com.tjger.gui.completed.Cover;
import com.tjger.gui.completed.Part;
import com.tjger.gui.completed.PartSet;
import com.tjger.gui.completed.PieceSet;
import com.tjger.lib.ConstantValue;

import java.util.Arrays;

import at.hagru.hgbase.android.awt.Color;

/**
 * Shows a preview of the panel depending on the arrangement.
 *
 * @author hagru
 */
public class PreviewPanel extends SimpleGamePanel {

    private final GameConfig config;
    protected int distance = 10;
    protected int width;
    protected int height;
    protected String[] cardSetTypes;
    protected PartsDlg partsDlg;
    protected String[] userParts;
    protected String[] userPartSets;

    public PreviewPanel(PartsDlg partsDlg, int width, int height) {
        super(partsDlg);
        this.partsDlg = partsDlg;
        this.width = width;
        this.height = height;
        this.config = GameManager.getInstance().getGameConfig();
        this.cardSetTypes = config.getCardSetTypes();
        this.userParts = config.getPartTypes();
        this.userPartSets = config.getPartSetTypes();
    }

    /**
     * Returns the selected background color or {@code null} if there is no background color.
     *
     * @return The selected background color or {@code null} if there is no background color.
     */
    protected Color getSelectedBackgroundColor() {
        return (Color) partsDlg.getPartSupplier().apply(ConstantValue.CONFIG_BACKCOLOR);
    }

    /**
     * Returns the selected background.
     *
     * @return The selected background.
     */
    protected Background getSelectedBackground() {
        return (Background) partsDlg.getPartSupplier().apply(ConstantValue.CONFIG_BACKGROUND);
    }

    /**
     * Returns the selected board.
     *
     * @return The selected board.
     */
    protected Board getSelectedBoard() {
        return (Board) partsDlg.getPartSupplier().apply(ConstantValue.CONFIG_BOARD);
    }

    /**
     * Returns the selected cover.
     *
     * @return The selected cover.
     */
    protected Cover getSelectedCover() {
        return (Cover) partsDlg.getPartSupplier().apply(ConstantValue.CONFIG_COVER);
    }

    /**
     * Returns the selected cardset of the specified type.
     *
     * @param type The type of the cardset.
     * @return The selected cardset of the specified type.
     */
    protected CardSet getSelectedCardSet(String type) {
        return (CardSet) partsDlg.getPartSupplier().apply(type);
    }

    /**
     * Returns the selected partset of the specified type.
     *
     * @param type The type of the partset.
     * @return The selected partset of the specified type.
     */
    protected PartSet getSelectedPartSet(String type) {
        return (PartSet) partsDlg.getPartSupplier().apply(type);
    }

    /**
     * Returns the selected part of the specified type.
     *
     * @param type The type of the part.
     * @return The selected part of the specified type.
     */
    protected Part getSelectedPart(String type) {
        return (Part) partsDlg.getPartSupplier().apply(type);
    }

    /**
     * Returns the selected pieceset.
     *
     * @return The selected pieceset.
     */
    protected PieceSet getSelectedPieceSet() {
        return (PieceSet) partsDlg.getPartSupplier().apply(ConstantValue.CONFIG_PIECESET);
    }

    /**
     * Returns the selected color of the specified type.
     *
     * @param type The type of the color.
     * @return The selected color of the specified type.
     */
    protected Color getSelectedColor(String type) {
        return (Color) partsDlg.getPartSupplier().apply(type);
    }


    @Override
    protected void paintBackground(Canvas g) {
        paintBackgroundColor(getSelectedBackgroundColor(), g);
        paintBackgroundImage(getSelectedBackground(), g);
    }

    @Override
    protected void paintBoard(Canvas g) {
        paintBoard(getSelectedBoard(), g);
    }

    @Override
    protected void paintParts(Canvas g) {
        paintStandardParts(g);
        paintUserParts(g);
    }

    /**
     * Paints the standard parts (Cards, Pieces, ...).
     */
    protected void paintStandardParts(Canvas g) {
        final int[] x = {distance}; // Using an array to simulate a mutable final variable for lambda operations.
        int y = 10;
        x[0] = drawPartAndAdvanceX(x[0], y, getSelectedCover(), g);
        Arrays.stream(cardSetTypes).forEach(type -> x[0] = drawPartAndAdvanceX(x[0], y, getSelectedCardSet(type), g));
        drawPartAndAdvanceX(x[0], y, getSelectedPieceSet(), g);
    }

    /**
     * Paints the user parts (Parts, PartSets).
     */
    protected void paintUserParts(Canvas g) {
        final int[] x = {distance}; // Using an array to simulate a mutable final variable for lambda operations.
        int y = getFieldHeight() / 2;
        // paint first the part sets and then the parts
        Arrays.stream(userPartSets).forEach(partSetType -> x[0] = drawPartAndAdvanceX(x[0], y, getSelectedPartSet(partSetType), g));
        Arrays.stream(userParts).forEach(partType -> x[0] = drawPartAndAdvanceX(x[0], y, getSelectedPart(partType), g));
    }

    /**
     * Draws the specified part and returns the new x position for the next part.
     *
     * @param x    X-position.
     * @param y    Y-position.
     * @param part The part to paint.
     * @param g    The graphics object.
     * @return The new x-position;
     */
    private int drawPartAndAdvanceX(int x, int y, Part part, Canvas g) {
        if (part == null) {
            return x;
        }
        Bitmap img = part.getImage();
        if (img == null) {
            return x;
        }
        drawPart(x, y, part, g);
        return x + distance + img.getWidth();
    }

    @Override
    public double getZoomFactor() {
        double wz = (double) width / (double) getFieldWidth();
        double hz = (double) height / (double) getFieldHeight();
        return Math.min(wz, hz);
    }

    @Override
    public int getFieldWidth() {
        return config.getFieldWidth(getSelectedBoard(), getSelectedBackground());
    }

    @Override
    public int getFieldHeight() {
        return config.getFieldHeight(getSelectedBoard(), getSelectedBackground());
    }
}
