package com.tjger.gui.gamepanel.configurablelayout.painter;

import com.tjger.game.completed.GameConfig;
import com.tjger.gui.SimpleGamePanel;
import com.tjger.gui.completed.Piece;
import com.tjger.gui.completed.PieceSet;
import com.tjger.gui.completed.configurablelayout.layoutelement.PiecesetLayout;

/**
 * The painter for an element of the type "Pieceset" of a configurable layout.
 */
public class PiecesetPainter implements ElementSetPainter<PiecesetLayout, PieceSet, Piece> {
    /**
     * The game panel where to paint.
     */
    private final SimpleGamePanel gamePanel;

    /**
     * Constructs a new instance.
     *
     * @param gamePanel The game panel where to paint.
     */
    public PiecesetPainter(SimpleGamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public SimpleGamePanel getGamePanel() {
        return gamePanel;
    }

    @Override
    public PieceSet getActiveElement(PiecesetLayout element) {
        return GameConfig.getInstance().getActivePieceSet();
    }

    @Override
    public Piece[] getActiveElements(PiecesetLayout element) {
        return getActiveElement(element).getPieces();
    }
}
