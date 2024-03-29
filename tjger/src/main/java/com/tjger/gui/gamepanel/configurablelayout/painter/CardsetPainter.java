package com.tjger.gui.gamepanel.configurablelayout.painter;

import com.tjger.game.completed.GameConfig;
import com.tjger.gui.SimpleGamePanel;
import com.tjger.gui.completed.Card;
import com.tjger.gui.completed.CardSet;
import com.tjger.gui.completed.configurablelayout.layoutelement.CardsetLayout;

/**
 * The painter for an element of the type "Cardset" of a configurable layout.
 */
public class CardsetPainter implements IndexedElementSetPainter<CardsetLayout, CardSet, Card> {
    /**
     * The game panel where to paint.
     */
    private final SimpleGamePanel gamePanel;

    /**
     * Constructs a new instance.
     *
     * @param gamePanel The game panel where to paint.
     */
    public CardsetPainter(SimpleGamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public SimpleGamePanel getGamePanel() {
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
