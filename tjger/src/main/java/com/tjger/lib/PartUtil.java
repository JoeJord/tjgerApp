package com.tjger.lib;

import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.Point;

import com.tjger.game.completed.GameConfig;
import com.tjger.game.completed.playingfield.PlayingField;
import com.tjger.gui.GamePanel;
import com.tjger.gui.completed.Card;
import com.tjger.gui.completed.CardSet;
import com.tjger.gui.completed.ColorValuePart;
import com.tjger.gui.completed.Cover;
import com.tjger.gui.completed.Part;
import com.tjger.gui.completed.PartSet;
import com.tjger.gui.completed.Piece;
import com.tjger.gui.completed.PieceSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import at.hagru.hgbase.android.awt.Color;
import at.hagru.hgbase.android.awt.Polygon;
import at.hagru.hgbase.android.awt.Rectangle;

/**
 * Some help methods for working with parts (board, background, pieces,...).
 *
 * @author hagru
 */
public class PartUtil {

    private PartUtil() {
        super();
    }

    /**
     * Useful method for parts that are hold in an PartSet.
     * So this method makes sense with AbstractColorValueParts.
     * Get the part that is equal to the given one of the given part set.
     *
     * @param part A part (of a part set).
     * @param set  A part set.
     * @return The part of the given part set or the part itself if there is no other one found.
     */
    protected static Part getActivePart(Part part, PartSet set) {
        if (part instanceof ColorValuePart) {
            ColorValuePart cvPart = (ColorValuePart) part;
            Part activePart = set.getPart(cvPart.getColor(), cvPart.getSequence());
            if (activePart != null) {
                return activePart;
            }
        }
        return part;
    }

    /**
     * Like <code>getActivePart</code>, but for an array of parts.
     *
     * @param parts An array with parts.
     * @param set   A part set.
     * @return The list with the parts of the given part set.
     */
    protected static List<Part> getActiveParts(Part[] parts, PartSet set) {
        List<Part> listParts = new ArrayList<>();
        if (parts instanceof ColorValuePart[]) {
            for (int i = 0; i < parts.length; i++) {
                listParts.add(getActivePart(parts[i], set));
            }
        } else {
            listParts.addAll(Arrays.asList(parts));
        }
        return listParts;
    }

    /**
     * @param card A card.
     * @return The same card of the active card set.
     */
    public static Card getActiveCard(Card card) {
        if (card != null) {
            String type = card.getCardSet().getType();
            CardSet set = GameConfig.getInstance().getActiveCardSet(type);
            return (Card) getActivePart(card, set);
        } else {
            return null;
        }
    }

    /**
     * @param cards A list with cards.
     * @return The same cards of the active card set.
     */
    public static Card[] getActiveCards(Card[] cards) {
        if (cards.length > 0) {
            String type = cards[0].getCardSet().getType();
            CardSet set = GameConfig.getInstance().getActiveCardSet(type);
            List<Part> activeParts = getActiveParts(cards, set);
            return activeParts.toArray(new Card[activeParts.size()]);
        } else {
            return cards;
        }
    }

    /**
     * @param piece A piece.
     * @return The same piece of the active piece set.
     */
    public static Piece getActivePiece(Piece piece) {
        PieceSet set = GameConfig.getInstance().getActivePieceSet();
        return (Piece) getActivePart(piece, set);
    }

    /**
     * @param cards A list with pieces.
     * @return The same pieces of the active piece set.
     */
    public static Piece[] getActivePieces(Piece[] pieces) {
        PieceSet set = GameConfig.getInstance().getActivePieceSet();
        List<Part> activeParts = getActiveParts(pieces, set);
        return activeParts.toArray(new Piece[activeParts.size()]);
    }

    /**
     * Return an array with covers, one cover for each card.
     * All covers are the same.
     *
     * @param cards A list with cards.
     * @return A list with covers.
     */
    public static Cover[] getCovers(Card[] cards) {
        Cover[] covers = new Cover[cards.length];
        Cover activeCover = GameConfig.getInstance().getActiveCover();
        if (activeCover != null) {
            Arrays.fill(covers, activeCover);
            return covers;
        } else {
            return new Cover[0];
        }
    }

    /**
     * Multilpies a single part to an array of the given length, where all the array elements
     * point to the single part.
     *
     * @param singlePart The single part to multiply.
     * @param number     The size of the result array.
     * @return A array with part (of type Part[]).
     */
    public static Part[] multiplyParts(Part singlePart, int number) {
        if (number > 0) {
            Part[] parts = new Part[number];
            Arrays.fill(parts, singlePart);
            return parts;
        } else {
            return new Part[0];
        }
    }

    /* (non-javadoc)
     * @see #multiplyParts(Part, int)
     */
    public static List<Part> multiplyPartsList(Part singlePart, int number) {
        List<Part> partList = new ArrayList<>();
        if (number > 0) {
            partList.addAll(Collections.nCopies(number, singlePart));
        }
        return partList;
    }

    /**
     * @param card A card.
     * @return The cover for this card.
     */
    public static Cover getCover(Card card) {
        // It doesn't matter what card, it's always the same cover
        return GameConfig.getInstance().getActiveCover();
    }

    /**
     * Returns the width which is needed to draw the specified part.
     *
     * @param part        The part to paint (Cover, Card, Piece,...).
     * @param percentSize The size in percent (100 is normal size).
     * @return The width which is needed to draw the specified part.
     */
    public static int getDrawingWidth(Part part, int percentSize) {
        if (part == null) {
            return 0;
        }
        return ((int) (part.getImage().getWidth() * (percentSize / 100.0)));
    }

    /**
     * Returns the width which is needed to draw the specified parts.
     *
     * @param parts       The parts to paint (Cover, Card, Piece,...).
     * @param percentSize The size in percent (100 is normal size).
     * @param xSpacing    The horizontal spacing.
     * @return The width which is needed to draw the specified parts.
     */
    public static int getDrawingWidth(Part[] parts, int percentSize, int xSpacing) {
        if ((parts == null) || (parts.length == 0)) {
            return 0;
        }
        return getDrawingWidth(parts[0], percentSize) + (xSpacing * (parts.length - 1));
    }

    /**
     * Returns the height which is needed to draw the specified part.
     *
     * @param part        The part to paint (Cover, Card, Piece,...).
     * @param percentSize The size in percent (100 is normal size).
     * @return The height which is needed to draw the specified part.
     */
    public static int getDrawingHeight(Part part, int percentSize) {
        if (part == null) {
            return 0;
        }
        return ((int) (part.getImage().getHeight() * (percentSize / 100.0)));
    }

    /**
     * Returns the height which is needed to draw the specified parts.
     *
     * @param parts       The parts to paint (Cover, Card, Piece,...).
     * @param percentSize The size in percent (100 is normal size).
     * @param ySpacing    The vertical spacing.
     * @return The height which is needed to draw the specified parts.
     */
    public static int getDrawingHeight(Part[] parts, int percentSize, int ySpacing) {
        if ((parts == null) || (parts.length == 0)) {
            return 0;
        }
        return getDrawingHeight(parts[0], percentSize) + (ySpacing * (parts.length - 1));
    }

    /**
     * Returns the drawing position under consideration of the alignment and the drawing size.
     *
     * @param x                  The x-position.
     * @param y                  The y-position.
     * @param hAlignment         The horizontal alignment relative to the specified position (ConstantValue.LEFT, ConstantValue.CENTER, ConstantValue.RIGHT).
     * @param vAlignment         The vertical alignment relative to the specified position (ConstantValue.ALIGN_TOP, ConstantValue.ALIGN_MIDDLE, ConstantValue.ALIGN_BOTTOM).
     * @param drawingWidth       The drawing width of the part.
     * @param drawingHeight      The drawing height of the part.
     * @param positiveCorrection Flag if the correction should be positive or negative.
     * @return The drawing position under consideration of the alignment and the drawing size.
     */
    public static Point getDrawingPosition(int x, int y, Align hAlignment, int vAlignment, int drawingWidth,
                                           int drawingHeight, boolean positiveCorrection) {
        int drawX;
        switch (hAlignment) {
            case CENTER:
                drawX = x + ((drawingWidth / 2) * (positiveCorrection ? 1 : -1));
                break;
            case RIGHT:
                drawX = x + (drawingWidth * (positiveCorrection ? 1 : -1));
                break;
            default:
                drawX = x;
                break;
        }
        int drawY;
        switch (vAlignment) {
            case ConstantValue.ALIGN_MIDDLE:
                drawY = y + ((drawingHeight / 2) * (positiveCorrection ? 1 : -1));
                break;
            case ConstantValue.ALIGN_BOTTOM:
                drawY = y + (drawingHeight * (positiveCorrection ? 1 : -1));
                break;
            default:
                drawY = y;
                break;
        }
        return new Point(drawX, drawY);
    }

    /**
     * Returns the drawing position for the specified part under consideration of the alignment.
     *
     * @param x           The x-position.
     * @param y           The y-position.
     * @param percentSize The size in percent (100 is normal size).
     * @param hAlignment  The horizontal alignment relative to the specified position (ConstantValue.LEFT, ConstantValue.CENTER, ConstantValue.RIGHT).
     * @param vAlignment  The vertical alignment relative to the specified position (ConstantValue.ALIGN_TOP, ConstantValue.ALIGN_MIDDLE, ConstantValue.ALIGN_BOTTOM).
     * @param part        The part to paint (Cover, Card, Piece,...).
     * @return The drawing position for the specified part under consideration of the alignment.
     */
    public static Point getDrawingPosition(int x, int y, int percentSize, Align hAlignment, int vAlignment,
                                           Part part) {
        return getDrawingPosition(x, y, hAlignment, vAlignment, getDrawingWidth(part, percentSize),
                getDrawingHeight(part, percentSize), false);
    }

    /**
     * Returns the drawing position for the specified parts under consideration of the alignment.
     *
     * @param x           The x-position.
     * @param y           The y-position.
     * @param percentSize The size in percent (100 is normal size).
     * @param hAlignment  The horizontal alignment relative to the specified position (ConstantValue.LEFT, ConstantValue.CENTER, ConstantValue.RIGHT).
     * @param vAlignment  The vertical alignment relative to the specified position (ConstantValue.ALIGN_TOP, ConstantValue.ALIGN_MIDDLE, ConstantValue.ALIGN_BOTTOM).
     * @param parts       The parts to paint (Cover, Card, Piece,...).
     * @param xSpacing    The horizontal spacing.
     * @param ySpacing    The vertical spacing.
     * @return The drawing position for the specified parts under consideration of the alignment.
     */
    public static Point getDrawingPosition(int x, int y, int percentSize, Align hAlignment, int vAlignment,
                                           Part[] parts, int xSpacing, int ySpacing) {
        return getDrawingPosition(x, y, hAlignment, vAlignment,
                PartUtil.getDrawingWidth(parts, percentSize, xSpacing),
                PartUtil.getDrawingHeight(parts, percentSize, ySpacing), false);
    }

    /**
     * Returns the part that is selected at the point xClick/yClick or -1 if no part is selected.
     *
     * @param parts       A list with all parts.
     * @param xClick      The x point (e.g. mouse click).
     * @param yClick      The y point (e.g. mouse click).
     * @param xPart       The starting x-position of the parts.
     * @param yPart       The starting y-position of the parts.
     * @param percentSize The percent size the parts are painted (100 is normal).
     * @param xSpacing    The horizontal spacing.
     * @param ySpacing    The vertical spacing.
     * @param rotated     True if the part is rotated by 90 degrees.
     * @return The index of the selected part or -1.
     */
    public static int getSelectedPart(Part[] parts, int xClick, int yClick, int xPart, int yPart, int percentSize, int xSpacing, int ySpacing, boolean rotated) {
        return getSelectedPart(parts, xClick, yClick, xPart, yPart, percentSize, ConstantValue.ALIGN_LEFT,
                ConstantValue.ALIGN_TOP, xSpacing, ySpacing, rotated);
    }

    /**
     * Returns the part that is selected at the point xClick/yClick or -1 if no part is selected.
     *
     * @param parts       A list with all parts.
     * @param xClick      The x point (e.g. mouse click).
     * @param yClick      The y point (e.g. mouse click).
     * @param xPart       The starting x-position of the parts.
     * @param yPart       The starting y-position of the parts.
     * @param percentSize The percent size the parts are painted (100 is normal).
     * @param hAlignment  The horizontal alignment relative to the specified position (ConstantValue.LEFT, ConstantValue.CENTER, ConstantValue.RIGHT).
     * @param vAlignment  The vertical alignment relative to the specified position (ConstantValue.ALIGN_TOP, ConstantValue.ALIGN_MIDDLE, ConstantValue.ALIGN_BOTTOM).
     * @param xSpacing    The horizontal spacing.
     * @param ySpacing    The vertical spacing.
     * @param rotated     True if the part is rotated by 90 degrees.
     * @return The index of the selected part or -1.
     */
    public static int getSelectedPart(Part[] parts, int xClick, int yClick, int xPart, int yPart,
                                      int percentSize, Align hAlignment, int vAlignment, int xSpacing, int ySpacing, boolean rotated) {
        int clickedPart = -1;
        int numParts = parts.length;
        if (numParts > 0) {
            int cardWidth = getDrawingWidth(parts[0], percentSize);
            int cardHeight = getDrawingHeight(parts[0], percentSize);
            if (rotated) {
                // correction for rotation
                int factor = (int) ((cardHeight - cardWidth) / 2.0);
                int help = cardWidth;
                cardWidth = cardHeight;
                cardHeight = help;
                xPart = xPart - factor;
                yPart = yPart + factor;
            }
            // test if a part was clicked (code adapted from Joe, as this can handle negative spacings)
            Point drawPos = getDrawingPosition(xPart, yPart, percentSize, hAlignment, vAlignment, parts, xSpacing, ySpacing);
            int minX = drawPos.x, minY = drawPos.y;
            for (int i = 0; i < parts.length; i++) {
                int maxX = minX + cardWidth;
                int maxY = minY + cardHeight;
                if (isValueBetween(xClick, minX, maxX) && isValueBetween(yClick, minY, maxY)) {
                    clickedPart = i;
                }
                minX += xSpacing;
                minY += ySpacing;
            }
        }
        return clickedPart;
    }

    /**
     * @param value  A value.
     * @param limit1 One limit.
     * @param limit2 The second limit.
     * @return True if the value is between the two limits.
     */
    private static boolean isValueBetween(int value, int limit1, int limit2) {
        if (limit1 > limit2) {
            int h = limit1;
            limit1 = limit2;
            limit2 = h;
        }
        return (value >= limit1 && value <= limit2);
    }

    /* (non-javadoc)
     * @see #getSelectedPart(Part[], int, int, int, int, int, int, int, boolean)
     */
    public static int getSelectedPart(Part[] parts, int xClick, int yClick, int xPart, int yPart, int percentSize, int xSpacing, int ySpacing) {
        return getSelectedPart(parts, xClick, yClick, xPart, yPart, percentSize, xSpacing, ySpacing, false);
    }

    /**
     * Returns the part that is selected at the point xClick/yClick or -1 if no part is selected.
     *
     * @param parts       A list with all parts.
     * @param xClick      The x point (e.g. mouse click).
     * @param yClick      The y point (e.g. mouse click).
     * @param xPart       The starting x-position of the parts.
     * @param yPart       The starting y-position of the parts.
     * @param percentSize The percent size the parts are painted (100 is normal).
     * @param hAlignment  The horizontal alignment relative to the specified position (ConstantValue.LEFT, ConstantValue.CENTER, ConstantValue.RIGHT).
     * @param vAlignment  The vertical alignment relative to the specified position (ConstantValue.ALIGN_TOP, ConstantValue.ALIGN_MIDDLE, ConstantValue.ALIGN_BOTTOM).
     * @param xSpacing    The horizontal spacing.
     * @param ySpacing    The vertical spacing.
     * @return The index of the selected part or -1.
     */
    public static int getSelectedPart(Part[] parts, int xClick, int yClick, int xPart, int yPart,
                                      int percentSize, Align hAlignment, int vAlignment, int xSpacing, int ySpacing) {
        return getSelectedPart(parts, xClick, yClick, xPart, yPart, percentSize, hAlignment, vAlignment, xSpacing, ySpacing, false);
    }

    /**
     * Returns the index of the part that is selected at the point xClick/yClick or -1 if no part is selected.
     *
     * @param parts      A list with all parts.
     * @param xClick     The x point (e.g. mouse click).
     * @param yClick     The y point (e.g. mouse click).
     * @param field      The playing field.
     * @param positionId The id of the position in the playing field.
     * @param hAlignment The horizontal alignment relative to the specified position (ConstantValue.LEFT, ConstantValue.CENTER, ConstantValue.RIGHT).
     * @param vAlignment The vertical alignment relative to the specified position (ConstantValue.ALIGN_TOP, ConstantValue.ALIGN_MIDDLE, ConstantValue.ALIGN_BOTTOM).
     * @param xSpacing   The horizontal spacing.
     * @param ySpacing   The vertical spacing.
     * @return The index of the part that is selected at the point xClick/yClick or -1 if no part is selected.
     */
    public static int getSelectedPart(Part[] parts, int xClick, int yClick, PlayingField field,
                                      String positionId, Align hAlignment, int vAlignment, int xSpacing, int ySpacing) {
        Rectangle rect = field.getFieldRectangle(field.getField(positionId));
        Point drawPos = PartUtil.getDrawingPosition(rect.x, rect.y, hAlignment, vAlignment, rect.width,
                rect.height, true);
        return getSelectedPart(parts, xClick, yClick, drawPos.x, drawPos.y, 100, hAlignment, vAlignment,
                xSpacing, ySpacing, false);
    }

    /**
     * Returns {@code true} if the specified part is selected at the point xClick/yClick.
     *
     * @param part        The part to check.
     * @param xClick      The x point (e.g. mouse click).
     * @param yClick      The y point (e.g. mouse click).
     * @param xPart       The starting x-position of the parts.
     * @param yPart       The starting y-position of the parts.
     * @param percentSize The percent size the parts are painted (100 is normal).
     * @return {@code true} if the specified part is selected at the point xClick/yClick.
     */
    public static boolean getSelectedPart(Part part, int xClick, int yClick, int xPart, int yPart,
                                          int percentSize) {
        return (getSelectedPart(new Part[]{part}, xClick, yClick, xPart, yPart, percentSize, 0, 0) != -1);
    }

    /**
     * Returns {@code true} if the specified part is selected at the point xClick/yClick.
     *
     * @param part        The part to check.
     * @param xClick      The x point (e.g. mouse click).
     * @param yClick      The y point (e.g. mouse click).
     * @param field       The playing field.
     * @param positionId  The id of the position in the playing field.
     * @param percentSize The percent size the parts are painted (100 is normal).
     * @return {@code true} if the specified part is selected at the point xClick/yClick.
     */
    public static boolean getSelectedPart(Part part, int xClick, int yClick, PlayingField field,
                                          String positionId, int percentSize) {
        Rectangle rect = field.getFieldRectangle(field.getField(positionId));
        Point drawPos = PartUtil.getDrawingPosition(rect.x, rect.y, ConstantValue.ALIGN_LEFT,
                ConstantValue.ALIGN_TOP, rect.width, rect.height, true);
        return getSelectedPart(part, xClick, yClick, drawPos.x, drawPos.y, percentSize);
    }

    /**
     * Draws a rectangle with the given color on the selected part.
     *
     * @param selectIndex Index of the selected part (0..parts.length-1).
     * @param parts       A list with the parts.
     * @param xPos        The bottom left x-position.
     * @param yPos        The bottom left y-position.
     * @param percentSize The percent size the parts are painted (100 is normal).
     * @param xSpacing    The horizontal spacing.
     * @param ySpacing    The vertical spacing.
     * @param rotated     True if parts are rotated by 90 degrees.
     * @param selectColor The color for the rectangle
     * @param gamePanel   The game panel where to paint the rectangle.
     * @param g           The graphics object.
     */
    public static void drawSelectedPart(int selectIndex, Part[] parts, int xPos, int yPos, int percentSize, int xSpacing, int ySpacing, boolean rotated, Color selectColor, GamePanel gamePanel, Canvas g) {
        drawSelectedPart(selectIndex, parts, xPos, yPos, percentSize, ConstantValue.ALIGN_LEFT,
                ConstantValue.ALIGN_TOP, xSpacing, ySpacing, rotated, selectColor, gamePanel, g);
    }

    /**
     * Draws a rectangle with the given color on the selected part.
     *
     * @param selectIndex Index of the selected part (0..parts.length-1).
     * @param parts       A list with the parts.
     * @param xPos        The bottom left x-position.
     * @param yPos        The bottom left y-position.
     * @param percentSize The percent size the parts are painted (100 is normal).
     * @param hAlignment  The horizontal alignment relative to the specified position (ConstantValue.LEFT, ConstantValue.CENTER, ConstantValue.RIGHT).
     * @param vAlignment  The vertical alignment relative to the specified position (ConstantValue.ALIGN_TOP, ConstantValue.ALIGN_MIDDLE, ConstantValue.ALIGN_BOTTOM).
     * @param xSpacing    The horizontal spacing.
     * @param ySpacing    The vertical spacing.
     * @param rotated     True if parts are rotated by 90 degrees.
     * @param selectColor The color for the rectangle
     * @param gamePanel   The game panel where to paint the rectangle.
     * @param g           The graphics object.
     */
    public static void drawSelectedPart(int selectIndex, Part[] parts, int xPos, int yPos, int percentSize,
                                        Align hAlignment, int vAlignment, int xSpacing, int ySpacing, boolean rotated, Color selectColor,
                                        GamePanel gamePanel, Canvas g) {
        int numParts = parts.length;
        if (selectIndex >= 0 && selectIndex < numParts) {
            int cardWidth = (int) Math.ceil(getDrawingWidth(parts[0], percentSize));
            int cardHeight = (int) Math.ceil(getDrawingHeight(parts[0], percentSize));
            if (rotated) {
                // correction for rotation
                int factor = (int) ((cardHeight - cardWidth) / 2.0);
                int help = cardWidth;
                cardWidth = cardHeight;
                cardHeight = help;
                xPos = xPos - factor;
                yPos = yPos + factor;
            }
            gamePanel.changeColor(selectColor);
            int width = (selectIndex == numParts - 1 || cardWidth < absValue(xSpacing)) ? cardWidth : absValue(xSpacing);
            int height = (selectIndex == numParts - 1 || cardHeight < absValue(ySpacing)) ? cardHeight : absValue(ySpacing);
            int x1 = xPos + xSpacing * selectIndex + ((xSpacing < 0) ? (cardWidth - width - 1) : 0);
            int y1 = yPos + ySpacing * selectIndex;
            Point drawPos1 = getDrawingPosition(x1, y1, percentSize, hAlignment, vAlignment, parts, xSpacing, ySpacing);
            gamePanel.drawRect(drawPos1.x, drawPos1.y, width, cardHeight, true, g);
            // paint the vertical rest
            int diffX = cardWidth - width;
            int x2 = xPos + xSpacing * selectIndex + ((xSpacing < 0) ? 0 : width);
            int y2 = yPos + ySpacing * selectIndex + ((ySpacing < 0) ? (cardHeight - height - 1) : 0);
            Point drawPos2 = getDrawingPosition(x2, y2, percentSize, hAlignment, vAlignment, parts, xSpacing, ySpacing);
            gamePanel.drawRect(drawPos2.x, drawPos2.y, diffX, height, true, g);
        }
    }

    /**
     * @param value A integer value.
     * @return The absolute value.
     */
    private static int absValue(int value) {
        return Math.abs(value);
    }

    /* (non-javadoc)
     * @see #drawSelectedPart(int, Part[], int, int, int, int, int, boolean, Color, GamePanel, Graphics)
     */
    public static void drawSelectedPart(int selectIndex, Part[] parts, int xPos, int yPos, int percentSize, int xSpacing, int ySpacing, Color selectColor, GamePanel gamePanel, Canvas g) {
        drawSelectedPart(selectIndex, parts, xPos, yPos, percentSize, xSpacing, ySpacing, false, selectColor, gamePanel, g);
    }

    /**
     * Draws a rectangle with the given color on the selected part.
     *
     * @param selectIndex Index of the selected part (0..parts.length-1).
     * @param parts       A list with the parts.
     * @param xPos        The bottom left x-position.
     * @param yPos        The bottom left y-position.
     * @param percentSize The percent size the parts are painted (100 is normal).
     * @param hAlignment  The horizontal alignment relative to the specified position (ConstantValue.LEFT, ConstantValue.CENTER, ConstantValue.RIGHT).
     * @param vAlignment  The vertical alignment relative to the specified position (ConstantValue.ALIGN_TOP, ConstantValue.ALIGN_MIDDLE, ConstantValue.ALIGN_BOTTOM).
     * @param xSpacing    The horizontal spacing.
     * @param ySpacing    The vertical spacing.
     * @param selectColor The color for the rectangle
     * @param gamePanel   The game panel where to paint the rectangle.
     * @param g           The graphics object.
     */
    public static void drawSelectedPart(int selectIndex, Part[] parts, int xPos, int yPos, int percentSize,
                                        Align hAlignment, int vAlignment, int xSpacing, int ySpacing, Color selectColor,
                                        GamePanel gamePanel, Canvas g) {
        drawSelectedPart(selectIndex, parts, xPos, yPos, percentSize, hAlignment, vAlignment, xSpacing,
                ySpacing, false, selectColor, gamePanel, g);
    }

    /**
     * Draws a rectangle with the given color on the selected part.
     *
     * @param part        The part to be selected.
     * @param xPos        The bottom left x-position.
     * @param yPos        The bottom left y-position.
     * @param percentSize The percent size the parts are painted (100 is normal).
     * @param selectColor The color for the rectangle
     * @param gamePanel   The game panel where to paint the rectangle.
     * @param g           The graphics object.
     */
    public static void drawSelectedPart(Part part, int xPos, int yPos, int percentSize, Color selectColor,
                                        GamePanel gamePanel, Canvas g) {
        drawSelectedPart(0, new Part[]{part}, xPos, yPos, percentSize, 0, 0, selectColor, gamePanel, g);
    }

    /**
     * Returns true if a given x-/y-position is inside a given polygon.
     * This method does not suit here, but in this class is already a similar method.
     *
     * @param p A polygon.
     * @param x The x-coordinate to test.
     * @param y The y-coordinate to test.
     * @return True if the position is inside the polygon, otherwise false.
     */
    public static boolean isInsidePolygon(Polygon p, int x, int y) {
        // http://geography.uoregon.edu/buckley/teaching/geog472-s01/lectures/lecture11/n11.html
        int ni = +1;
        int u = x;
        int v = y;
        for (int i = 0; i < p.npoints; i++) {
            int ip1 = (i + 1) % p.npoints;
            if (p.xpoints[ip1] != p.xpoints[i]) {
                if ((p.xpoints[ip1] - u) * (u - p.xpoints[i]) >= 0) {
                    if (p.xpoints[ip1] != u || p.xpoints[i] >= u) {
                        if (p.xpoints[i] != u || p.xpoints[ip1] >= u) {
                            int b = (p.ypoints[ip1] - p.ypoints[i]) / (p.xpoints[ip1] - p.xpoints[i]);
                            int a = p.ypoints[i] - b * p.xpoints[i];
                            int yi = a + b * u;
                            if (yi > v) {
                                ni = ni * (-1);
                            }
                        }
                    }
                }
            }
        }
        return (ni == -1);
    }

    /**
     * Test if a given coordinate (x,y) is inside a rectangle.
     *
     * @param ulX The x-position upper left corner of the rectangle.
     * @param ulY The y-position upper left corner of the rectangle.
     * @param w   The width of the rectangle.
     * @param h   The height of the rectangle.
     * @param x   The x-coordinate to test.
     * @param y   The y-coordinate to test.
     * @return True if the coordinate is inside the rectangle.
     */
    public static boolean isInsideRectangle(int ulX, int ulY, int w, int h, int x, int y) {
        Rectangle rect = new Rectangle(ulX, ulY, w, h);
        return rect.contains(x, y);
    }

    /**
     * @param color A player's color.
     * @return The pieces for this color, can have 0 size.
     */
    public static Piece[] getPiecesWithColor(String color) {
        PieceSet set = GameConfig.getInstance().getActivePieceSet();
        if (set != null) {
            return set.getPieces(color);
        } else {
            return new Piece[0];
        }
    }

    /**
     * @param color A player's color.
     * @return The cards for this color taking the first set with cards, can have 0 size.
     */
    public static Card[] getCardsWithColor(String color) {
        final GameConfig gc = GameConfig.getInstance();
        for (String setType : gc.getCardSetTypes()) {
            CardSet set = gc.getActiveCardSet(setType);
            if (set != null) {
                Card[] cards = set.getCards(color);
                if (cards.length > 0) {
                    return cards;
                }
            }
        }
        return new Card[0];
    }

}
