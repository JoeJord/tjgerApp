package com.tjger.gui.gamepanel.configurablelayout.painter;

import com.tjger.game.completed.GameConfig;
import com.tjger.gui.SimpleGamePanel;
import com.tjger.gui.completed.Card;
import com.tjger.gui.completed.CardSet;
import com.tjger.gui.completed.configurablelayout.layoutelement.CardsetLayout;

/**
 * The painter for an element of the type "Cardset" of a configurable layout.
 *
 * @param <G> The type of the game panel.
 */
public class CardsetPainter<G extends SimpleGamePanel> implements IndexedElementSetPainter<CardsetLayout, CardSet, Card, G> {
    /**
     * The game panel where to paint.
     */
    private final G gamePanel;

    /**
     * Constructs a new instance.
     *
     * @param gamePanel The game panel where to paint.
     */
    public CardsetPainter(G gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public G getGamePanel() {
        return gamePanel;
    }

    @Override
    public CardSet getActiveElement(CardsetLayout element) {
        return GameConfig.getInstance().getActiveCardSet(element.getType());
    }

    @Override
    public Card[] getActiveElements(CardsetLayout element) {
        return getActiveElement(element).getCards();
    }
}
