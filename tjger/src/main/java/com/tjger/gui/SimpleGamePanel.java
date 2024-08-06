package com.tjger.gui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.tjger.game.completed.GameConfig;
import com.tjger.game.completed.GameManager;
import com.tjger.game.completed.playingfield.PlayingField;
import com.tjger.gui.completed.Background;
import com.tjger.gui.completed.Board;
import com.tjger.gui.completed.ImageReflection;
import com.tjger.gui.completed.ImageShadow;
import com.tjger.gui.completed.Part;
import com.tjger.lib.ConstantValue;
import com.tjger.lib.PartUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.hagru.hgbase.android.awt.Color;
import at.hagru.hgbase.android.awt.Dimension;
import at.hagru.hgbase.android.awt.Polygon;
import at.hagru.hgbase.android.awt.Rectangle;
import at.hagru.hgbase.gui.BitmapCanvas;
import at.hagru.hgbase.lib.HGBaseConfig;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * A simple game panel that is thought just for viewing the panel.
 *
 * @author hagru
 */
public class SimpleGamePanel extends AppCompatImageView {

    final private GameManager gameManager;
    final private GameConfig gameConfig;
    private final Paint currentPaint = new Paint();
    // store all black images in a map for performance reasons
    private final Map<String, Bitmap> blackImages = new HashMap<>();
    private ImageShadow nextImageShadow;
    private ImageReflection nextImageReflection;
    private boolean noReflectionForMultipleParts;

    public SimpleGamePanel(Activity activity) {
        super(activity);
        this.gameManager = GameManager.getInstance();
        this.gameConfig = gameManager.getGameConfig();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paintComponent(canvas);
    }

    /**
     * @return The game manager.
     */
    public GameManager getGameManager() {
        return this.gameManager;
    }

    /**
     * @return The game configuration.
     */
    public GameConfig getGameConfig() {
        return getGameManager().getGameConfig();
    }

    /**
     * @return the current paint object
     * @see #changeColor
     * @see #changeStyle
     */
    protected Paint getCurrentPaint() {
        return currentPaint;
    }

    /**
     * For the simple panel just return 100%. <p>
     * If the image view is fitted to the view by Android, this zoom factor is ignored.
     *
     * @return The current zoom factor.
     */
    public double getZoomFactor() {
        return 1.0;
    }

    /**
     * @param value A size value (x, y, width, height).
     * @return The zoom dependent transformation of this value.
     */
    public int transform(int value) {
        return transform((double) (value));
    }

    /**
     * @param value A size value (x, y, width, height).
     * @return The zoom dependent transformation of this value.
     */
    public int transform(double value) {
        return (int) Math.ceil(value * getZoomFactor());
    }

    /**
     * Returns the zoom dependent transformation of the specified rectangle.
     *
     * @param rect The rectangle to transform.
     * @return The zoom dependent transformation of the specified rectangle.
     */
    @NonNull
    protected Rectangle transform(@NonNull Rectangle rect) {
        return new Rectangle(transform(rect.x), transform(rect.y), transform(rect.width), transform(rect.height));
    }

    /**
     * @param value A real value on the screen.
     * @return The zoom dependent invert transformation for the game field.
     */
    public int invertTransform(int value) {
        return invertTransform((double) (value));
    }

    /**
     * @param value A real value on the screen.
     * @return The zoom dependent invert transformation for the game field.
     */
    public int invertTransform(double value) {
        return (int) (value / getZoomFactor());
    }

    /**
     * Returns the x coordinate to align an element horizontal in the specified area.
     *
     * @param hAlignment The horizontal alignment in the specified position (ConstantValue.ALIGN_LEFT, ConstantValue.ALIGN_CENTER, ConstantValue.ALIGN_RIGHT).
     * @param area       The area in which the element should be aligned horizontal.
     * @return The x coordinate to align an element horizontal in the specified area.
     */
    protected int getXAreaHorizontalAlignment(@NonNull Paint.Align hAlignment, @NonNull Rectangle area) {
        return (hAlignment == Paint.Align.CENTER) ? area.x + (area.width / 2) : ((hAlignment == Paint.Align.RIGHT) ? area.x + area.width : area.x);
    }

    /**
     * Returns a rectangle that surrounds the specified string.
     *
     * @param string The string to be measured.
     * @return A rectangle that surrounds the specified string.
     */
    @NonNull
    protected Rectangle getStringBounds(@NonNull String string) {
        Rect bounds = new Rect();
        currentPaint.getTextBounds(string, 0, string.length(), bounds);
        return new Rectangle(bounds);
    }

    /**
     * Returns the width of the specified string.
     *
     * @param string The string to be measured.
     * @return The width of the specified string.
     */
    protected int getStringWidth(String string) {
        return getStringBounds(string).width;
    }

    /**
     * Returns the height of the specified string.
     *
     * @param string The string to be measured.
     * @return The height of the specified string.
     */
    protected int getStringHeight(String string) {
        return getStringBounds(string).height;
    }

    /**
     * Repaints the game panel in an extra thread that should work from non UI-threads.<p>
     * NOTE: use {@link #invalidate()} to perform a repaint from a UI thread.
     */
    public void repaint() {
        postInvalidate();
    }

    /**
     * Paint all parts on the panel. Calls methods to paint background, board and parts.
     *
     * @param g the canvas object
     */
    protected void paintComponent(Canvas g) {
        paintBackground(g);
        paintBoard(g);
        paintParts(g);
    }

    /**
     * Override this method to paint all the parts on the game field.
     *
     * @param g The canvas object.
     */
    protected void paintParts(Canvas g) {
    }

    /**
     * Is called by <code>paintComponent(Graphics)</code>.
     *
     * @param g The canvas object.
     */
    protected void paintBoard(Canvas g) {
        paintBoard(gameConfig.getActiveBoard(), g);
    }

    /**
     * @param board The board to paint.
     * @param g     The canvas object.
     */
    protected void paintBoard(Board board, Canvas g) {
        if (board != null) {
            drawPart(board.getXPos(), board.getYPos(), board.getZoom(), board, g);
        }
    }

    /**
     * Is called by <code>paintComponent(Canvas)</code>.
     *
     * @param g The canvas object.
     */
    protected void paintBackground(Canvas g) {
        paintBackgroundColor(gameConfig.getActiveBackgroundColor(), g);
        paintBackgroundImage(gameConfig.getActiveBackground(), g);
    }

    /**
     * @param backColor the background color to paint, may be null
     * @param g         the canvas object
     */
    protected void paintBackgroundColor(Color backColor, Canvas g) {
        if (backColor != null) {
            changeColor(backColor);
            Style oldStyle = setStyle(true);
            g.drawRect(new RectF(0, 0, this.getWidth(), this.getHeight()), currentPaint);
            currentPaint.setStyle(oldStyle);
        }
    }

    /**
     * @param back The background to paint.
     * @param g    The canvas object.
     */
    protected void paintBackgroundImage(Background back, Canvas g) {
        if (back != null) {
            Bitmap backImg = back.getImage();
            if (backImg != null) {
                if (back.isRepeatMode()) {
                    int backW = (back.isIgnoreZoom()) ? backImg.getWidth() : (int) (backImg.getWidth() * back.getZoom() / 100.0);
                    int backH = (back.isIgnoreZoom()) ? backImg.getHeight() : (int) (backImg.getHeight() * back.getZoom() / 100.0);
                    int fieldW = (back.isIgnoreZoom()) ? this.getWidth() : invertTransform(this.getWidth());
                    int fieldH = (back.isIgnoreZoom()) ? this.getHeight() : invertTransform(this.getHeight());
                    for (int x = 0; x <= fieldW / backW; x++) {
                        for (int y = 0; y <= fieldH / backH; y++) {
                            int xPos = x * backW;
                            int yPos = y * backH;
                            if (back.isIgnoreZoom()) {
                                g.drawBitmap(backImg, xPos, yPos, null);
                            } else {
                                drawImage(xPos, yPos, back.getZoom(), backImg, g);
                            }
                        }
                    }
                } else {
                    checkShadowForPart(back);
                    checkReflectionForPart(back);
                    if (back.isIgnoreZoom()) {
                        g.drawBitmap(backImg, 0, 0, null);
                    } else {
                        drawImage(0, 0, back.getZoom(), back.getImage(), g);
                    }
                }
            }
        }
    }

    /**
     * Draws the specified part at the specified position.
     *
     * @param x    The x-position.
     * @param y    The y-position.
     * @param part The part to paint (Cover, Card, Piece,...).
     * @param g    The canvas object.
     */
    public void drawPart(int x, int y, Part part, Canvas g) {
        drawPart(x, y, 100, part, g);
    }

    /**
     * Draws the specified part at the specified position (consideration of the alignment).
     *
     * @param x          The x-position.
     * @param y          The y-position.
     * @param hAlignment The horizontal alignment relative to the specified position (ConstantValue.ALIGN_LEFT, ConstantValue.ALIGN_CENTER, ConstantValue.ALIGN_RIGHT).
     * @param vAlignment The vertical alignment relative to the specified position (ConstantValue.ALIGN_TOP, ConstantValue.ALIGN_MIDDLE, ConstantValue.ALIGN_BOTTOM).
     * @param part       The part to paint (Cover, Card, Piece,...).
     * @param g          The canvas object.
     */
    public void drawPart(int x, int y, Align hAlignment, int vAlignment, Part part, Canvas g) {
        drawPart(x, y, 100, hAlignment, vAlignment, part, g);
    }

    /**
     * Draws the specified part at the specified position with the specified size.
     *
     * @param x           The x-position.
     * @param y           The y-position.
     * @param percentSize The size in percent (100 is normal size).
     * @param part        The part to paint (Cover, Card, Piece,...).
     * @param g           The canvas object.
     */
    public void drawPart(int x, int y, int percentSize, Part part, Canvas g) {
        drawPart(x, y, percentSize, 0.0, part, g);
    }

    /**
     * Draws the specified part at the specified position (consideration of the alignment) with the specified size.
     *
     * @param x           The x-position.
     * @param y           The y-position.
     * @param percentSize The size in percent (100 is normal size).
     * @param hAlignment  The horizontal alignment relative to the specified position (ConstantValue.ALIGN_LEFT, ConstantValue.ALIGN_CENTER, ConstantValue.ALIGN_RIGHT).
     * @param vAlignment  The vertical alignment relative to the specified position (ConstantValue.ALIGN_TOP, ConstantValue.ALIGN_MIDDLE, ConstantValue.ALIGN_BOTTOM).
     * @param part        The part to paint (Cover, Card, Piece,...).
     * @param g           The canvas object.
     */
    public void drawPart(int x, int y, int percentSize, Align hAlignment, int vAlignment, Part part, Canvas g) {
        drawPart(x, y, percentSize, 0.0, hAlignment, vAlignment, part, g);
    }

    /**
     * Draws the specified part at the specified position with the specified size rotated by the specified angle.
     *
     * @param x           The x-position.
     * @param y           The y-position.
     * @param percentSize The size in percent (100 is normal size).
     * @param angle       The angle to rotate the part.
     * @param part        The part to paint (Cover, Card, Piece,...).
     * @param g           The canvas object.
     */
    public void drawPart(int x, int y, int percentSize, double angle, Part part, Canvas g) {
        drawPart(x, y, percentSize, angle, ConstantValue.ALIGN_LEFT, ConstantValue.ALIGN_TOP, part, g);
    }

    /**
     * Draws the specified part at the specified position (consideration of the alignment) with the specified size rotated by the specified angle.
     *
     * @param x           The x-position.
     * @param y           The y-position.
     * @param percentSize The size in percent (100 is normal size).
     * @param angle       The angle to rotate the part.
     * @param hAlignment  The horizontal alignment relative to the specified position (ConstantValue.ALIGN_LEFT, ConstantValue.ALIGN_CENTER, ConstantValue.ALIGN_RIGHT).
     * @param vAlignment  The vertical alignment relative to the specified position (ConstantValue.ALIGN_TOP, ConstantValue.ALIGN_MIDDLE, ConstantValue.ALIGN_BOTTOM).
     * @param part        The part to paint (Cover, Card, Piece,...).
     * @param g           The canvas object.
     */
    public void drawPart(int x, int y, int percentSize, double angle, Align hAlignment, int vAlignment, Part part, Canvas g) {
        Point drawingPosition = PartUtil.getDrawingPosition(x, y, percentSize, hAlignment, vAlignment, part);
        if (part != null) {
            checkShadowForPart(part);
            checkReflectionForPart(part);
            drawImage(drawingPosition.x, drawingPosition.y, percentSize, angle, part.getImage(), g);
        }
    }

    /**
     * Draws the specified part at the specified position of the specified playing field.
     *
     * @param field      The playing field.
     * @param positionId The id of the position in the playing field.
     * @param part       The part to draw.
     * @param g          The canvas object.
     */
    public void drawPart(PlayingField field, String positionId, Part part, Canvas g) {
        drawPart(field, positionId, ConstantValue.ALIGN_LEFT, ConstantValue.ALIGN_TOP, part, g);
    }

    /**
     * Draws the specified part at the specified position of the specified playing field under consideration of the alignment.
     *
     * @param field      The playing field.
     * @param positionId The id of the position in the playing field.
     * @param hAlignment The horizontal alignment relative to the specified position (ConstantValue.ALIGN_LEFT, ConstantValue.ALIGN_CENTER, ConstantValue.ALIGN_RIGHT).
     * @param vAlignment The vertical alignment relative to the specified position (ConstantValue.ALIGN_TOP, ConstantValue.ALIGN_MIDDLE, ConstantValue.ALIGN_BOTTOM).
     * @param part       The part to draw.
     * @param g          The canvas object.
     */
    public void drawPart(PlayingField field, String positionId, Align hAlignment, int vAlignment, Part part, Canvas g) {
        Rectangle rect = field.getFieldRectangle(field.getField(positionId));
        Point drawPos = PartUtil.getDrawingPosition(rect.x, rect.y, hAlignment, vAlignment, rect.width, rect.height, true);
        drawPart(drawPos.x, drawPos.y, hAlignment, vAlignment, part, g);
    }

    /**
     * @param x        The x-position.
     * @param y        The y-position.
     * @param parts    The parts to paint (Cover, Card, Piece,...).
     * @param xSpacing The horizontal spacing.
     * @param ySpacing The vertical spacing.
     * @param g        The canvas object.
     */
    public void drawParts(int x, int y, Part[] parts, int xSpacing, int ySpacing, Canvas g) {
        int reflectionIndex = checkReflectionIndex(parts, ySpacing);
        for (int i = 0; parts != null && i < parts.length; i++) {
            noReflectionForMultipleParts = (reflectionIndex != -1 && reflectionIndex != i);
            drawPart(x + i * xSpacing, y + i * ySpacing, parts[i], g);
        }
        noReflectionForMultipleParts = false;
    }

    /**
     * @param x           The x-position.
     * @param y           The y-position.
     * @param percentSize The size in percent (100 is normal size).
     * @param parts       The parts to paint (Cover, Card, Piece,...).
     * @param xSpacing    The horizontal spacing.
     * @param ySpacing    The vertical spacing.
     * @param g           The canvas object.
     */
    public void drawParts(int x, int y, int percentSize, Part[] parts, int xSpacing, int ySpacing, Canvas g) {
        int reflectionIndex = checkReflectionIndex(parts, ySpacing);
        for (int i = 0; parts != null && i < parts.length; i++) {
            noReflectionForMultipleParts = (reflectionIndex != -1 && reflectionIndex != i);
            drawPart(x + i * xSpacing, y + i * ySpacing, percentSize, parts[i], g);
        }
        noReflectionForMultipleParts = false;
    }

    /**
     * Draws the specified parts.
     *
     * @param x           The x-position.
     * @param y           The y-position.
     * @param percentSize The size in percent (100 is normal size).
     * @param hAlignment  The horizontal alignment relative to the specified position (ConstantValue.ALIGN_LEFT, ConstantValue.ALIGN_CENTER, ConstantValue.ALIGN_RIGHT).
     * @param vAlignment  The vertical alignment relative to the specified position (ConstantValue.ALIGN_TOP, ConstantValue.ALIGN_MIDDLE, ConstantValue.ALIGN_BOTTOM).
     * @param parts       The parts to paint (Cover, Card, Piece,...).
     * @param xSpacing    The horizontal spacing.
     * @param ySpacing    The vertical spacing.
     * @param g           The canvas object.
     */
    public void drawParts(int x, int y, int percentSize, Align hAlignment, int vAlignment, Part[] parts, int xSpacing, int ySpacing, Canvas g) {
        int reflectionIndex = checkReflectionIndex(parts, ySpacing);
        Point drawPos = PartUtil.getDrawingPosition(x, y, percentSize, hAlignment, vAlignment, parts, xSpacing, ySpacing);
        for (int i = 0; parts != null && i < parts.length; i++) {
            noReflectionForMultipleParts = (reflectionIndex != -1 && reflectionIndex != i);
            drawPart(drawPos.x + i * xSpacing, drawPos.y + i * ySpacing, percentSize, parts[i], g);
        }
        noReflectionForMultipleParts = false;
    }

    /**
     * Draws the specified parts.
     *
     * @param x           The x-position.
     * @param y           The y-position.
     * @param percentSize The size in percent (100 is normal size).
     * @param angle       The angle.
     * @param parts       The parts to paint (Cover, Card, Piece,...).
     * @param xSpacing    The horizontal spacing.
     * @param ySpacing    The vertical spacing.
     * @param g           The canvas object.
     */
    public void drawParts(int x, int y, int percentSize, double angle, Part[] parts, int xSpacing, int ySpacing, Canvas g) {
        int reflectionIndex = checkReflectionIndex(parts, ySpacing);
        if (angle == 0.0 || angle == 180.0) {
            for (int i = 0; parts != null && i < parts.length; i++) {
                noReflectionForMultipleParts = (reflectionIndex != -1 && reflectionIndex != i);
                drawPart(x + i * xSpacing, y + i * ySpacing, percentSize, parts[i], g);
            }
        } else if (parts != null && parts.length > 0) {
            // works only for positive spacing!
            int len = parts.length - 1;
            int width = transform(xSpacing * len + Math.floor((parts[len].getImage().getWidth() * percentSize * 1.0) / 100.0));
            int height = transform(ySpacing * len + Math.floor((parts[len].getImage().getHeight() * percentSize * 1.0) / 100.0));
            BitmapCanvas g2 = new BitmapCanvas(width, height);
            for (int i = 0; i < parts.length; i++) {
                if (parts[i] != null) {
                    noReflectionForMultipleParts = (reflectionIndex != -1 && reflectionIndex != i);
                    checkShadowForPart(parts[i]);
                    checkReflectionForPart(parts[i]);
                    drawImage(i * xSpacing, i * ySpacing, percentSize, parts[i].getImage(), g2);
                }
            }
            drawImage(transform(x), transform(y), 100, angle, g2.getBitmap(), g, true);
        }
        noReflectionForMultipleParts = false;
    }

    /**
     * Draws the specified parts at the specified position of the specified playing field under consideration of the alignment.
     *
     * @param field      The playing field.
     * @param positionId The id of the position in the playing field.
     * @param hAlignment The horizontal alignment relative to the specified position (ConstantValue.ALIGN_LEFT, ConstantValue.ALIGN_CENTER, ConstantValue.ALIGN_RIGHT).
     * @param vAlignment The vertical alignment relative to the specified position (ConstantValue.ALIGN_TOP, ConstantValue.ALIGN_MIDDLE, ConstantValue.ALIGN_BOTTOM).
     * @param parts      The parts to draw.
     * @param xSpacing   The horizontal spacing.
     * @param ySpacing   The vertical spacing.
     * @param g          The canvas object.
     */
    public void drawParts(PlayingField field, String positionId, Align hAlignment, int vAlignment, Part[] parts, int xSpacing, int ySpacing, Canvas g) {
        Rectangle rect = field.getFieldRectangle(field.getField(positionId));
        Point drawPos = PartUtil.getDrawingPosition(rect.x, rect.y, hAlignment, vAlignment, rect.width, rect.height, true);
        drawParts(drawPos.x, drawPos.y, 100, hAlignment, vAlignment, parts, xSpacing, ySpacing, g);
    }

    /**
     * Draws the specified parts.
     *
     * @param x             The x-position.
     * @param y             The y-position.
     * @param percentSize   The size in percent (100 is normal size).
     * @param orientation   The orientation with which the parts should be drawn.
     * @param wrapThreshold The threshold at which a wrapping is done.
     * @param parts         The parts to draw.
     * @param xSpacing      The horizontal spacing.
     * @param ySpacing      The vertical spacing.
     * @param g             The canvas object.
     */
    public void drawParts(int x, int y, int percentSize, Orientation orientation, int wrapThreshold, Part[] parts, int xSpacing, int ySpacing, Canvas g) {
        drawParts(x, y, percentSize, 0.0, orientation, wrapThreshold, parts, xSpacing, ySpacing, g);
    }

    /**
     * Draws the specified parts.
     *
     * @param x             The x-position.
     * @param y             The y-position.
     * @param percentSize   The size in percent (100 is normal size).
     * @param angle         The angle with which the parts should be drawn.
     * @param orientation   The orientation with which the parts should be drawn.
     * @param wrapThreshold The threshold at which a wrapping is done.
     * @param parts         The parts to draw.
     * @param xSpacing      The horizontal spacing.
     * @param ySpacing      The vertical spacing.
     * @param g             The canvas object.
     */
    public void drawParts(int x, int y, int percentSize, double angle, Orientation orientation, int wrapThreshold, Part[] parts, int xSpacing, int ySpacing, Canvas g) {
        for (int i = 0; parts != null && i < parts.length; i++) {
            int rowIndex = getOrientationRowIndex(orientation, i, wrapThreshold);
            int colIndex = getOrientationColumnIndex(orientation, i, wrapThreshold);
            int offsetX = getOrientationXOffset(orientation, colIndex, xSpacing);
            int offsetY = getOrientationYOffset(orientation, rowIndex, ySpacing);

            drawPart(x + offsetX, y + offsetY, percentSize, angle, parts[i], g);
        }
    }

    /**
     * Returns the row index from the position where a part should be drawn corresponding to the specified orientation.
     *
     * @param orientation   The orientation with which the parts should be drawn.
     * @param index         The index of the part.
     * @param wrapThreshold The threshold at which a wrapping is done.
     * @return The row index from the position where a part should be drawn corresponding to the specified orientation.
     */
    private int getOrientationRowIndex(Orientation orientation, int index, int wrapThreshold) {
        switch (orientation) {
            case DOWN_LTR:
            case DOWN_RTL:
            case UP_LTR:
            case UP_RTL:
                return index % wrapThreshold;
            case LTR_DOWN:
            case LTR_UP:
            case RTL_DOWN:
            case RTL_UP:
                return index / wrapThreshold;
        }
        return 0;
    }

    /**
     * Returns the column index from the position where a part should be drawn corresponding to the specified orientation.
     *
     * @param orientation   The orientation with which the parts should be drawn.
     * @param index         The index of the part.
     * @param wrapThreshold The threshold at which a wrapping is done.
     * @return The column index from the position where a part should be drawn corresponding to the specified orientation.
     */
    private int getOrientationColumnIndex(Orientation orientation, int index, int wrapThreshold) {
        switch (orientation) {
            case DOWN_LTR:
            case DOWN_RTL:
            case UP_LTR:
            case UP_RTL:
                return index / wrapThreshold;
            case LTR_DOWN:
            case LTR_UP:
            case RTL_DOWN:
            case RTL_UP:
                return index % wrapThreshold;
        }
        return 0;
    }

    /**
     * Returns the horizontal offset where to draw a part corresponding to the specified orientation and column index.
     *
     * @param orientation The orientation with which the parts should be drawn.
     * @param colIndex    The column index from the position where the part should be drawn.
     * @param xSpacing    The horizontal spacing.
     * @return The horizontal offset where to draw a part corresponding to the specified orientation and column index.
     */
    private int getOrientationXOffset(Orientation orientation, int colIndex, int xSpacing) {
        switch (orientation) {
            case LTR_DOWN:
            case LTR_UP:
            case DOWN_LTR:
            case UP_LTR:
                return colIndex * xSpacing;
            case DOWN_RTL:
            case UP_RTL:
            case RTL_DOWN:
            case RTL_UP:
                return colIndex * xSpacing * -1;
        }
        return 0;
    }

    /**
     * Returns the vertical offset where to draw a part corresponding to the specified orientation and row index.
     *
     * @param orientation The orientation with which the parts should be drawn.
     * @param rowIndex    The row index from the position where the part should be drawn.
     * @param ySpacing    The vertical spacing.
     * @return The vertical offset where to draw a part corresponding to the specified orientation and row index.
     */
    private int getOrientationYOffset(Orientation orientation, int rowIndex, int ySpacing) {
        switch (orientation) {
            case DOWN_LTR:
            case DOWN_RTL:
            case LTR_DOWN:
            case RTL_DOWN:
                return rowIndex * ySpacing;
            case UP_LTR:
            case UP_RTL:
            case LTR_UP:
            case RTL_UP:
                return rowIndex * ySpacing * -1;
        }
        return 0;
    }

    /**
     * If there are multiple parts, draw only one reflection if they have vertical spacing.
     *
     * @param parts    the parts to be painted
     * @param ySpacing the ySpacing
     * @return the index with reflection or -1 if reflection is allowed for all parts
     */
    private int checkReflectionIndex(Part[] parts, int ySpacing) {
        if (ySpacing == 0) {
            return -1;
        } else {
            return (ySpacing < 0) ? 0 : parts.length - 1;
        }
    }

    /**
     * Draw the image zoom dependent.
     */
    public void drawImage(int x, int y, Bitmap img, Canvas g) {
        if (img != null) {
            drawImage(x, y, img.getWidth(), img.getHeight(), img, g);
        }
    }

    /**
     * Draw the image with the given size in percent (100 is normal size).
     * The zoom is considered, too.
     */
    public void drawImage(int x, int y, int percentSize, Bitmap img, Canvas g) {
        if (img != null) {
            drawImage(x, y, percentSize, 0.0, img, g);
        }
    }

    /**
     * Draw the image with the given size in percent (100 is normal size).
     * The zoom is considered, too.
     */
    public void drawImage(int x, int y, int percentSize, double angle, Bitmap img, Canvas g) {
        drawImage(x, y, percentSize, angle, img, g, false);
    }

    /**
     * Draw the image with the given size in percent (100 is normal size).
     * The zoom is considered, too.
     */
    public void drawImage(int x, int y, int percentSize, double angle, Bitmap img, Canvas g, boolean ignoreZoom) {
        if (img != null) {
            int newX = (ignoreZoom) ? x : transform(x);
            int newY = (ignoreZoom) ? y : transform(y);
            int width = (int) ((ignoreZoom) ? img.getWidth() * (percentSize / 100.0) : transform(img.getWidth() * (percentSize / 100.0)));
            int height = (int) ((ignoreZoom) ? img.getHeight() * (percentSize / 100.0) : transform(img.getHeight() * (percentSize / 100.0)));
            int tx = newX + width / 2;
            int ty = newY + height / 2;
            if (!HGBaseTools.isEqual(angle, 0.0)) {
                g.save();
                g.rotate((float) angle, tx, ty);
            }
            // test whether to draw a shadow
            if (nextImageShadow != null) {
                nextImageShadow = transformShadow(nextImageShadow, angle);
                drawImageShadow(img, nextImageShadow, newX, newY, width, height, g, ignoreZoom);
                nextImageShadow = null;
            }
            // test whether to draw a reflection
            if (nextImageReflection != null) {
                drawImageReflection(img, nextImageReflection, newX, newY, width, height, angle, g, ignoreZoom);
                nextImageReflection = null;
            }
            // draw the image with the correct size
            g.drawBitmap(img, null, new Rect(newX, newY, newX + width, newY + height), null);
            if (!HGBaseTools.isEqual(angle, 0.0)) {
                g.restore();
            }
        }
    }

    /**
     * Draws a shadow for the next image that will be drawn.
     *
     * @param shadow the shadow data for the next image.
     */
    public void drawNextImageWithShadow(ImageShadow shadow) {
        this.nextImageShadow = shadow;
    }

    /**
     * Draws a reflection for the next image that will be drawn.
     *
     * @param reflection the reflection data for the next image.
     */
    public void drawNextImageWithReflection(ImageReflection reflection) {
        this.nextImageReflection = reflection;
    }

    /**
     * Checks whether the given part has a shadow and advices to draw a shadow for the image if so.
     *
     * @param part the part to test.
     */
    void checkShadowForPart(Part part) {
        ImageShadow shadow = part.getShadow();
        if (shadow != null && HGBaseConfig.getBoolean(ConstantValue.CONFIG_DRAW_SHADOWS, false)) {
            drawNextImageWithShadow(shadow);
        }
    }

    /**
     * Checks whether the given part has a reflection and advices to draw a reflection for the image if so.
     *
     * @param part the part to test.
     */
    void checkReflectionForPart(Part part) {
        ImageReflection reflection = part.getReflection();
        if (reflection != null && !noReflectionForMultipleParts && HGBaseConfig.getBoolean(ConstantValue.CONFIG_DRAW_REFLECTIONS, false)) {
            drawNextImageWithReflection(reflection);
        }
    }

    /**
     * Draws the shadow for the given image.
     *
     * @param img        the original image.
     * @param shadow     the object holding the shadow data.
     * @param x          the x position of the original image.
     * @param y          the y position of the original image.
     * @param width      the new width for resizing.
     * @param height     the new height for resizing.
     * @param g          the graphics object.
     * @param ignoreZoom indicates whether zoom shall be ignored.
     */
    private void drawImageShadow(Bitmap img, ImageShadow shadow, int x, int y, int width, int height, Canvas g, boolean ignoreZoom) {
        // create a black image
        Bitmap blackImg = createBlackImage(img);
        // paint it with the given transparency
        Paint paint = new Paint();
        paint.setAlpha((int) (shadow.getAlpha() * 255));
        int shadowX = (ignoreZoom) ? shadow.getShadowX() : transform(shadow.getShadowX());
        int shadowY = (ignoreZoom) ? shadow.getShadowY() : transform(shadow.getShadowY());
        g.drawBitmap(blackImg, null, new Rect(x + shadowX, y + shadowY, x + width + shadowX, y + height + shadowY), paint);
    }

    /**
     * Transforms the shadow depending on the angle to be painted correct again.
     *
     * @param shadow the original shadow, must not be null
     * @param angle  the rotation angle
     * @return the transformed shadow or the original one
     */
    private ImageShadow transformShadow(ImageShadow shadow, double angle) {
        if (angle == 0.0) {
            return shadow;
        } else {
            int x = shadow.getShadowX();
            int y = shadow.getShadowX();
            EffectType shadowType = getEffectType(angle);
            x = (shadowType == EffectType.LEFT || shadowType == EffectType.TOP) ? -x : x;
            y = (shadowType == EffectType.RIGHT || shadowType == EffectType.TOP) ? -y : y;
            return new ImageShadow(x, y, shadow.getAlpha());
        }
    }

    /**
     * Create a black image from the given one by maintaining the alpha component.
     * For performance reason all black representations of images are stored in a map.
     *
     * @param img the original image.
     * @return the black image.
     */
    private Bitmap createBlackImage(Bitmap img) {
        Bitmap blackImg = blackImages.get(img.toString());
        if (blackImg == null) {
            BitmapCanvas blackCanvas = new BitmapCanvas(img.getWidth(), img.getHeight());
            float[] blackMask = new float[]{0.1f, 0, 0, 0, 0, 0, 0.1f, 0, 0, 0, 0, 0, 0.1f, 0, 0, 0, 0, 0, 1, 0};
            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(blackMask)));
            blackCanvas.drawBitmap(img, 0, 0, paint);
            blackImg = blackCanvas.getBitmap();
            blackImages.put(img.toString(), blackImg);
        }
        return blackImg;
    }

    /**
     * Draws the reflection for the given image.
     *
     * @param img        the original image.
     * @param reflection the object reflection the shadow data.
     * @param x          the x position of the original image.
     * @param y          the y position of the original image.
     * @param width      the new width for resizing.
     * @param height     the new height for resizing.
     * @param angle      the angle the image is rotated by
     * @param g          the canvas object.
     * @param ignoreZoom indicates whether zoom shall be ignored.
     */
    private void drawImageReflection(Bitmap img, ImageReflection reflection, int x, int y, int width, int height, double angle, Canvas g, boolean ignoreZoom) {
        int reflectionHeight = (int) (height * reflection.getImageHeight());
        int reflectionX = x;
        int reflectionY = y;
        int reflectionGap = (ignoreZoom) ? reflection.getGap() : transform(reflection.getGap());
        EffectType reflectionType = getEffectType(angle);
        // create a new image with the height of the reflection
        int imageWidth = (reflectionType == EffectType.NORMAL || reflectionType == EffectType.TOP) ? width : reflectionHeight;
        int imageHeight = (reflectionType == EffectType.NORMAL || reflectionType == EffectType.TOP) ? reflectionHeight : height;
        BitmapCanvas rg = new BitmapCanvas(imageWidth, imageHeight);
        Bitmap reflectionImg = rg.getBitmap();
        // draw the reflection depending on the angle
        if (reflectionType == EffectType.NORMAL) {
            rg.drawBitmap(img, new Rect(0, (int) (img.getHeight() * (1 - reflection.getImageHeight())), img.getWidth(), img.getHeight()), new Rect(0, 0, imageWidth, imageHeight), null);
            fillGradientTransparency(true, reflection.getAlphaStart(), reflection.getAlphaEnd(), imageWidth, imageHeight, rg);
            reflectionImg = flipReflectionImage(true, reflectionImg);
            reflectionY = reflectionY + height + reflectionGap;
        } else if (reflectionType == EffectType.TOP) {
            rg.drawBitmap(img, new Rect(0, 0, img.getWidth(), (int) (img.getHeight() * reflection.getImageHeight())), new Rect(0, 0, imageWidth, imageHeight), null);
            fillGradientTransparency(true, reflection.getAlphaEnd(), reflection.getAlphaStart(), imageWidth, imageHeight, rg);
            reflectionImg = flipReflectionImage(true, reflectionImg);
            reflectionY = reflectionY - reflectionHeight - reflectionGap;
        } else if (reflectionType == EffectType.LEFT) {
            rg.drawBitmap(img, new Rect(0, 0, (int) (img.getWidth() * reflection.getImageHeight()), img.getHeight()), new Rect(0, 0, imageWidth, imageHeight), null);
            fillGradientTransparency(false, reflection.getAlphaEnd(), reflection.getAlphaStart(), imageWidth, imageHeight, rg);
            reflectionImg = flipReflectionImage(false, reflectionImg);
            reflectionX = reflectionX - reflectionHeight - reflectionGap;
        } else if (reflectionType == EffectType.RIGHT) {
            rg.drawBitmap(img, new Rect((int) (img.getWidth() * (1 - reflection.getImageHeight())), 0, img.getWidth(), img.getHeight()), new Rect(0, 0, imageWidth, imageHeight), null);
            fillGradientTransparency(false, reflection.getAlphaStart(), reflection.getAlphaEnd(), imageWidth, imageHeight, rg);
            reflectionImg = flipReflectionImage(false, reflectionImg);
            reflectionX = reflectionX + width + reflectionGap;
        }
        // draw the reflection image
        g.drawBitmap(reflectionImg, reflectionX, reflectionY, null);
    }

    /**
     * Flips the reflection image and returns the transformed one.
     *
     * @param vertical true for vertical flip, false for horizontal flip
     * @param image    the image to flip
     * @return the new image with the transformation.
     */
    private Bitmap flipReflectionImage(boolean vertical, Bitmap image) {
        Matrix m = new Matrix();
        if (vertical) {
            m.preScale(1, -1);
        } else {
            m.preScale(-1, 1);
        }
        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), m, false);
    }

    /**
     * Fills a rectangle with gradient transparency.
     *
     * @param vertical   true for vertical gradient, false for horizontal gradient
     * @param alphaStart the starting alpha value
     * @param alphaEnd   the ending alpha value
     * @param width      the width of the rectangle
     * @param height     the height of the rectangle
     * @param g2         the graphics object
     */
    private void fillGradientTransparency(boolean vertical, float alphaStart, float alphaEnd, int width, int height, Canvas g2) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        Shader shader = new LinearGradient(0, 0, (!vertical) ? width : 0, (vertical) ? height : 0, new Color(0.0f, 0.0f, 0.0f, alphaEnd).getColorCode(), new Color(0.0f, 0.0f, 0.0f, alphaStart).getColorCode(), Shader.TileMode.CLAMP);
        p.setShader(shader);
        // the bitmap is dynamically generated beforehand
        g2.drawRect(0, 0, width, height, p);
    }

    /**
     * Returns the effect type depending on the angle.
     *
     * @param angle the angle to rotate the image
     * @return the according effect type, at least <code>EffectType.NORMAL</code>
     */
    private EffectType getEffectType(double angle) {
        // boolean isNormal = (angle<=45.0 || angle>315.0);
        if (angle > 135.0 && angle <= 215.0) {
            return EffectType.TOP;
        }
        if (angle > 45.0 && angle <= 135.0) {
            return EffectType.RIGHT;
        }
        if (angle > 215.0 && angle <= 315.0) {
            return EffectType.LEFT;
        }
        return EffectType.NORMAL;
    }

    /**
     * Draw a line zoom dependent.
     */
    public void drawLine(int x1, int y1, int x2, int y2, Canvas g) {
        g.drawLine(transform(x1), transform(y1), transform(x2), transform(y2), currentPaint);
    }

    /**
     * Draw a rectangle zoom dependent.
     *
     * @param x      The x coordinate where to draw the rectangle.
     * @param y      The y coordinate where to draw the rectangle.
     * @param width  The width of the rectangle to draw.
     * @param height The height of the rectangle to draw.
     * @param filled Flag, if the rectangle should be filled.
     * @param g      The canvas where to draw the rectangle.
     */
    public void drawRect(int x, int y, int width, int height, boolean filled, Canvas g) {
        Style oldStyle = setStyle(filled);
        g.drawRect(new RectF(transform(x), transform(y), transform(x + width), transform(y + height)), currentPaint);
        currentPaint.setStyle(oldStyle);
    }

    /**
     * Draw a rectangle zoom dependent.
     *
     * @param rect   The rectangle to draw.
     * @param filled Flag, if the rectangle should be filled.
     * @param g      The canvas where to draw the rectangle.
     */
    public void drawRect(@NonNull Rectangle rect, boolean filled, Canvas g) {
        drawRect(rect.x, rect.y, rect.width, rect.height, filled, g);
    }

    /**
     * Draw an oval zoom dependent.
     */
    public void drawOval(int x, int y, int width, int height, boolean filled, Canvas g) {
        Style oldStyle = setStyle(filled);
        g.drawOval(new RectF(transform(x), transform(y), transform(x + width), transform(y + height)), currentPaint);
        currentPaint.setStyle(oldStyle);
    }

    /**
     * Draws an oval zoom dependent into the specified position of the specified playing field.
     *
     * @param field      The playing field.
     * @param positionId The id of the position in the playing field.
     * @param margin     The margin to the border of the field the oval should have.
     * @param filled     If {@code true} the oval will be filled.
     * @param g          The canvas object.
     */
    public void drawOval(PlayingField field, String positionId, int margin, boolean filled, Canvas g) {
        Rectangle rect = field.getFieldRectangle(field.getField(positionId));
        drawOval(rect.x + margin, rect.y + margin, rect.width - (margin * 2), rect.height - (margin * 2), filled, g);
    }

    /**
     * Draw an arc zoom dependent.
     */
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle, boolean filled, Canvas g) {
        Style oldStyle = setStyle(filled);
        RectF rect = new RectF(transform(x), transform(y), transform(x + width), transform(y + height));
        g.drawArc(rect, startAngle, arcAngle, false, currentPaint);
        currentPaint.setStyle(oldStyle);
    }

    /**
     * Draw the given polygon zoom dependent.<p>
     * NOTE: The parameter {@code filled} is not currently implemented.
     */
    public void drawPolygon(Polygon p, boolean filled, Canvas g) {
        if (p != null && p.npoints > 0) {
            p.addPoint(p.xpoints[0], p.ypoints[0]);
            float[] xy = new float[p.npoints * 4];
            for (int i = 0; i < p.npoints - 1; i++) {
                xy[i * 4] = transform(p.xpoints[i]);
                xy[i * 4 + 1] = transform(p.ypoints[i]);
                xy[i * 4 + 2] = transform(p.xpoints[i + 1]);
                xy[i * 4 + 3] = transform(p.ypoints[i + 1]);
            }
            Style oldStyle = setStyle(filled);
            g.drawLines(xy, currentPaint);
            currentPaint.setStyle(oldStyle);
        }
    }

    /**
     * Draws an arrow from the x1/y1 to the x2/y2 position. The line of the error will be a single line with
     * size 1.
     *
     * @param x1   the starting x
     * @param y1   the starting y
     * @param x2   the target x
     * @param y2   the target y
     * @param size the size of the arrow
     * @param g    the canvas object
     */
    public void drawArrow(int x1, int y1, int x2, int y2, int size, Canvas g) {
        x1 = transform(x1);
        y1 = transform(y1);
        x2 = transform(x2);
        y2 = transform(y2);
        size = Math.max(transform(size), 1);
        double dx = x2 - x1;
        double dy = y2 - y1;
        float angle = (float) Math.atan2(dy, dx);
        g.drawLine(x1, y1, x2, y2, currentPaint);
        g.save();
        g.rotate(angle, x2, y2);
        Polygon p = new Polygon(new int[]{x2, x2 - size, x2 - size, x2}, new int[]{y2, y2 - size, y2 + size, y2}, 4);
        drawPolygon(p, true, g);
        g.restore();
    }

    /**
     * Draws a text zoom dependent.
     *
     * @param text the text to draw.
     * @param x    the x position.
     * @param y    the y position.
     * @param g    the canvas to draw on
     */
    public void drawString(String text, int x, int y, Canvas g) {
        drawString(text, x, y, Align.LEFT, g);
    }

    /**
     * Draws a text zoom dependent.
     *
     * @param text      the text to draw.
     * @param x         the x position.
     * @param y         the y position.
     * @param alignment the text alignment
     * @param g         the canvas to draw on
     */
    public void drawString(String text, int x, int y, Align alignment, Canvas g) {
        float oldFontSize = currentPaint.getTextSize();
        float zoomFontSize = (float) Math.floor(oldFontSize * getZoomFactor());
        changeFontSize(zoomFontSize);
        int newX = transform(x);
        int newY = transform(y);
        currentPaint.setTextAlign(alignment);
        g.drawText(text, 0, text.length(), newX, newY, currentPaint);
        changeFontSize(oldFontSize);
    }

    /**
     * Draw the text with center alignment, relative from the given x/y position.
     *
     * @param text the text to draw.
     * @param x    the x position.
     * @param y    the y position.
     * @param g    the canvas to draw on
     */
    public void drawStringCentered(String text, int x, int y, Canvas g) {
        drawString(text, x, y, Align.CENTER, g);
    }

    /**
     * Draws the specified text as the specified position of the specified playing field under consideration of the alignment.
     *
     * @param text       The text to draw.
     * @param field      The playing field.
     * @param positionId The id of the position in the playing field.
     * @param hAlignment The horizontal alignment relative to the specified position (ConstantValue.ALIGN_LEFT, ConstantValue.ALIGN_CENTER, ConstantValue.ALIGN_RIGHT).
     * @param g          The canvas object.
     */
    public void drawString(String text, PlayingField field, String positionId, Align hAlignment, Canvas g) {
        Rectangle rect = field.getFieldRectangle(field.getField(positionId));
        Point drawPos = PartUtil.getDrawingPosition(rect.x, rect.y, hAlignment, ConstantValue.ALIGN_BOTTOM, rect.width, rect.height, true);
        drawString(text, drawPos.x, drawPos.y, hAlignment, g);
    }

    /**
     * Draws the specified text into the specified area with the specified wrap style and horizontal alignment.
     *
     * @param text       The text to draw.
     * @param area       The area where the text should be drawn.
     * @param wrapStyle  The style how to wrap the text in the area.
     * @param hAlignment The horizontal alignment in the area (ConstantValue.ALIGN_LEFT, ConstantValue.ALIGN_CENTER, ConstantValue.ALIGN_RIGHT).
     * @param g          The canvas object.
     */
    public void drawString(@NonNull String text, @NonNull Rectangle area, @NonNull WrapStyle wrapStyle, @NonNull Paint.Align hAlignment, @NonNull Canvas g) {
        float oldFontSize = currentPaint.getTextSize();
        float zoomFontSize = (float) Math.floor(oldFontSize * getZoomFactor());
        changeFontSize(zoomFontSize);

        Rectangle newRect = transform(area);
        int textHeight = getStringHeight(text);

        int x = getXAreaHorizontalAlignment(hAlignment, newRect);
        int y = newRect.y + textHeight;
        currentPaint.setTextAlign(hAlignment);

        String[] lines = wrapText(text, newRect.width, wrapStyle);
        for (String line : lines) {
            g.drawText(line, 0, line.length(), x, y, currentPaint);
            y += textHeight;
        }

        changeFontSize(oldFontSize);
    }

    /**
     * Returns the index of the next possible wrap position.
     *
     * @param text      The text to wrap.
     * @param fromIndex The index where to start from.
     * @param wrapStyle The style how to wrap the text.
     * @return The index of the next possible wrap position.
     */
    protected int getNextWrapIndex(@NonNull String text, int fromIndex, @NonNull WrapStyle wrapStyle) {
        if (wrapStyle == WrapStyle.CHARACTER) {
            return fromIndex;
        } else if (wrapStyle == WrapStyle.WORD) {
            int nextIndex = text.indexOf(' ', fromIndex);
            return (nextIndex > -1) ? nextIndex + 1 : text.length();
        } else {
            return text.length();
        }
    }

    /**
     * Returns the portion of the specified text which fits into the specified width under consideration of the specified wrap style.
     *
     * @param text      The text to wrap.
     * @param maxWidth  The maximum width the portion may have.
     * @param wrapStyle The style how to wrap the text.
     * @return The portion of the specified text which fits into the specified width under consideration of the specified wrap style.
     */
    protected String getFittingLine(@NonNull String text, int maxWidth, @NonNull WrapStyle wrapStyle) {
        String line = null;
        int fromIndex = 0;
        int lastFittingIndex = -1;
        do {
            int nextWrapIndex = getNextWrapIndex(text, fromIndex, wrapStyle);
            int width = getStringWidth(text.substring(0, nextWrapIndex).trim());
            if (width > maxWidth) {
                line = text.substring(0, (lastFittingIndex >= 0) ? lastFittingIndex : nextWrapIndex);
            } else {
                lastFittingIndex = nextWrapIndex;
            }
            fromIndex = nextWrapIndex + 1;
        } while ((fromIndex <= text.length()) && (line == null));
        if (line == null) {
            line = text;
        }
        return line;
    }

    /**
     * Wraps the specified text to that it fits into the specified width.
     *
     * @param text      The text to wrap.
     * @param maxWidth  The maximum width one line may have.
     * @param wrapStyle The style how to wrap the text.
     * @return The specified text wrapped into as much lines as needed.
     */
    protected String[] wrapText(@NonNull String text, int maxWidth, @NonNull WrapStyle wrapStyle) {
        List<String> lines = new ArrayList<>();
        String remaining = text;
        while (remaining.length() > 0) {
            String line = getFittingLine(remaining, maxWidth, wrapStyle);
            lines.add(line.trim());
            remaining = remaining.substring(line.length());
        }
        return lines.toArray(new String[0]);
    }

    /**
     * @param size the size of the font
     * @return the old size
     */
    public float changeFontSize(float size) {
        float oldSize = currentPaint.getTextSize();
        currentPaint.setTextSize(size);
        return oldSize;
    }

    /**
     * @param style the style of the font
     * @return the old style
     */
    public Typeface changeFontStyle(Typeface style) {
        Typeface oldStyle = currentPaint.getTypeface();
        currentPaint.setTypeface(style);
        return oldStyle;
    }

    /**
     * Convenient method to change the color.
     *
     * @param newColor the new color
     */
    public void setColor(Color newColor) {
        changeColor(newColor);
    }

    /**
     * @param newColor The new color that shall be set.
     * @return The old color
     */
    public Color changeColor(Color newColor) {
        Color oldColor = new Color(currentPaint.getColor());
        currentPaint.setColor(newColor.getColorCode());
        return oldColor;
    }

    /**
     * @param newStyle the new style
     * @return the old style
     */
    public Style changeStyle(Style newStyle) {
        Style oldStyle = currentPaint.getStyle();
        currentPaint.setStyle(newStyle);
        return oldStyle;
    }

    /**
     * Sets the style to fill (and stroke) or stroke only.
     *
     * @param filled true to fill and stroke
     * @return the old style
     */
    public Style setStyle(boolean filled) {
        if (filled) {
            return changeStyle(Style.FILL_AND_STROKE);
        } else {
            return changeStyle(Style.STROKE);
        }
    }

    /**
     * Returns the center position of the game field.
     *
     * @return The center position of the game field.
     */
    protected Point getCenterPos() {
        return new Point(getFieldWidth() / 2, getFieldHeight() / 2);
    }

    /**
     * Returns the height of the game field.
     *
     * @return The height of the game field.
     */
    public int getFieldHeight() {
        return gameConfig.getFieldHeight();
    }

    /**
     * Returns the width of the game field.
     *
     * @return The width of the game field.
     */
    public int getFieldWidth() {
        return gameConfig.getFieldWidth();
    }

    /**
     * Returns the size of the game field.
     *
     * @return The size of the game field.
     */
    public Dimension getFieldSize() {
        return new Dimension(getFieldWidth(), getFieldHeight());
    }

    @SuppressLint("RtlHardcoded")
    protected enum EffectType {
        NORMAL, TOP, LEFT, RIGHT
    }

    /**
     * The possible styles to wrap a string.
     */
    public enum WrapStyle {
        NONE, CHARACTER, WORD
    }
}
