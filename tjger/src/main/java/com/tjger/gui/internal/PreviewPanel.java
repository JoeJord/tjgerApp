package com.tjger.gui.internal;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.tjger.game.completed.GameConfig;
import com.tjger.game.completed.GameManager;
import com.tjger.gui.SimpleGamePanel;
import com.tjger.gui.completed.Part;
import com.tjger.lib.ConstantValue;

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
    protected PartsDlg dlg;
    protected String[] userParts;
    protected String[] userPartSets;

    public PreviewPanel(PartsDlg partsDlg, int width, int height) {
        super(partsDlg);
        this.dlg = partsDlg;
        this.width = width;
        this.height = height;
        this.config = GameManager.getInstance().getGameConfig();
        this.cardSetTypes = config.getCardSetTypes();
        this.userParts = config.getPartTypes();
        this.userPartSets = config.getPartSetTypes();
    }

    @Override
    protected void paintBackground(Canvas g) {
        paintBackgroundColor(dlg.getBackgroundColor(), g);
        paintBackgroundImage(dlg.getSelectedBackground(), g);
    }

    @Override
    protected void paintBoard(Canvas g) {
        paintBoard(dlg.getSelectedBoard(), g);
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
        int x = distance;
        int y = 10;
        x = drawSelectedPart(x, y, dlg.indexOf(ConstantValue.CONFIG_COVER, true), g);
        for (String cardSetType : cardSetTypes) {
            x = drawSelectedPart(x, y, dlg.indexOf(cardSetType, true), g);
        }
        drawSelectedPart(x, y, dlg.indexOf(ConstantValue.CONFIG_PIECESET, true), g);
    }

    /**
     * Paints the user parts (Parts, PartSets).
     */
    protected void paintUserParts(Canvas g) {
        int x = distance;
        int y = getFieldHeight() / 2;
        userParts = config.getPartTypes();
        userPartSets = config.getPartSetTypes();
        // paint first the part sets and then the parts
        for (String userPartSet : userPartSets) {
            x = drawSelectedPart(x, y, dlg.indexOf(userPartSet, false), g);
        }
        for (String userPart : userParts) {
            x = drawSelectedPart(x, y, dlg.indexOf(userPart, false), g);
        }
    }

    /**
     * Draws the selected part.
     *
     * @param x     X-position.
     * @param y     Y-position.
     * @param index The index of the selected part in PartsDlg.
     * @param g     The graphics object.
     * @return The new x-position;
     */
    private int drawSelectedPart(int x, int y, int index, Canvas g) {
        if (index >= 0) {
            Part selectedPart = dlg.getSelectedPart(index);
            Bitmap img = (selectedPart == null) ? null : selectedPart.getImage();
            if (img != null) {
                drawPart(x, y, dlg.getSelectedPart(index), g);
                x = x + distance + img.getWidth();
            }
        }
        return x;
    }

    @Override
    public double getZoomFactor() {
        double wz = (double) width / (double) getFieldWidth();
        double hz = (double) height / (double) getFieldHeight();
        return Math.min(wz, hz);
    }

    @Override
    public int getFieldWidth() {
        return config.getFieldWidth(dlg.getSelectedBoard(), dlg.getSelectedBackground());
    }

    @Override
    public int getFieldHeight() {
        return config.getFieldHeight(dlg.getSelectedBoard(), dlg.getSelectedBackground());
    }

}
