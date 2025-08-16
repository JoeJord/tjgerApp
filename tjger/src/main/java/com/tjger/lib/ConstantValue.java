package com.tjger.lib;

import android.graphics.Paint;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * All constant values that are used for tjger.
 *
 * @author hagru
 */
public class ConstantValue {

    // font alignment (GamePanel.drawString...)
    public static final Paint.Align ALIGN_LEFT = Paint.Align.LEFT;
    public static final Paint.Align ALIGN_CENTER = Paint.Align.CENTER;
    public static final Paint.Align ALIGN_RIGHT = Paint.Align.RIGHT;

    // Vertical alignments.
    public static final int ALIGN_TOP = 1;
    public static final int ALIGN_MIDDLE = 0;
    public static final int ALIGN_BOTTOM = -1;

    // for drop out state of players
    public static final boolean INCLUDE_DROPOUT = false;
    public static final boolean EXCLUDE_DROPOUT = true;

    // constants for configuration values
    public static final String CONFIG_ARRANGEMENT = "game_arrangement";
    public static final String CONFIG_BACKGROUND = "game_background";
    public static final String CONFIG_BACKCOLOR = "game_backcolor";
    public static final String CONFIG_BOARD = "game_board";
    public static final String CONFIG_COVER = "game_cover";
    public static final String CONFIG_CARDSET = "game_cardset";
    public static final String CONFIG_CARD = "game_card";
    public static final String CONFIG_PIECESET = "game_pieceset";
    public static final String CONFIG_PIECE = "game_piece";
    public static final String CONFIG_ZOOM = "game_zoom";
    public static final String CONFIG_NETWORKPORT = "game_networkport";
    public static final String CONFIG_NUMPLAYERS = "player_number";
    public static final String CONFIG_PLAYERNAME = "player_name";
    public static final String CONFIG_PLAYERPIECECOLOR = "player_piececolor";
    public static final String CONFIG_NUMHUMANS = "human_number";
    public static final String CONFIG_COMPUTERTYPE = "computer_type";
    public static final String CONFIG_HIGHSCORE = "highscore";
    public static final String CONFIG_GAMESPLAYED = "gamesplayed";
    public static final String CONFIG_PLAYERSCORE = "playerscore";
    public static final String CONFIG_HINT_DONTSHOW = "hint_dontshow";
    public static final String CONFIG_STATEPANEL = "settings_statepanel";
    public static final String CONFIG_PLAYSOUND = "settings_playsound";
    public static final String CONFIG_DRAW_SHADOWS = "settings_drawshadows";
    public static final String CONFIG_DRAW_REFLECTIONS = "settings_drawreflections";
    /**
     * The configuration key for the sound arrangement.
     */
    public static final String CONFIG_SOUND_ARRANGEMENT = "game_soundarrangement";

    // stadard classes that can be redefined
    public static final String STANDARD_NEWGAMEDIALOG = "com.tjger.gui.NewGameDialog";
    public static final String STANDARD_GAMEDIALOGS = "com.tjger.gui.GameDialogs";

    // constants for hints (tip of the day)
    public static final String HINTS_PATH = "path";
    public static final String HINTS_EXTENSION = "extension";
    public static final String HINTS_APPLICATION = "application";
    public static final String HINTS_GAME = "game";
    public static final String HINTS_ROUND = "round";
    public static final String HINTS_TURN = "turn";
    public static final String HINTS_MOVE = "move";
    // network transfer constants
    public static final int NETWORK_WAITINTERVAL = 150;
    public static final String NETWORK_SEPARATE = ";"; // take this for seperating different network datas
    public static final String NETWORK_DIVIDEPART = "/"; // used for internal structs (e.g. Lists, PlayerCardMap)
    public static final String NETWORK_DIVIDEPART2 = "$"; // - "" -
    public static final String NETWORK_DIVIDEPAIR = "="; // - "" -
    public static final String NETWORK_NULL = "^";
    private static final String[] HINTS_LIST = {HINTS_APPLICATION, HINTS_GAME, HINTS_ROUND, HINTS_TURN, HINTS_MOVE};
    private static final String[] NETWORK_SPECIALCHAR = {NETWORK_SEPARATE, NETWORK_DIVIDEPART, NETWORK_DIVIDEPART2, NETWORK_DIVIDEPAIR, NETWORK_NULL};

    /**
     * @return all hint types as unmodifiable collection
     */
    public static Collection<String> getHintTypes() {
        return Collections.unmodifiableCollection(Arrays.asList(HINTS_LIST));
    }

    /**
     * @return all network characters for special purpose as unmodifiable collection
     */
    public static Collection<String> getNetworkSpecialChars() {
        return Collections.unmodifiableCollection(Arrays.asList(NETWORK_SPECIALCHAR));
    }

}
