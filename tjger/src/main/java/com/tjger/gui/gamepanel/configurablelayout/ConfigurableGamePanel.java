package com.tjger.gui.gamepanel.configurablelayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Canvas;

import com.tjger.game.completed.GameConfig;
import com.tjger.gui.AnimatedGamePanel;
import com.tjger.gui.completed.Part;
import com.tjger.gui.completed.configurablelayout.layoutelement.AreaLayout;
import com.tjger.gui.completed.configurablelayout.layoutelement.CardsetLayout;
import com.tjger.gui.completed.configurablelayout.layoutelement.LayoutElement;
import com.tjger.gui.completed.configurablelayout.layoutelement.PartLayout;
import com.tjger.gui.completed.configurablelayout.layoutelement.PartsetLayout;
import com.tjger.gui.completed.configurablelayout.layoutelement.PiecesetLayout;
import com.tjger.gui.completed.configurablelayout.layoutelement.PlayerInfoLayout;
import com.tjger.gui.gamepanel.configurablelayout.painter.AreaPainter;
import com.tjger.gui.gamepanel.configurablelayout.painter.CardsetPainter;
import com.tjger.gui.gamepanel.configurablelayout.painter.ElementPainter;
import com.tjger.gui.gamepanel.configurablelayout.painter.PartPainter;
import com.tjger.gui.gamepanel.configurablelayout.painter.PartsetPainter;
import com.tjger.gui.gamepanel.configurablelayout.painter.PiecesetPainter;
import com.tjger.gui.gamepanel.configurablelayout.painter.PlayerInfoPainter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import at.hagru.hgbase.android.awt.Dimension;
import at.hagru.hgbase.android.awt.Insets;

/**
 * A game panel where the layout of the game parts are configured in an XML file.
 */
@SuppressLint("ViewConstructor")
public class ConfigurableGamePanel extends AnimatedGamePanel {
    /**
     * A reference to the game configuration.
     */
    private final GameConfig config;
    /**
     * The map with the painters for the layout elements.
     */
    private HashMap<Class<? extends LayoutElement>, ElementPainter<? extends LayoutElement, ? extends Part>> painterMap;

    /**
     * Constructs a new instance.
     *
     * @param activity the activity
     */
    public ConfigurableGamePanel(Activity activity) {
        super(activity);
        config = getGameConfig();
        createPainter();
    }

    /**
     * Creates the painter for the layout elements.
     */
    private void createPainter() {
        painterMap = new HashMap<>();
        painterMap.put(AreaLayout.class, createAreaPainter());
        painterMap.put(PartLayout.class, createPartPainter());
        painterMap.put(PartsetLayout.class, createPartsetPainter());
        painterMap.put(CardsetLayout.class, createCardsetPainter());
        painterMap.put(PiecesetLayout.class, createPiecesetPainter());
        painterMap.put(PlayerInfoLayout.class, createPlayerInfoPainter());
    }

    @Override
    public Dimension getFieldSize() {
        Insets margin = config.getGamefieldLayoutMargin();
        return new Dimension(getFieldWidth() - margin.left - margin.right, getFieldHeight() - margin.top - margin.bottom);
    }

    /**
     * Creates the painter for layout elements of the type "Area".
     *
     * @return The created painter.
     */
    protected AreaPainter createAreaPainter() {
        return new AreaPainter(this);
    }

    /**
     * Returns the painter for layout elements of the type "Area".
     *
     * @return The painter for layout elements of the type "Area".
     */
    public final AreaPainter getAreaPainter() {
        return (AreaPainter) painterMap.get(AreaLayout.class);
    }

    /**
     * Creates the painter for layout elements of the type "Part".
     *
     * @return The created painter.
     */
    protected PartPainter createPartPainter() {
        return new PartPainter(this);
    }

    /**
     * Returns the painter for layout elements of the type "Part".
     *
     * @return The painter for layout elements of the type "Part".
     */
    protected final PartPainter getPartPainter() {
        return (PartPainter) painterMap.get(PartLayout.class);
    }

    /**
     * Creates the painter for layout elements of the type "Partset".
     *
     * @return The created painter.
     */
    protected PartsetPainter createPartsetPainter() {
        return new PartsetPainter(this);
    }

    /**
     * Returns the painter for layout elements of the type "Partset".
     *
     * @return The painter for layout elements of the type "Partset".
     */
    protected final PartsetPainter getPartsetPainter() {
        return (PartsetPainter) painterMap.get(PartsetLayout.class);
    }

    /**
     * Creates the painter for layout elements of the type "Cardset".
     *
     * @return The created painter.
     */
    protected CardsetPainter createCardsetPainter() {
        return new CardsetPainter(this);
    }

    /**
     * Returns the painter for layout elements of the type "Cardset".
     *
     * @return The painter for layout elements of the type "Cardset".
     */
    protected final CardsetPainter getCardsetPainter() {
        return (CardsetPainter) painterMap.get(CardsetLayout.class);
    }

    /**
     * Creates the painter for layout elements of the type "Pieceset".
     *
     * @return The created painter.
     */
    protected PiecesetPainter createPiecesetPainter() {
        return new PiecesetPainter(this);
    }

    /**
     * Returns the painter for layout elements of the type "Pieceset".
     *
     * @return The painter for layout elements of the type "Pieceset".
     */
    protected final PiecesetPainter getPiecesetPainter() {
        return (PiecesetPainter) painterMap.get(PiecesetLayout.class);
    }

    /**
     * Creates the painter for layout elements of the type "player information".
     *
     * @return The created painter.
     */
    protected PlayerInfoPainter createPlayerInfoPainter() {
        return new PlayerInfoPainter(this);
    }

    /**
     * Returns the painter for layout elements of the type "player information".
     *
     * @return The painter for layout elements of the type "player information".
     */
    protected final PlayerInfoPainter getPlayerInfoPainter() {
        return (PlayerInfoPainter) painterMap.get(PlayerInfoLayout.class);
    }

    /**
     * Returns {@code true} if it is allowed to paint the parts.
     *
     * @return {@code true} if it is allowed to paint the parts.
     */
    protected boolean isPaintPartsAllowed() {
        return true;
    }

    /**
     * Returns the layout elements of the specified layout class.
     *
     * @param elementClass The class of the layout element to get.
     * @return The layout elements of the specified layout class.
     */
    private Map<String, LayoutElement> getLayoutElements(Class<? extends LayoutElement> elementClass) {
        return config.getLayoutElements().getOrDefault(elementClass, new HashMap<>());
    }

    /**
     * Returns a stream of the layout areas of a configurable field.
     *
     * @return A stream of the layout areas of a configurable field.
     */
    protected Stream<AreaLayout> getLayoutAreas() {
        return getLayoutElements(AreaLayout.class).values().stream().map(AreaLayout.class::cast);
    }

    /**
     * Returns a stream of the layout parts of a configurable field.
     *
     * @return A stream of the layout parts of a configurable field.
     */
    protected Stream<PartLayout> getLayoutParts() {
        return getLayoutElements(PartLayout.class).values().stream().map(PartLayout.class::cast);
    }

    /**
     * Returns a stream of the layout partsets of a configurable field.
     *
     * @return A stream of the layout partsets of a configurable field.
     */
    protected Stream<PartsetLayout> getLayoutPartsets() {
        return getLayoutElements(PartsetLayout.class).values().stream().map(PartsetLayout.class::cast);
    }

    /**
     * Returns a stream of the layout cardsets of a configurable field.
     *
     * @return A stream of the layout cardsets of a configurable field.
     */
    protected Stream<CardsetLayout> getLayoutCardsets() {
        return getLayoutElements(CardsetLayout.class).values().stream().map(CardsetLayout.class::cast);
    }

    /**
     * Returns a stream of the layout piecesets of a configurable field.
     *
     * @return A stream of the layout piecesets of a configurable field.
     */
    protected Stream<PiecesetLayout> getLayoutPiecesets() {
        return getLayoutElements(PiecesetLayout.class).values().stream().map(PiecesetLayout.class::cast);
    }

    /**
     * Returns a stream of the layout player informations of a configurable field.
     *
     * @return A stream of the layout player informations of a configurable field.
     */
    protected final Stream<PlayerInfoLayout> getLayoutPlayerInfos() {
        return getLayoutElements(PlayerInfoLayout.class).values().stream().map(PlayerInfoLayout.class::cast);
    }

    @Override
    protected void paintParts(Canvas g) {
        super.paintParts(g);
        if (!isPaintPartsAllowed()) {
            return;
        }
        paintLayoutAreas(getLayoutAreas(), g);
        paintLayoutParts(getLayoutParts(), g);
        paintLayoutPartsets(getLayoutPartsets(), g);
        paintLayoutCardsets(getLayoutCardsets(), g);
        paintLayoutPiecesets(getLayoutPiecesets(), g);
        paintLayoutPlayerInfos(getLayoutPlayerInfos(), g);
    }

    /**
     * Paints the specified layout element of the type "Area".
     *
     * @param areas A stream with the layout configurations for the areas to paint.
     * @param g     The canvas object.
     */
    protected void paintLayoutAreas(Stream<AreaLayout> areas, Canvas g) {
        if (areas == null) {
            return;
        }
        areas.forEach(area -> paintLayoutArea(area, g));
    }

    /**
     * Paints the specified layout element of the type "Area".
     *
     * @param area The layout configuration for the area to paint.
     * @param g    The canvas object.
     */
    protected void paintLayoutArea(AreaLayout area, Canvas g) {
        AreaPainter areaPainter = getAreaPainter();
        if ((area == null) || (!areaPainter.shouldPaint(area))) {
            return;
        }
        areaPainter.paint(area, g);
    }

    /**
     * Paints the specified layout elements of the type "Part".
     *
     * @param parts A stream of layout configurations for the parts to paint.
     * @param g     The canvas object.
     */
    protected void paintLayoutParts(Stream<PartLayout> parts, Canvas g) {
        if (parts == null) {
            return;
        }
        parts.forEach(part -> paintLayoutPart(part, g));
    }

    /**
     * Paints the specified layout element of the type "Part".
     *
     * @param part The layout configuration for the part to paint.
     * @param g    The canvas object.
     */
    protected void paintLayoutPart(PartLayout part, Canvas g) {
        PartPainter partPainter = getPartPainter();
        if ((part == null) || (!partPainter.shouldPaint(part))) {
            return;
        }
        partPainter.paint(part, g);
    }

    /**
     * Paints the specified layout elements of the type "Partset".
     *
     * @param partsets A stream of layout configurations for the partsets to paint.
     * @param g        The canvas object.
     */
    protected void paintLayoutPartsets(Stream<PartsetLayout> partsets, Canvas g) {
        if (partsets == null) {
            return;
        }
        partsets.forEach(partset -> paintLayoutPartset(partset, g));
    }

    /**
     * Paints the specified layout element of the type "Partset".
     *
     * @param partset The layout configuration for the partset to paint.
     * @param g       The canvas object.
     */
    protected void paintLayoutPartset(PartsetLayout partset, Canvas g) {
        PartsetPainter partsetPainter = getPartsetPainter();
        if ((partset == null) || (!partsetPainter.shouldPaint(partset))) {
            return;
        }
        partsetPainter.paint(partset, g);
    }

    /**
     * Paints the specified layout elements of the type "Cardset".
     *
     * @param cardsets A stream of layout configurations for the cardsets to paint.
     * @param g        The canvas object.
     */
    protected void paintLayoutCardsets(Stream<CardsetLayout> cardsets, Canvas g) {
        if (cardsets == null) {
            return;
        }
        cardsets.forEach(cardset -> paintLayoutCardset(cardset, g));
    }

    /**
     * Paints the specified layout element of the type "Cardset".
     *
     * @param cardset The layout configuration for the cardset to paint.
     * @param g       The canvas object.
     */
    protected void paintLayoutCardset(CardsetLayout cardset, Canvas g) {
        CardsetPainter cardsetPainter = getCardsetPainter();
        if ((cardset == null) || (!cardsetPainter.shouldPaint(cardset))) {
            return;
        }
        cardsetPainter.paint(cardset, g);
    }

    /**
     * Paints the specified layout elements of the type "Pieceset".
     *
     * @param piecesets A stream of layout configurations for the piecesets to paint.
     * @param g         The canvas object.
     */
    protected void paintLayoutPiecesets(Stream<PiecesetLayout> piecesets, Canvas g) {
        if (piecesets == null) {
            return;
        }
        piecesets.forEach(pieceset -> paintLayoutPieceset(pieceset, g));
    }

    /**
     * Paints the specified layout element of the type "Pieceset".
     *
     * @param pieceset The layout configuration for the pieceset to paint.
     * @param g        The canvas object.
     */
    protected void paintLayoutPieceset(PiecesetLayout pieceset, Canvas g) {
        PiecesetPainter piecesetPainter = getPiecesetPainter();
        if ((pieceset == null) || (!piecesetPainter.shouldPaint(pieceset))) {
            return;
        }
        piecesetPainter.paint(pieceset, g);
    }

    /**
     * Paints the specified layout elements for the type "player information".
     *
     * @param playerInfos A stream of layout configurations for the player informations to paint.
     * @param g           The canvas object.
     */
    private void paintLayoutPlayerInfos(Stream<PlayerInfoLayout> playerInfos, Canvas g) {
        if (playerInfos == null) {
            return;
        }
        playerInfos.forEach(playerInfo -> paintLayoutPlayerInfo(playerInfo, g));
    }

    /**
     * Paints the specified layout element of the type "player information".
     *
     * @param playerInfo The layout configuration for the player information to paint
     * @param g          The canvas object.
     */
    private void paintLayoutPlayerInfo(PlayerInfoLayout playerInfo, Canvas g) {
        PlayerInfoPainter playerInfoPainter = getPlayerInfoPainter();
        if ((playerInfo == null) || (!playerInfoPainter.shouldPaint(playerInfo))) {
            return;
        }
        playerInfoPainter.paint(playerInfo, g);
    }
}
