package com.tjger.gui.gamepanel.configurablelayout.painter;

import com.tjger.game.completed.GameConfig;
import com.tjger.gui.SimpleGamePanel;
import com.tjger.gui.completed.Part;
import com.tjger.gui.completed.PartSet;
import com.tjger.gui.completed.configurablelayout.layoutelement.PartsetLayout;

/**
 * The painter for an element of the type "Partset" of a configurable layout.
 *
 * @param <G> The type of the game panel.
 */
public class PartsetPainter<G extends SimpleGamePanel> implements ElementSetPainter<PartsetLayout, PartSet, Part, G> {
    /**
     * The game panel where to paint.
     */
    private final G gamePanel;

    /**
     * Constructs a new instance.
     *
     * @param gamePanel The game panel where to paint.
     */
    public PartsetPainter(G gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public G getGamePanel() {
        return gamePanel;
    }

    @Override
    public PartSet getActiveElement(PartsetLayout element) {
        return GameConfig.getInstance().getActivePartSet(element.getType());
    }

    @Override
    public Part[] getActiveElements(PartsetLayout element) {
        return getActiveElement(element).getParts().toArray(new Part[0]);
    }
}
