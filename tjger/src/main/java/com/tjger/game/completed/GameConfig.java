package com.tjger.game.completed;

import android.widget.ImageView.ScaleType;

import com.tjger.game.SimpleComputerPlayer;
import com.tjger.gui.completed.Arrangement;
import com.tjger.gui.completed.Background;
import com.tjger.gui.completed.BackgroundColor;
import com.tjger.gui.completed.Board;
import com.tjger.gui.completed.CardSet;
import com.tjger.gui.completed.Cover;
import com.tjger.gui.completed.Part;
import com.tjger.gui.completed.PartSet;
import com.tjger.gui.completed.PieceSet;
import com.tjger.gui.completed.configurablelayout.layoutelement.AreaLayout;
import com.tjger.gui.completed.configurablelayout.layoutelement.LayoutElement;
import com.tjger.lib.ArrayUtil;
import com.tjger.lib.ConstantValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import at.hagru.hgbase.android.awt.Color;
import at.hagru.hgbase.android.awt.Insets;
import at.hagru.hgbase.lib.HGBaseConfig;
import at.hagru.hgbase.lib.HGBaseTools;
import at.hagru.hgbase.lib.internal.IntBooleanStringMap;

/**
 * Contains all game specific configurations.  Do not inherit from this class.
 *
 * @author hagru
 */
public final class GameConfig {

    public static final int STANDARD_ZOOM = 100;
    public static final int ORDERBY_NONE = 0;
    public static final int ORDERBY_COLOR = 1;
    public static final int ORDERBY_VALUE = 2;
    public static final int PLAYERS_LEFTTORIGHT = 0;
    public static final int PLAYERS_CLOCKWISE = 1;
    public static final int PLAYERS_COUNTERCLOCKWISE = 2;
    public static final String CONFIG_SHADOW = "shadow";
    public static final String CONFIG_REFLECTION = "reflection";

    static final String EMPTY_STRING = "";
    static final int WELCOME_TIMEOUT_DEFAULT = 1700;

    private static final int CONFIG_STANDARD_WIDTH = 800;
    private static final int CONFIG_STANDARD_HEIGHT = 480;

    private static final GameConfig INSTANCE = new GameConfig();

    /*
     * All data needs to be package-protected for the game config file reader.
     */ boolean proVersion;
    String fullscreenMode;
    boolean advertisements;
    String advertisementURL;
    String advertisementErrorPageURL;
    int advertisementWidthPercent;
    int advertisementHeightPercent;
    BackgroundColor backgroundColor;
    Background[] backgrounds;
    Board[] boards;
    Cover[] covers;
    Map<String, List<CardSet>> cardSetsMap;
    PieceSet[] pieceSets;
    Arrangement[] arrangements;
    Map<String, Color> colors; // the color types as key and the default color (or null) as value
    Map<String, List<Part>> partMap; // for all other parts
    Map<String, List<PartSet>> partSetMap; // for all other part sets
    Map<String, String> extendPartMap; // for extending part
    Map<String, String> extendPartSetMap; // for extending part and partset
    Map<String, String> extendCvpMap; // for extending color value part
    boolean completeArrangement;
    int minZoom;
    int maxZoom;
    Map<String, String> extensionMap;
    Map<String, String> pathMap;
    boolean networkPossible;
    int networkPort;
    int minPlayers;
    int maxPlayers;
    int defaultPlayers;
    int fieldWidth;
    int fieldHeight;
    boolean hasErrors;
    boolean oneHumanPlayer;
    boolean withoutHumanPlayer;
    /**
     * The default number of human players if more (or less) than one human player is allowed.
     */
    int defaultHumanPlayers;
    String playerPieceColor;
    boolean helpHidden;
    boolean rememberScores;
    boolean rememberGames;
    int highScoreLength;
    boolean onlyFirstHighScore;
    boolean isLowerScoreBetter;
    int statisticsScrollWhen;
    boolean changedOnNewGame;
    boolean changedOnNewRound;
    boolean changedOnNewTurn;
    boolean changedAfterMove;
    boolean dialogAfterTurn;
    boolean dialogAfterRound;
    boolean dialogAfterGame;
    boolean interruptAfterRound;
    IntBooleanStringMap setOrderBy;
    /**
     * Flag, if the {@code resetMove} method of the game state shall be called local during network games.
     */
    boolean localGameStateMove;
    boolean localGameStateTurn;
    boolean localGameStateRound;
    boolean localGameStateGame;
    LinkedHashMap<String, String> hintsMap;
    int delayRound;
    int delayTurn;
    int delayMove;
    int delayPlayer;
    double gameSpeed;
    int playersOrder;
    boolean recordOnNewGame;
    boolean recordOnNewRound;
    boolean recordOnNewTurn;
    String gameStateXmlRoot;
    String mainMenuImageScaleType;
    /**
     * The layout elements of the configurable game field.
     */
    Map<Class<? extends LayoutElement>, Map<String, LayoutElement>> layoutElements;
    /**
     * The margin of the configurable game field.
     */
    Insets gamefieldLayoutMargin;

    private GameConfig() {
        super();
        GameConfigFileReader.read(this);
    }

    /**
     * @return The one and only instance of GameConfig.
     */
    public static GameConfig getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the name of the player with the specified index.
     *
     * @param playerIndex The index of the player for which the name should be returned.
     * @return The name of the player with the specified index.
     */
    public static String getPlayerName(int playerIndex) {
        String nameConfigKey = ConstantValue.CONFIG_PLAYERNAME + ((playerIndex == PlayerManager.HUMAN_PLAYER_INDEX) ? "" : playerIndex);
        return HGBaseConfig.get(nameConfigKey);
    }

    /**
     * @return True if there were errors in the game configuration file.
     */
    public boolean hasErrors() {
        return this.hasErrors;
    }

    /**
     * @return True if the game is currently the pro version, i.e., the pro attribute is set.
     */
    public boolean isProVersion() {
        return this.proVersion;
    }

    /**
     * Returns the fullscreen mode.
     *
     * @return The fullscreen mode.
     */
    public String getFullscreenMode() {
        return this.fullscreenMode;
    }

    /**
     * @return True if advertisements shall be shown, i.e., it's not the pro version and the advertisements attribute is set.
     */
    public boolean hasAdvertisements() {
        return this.advertisements && !isProVersion();
    }

    /**
     * Returns the advertisement URL.
     *
     * @return The advertisement URL.
     */
    public String getAdvertisementURL() {
        return advertisementURL;
    }

    /**
     * Returns the URL of the page, which should be displayed if the advertisement page could not be loaded.
     *
     * @return The URL of the page, which should be displayed if the advertisement page could not be loaded.
     */
    public String getAdvertisementErrorPageURL() {
        return advertisementErrorPageURL;
    }

    /**
     * Returns how much percent of the screen width the advertisement view should have.
     *
     * @return The percentage of the screen width, how much the advertisement view should have.
     */
    public int getAdvertisementWidthPercent() {
        return advertisementWidthPercent;
    }

    /**
     * Returns how much percent of the screen height the advertisement view should have.
     *
     * @return The percentage of the screen height, how much the advertisement view should have.
     */
    public int getAdvertisementHeightPercent() {
        return advertisementHeightPercent;
    }

    /**
     * @return True, if there is only one human player (in front of the computer) allowed.
     */
    public boolean isOneHumanPlayer() {
        return oneHumanPlayer;
    }

    /**
     * @return True, if games without human players are allowed.
     */
    public boolean isWithoutHumanPlayer() {
        return withoutHumanPlayer;
    }

    /**
     * Returns the default number of human players if more (or less) than one human player is allowed.
     *
     * @return The default number of human players if more (or less) than one human player is allowed.
     */
    public int getDefaultHumanPlayers() {
        return defaultHumanPlayers;
    }

    /**
     * Returns the minimum allowed number of human players.
     *
     * @return The minimum allowed number of human players.
     */
    public int getMinHumanPlayers() {
        return (isWithoutHumanPlayer()) ? 0 : 1;
    }

    /**
     * @return The minimal possible zoom.
     */
    public int getMinZoom() {
        return minZoom;
    }

    /**
     * @return The maximal possible zoom.
     */
    public int getMaxZoom() {
        return maxZoom;
    }

    /**
     * @return The active zoom or the standard zoom, if there is no active zoom set.
     */
    public int getActiveZoom() {
        int zoom = HGBaseConfig.getInt(ConstantValue.CONFIG_ZOOM);
        if (zoom != HGBaseTools.INVALID_INT && zoom >= getMinZoom() && zoom <= getMaxZoom()) {
            return zoom;
        } else {
            return getStandardZoom();
        }
    }

    /**
     * @return The standard zoom.
     */
    public int getStandardZoom() {
        int zoom = STANDARD_ZOOM;
        if (zoom >= getMinZoom() && zoom <= getMaxZoom()) {
            return zoom;
        } else {
            return (getMinZoom() + getMaxZoom()) / 2;
        }
    }

    /**
     * @return The minimum number of players that are allowed.
     */
    public int getMinPlayers() {
        return minPlayers;
    }

    /**
     * @return The maximum number of players that are allowed.
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * @return the default number of players.
     */
    public int getDefaultPlayers() {
        return defaultPlayers;
    }

    /**
     * @return The order of the active players (in contrast to all players).
     * @see #PLAYERS_LEFTTORIGHT (default)
     * @see #PLAYERS_CLOCKWISE
     * @see #PLAYERS_COUNTERCLOCKWISE
     */
    public int getPlayersOrder() {
        return playersOrder;
    }

    /**
     * @return The length of the high-score list.
     */
    public int getHighScoreLength() {
        return highScoreLength;
    }

    /**
     * @return True, if only the winner shall get an entry in the highscore list.
     */
    public boolean isOnlyFirstHighScore() {
        return onlyFirstHighScore;
    }

    /**
     * Returns <code>true</code> if a lower score is better.
     *
     * @return <code>true</code> if a lower score is better.
     */
    public boolean isLowerScoreBetter() {
        return this.isLowerScoreBetter;
    }

    /**
     * @return The number of rows of the panels in the statistics dialog when scrolling shall start (or 0 if no scrolling).
     */
    public int getStatisticsScrollWhen() {
        return statisticsScrollWhen;
    }

    /**
     * @return True, if played and won games shall be remembered.
     */
    public boolean isRememberGames() {
        return rememberGames;
    }

    /**
     * @param remember True to remember the played and won games.
     */
    public void setRememberGames(boolean remember) {
        rememberGames = remember;
    }

    /**
     * @return True, if scores shall be kept between games.
     */
    public boolean isRememberScores() {
        return rememberScores;
    }

    /**
     * @param remember True to remember the scores between games.
     */
    public void setRememberScores(boolean remember) {
        rememberScores = remember;
    }

    /**
     * @return Whether network games are possible or not.
     */
    public boolean isNetworkPossible() {
        return networkPossible;
    }

    /**
     * @return The network port that is defined in the file.
     */
    public int getNetworkDefaultPort() {
        return networkPort;
    }

    /**
     * @return The network port that is used.
     */
    public int getNetworkPort() {
        int port = HGBaseConfig.getInt(ConstantValue.CONFIG_NETWORKPORT);
        if (port != HGBaseTools.INVALID_INT) {
            return port;
        }
        return networkPort;
    }

    /**
     * @return True if the resetGame method of the game state shall be called local during network games.
     */
    public boolean isLocalGameStateGame() {
        return localGameStateGame;
    }

    /**
     * @param local True to call the method resetGame locally during network games.
     */
    public void setLocalGameStateGame(boolean local) {
        localGameStateGame = local;
    }

    /**
     * @return True if the resetRound method of the game state shall be called local during network games.
     */
    public boolean isLocalGameStateRound() {
        return localGameStateRound;
    }

    /**
     * @param local True to call the method resetRound locally during network games.
     */
    public void setLocalGameStateRound(boolean local) {
        localGameStateRound = local;
    }

    /**
     * @return True if the resetTurn method of the game state shall be called local during network games.
     */
    public boolean isLocalGameStateTurn() {
        return localGameStateTurn;
    }

    /**
     * @param local True to call the method resetTurn locally during network games.
     */
    public void setLocalGameStateTurn(boolean local) {
        localGameStateTurn = local;
    }

    /**
     * Returns {@code true} if the {@code resetMove} method of the game state shall be called local during network games.
     *
     * @return {@code true} if the {@code resetMove} method of the game state shall be called local during network games.
     */
    public boolean isLocalGameStateMove() {
        return localGameStateMove;
    }

    /**
     * @return All possible arrangements.
     */
    public Arrangement[] getArrangements() {
        return HGBaseTools.clone(arrangements);
    }

    /**
     * @return the background color, may be null
     */
    public BackgroundColor getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @return All possible backgrounds.
     */
    public Background[] getBackgrounds() {
        return HGBaseTools.clone(backgrounds);
    }

    /**
     * @return All possible boards.
     */
    public Board[] getBoards() {
        return HGBaseTools.clone(boards);
    }

    /**
     * @return All possible covers.
     */
    public Cover[] getCovers() {
        return HGBaseTools.clone(covers);
    }

    /**
     * @return All possible card sets (of the default type).
     */
    public CardSet[] getCardSets() {
        return getCardSets(ConstantValue.CONFIG_CARDSET);
    }

    /**
     * @param type The card set type.
     * @return The card sets of the given type.
     */
    public CardSet[] getCardSets(String type) {
        return getCardSetsFromMap(cardSetsMap, type).toArray(new CardSet[0]);
    }

    /**
     * @return A list with all existing card set types.
     */
    public String[] getCardSetTypes() {
        return cardSetsMap.keySet().toArray(new String[0]);
    }

    /**
     * @return All possible piece sets.
     */
    public PieceSet[] getPieceSets() {
        return HGBaseTools.clone(pieceSets);
    }

    /**
     * @param name Name of the arrangement.
     * @return The arrangement or null.
     */
    public Arrangement getArrangement(String name) {
        Part part = getPartByName(getArrangements(), name);
        if (part != null) {
            return (Arrangement) part;
        }
        return null;
    }

    /**
     * @param name Name of the background.
     * @return The background or null.
     */
    public Background getBackground(String name) {
        Part part = getPartByName(getBackgrounds(), name);
        if (part != null) {
            return (Background) part;
        }
        return null;
    }

    /**
     * @param name Name of the board.
     * @return The board or null.
     */
    public Board getBoard(String name) {
        Part part = getPartByName(getBoards(), name);
        if (part != null) {
            return (Board) part;
        }
        return null;
    }

    /**
     * @param name Name of the cover.
     * @return The cover or null.
     */
    public Cover getCover(String name) {
        Part part = getPartByName(getCovers(), name);
        if (part != null) {
            return (Cover) part;
        }
        return null;
    }

    /**
     * @param name Name of the card set.
     * @return The card set or null.
     */
    public CardSet getCardSet(String name) {
        return getCardSet(ConstantValue.CONFIG_CARDSET, name);
    }

    /**
     * @param type The type name of the card set.
     * @param name Name of the card set.
     * @return The card set or null.
     */
    public CardSet getCardSet(String type, String name) {
        Part part = getPartByName(getCardSets(type), name);
        return (CardSet) part;
    }

    /**
     * @param name Name of the piece set.
     * @return The piece set or null.
     */
    public PieceSet getPieceSet(String name) {
        Part part = getPartByName(getPieceSets(), name);
        return (PieceSet) part;
    }

    /**
     * @return The active arrangement or null.
     */
    public Arrangement getActiveArrangement() {
        String name = getActivePartName(getArrangements(), ConstantValue.CONFIG_ARRANGEMENT);
        if (HGBaseConfig.existsKey(ConstantValue.CONFIG_ARRANGEMENT) && !name.equals(HGBaseConfig.get(ConstantValue.CONFIG_ARRANGEMENT))) {
            // if there is a user defined arrangement, a dummy id was stored
            return null;
        }
        return getArrangement(name);
    }

    /**
     * @return the active background color or null if no color is set
     */
    public Color getActiveBackgroundColor() {
        return (backgroundColor == null) ? null : backgroundColor.getActiveColor();
    }

    /**
     * @return The active background or null.
     */
    public Background getActiveBackground() {
        String name = getActivePartName(getBackgrounds(), ConstantValue.CONFIG_BACKGROUND);
        return getBackground(name);
    }

    /**
     * @return The active board or null.
     */
    public Board getActiveBoard() {
        String name = getActivePartName(getBoards(), ConstantValue.CONFIG_BOARD);
        return getBoard(name);
    }

    /**
     * @return The active cover or null.
     */
    public Cover getActiveCover() {
        String name = getActivePartName(getCovers(), ConstantValue.CONFIG_COVER);
        return getCover(name);
    }

    /**
     * @return The active card set or null.
     */
    public CardSet getActiveCardSet() {
        return getActiveCardSet(ConstantValue.CONFIG_CARDSET);
    }

    /**
     * @param type The card set type.
     * @return The active card set or null.
     */
    public CardSet getActiveCardSet(String type) {
        String name = getActivePartName(getCardSets(type), type);
        return getCardSet(type, name);
    }

    /**
     * @return The active piece set or null.
     */
    public PieceSet getActivePieceSet() {
        String name = getActivePartName(getPieceSets(), ConstantValue.CONFIG_PIECESET);
        return getPieceSet(name);
    }

    /**
     * Returns all parts from the given type.
     * Only user defined parts are returned.
     *
     * @param type The user defined type.
     * @return A list with parts.
     */
    public Part[] getParts(String type) {
        return getPartsFromMap(partMap, type).toArray(new Part[0]);
    }

    /**
     * Returns a user defined part from the given type with the given name.
     *
     * @param type The user defined type.
     * @param name Name of the part.
     * @return The part or null.
     */
    public Part getPart(String type, String name) {
        return getPartByName(getParts(type), name);
    }

    /**
     * Returns all part sets from the given type.
     * Only user defined part sets are returned.
     *
     * @param type The user defined type.
     * @return A list with part sets.
     */
    public PartSet[] getPartSets(String type) {
        return getPartSetsFromMap(partSetMap, type).toArray(new PartSet[0]);
    }

    /**
     * Returns a user defined part set from the given type with the given name.
     *
     * @param type The user defined type.
     * @param name Name of the part set.
     * @return The part set or null.
     */
    public PartSet getPartSet(String type, String name) {
        return (PartSet) getPartByName(getPartSets(type), name);
    }

    /**
     * @return All used defined part types that aren't hidden.
     */
    public String[] getPartTypes() {
        return partMap.keySet().toArray(new String[0]);
    }

    /**
     * @return All used defined part types that aren't hidden.
     */
    public String[] getPartSetTypes() {
        return partSetMap.keySet().toArray(new String[0]);
    }

    /**
     * Returns the active part from the user defined type.
     * An active part must not be {@code hidden}.
     *
     * @param type The user defined type.
     * @return The active part or null.
     */
    public Part getActivePart(String type) {
        String name = getActivePartName(getParts(type), type);
        return getPart(type, name);
    }

    /**
     * Returns the active part from a user defined part set.
     *
     * @param partSetType the user defined part set
     * @param partColor   the user defined part color
     * @param sequence    the sequence of the part
     * @return the active part or null
     */
    public Part getActivePartFromSet(String partSetType, String partColor, int sequence) {
        PartSet ps = getActivePartSet(partSetType);
        return (ps == null) ? null : ps.getPart(partColor, sequence);
    }

    /**
     * Returns the active part set from the user defined type.
     * An active part set must not be {@code hidden}.
     *
     * @param type The user defined type.
     * @return The active part set or null.
     */
    public PartSet getActivePartSet(String type) {
        String name = getActivePartName(getPartSets(type), type);
        return getPartSet(type, name);
    }

    /**
     * @return A list with all possible color types.
     */
    public String[] getColorTypes() {
        return colors.keySet().toArray(new String[0]);
    }

    /**
     * @param colorType A color type.
     * @return The active color of a color type, can be null.
     */
    public Color getActiveColor(String colorType) {
        Color c = HGBaseConfig.getColor(colorType);
        return (c == null) ? getDefaultColor(colorType) : c;
    }

    /**
     * @param colorType A color type.
     * @return The default color or null if no one defined or if the type is invalid.
     */
    public Color getDefaultColor(String colorType) {
        return colors.get(colorType);
    }

    /**
     * @return The width of the game field.
     */
    public int getFieldWidth() {
        return (fieldWidth > 0) ? fieldWidth : getFieldWidth(getActiveBoard(), getActiveBackground());
    }

    /**
     * Sets the width of the game field. The value will <b>NOT</b> be written
     * back to the configuration file.
     *
     * @param fieldWidth The width of the game field.
     */
    public void setFieldWidth(int fieldWidth) {
        this.fieldWidth = fieldWidth;
    }

    /**
     * Is used internally.
     *
     * @return The width of the game field.
     */
    public int getFieldWidth(Board board, Background back) {
        int wBoard = (board == null) ? 0 : (int) (board.getImage().getWidth() * board.getZoom() / 100.0) + 2 * board.getXPos();
        if (wBoard == 1) {
            wBoard = 0;
        }
        int wBack = (back == null || back.getImage() == null || back.isRepeatMode()) ? 0 : (int) (back.getImage().getWidth() * back.getZoom() / 100.0);
        if (wBoard > wBack) {
            return wBoard;
        }
        if (wBack > wBoard) {
            return wBack;
        }
        if (wBack > fieldWidth) {
            return wBack; // wBack==wBoard
        }
        if (fieldWidth > 0) {
            return fieldWidth;
        }
        return CONFIG_STANDARD_WIDTH;
    }

    /**
     * @return The height of the game field.
     */
    public int getFieldHeight() {
        return (fieldHeight > 0) ? fieldHeight : getFieldHeight(getActiveBoard(), getActiveBackground());
    }

    /**
     * Sets the height of the game field. The value will <b>NOT</b> be written
     * back to the configuration file.
     *
     * @param fieldHeight The height of the game field.
     */
    public void setFieldHeight(int fieldHeight) {
        this.fieldHeight = fieldHeight;
    }

    /**
     * Is used internally.
     *
     * @return The height of the game field.
     */
    public int getFieldHeight(Board board, Background back) {
        int hBoard = (board == null) ? 0 : (int) (board.getImage().getHeight() * board.getZoom() / 100.0) + 2 * board.getYPos();
        if (hBoard == 1) {
            hBoard = 0;
        }
        int hBack = (back == null || back.getImage() == null || back.isRepeatMode()) ? 0 : (int) (back.getImage().getHeight() * back.getZoom() / 100.0);
        if (hBoard > hBack) {
            return hBoard;
        }
        if (hBack > hBoard) {
            return hBack;
        }
        // hBack==hBoard
        if (hBack > fieldHeight) {
            return hBack;
        }
        if (fieldHeight > 0) {
            return fieldHeight;
        }
        return CONFIG_STANDARD_HEIGHT;
    }

    /**
     * @param map  The map to look for a list.
     * @param type The part or partset type.
     * @return A list, can be empty.
     */
    private List<Part> getPartsFromMap(Map<String, List<Part>> map, String type) {
        List<Part> list = map.get(type);
        return (list != null) ? list : new ArrayList<>();
    }

    /**
     * @param map  The map to look for a list.
     * @param type The part or partset type.
     * @return A list, can be empty.
     */
    private List<PartSet> getPartSetsFromMap(Map<String, List<PartSet>> map, String type) {
        List<PartSet> list = map.get(type);
        return (list != null) ? list : new ArrayList<>();
    }

    /**
     * @param map  The map to look for a list.
     * @param type The part or partset type.
     * @return A list, can be empty.
     */
    private List<CardSet> getCardSetsFromMap(Map<String, List<CardSet>> map, String type) {
        List<CardSet> list = map.get(type);
        return (list != null) ? list : new ArrayList<>();
    }

    /**
     * @param parts An array with a sort of parts.
     * @param name  Name of the active part.
     * @return The active part or null.
     */
    private Part getPartByName(Part[] parts, String name) {
        if (HGBaseTools.hasContent(name)) {
            int index = getIndexOfPart(parts, name);
            if (index >= 0 && parts.length > index) {
                return parts[index];
            }
        }
        return null;
    }

    /**
     * Returns the first available part from the specified parts.<br>
     * Hidden parts will be skipped.<br>
     * If the game is not the pro version, then teaser parts will also be skipped.
     *
     * @param parts The parts to check.
     * @return The name of the found part an empty string if nothing could be found.
     */
    private String getFirstAvailablePart(Part[] parts) {
        if ((parts == null) || (parts.length == 0)) {
            return null;
        }
        for (Part part : parts) {
            // Search for the first not hidden part. If the game is not the pro version, then teaser parts are also skipped.
            if ((!part.isHidden()) && (isProVersion() || !part.isProTeaser())) {
                return part.getName();
            }
        }
        return EMPTY_STRING;
    }

    /**
     * @param parts     An array with a sort of parts.
     * @param configKey Name of the part configuration.
     * @return The name of the active part configuration or "".
     */
    private String getActivePartName(Part[] parts, String configKey) {
        String name = HGBaseConfig.get(configKey);
        int index = getIndexOfPart(parts, name);
        if (index >= 0 && index < parts.length && !parts[index].isHidden()) {
            return parts[index].getName();
        }
        return getFirstAvailablePart(parts);
    }

    /**
     * @param parts Array with parts (Background, Board, CardSet).
     * @param name  Name of the part that is searched.
     * @return index of the item or -1 if it was not found.
     */
    private int getIndexOfPart(Part[] parts, String name) {
        String[] itemNames = ArrayUtil.toPartNames(parts);
        return HGBaseTools.getIndexOf(itemNames, name);
    }

    /**
     * @param pieceType A piece type
     * @return The extension for this piece type.
     */
    public String getImageExtension(String pieceType) {
        return extensionMap.getOrDefault(pieceType, EMPTY_STRING);
    }

    /**
     * @param pieceType A piece type
     * @return The image path for this piece type.
     */
    public String getImagePath(String pieceType) {
        return pathMap.getOrDefault(pieceType, EMPTY_STRING);
    }

    /**
     * @return The name part, the players' colors begin with.
     */
    public String getPlayerPieceColor() {
        return playerPieceColor;
    }

    /**
     * @return True, if no user defined arrangements are allowed.
     */
    public boolean isCompleteArrangement() {
        return this.completeArrangement;
    }

    /**
     * @return True, if the game state is changed after every move.
     */
    public boolean isChangedAfterMove() {
        return changedAfterMove;
    }

    /**
     * @param changed True to set the game changed after every move.
     */
    public void setChangedAfterMove(boolean changed) {
        changedAfterMove = changed;
    }

    /**
     * @return True, if the game state is changed as soon as a game starts.
     */
    public boolean isChangedOnNewGame() {
        return changedOnNewGame;
    }

    /**
     * @param changed True to set the game changed as soon as a game starts.
     */
    public void setChangedOnNewGame(boolean changed) {
        changedOnNewGame = changed;
    }

    /**
     * @return True, if the game state is changed at the beginning of every round.
     */
    public boolean isChangedOnNewRound() {
        return changedOnNewRound;
    }

    /**
     * @param changed True to set the game changed at the beginning of every round.
     */
    public void setChangedOnNewRound(boolean changed) {
        changedOnNewRound = changed;
    }

    /**
     * @return True, if the game state is changed on every new turn.
     */
    public boolean isChangedOnNewTurn() {
        return changedOnNewTurn;
    }

    /**
     * @param changed True to set the game changed on every new turn.
     */
    public void setChangedOnNewTurn(boolean changed) {
        changedOnNewTurn = changed;
    }

    /**
     * @return True, if there a dialog after each turn shall appear.
     */
    public boolean showDialogAfterTurn() {
        return dialogAfterTurn;
    }

    /**
     * @param show True to show a dialog after a turn, otherwise false.
     */
    public void setDialogAfterTurn(boolean show) {
        dialogAfterTurn = show;
    }

    /**
     * @return True, if there a dialog after each round shall appear.
     */
    public boolean showDialogAfterRound() {
        return dialogAfterRound;
    }

    /**
     * @param show True to show a dialog after a round, otherwise false.
     */
    public void setDialogAfterRound(boolean show) {
        dialogAfterRound = show;
    }

    /**
     * @return True, if there a dialog after the shall appear.
     */
    public boolean showDialogAfterGame() {
        return dialogAfterGame;
    }

    /**
     * @param show True to show a dialog after a game, otherwise false.
     */
    public void setDialogAfterGame(boolean show) {
        dialogAfterGame = show;
    }

    /**
     * @return True if the game shall be interrupted after every round. Than for a newRound()-call is waited.
     */
    public boolean isInterruptAfterRound() {
        return interruptAfterRound;
    }

    /**
     * @param interrupt True to interrupt the game after every round.
     */
    public void setInterruptAfterRound(boolean interrupt) {
        interruptAfterRound = interrupt;
    }

    /**
     * @param type The color value part's type (e.g. CONFIG_CARDSET).
     * @return The mode to sort (ORDERBY_NONE, ORDERBY_COLOR, ORDERBY_VALUE).
     */
    public int getOrderby(String type) {
        return setOrderBy.getInt(type);
    }

    /**
     * Returns a setting that is defined at the "hints". The key for this settings
     * can be taken from ConstantValue.HINTS_xxx.
     *
     * @param setting The setting's key.
     * @return The setting or null.
     */
    public String getHintsSetting(String setting) {
        return hintsMap.get(setting);
    }

    /**
     * Returns a list of all hint keys.
     *
     * @return a list of all hint keys, may be empty
     */
    public String[] getHintsSettingKeys() {
        return hintsMap.keySet().toArray(new String[0]);
    }

    /**
     * @return The delay before a new round.
     */
    public int getDelayRound() {
        return delayRound;
    }

    /**
     * @param delay The delay before a new round.
     */
    public void setDelayRound(int delay) {
        delayRound = Math.max(delay, 0);
    }

    /**
     * @return the delay before a round considering the game speed factor.
     */
    public int getDelayRoundWithSpeedFactor() {
        return (int) (getDelayRound() * getGameSpeedFactor());
    }

    /**
     * @return The delay before a new turn.
     */
    public int getDelayTurn() {
        return delayTurn;
    }

    /**
     * @param delay The delay before a new turn.
     */
    public void setDelayTurn(int delay) {
        delayTurn = Math.max(delay, 0);
    }

    /**
     * @return the delay before a turn considering the game speed factor.
     */
    public int getDelayTurnWithSpeedFactor() {
        return (int) (getDelayTurn() * getGameSpeedFactor());
    }

    /**
     * @return The delay before a new move.
     */
    public int getDelayMove() {
        return delayMove;
    }

    /**
     * @param delay The delay before a new move.
     */
    public void setDelayMove(int delay) {
        delayMove = Math.max(delay, 0);
    }

    /**
     * @return the delay before a move considering the game speed factor.
     */
    public int getDelayMoveWithSpeedFactor() {
        return (int) (getDelayMove() * getGameSpeedFactor());
    }

    /**
     * @return the delay time for a computer player, with respect to the thinking time
     * @see SimpleComputerPlayer
     */
    public int getDelayPlayer() {
        return delayPlayer;
    }

    /**
     * @param delay The delay for a computer player.
     */
    public void setDelayPlayer(int delay) {
        delayPlayer = Math.max(delay, 0);
    }

    /**
     * @return the delay for a computer player considering the game speed factor.
     */
    public int getDelayPlayerWithSpeedFactor() {
        return (int) (getDelayMove() * getGameSpeedFactor());
    }

    /**
     * @return the game speed factor (e.g. used by the delay for move, turn, etc.).
     */
    public double getGameSpeedFactor() {
        return gameSpeed;
    }

    /**
     * @param factor the factor is set by the main menu.
     */
    public void setGameSpeedFactor(double factor) {
        gameSpeed = factor;
    }

    /**
     * @return True, if the game state shall be recorded as soon as a game starts.
     */
    public boolean isRecordedOnNewGame() {
        return recordOnNewGame;
    }

    /**
     * @return True, if the game state shall be recorded at the beginning of every round.
     */
    public boolean isRecordedOnNewRound() {
        return recordOnNewRound;
    }

    /**
     * @return True, if the game state shall be recorded at the beginning of every turn.
     */
    public boolean isRecordedOnNewTurn() {
        return recordOnNewTurn;
    }

    /**
     * @return a user defined name for the xml root or an empty string for default xml structure for a game.
     */
    public String getGameStateXmlRoot() {
        return gameStateXmlRoot;
    }

    /**
     * Returns the {@code ScaleType} for the main menu image.
     *
     * @return The {@code ScaleType} for the main menu image.
     * @see ScaleType
     */
    public ScaleType getMainMenuImageScaleType() {
        ScaleType scaleType;
        try {
            scaleType = ScaleType.valueOf(mainMenuImageScaleType);
        } catch (Exception e) {
            scaleType = null;
        }
        return scaleType;
    }

    /**
     * Returns {@code true} if at least one selected part is only available in the pro version but should be shown in the free version as teaser for the the pro version.
     *
     * @return {@code true} if at least one selected part is only available in the pro version but should be shown in the free version as teaser for the the pro version.
     */
    public boolean isProTeaserPartSelected() {
        ArrayList<Part> partsToCheck = new ArrayList<>();
        partsToCheck.add(getActiveArrangement());
        partsToCheck.add(getActiveBackground());
        partsToCheck.add(getActiveBoard());
        partsToCheck.add(getActiveCardSet());
        partsToCheck.add(getActiveCover());
        for (String cardSetType : getCardSetTypes()) {
            partsToCheck.add(getActiveCardSet(cardSetType));
        }
        for (String partType : getPartTypes()) {
            partsToCheck.add(getActivePart(partType));
        }
        for (String partSetType : getPartSetTypes()) {
            partsToCheck.add(getActivePartSet(partSetType));
        }
        for (Part part : partsToCheck) {
            if ((part != null) && (part.isProTeaser())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a layout element to the map of elements.
     *
     * @param elementClass The class of the layout element.
     * @param elementKey   The map key of the layout element.
     * @param element      The element to add.
     */
    public void addLayoutElement(Class<? extends LayoutElement> elementClass, String elementKey, LayoutElement element) {
        if (elementClass == null || elementKey == null || element == null) {
            return;
        }
        Map<String, LayoutElement> elementMap = layoutElements.getOrDefault(elementClass, new HashMap<>());
        elementMap.put(elementKey, element);
        layoutElements.put(elementClass, elementMap);
    }

    /**
     * Returns the layout elements of a configurable game field.
     *
     * @return The layout elements of a configurable game field.
     */
    public Map<Class<? extends LayoutElement>, Map<String, LayoutElement>> getLayoutElements() {
        return layoutElements;
    }

    /**
     * Returns the margin of a configurable game field.
     *
     * @return The margin of a configurable game field.
     */
    public Insets getGamefieldLayoutMargin() {
        return gamefieldLayoutMargin;
    }

    /**
     * Sets the margin of a configurable game field.
     *
     * @param margin The margin to set.
     */
    public void setGamefieldLayoutMargin(Insets margin) {
        gamefieldLayoutMargin = margin;
    }

    /**
     * Returns the area layout with the specified name or {@code null} if there is no area with that name.
     *
     * @param name The name of the area layout.
     * @return The area layout with the specified name or {@code null} if there is no area with that name.
     */
    public AreaLayout getLayoutArea(String name) {
        if (!HGBaseTools.hasContent(name)) {
            return null;
        }
        Map<String, LayoutElement> areaLayouts = layoutElements.get(AreaLayout.class);
        if (areaLayouts == null) {
            return null;
        }
        return (AreaLayout) areaLayouts.get(name);
    }
}
