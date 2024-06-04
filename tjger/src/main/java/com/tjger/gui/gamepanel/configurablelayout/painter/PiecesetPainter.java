package com.tjger.gui.gamepanel.configurablelayout.painter;

import com.tjger.game.completed.GameConfig;
import com.tjger.gui.SimpleGamePanel;
import com.tjger.gui.completed.Piece;
import com.tjger.gui.completed.PieceSet;
import com.tjger.gui.completed.configurablelayout.layoutelement.PiecesetLayout;

/**
 * The painter for an element of the type "Pieceset" of a configurable layout.
 *
 * @param <G> The type of the game panel.
 */
public class PiecesetPainter<G extends SimpleGamePanel> implements ElementSetPainter<PiecesetLayout, PieceSet, Piece, G> {
    /**
     * The game panel where to paint.
     */
    private final G gamePanel;

    /**
     * Constructs a new instance.
     *
     * @param gamePanel The game panel where to paint.
     */
    public PiecesetPainter(G gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public G getGamePanel() {
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
