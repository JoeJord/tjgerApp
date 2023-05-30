package com.tjger.gui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.tjger.MainFrame;
import com.tjger.R;
import com.tjger.game.GamePlayer;
import com.tjger.game.GameRules;
import com.tjger.game.GameState;
import com.tjger.game.completed.GameConfig;
import com.tjger.game.completed.GameEngine;
import com.tjger.game.completed.GameManager;
import com.tjger.game.internal.GameStatistics;
import com.tjger.game.internal.HighScoreElement;
import com.tjger.game.internal.PlayerProfiles;
import com.tjger.game.internal.ScoreRankElement;
import com.tjger.gui.completed.Part;
import com.tjger.gui.completed.PlayerInfoPanel;
import com.tjger.gui.internal.GameDialogFactory;
import com.tjger.gui.internal.HintPanel;
import com.tjger.gui.internal.PartsDlg;
import com.tjger.gui.internal.PreviewPanel;
import com.tjger.lib.ConstantValue;
import com.tjger.lib.DiceUtil;
import com.tjger.lib.PlayerUtil;
import com.tjger.lib.ScoreUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import at.hagru.hgbase.android.HGBaseAdvertisements;
import at.hagru.hgbase.android.HGBaseAppTools;
import at.hagru.hgbase.android.HGBaseResources;
import at.hagru.hgbase.android.awt.Color;
import at.hagru.hgbase.android.awt.Dimension;
import at.hagru.hgbase.android.dialog.DialogOnClickCloseListener;
import at.hagru.hgbase.gui.HGBaseDialog;
import at.hagru.hgbase.gui.HGBaseGuiTools;
import at.hagru.hgbase.gui.HGBaseHTMLPageWebView;
import at.hagru.hgbase.gui.HGBaseMultiTextPanel;
import at.hagru.hgbase.lib.HGBaseConfig;
import at.hagru.hgbase.lib.HGBaseFileTools;
import at.hagru.hgbase.lib.HGBaseLog;
import at.hagru.hgbase.lib.HGBaseText;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * This class returns various panels for some game dialogs (not for new game, use NewGameDialog for that).
 * By overwriting methods it's able to change the design of particular dialogs.
 *
 * @author hagru
 */
public class GameDialogs {

    public static final String SPACING = "   ";

    private static final int COLOR_TEXT = Color.BLACK.getColorCode();
    private static final int COLOR_ROW1 = Color.TRANSPARENT.getColorCode();
    private static final int COLOR_ROW2 = Color.TRANSPARENT.getColorCode();
    private static final int COLOR_SELECT = Color.WHITE.getColorCode();
    private static final int COLOR_FIRST_RANK = Color.BLUE.getColorCode();

    private static Activity activity;

    public GameDialogs() {
        super();
    }

    /**
     * @return the activity the dialog is shown from
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * The activity is set by the {@link GameDialogFactory} by default.
     *
     * @param activity the activity to set
     */
    public void setActivity(Activity activity) {
        GameDialogs.activity = activity;
    }

    /**
     * Creates a panel with a row for every player showing name and score of the given type.
     *
     * @param engine    The game engine.
     * @param scoreType The score type to display.
     * @param ranks     The ranking of the players.
     * @return A panel with player names and score.
     */
    protected View createScorePanel(GameEngine engine, int scoreType, int[] ranks) {
        GamePlayer[] player = getPlayersInOrderForScorePanel(engine, scoreType, ranks);
        TableLayout pnInfo = new TableLayout(getActivity());
        for (int i = 0; i < player.length; i++) {
            GamePlayer p = player[i];
            int rank = i + 1;
            createPlayerScorePanel(pnInfo, rank, p, scoreType);
        }
        return pnInfo;
    }

    /**
     * @return The players ordered in the way they shall be displayed on the score panel.
     */
    private GamePlayer[] getPlayersInOrderForScorePanel(GameEngine engine, int scoreType, int[] ranks) {
        GamePlayer[] player = engine.getActivePlayers();
        player = ScoreUtil.getPlayersInRankingOrder(player, ranks);
        return player;
    }

    /**
     * Creates the score panel for a single player.
     *
     * @param rank Starts with 1.
     */
    protected void createPlayerScorePanel(TableLayout pnInfo, int rank, GamePlayer p, int scoreType) {
        TableRow pnPlayerScore = new TableRow(getActivity());
        pnPlayerScore.addView(createPlayerScorePanelRank(rank));
        pnPlayerScore.addView(createPlayerScorePanelName(p));
        pnPlayerScore.addView(createPlayerScorePanelScore(p, scoreType));
        addAdditionalPlayerScoresOnPanel(pnPlayerScore, p, scoreType);
        pnInfo.addView(pnPlayerScore);
    }

    /**
     * @param pnPlayerScore the player score to automatically add additional score views
     * @param p             the player
     * @param scoreType     the score type that is displayed on the score panel
     */
    protected void addAdditionalPlayerScoresOnPanel(TableRow pnPlayerScore, GamePlayer p, int scoreType) {
        GameConfig config = GameManager.getInstance().getGameConfig();
        if (scoreType == GamePlayer.SCORE_TURN && config.showDialogAfterRound()) {
            pnPlayerScore.addView(createPlayerScorePanelScore(p, GamePlayer.SCORE_ROUND));
        }
        if ((scoreType == GamePlayer.SCORE_TURN || scoreType == GamePlayer.SCORE_ROUND) && config.showDialogAfterGame()) {
            pnPlayerScore.addView(createPlayerScorePanelScore(p, GamePlayer.SCORE_GAME));
        }
    }

    protected View createPlayerScorePanelRank(int rank) {
        return HGBaseGuiTools.createViewForMessage(getActivity(), rank + ". ");
    }

    protected View createPlayerScorePanelName(GamePlayer p) {
        return HGBaseGuiTools.createViewForMessage(getActivity(), p.getName());
    }

    protected View createPlayerScorePanelScore(GamePlayer p, int scoreType) {
        View scorePanel = HGBaseGuiTools.createViewForMessage(getActivity(), String.format("%10d", +p.getScore(scoreType)));
        if (scoreType == GamePlayer.SCORE_TURN) {
            HGBaseGuiTools.setToolTipText(scorePanel, "tooltip_scoreturn");
        } else if (scoreType == GamePlayer.SCORE_ROUND) {
            HGBaseGuiTools.setToolTipText(scorePanel, "tooltip_scoreround");
        } else if (scoreType == GamePlayer.SCORE_GAME) {
            HGBaseGuiTools.setToolTipText(scorePanel, "tooltip_scoregame");
        }
        return scorePanel;
    }

    /**
     * Shows the dialog that is called at the end of games, rounds and turns.
     *
     * @param mainFrame      The main frame.
     * @param dialogMode     Says which dialog shall be displayed (AFTER_TURN, AFTER_ROUND, AFTER_GAME).
     * @param isMoveComplete true if move is complete
     * @param turnFinished   true if turn is finished
     * @param state          The game state.
     * @param engine         The game engine.
     * @param rules          The game rules.
     */
    public void showGameStateInfoPanel(final MainFrame mainFrame, int dialogMode, final boolean isMoveComplete, final boolean turnFinished,
                                       final GameState state, final GameEngine engine, final GameRules rules) {
        String titleId = "";
        View msg = null;
        int step = 0;
        switch (dialogMode) {
            case GameEngine.AFTER_TURN:
                titleId = "dlg_turnfinished";
                msg = getTurnInfoPanel(state, engine, rules);
                step = 2;
                break;
            case GameEngine.AFTER_ROUND:
                titleId = "dlg_roundfinished";
                msg = getRoundInfoPanel(state, engine, rules);
                step = 3;
                break;
            case GameEngine.AFTER_GAME:
                titleId = "dlg_gamefinished";
                msg = getGameInfoPanel(state, engine, rules);
                step = 4;
                break;
        }
        if (msg != null) {
            final int currentStep = step;
            HGBaseDialog.showOkCancelDialog(mainFrame, msg, HGBaseText.getText(titleId), new DialogOnClickCloseListener(), null, dialog -> engine.showDialogsIntern(isMoveComplete, turnFinished, rules, currentStep));
        } else {
            engine.showDialogsIntern(isMoveComplete, turnFinished, rules, step);
        }
    }

    /**
     * Returns the panel that appears on the dialog at the end of turns.
     *
     * @param state  The game state.
     * @param engine The game engine.
     * @param rules  The game rules.
     * @return The panel for the turn info.
     */
    public View getTurnInfoPanel(GameState state, GameEngine engine, GameRules rules) {
        int[] ranks = rules.getTurnRanking(engine.getActivePlayers());
        return createScorePanel(engine, GamePlayer.SCORE_TURN, ranks);
    }

    /**
     * Returns the panel that appears on the dialog at the end of rounds.
     *
     * @param state  The game state.
     * @param engine The game engine.
     * @param rules  The game rules.
     * @return The panel for the round info.
     */
    public View getRoundInfoPanel(GameState state, GameEngine engine, GameRules rules) {
        int[] ranks = rules.getRoundRanking(engine.getActivePlayers());
        return createScorePanel(engine, GamePlayer.SCORE_ROUND, ranks);
    }

    /**
     * Returns the panel that appears on the dialog at the end of the game.
     *
     * @param state  The game state.
     * @param engine The game engine.
     * @param rules  The game rules.
     * @return The panel for the game info.
     */
    public View getGameInfoPanel(GameState state, GameEngine engine, GameRules rules) {
        int[] ranks = rules.getGameRanking(engine.getActivePlayers());
        return createScorePanel(engine, GamePlayer.SCORE_GAME, ranks);
    }

    /**
     * Shows a dialog with just the winner and a message with player name and {@code dlg.iswinner}.
     *
     * @param state  the game state
     * @param engine the game engine
     * @param rules  the game rules
     * @return the panel that shows only that a player is the winner
     */
    protected View getWinnerOnlyPanel(GameState state, GameEngine engine, GameRules rules) {
        return getWinnerOnlyPanel(state, engine, rules, GamePlayer.SCORE_GAME, false);
    }

    /**
     * Shows a dialog with just the winner and a message with player name and {@code dlg.iswinner}.<p>
     * Intended to be used instead of the original panel with scores.
     *
     * @param state        the game state
     * @param engine       the game engine
     * @param rules        the game rules
     * @param scoreType    the score type
     * @param lessIsBetter true if less score is better, false if higher score is better
     * @return the panel that shows only that a player is the winner
     */
    protected View getWinnerOnlyPanel(GameState state, GameEngine engine, GameRules rules, int scoreType, boolean lessIsBetter) {
        GamePlayer[] players = engine.getActivePlayers();
        int[] ranks = ScoreUtil.getScoreRanking(players, scoreType, lessIsBetter);
        LinearLayout winnerPanel = HGBaseGuiTools.createLinearLayout(getActivity(), true);
        String winnerText;
        if (ScoreUtil.numberOfRanks(ranks, ranks[0]) == players.length) {
            // all player have the same rank, so it's a draw game
            winnerText = getWinnerOnlyTextForDrawGame(state, engine);
        } else {
            // assume that only one player has the best rank
            players = ScoreUtil.getPlayersInRankingOrder(players, ranks);
            GamePlayer winner = players[0];
            Part part = getWinnerImage(winner);
            if (part != null) {
                int imageSize = HGBaseGuiTools.getFieldHeight();
                ImageView imgPiece = new ImageView(getActivity());
                imgPiece.setImageBitmap(HGBaseGuiTools.getScaledBitmap(part.getImage(), imageSize, imageSize));
                winnerPanel.addView(imgPiece, new LinearLayout.LayoutParams(imageSize, imageSize));
            }
            winnerText = getWinnerOnlyTextForWinGame(state, engine, winner);
        }
        View lbMessage = HGBaseGuiTools.createViewForMessage(getActivity(), winnerText);
        winnerPanel.addView(lbMessage, HGBaseGuiTools.createLinearLayoutParams(true, true));
        return winnerPanel;
    }

    /**
     * Returns the image of the player.
     *
     * @param winner the winner
     * @return the part of the winner referencing the image
     */
    protected Part getWinnerImage(GamePlayer winner) {
        return PlayerUtil.getPlayerImage(winner);
    }

    /**
     * Returns the text to be displayed for a draw game.
     *
     * @param state  the game state
     * @param engine the game engine
     * @return the text to be displayed for a draw game
     */
    protected String getWinnerOnlyTextForDrawGame(GameState state, GameEngine engine) {
        return HGBaseText.getText("dlg_drawgame");
    }

    /**
     * Returns the text to be displayed if a player has won the game.
     *
     * @param state  the game state
     * @param engine the game engine
     * @param winner the winner of the game, must not be null
     * @return the text to be displayed for a player that won the game
     */
    protected String getWinnerOnlyTextForWinGame(GameState state, GameEngine engine, GamePlayer winner) {
        String msgCode = getWinnerOnlyMessageCodeForWinner(state, engine, winner);
        return HGBaseText.getText(msgCode, winner.getName());
    }

    /**
     * Returns the message code to be used if there is a winner. This code differs if the only human player has
     * one or another player.
     *
     * @param state  the game state
     * @param engine the game engine
     * @param winner the winner of the game, must not be null
     * @return the message code to be used if there is a winner
     */
    protected String getWinnerOnlyMessageCodeForWinner(GameState state, GameEngine engine, GamePlayer winner) {
        if (PlayerUtil.showYouMessageForPlayer(engine, winner)) {
            return "dlg_iswinner_you";
        } else {
            return "dlg_iswinner";
        }
    }

    /**
     * @return The player info panel for a single row.
     */
    protected PlayerInfoPanel getPlayerInfoPanel(GamePlayer player, int fields) {
        return new PlayerInfoPanel(getActivity(), player, fields, COLOR_TEXT, true);
    }

    /**
     * Shows the game information panel.
     *
     * @param mainFrame      The main frame.
     * @param config         The game configuration.
     * @param engine         The game engine.
     * @param state          The game state.
     * @param statistics     The statistics.
     * @param playerProfiles The player profiles.
     */
    public void showGameInfoDialog(MainFrame mainFrame, GameConfig config, GameEngine engine,
                                   GameState state, GameStatistics statistics, PlayerProfiles playerProfiles) {
        TabHost tabbedPane = new TabHost(mainFrame, null);
        tabbedPane.setLayoutParams(HGBaseGuiTools.createLinearLayoutParams(true, true));
        TabWidget tabWidget = new TabWidget(mainFrame);
        tabWidget.setId(android.R.id.tabs);
        tabbedPane.addView(tabWidget, HGBaseGuiTools.createLinearLayoutParams(true, true));
        FrameLayout tabContent = new FrameLayout(mainFrame);
        tabContent.setId(android.R.id.tabcontent);
        tabContent.setPadding(0, HGBaseGuiTools.getButtonHeight() * 2, 0, 0);
        tabbedPane.addView(tabContent, HGBaseGuiTools.createLinearLayoutParams(false, false));
        tabbedPane.setup();
        GamePlayer[] statPlayer = statistics.getGamePlayerList();
        if (statPlayer != null && statPlayer.length > 0) {
            View gamePanel = getCurrentGamePanel(statistics, engine, state);
            if (gamePanel != null) {
                HGBaseGuiTools.addViewToTab(tabbedPane, gamePanel, "dlg_currentgame");
            }
        }
        if (config.isRememberGames() || config.isRememberScores()) {
            View statPanel = getGameStatisticsPanel(playerProfiles, statistics, config);
            if (statPanel != null) {
                HGBaseGuiTools.addViewToTab(tabbedPane, statPanel, "dlg_gamestatistics");
            }
        }
        int highScores = config.getHighScoreLength();
        if (highScores > 0) {
            View highPanel = getHighScorePanel(statistics, highScores);
            if (highPanel != null) {
                HGBaseGuiTools.addViewToTab(tabbedPane, highPanel, "dlg_highscore");
            }
        }
        HGBaseDialog.showOkDialog(mainFrame, tabbedPane, HGBaseText.getText("action_gameinfo").replace('.', ' '));
    }

    /**
     * Returns the panel for displaying high scores.
     *
     * @param statistics The game statistics holds the high scores.
     * @param highScores Number of high scores to display.
     * @return A panel with high scores.
     */
    public View getHighScorePanel(GameStatistics statistics, int highScores) {
        ViewGroup pnHighScore = createHighScorePanel(statistics, highScores);
        View pnButtons = createHighScoreButtons(pnHighScore, statistics, highScores);
        LinearLayout pnDummy = HGBaseGuiTools.createLinearLayout(getActivity(), false);
        pnDummy.addView(pnHighScore);
        pnDummy.addView(HGBaseGuiTools.createLabel(getActivity(), ""));
        pnDummy.addView(pnButtons);
        return pnDummy;
    }

    /**
     * Creates the panel that lists the players who have reached a high score.
     *
     * @return The panel with the players' high scores.
     */
    protected ViewGroup createHighScorePanel(final GameStatistics statistics, final int highScores) {
        // create a panel with one row for every high score
        LinearLayout pnHighScore = HGBaseGuiTools.createLinearLayout(getActivity(), false);
        placeHighScoreRows(pnHighScore, statistics, highScores);
        return pnHighScore;
    }

    /**
     * Places the rows for the players high scores on the panel.
     */
    private void placeHighScoreRows(ViewGroup pnHighScore, final GameStatistics statistics, final int highScores) {
        HighScoreElement[] hi = statistics.getHighScore();
        for (int i = 0; i < highScores; i++) {
            HGBaseMultiTextPanel pnRow = new HGBaseMultiTextPanel(getActivity(), new int[]{35, 0, 100, 20, 70},
                    false, true, COLOR_TEXT, Color.TRANSPARENT.getColorCode());
            pnRow.setBackgroundColor(HGBaseTools.isEven(i) ? COLOR_ROW1 : COLOR_ROW2);
            pnRow.setText(0, (i + 1) + ". ");
            if (hi.length > i) {
                pnRow.setText(1, hi[i].getName());
                pnRow.setText(2, " " + HGBaseTools.convertDate2String(hi[i].getDay()));
                pnRow.getLabel(2).setGravity(Gravity.END);
                pnRow.setText(3, " ");
                pnRow.setText(4, this.formatHighScore(hi[i].getScore()));
                pnRow.getLabel(4).setGravity(Gravity.END);
            }
            pnHighScore.addView(pnRow);
        }
    }

    /**
     * Formats the score.
     *
     * @param score The score to format.
     * @return The formatted score.
     */
    protected String formatHighScore(int score) {
        return String.valueOf(score);
    }

    /**
     * Creates the panel with the highscore controls.
     *
     * @return The panel with the buttons.
     */
    protected View createHighScoreButtons(final ViewGroup pnHighScore, final GameStatistics statistics, final int highScores) {
        HighScoreElement[] hi = statistics.getHighScore();
        ViewGroup pnButton = HGBaseGuiTools.createLinearLayout(getActivity(), false);
        Button btRemove = HGBaseGuiTools.createButton(getActivity(), HGBaseText.getText("dlg_removescore"), v -> {
            statistics.removeHighScore();
            pnHighScore.removeAllViews();
            placeHighScoreRows(pnHighScore, statistics, highScores);
            pnHighScore.invalidate();
        });
        HGBaseGuiTools.setViewSize(btRemove, new Dimension(HGBaseGuiTools.getScreenSize(getActivity()).x / 2,
                HGBaseGuiTools.getButtonHeight()));
        btRemove.setEnabled(hi != null && hi.length > 0);
        pnButton.addView(btRemove);
        btRemove.setGravity(Gravity.CENTER);
        return pnButton;
    }

    /**
     * Returns the panel for displaying statistics (profiles).
     *
     * @param playerProfiles The player profiles.
     * @param statistics     The game statistics.
     * @param config         The game configuration.
     * @return A panel displaying game statistics.
     */
    public View getGameStatisticsPanel(PlayerProfiles playerProfiles, GameStatistics statistics, GameConfig config) {
        ViewGroup pnStatsScroll = createGameStatisticsPanel(playerProfiles, statistics, config);
        ViewGroup pnStats = (pnStatsScroll instanceof ScrollView) ? (ViewGroup) pnStatsScroll.getChildAt(0) : pnStatsScroll;
        View pnButtons = createGameStatisticsButtons(pnStats, playerProfiles, statistics, config);
        boolean isLandscape = HGBaseGuiTools.isScreenLandscape(getActivity());
        LinearLayout pnDummy = HGBaseGuiTools.createLinearLayout(getActivity(), isLandscape);
        pnDummy.addView(pnStatsScroll);
        pnDummy.addView(HGBaseGuiTools.createLabel(getActivity(), ""));
        pnDummy.addView(pnButtons);
        LinearLayout.LayoutParams lpStats = (LinearLayout.LayoutParams) pnStatsScroll.getLayoutParams();
        lpStats.weight = 2.0f;
        LinearLayout.LayoutParams lpButtons = (LinearLayout.LayoutParams) pnButtons.getLayoutParams();
        lpButtons.weight = 0.1f;
        return pnDummy;
    }

    /**
     * Create the buttons for the game statistics panel.
     *
     * @param pnStats        The panel displaying the game statistics.
     * @param playerProfiles The player profiles.
     * @param config The game configuration.
     * @param statistics The game statistics.
     * @return The panel with the buttons.
     */
    protected View createGameStatisticsButtons(final ViewGroup pnStats, final PlayerProfiles playerProfiles, final GameStatistics statistics, final GameConfig config) {
        ViewGroup pnButton = HGBaseGuiTools.createLinearLayout(getActivity(), HGBaseGuiTools.isScreenPortrait(getActivity()));
        Button btRemove = HGBaseGuiTools.createButton(getActivity(), HGBaseText.getText("dlg_removestatistics"), v -> {
            statistics.removeGamesPlayedWon();
            statistics.removeGameScores();
            pnStats.removeAllViews();
            placeStatisticsRows(pnStats, playerProfiles, statistics, config);
            pnStats.invalidate();
        });
        int factor = (HGBaseGuiTools.isScreenPortrait(getActivity())) ? 2 : 3;
        HGBaseGuiTools.setViewSize(btRemove, new Dimension(HGBaseGuiTools.getScreenSize(getActivity()).x / factor,
                HGBaseGuiTools.getButtonHeight()));
        // enable the button only, if no game is active
        boolean enabled = (playerProfiles.getPlayers().length > 0 && !GameManager.getInstance().getGameEngine().isActiveGame());
        btRemove.setEnabled(enabled);
        pnButton.addView(btRemove);
        return pnButton;
    }

    /**
     * Create the panel displaying the game statistics (played and won games).
     *
     * @param playerProfiles The player profiles.
     * @param statistics     The game statistics.
     * @param config         The game configuration.
     * @return A panel displaying game statistics.
     */
    protected ViewGroup createGameStatisticsPanel(PlayerProfiles playerProfiles, GameStatistics statistics, GameConfig config) {
        LinearLayout pnStats = HGBaseGuiTools.createLinearLayout(getActivity(), false);
        placeStatisticsRows(pnStats, playerProfiles, statistics, config);
        ScrollView scroll = HGBaseGuiTools.createScrollView(getActivity());
        scroll.addView(pnStats);
        return scroll;
    }

    /**
     * Places the statistics rows.
     *
     * @param pnStats        The panel for the statistics.
     * @param playerProfiles The player profiles.
     * @param statistics     The game statistics.
     * @param config         The game configuration.
     */
    private void placeStatisticsRows(ViewGroup pnStats, PlayerProfiles playerProfiles, GameStatistics statistics, GameConfig config) {
        GamePlayer[] player = playerProfiles.getPlayers();
        int fields = PlayerInfoPanel.NAMETYPE;
        if (config.isRememberScores()) {
            fields = fields | PlayerInfoPanel.SCORE;
        }
        if (config.isRememberGames()) {
            fields = fields | PlayerInfoPanel.GAMES | PlayerInfoPanel.WON;
        }
        for (int i = 0; i < player.length; i++) {
            PlayerInfoPanel pnInfo = getPlayerInfoPanel(player[i], fields);
            pnInfo.setBackgroundColor(HGBaseTools.isEven(i) ? COLOR_ROW1 : COLOR_ROW2);
            if (HGBaseGuiTools.isScreenLandscape(getActivity())) {
                HGBaseMultiTextPanel pnPlayer = pnInfo.getMultiTextPanel();
                if (pnPlayer != null) {
                    View centerPanel = pnPlayer.getCenterPanel();
                    if (centerPanel != null) {
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) centerPanel.getLayoutParams();
                        int newWidth = lp.width - HGBaseGuiTools.getScreenSize(getActivity()).x / 3;
                        if (newWidth > 0) {
                            lp.width = newWidth;
                        }
                    }
                }
            }
            pnStats.addView(pnInfo);
        }
    }

    /**
     * Returns a panel with information about the current game.
     *
     * @param statistics The game statistics.
     * @param engine     The game engine.
     * @param gameState  The game state.
     * @return A panel with current game information.
     */
    public View getCurrentGamePanel(GameStatistics statistics, GameEngine engine, GameState gameState) {
        // create a dummy panel for horizontal fixed size
        ViewGroup pnDummy = HGBaseGuiTools.createLinearLayout(getActivity(), false);
        pnDummy.addView(createCurrentGamePanel(statistics, engine, gameState));
        pnDummy.addView(createNewGameInfoPanel(engine.getGameManager()));
        return pnDummy;
    }

    /**
     * Creates the panel for the current game state.
     *
     * @param statistics The game statistics.
     * @param engine     The game engine.
     * @param gameState  The game state.
     * @return A panel with current game state.
     */
    protected View createCurrentGamePanel(GameStatistics statistics, GameEngine engine, GameState gameState) {
        GamePlayer[] player = statistics.getGamePlayerList();
        int[] gameRank = statistics.getGameRankingList();
        List<ScoreRankElement[]> roundRanking = statistics.getRoundScoreRankingList();
        TableLayout pnGame = new TableLayout(getActivity());
        // draw a header with the players names        
        TableRow pnPlayerRow = new TableRow(getActivity());
        pnPlayerRow.addView(HGBaseGuiTools.createViewForMessage(getActivity(), SPACING));
        for (GamePlayer gamePlayer : player) {
            View lbPlayer = HGBaseGuiTools.createViewForMessage(getActivity(), gamePlayer.getName() + " ");
            pnPlayerRow.addView(lbPlayer);
        }
        pnGame.addView(pnPlayerRow);
        // draw the scores per round
        TableRow pnScoreRound = new TableRow(getActivity());
        for (int i = 0; i < roundRanking.size(); i++) {
            int colBack = (HGBaseTools.isEven(i)) ? COLOR_ROW1 : COLOR_ROW2;
            int round = i + 1;
            String roundNum = (round == engine.getCurrentRound() && (engine.isActiveGame() || round == 1)) ?
                    " > " : " " + round;
            pnScoreRound.addView(HGBaseGuiTools.createViewForMessage(getActivity(), roundNum));
            ScoreRankElement[] sre = roundRanking.get(i);
            for (int j = 0; j < sre.length; j++) {
                View lbScore = createCurrentGamePanelRoundScore(sre, j, COLOR_SELECT, colBack);
                pnScoreRound.addView(lbScore);
            }
        }
        pnGame.addView(pnScoreRound);
        // draw the game ranking
        if (!engine.isActiveGame()) {
            TableRow pnRanking = new TableRow(getActivity());
            View lbScore = HGBaseGuiTools.createViewForMessage(getActivity(), HGBaseText.getText("dlg_score"));
            pnRanking.addView(lbScore);
            for (GamePlayer gamePlayer : player) {
                View lbRank = createCurrentGamePanelTotalScore(gamePlayer);
                pnRanking.addView(lbRank);
            }
            if (gameRank != null) {
                pnRanking.addView(HGBaseGuiTools.createViewForMessage(getActivity(), ""));
                for (int j : gameRank) {
                    View lbRank = createCurrentGamePanelRank(j);
                    pnRanking.addView(lbRank);
                }
            }
            pnGame.addView(pnRanking);
        }
        return pnGame;
    }

    /**
     * @return The control that displays the rank.
     */
    protected View createCurrentGamePanelRank(int rank) {
        TextView lbRank = HGBaseGuiTools.createViewForMessage(getActivity(), rank + ". ");
        lbRank.setGravity(Gravity.END);
        if (rank == 1) {
            lbRank.setTextColor(COLOR_FIRST_RANK);
        }
        return lbRank;
    }

    /**
     * @return The control that displays the total score of a player.
     */
    protected View createCurrentGamePanelTotalScore(GamePlayer p) {
        return HGBaseGuiTools.createViewForMessage(getActivity(), p.getScore(GamePlayer.SCORE_GAME) + "  ");
    }

    /**
     * @return The control that displays a single round score of a player.
     */
    protected View createCurrentGamePanelRoundScore(ScoreRankElement[] sre, int playerIndex, int rankColor, int colBack) {
        TextView lbScore = HGBaseGuiTools.createViewForMessage(getActivity(), sre[playerIndex].getScore() + "  ");
        lbScore.setBackgroundColor(colBack);
        if (sre[playerIndex].getRank() == 1) {
            lbScore.setTextColor(rankColor);
        }
        return lbScore;
    }

    /**
     * Creates the panel for the new game information.
     *
     * @param manager The game manager.
     * @return The panel with the new game information.
     */
    protected View createNewGameInfoPanel(GameManager manager) {
        String[] keys = manager.getNewGameInformationKeys();
        TableLayout pnInfo = new TableLayout(getActivity());
        for (String key : keys) {
            TableRow pnInfoRow = new TableRow(getActivity());
            pnInfoRow.addView(HGBaseGuiTools.createViewForMessage(getActivity(), SPACING +
                    HGBaseText.getText(key)));
            pnInfoRow.addView(HGBaseGuiTools.createViewForMessage(getActivity(), SPACING +
                    getNewGameInformationForKey(manager, key)));
            pnInfo.addView(pnInfoRow);
        }
        return pnInfo;
    }

    /**
     * @param manager The game manager.
     * @param key     The key for a new game information.
     * @return The value to display.
     */
    protected String getNewGameInformationForKey(GameManager manager, String key) {
        String text = manager.getNewGameInformationText(key);
        return HGBaseText.existsText(text) ? HGBaseText.getText(text) : text;
    }

    /**
     * Returns the preview panel for the arrangement dialog.
     *
     * @param dlg    The arrangement dialog.
     * @param width  The panel's width.
     * @param height The panel's height.
     * @return The panel.
     */
    public PreviewPanel getPreviewPanel(PartsDlg dlg, int width, int height) {
        return new PreviewPanel(dlg, width, height);
    }

    /**
     * Shows the dialog for displaying game instructions.
     *
     * @param mainFrame The main frame.
     */
    public void showGameInstructionsDialog(MainFrame mainFrame) {
        String url = HGBaseText.getText("help_instructionsurl");
        String title = HGBaseText.getText("help_instructions").replace('.', ' ');
        int resId = HGBaseResources.getResourceIdByName(url, HGBaseResources.RAW);
        if (resId > 0) {
            HGBaseDialog.showHtmlDialog(mainFrame, HGBaseResources.getFileContent(resId), title);
        } else {
            InputStream in = HGBaseFileTools.openAssetsFileStream(url);
            if (in != null) {
                HGBaseDialog.showHtmlDialog(mainFrame, HGBaseFileTools.getString(in), title);
            } else {
                HGBaseLog.logError("Game instructions file '" + url + "' not found in raw resources or assets folder!");
            }
        }
    }

    /**
     * Shows the dialog for displaying game hints ("tip of the day").
     *
     * @param mainFrame  The main frame.
     * @param hintType   The hint type as defined in ConstantValue (HINTS_MOVE, HINTS_TURN, HINTS_ROUND, HINTS_GAME) or null.
     * @param gameEngine The game engine.
     */
    public void showGameHintsDialog(MainFrame mainFrame, final String hintType, final GameEngine gameEngine,
                                    final GameConfig config) {
        final Uri[] hintUrls = getGameHintUrls(hintType, gameEngine, config);
        if (hintUrls == null) {
            return;
        }
        if (hintUrls.length > 0) {
            String title = HGBaseText.getText("help_gamehints").replace('.', ' ');
            final HintPanel pnDlg = createGameHintsPanel(hintType, hintUrls, gameEngine, config);
            Dialog dlg = HGBaseDialog.showOkDialog(mainFrame, pnDlg, title, (dialog, which) -> HGBaseConfig.set(ConstantValue.CONFIG_HINT_DONTSHOW, pnDlg.isNotShowAgain()));
            HGBaseGuiTools.setSizeToFullScreen(mainFrame, dlg);
        } else {
            HGBaseLog.logError("The game hints aren't configured correctly!");
        }
    }

    /**
     * Get the hint urls that are defined in the configuration.
     *
     * @param hintType   The hint type to look for.
     * @param gameEngine The game engine.
     * @param config     The game configuration.
     * @return A list with urls.
     */
    protected Uri[] getGameHintUrls(String hintType, GameEngine gameEngine, GameConfig config) {
        Collection<String> hintTypes = (hintType == null) ? ConstantValue.getHintTypes() : List.of(hintType);
        Map<String, Uri> listFiles = new LinkedHashMap<>();
        String path = config.getHintsSetting(ConstantValue.HINTS_PATH);
        String extension = config.getHintsSetting(ConstantValue.HINTS_EXTENSION);
        String languageCode = "-" + HGBaseAppTools.getLanguageCode();
        for (String hintTypeFromList : hintTypes) {
            int hintIndex = 0;
            while (hintIndex >= 0) {
                String hintCode = config.getHintsSetting(hintTypeFromList);
                if (hintCode == null) {
                    hintIndex = -1;
                } else {
                    String fileName = HGBaseText.getText(hintCode);
                    String fullPath = HGBaseFileTools.correctAssetsPath(path + languageCode + "/" + fileName + hintIndex);
                    Uri urlPath = getGameHintUrlPath(fullPath, extension);
                    if (urlPath == null) {
                        fullPath = HGBaseFileTools.correctAssetsPath(path + "/" + fileName + hintIndex);
                        urlPath = getGameHintUrlPath(fullPath, extension);
                    }
                    if (urlPath != null) {
                        hintIndex++;
                        listFiles.put(fullPath, urlPath);
                    } else {
                        if (hintIndex == 0) {
                            HGBaseLog.logWarn("Hint file '" + fullPath + "." + extension + "' not found!");
                        }
                        hintIndex = -1;
                    }
                }
            }
        }
        return listFiles.values().toArray(new Uri[0]);
    }

    /**
     * Returns the URI path for the given path name.
     *
     * @param fullPath  the path to the game hint without extension
     * @param extension the extension to the hint file
     * @return the URI or null
     */
    private Uri getGameHintUrlPath(String fullPath, String extension) {
        String fullPathExt = fullPath + "." + extension;
        Uri urlPath = Uri.parse("android.resource://" + HGBaseAppTools.getPackageName() + "/raw/" + fullPath);
        if (HGBaseFileTools.openUriStream(urlPath) == null) {
            urlPath = Uri.parse(fullPathExt);
            if (HGBaseFileTools.openUriStream(urlPath) == null) {
                urlPath = null;
            }
        }
        return urlPath;
    }

    /**
     * Returns the panel where the game hints are displayed.
     *
     * @param hintType   The hint type as defined in ConstantValue (HINTS_MOVE, HINTS_TURN, HINTS_ROUND, HINTS_GAME) or null.
     * @param hintUrls   The urls with possible hints.
     * @param gameEngine The game engine.
     * @param config The game configuration.
     * @return The panel with game hints.
     */
    protected HintPanel createGameHintsPanel(String hintType, Uri[] hintUrls, GameEngine gameEngine, GameConfig config) {
        return new HintPanel(this, hintUrls);
    }

    /**
     * Get the index for the first index when showing hints.
     *
     * @param numHints the number of available hints.
     * @return the desired index.
     */
    public int getIndexOfIntialHint(int numHints) {
        return DiceUtil.throwDice(0, numHints);
    }

    /**
     * Create a html page for showing the given hint.
     *
     * @param urlHint the url of the hint.
     * @return a new html page.
     * @throws IOException an exception when creating the html page.
     */
    public HGBaseHTMLPageWebView createHtmlPageForHint(Uri urlHint) throws IOException {
        return new HGBaseHTMLPageWebView(getActivity(), urlHint);
    }

    /**
     * Use the html page created by {@link #createHtmlPageForHint(URL)} and show another page.
     *
     * @param htmlPage the html page created by {@link #createHtmlPageForHint(URL)}.
     * @param urlHint  the url of the hint.
     * @throws IOException an exception when setting the html page.
     */
    public void showAnotherHtmlPageForHint(HGBaseHTMLPageWebView htmlPage, Uri urlHint) throws IOException {
        htmlPage.setPage(urlHint);
    }

    /**
     * Shows an advertisement if advertisements are currently activated and it's currently not the pro version.
     *
     * @param mainFrame the main frame
     * @param config    the game configuration
     */
    public void showAdvertisementDialog(MainFrame mainFrame, GameConfig config) {
        if (config.hasAdvertisements() && !config.isProVersion()) {
            HGBaseAdvertisements.showAdvertisementDialog(mainFrame);
        }
    }

    /**
     * Hides the advertisement if there is currently one displayed.
     *
     * @param mainFrame the main frame
     */
    public void hideAdvertisementDialog(MainFrame mainFrame) {
        HGBaseAdvertisements.hideAdvertisementDialog(mainFrame);
    }

    /**
     * Shows the credits dialog.
     *
     * @param parentActivity The parent activity.
     */
    public void showCreditsDlg(Activity parentActivity) {
        HGBaseDialog.showOkDialog(parentActivity, createCreditsPanel(parentActivity), HGBaseText.getText(R.string.credits_dlg_title));
    }

    /**
     * Creates the panel for the credits dialog.
     *
     * @param context The activity context.
     * @return The created panel.
     */
    protected View createCreditsPanel(Context context) {
        WebView webView = new WebView(context);
        webView.loadUrl("file:///android_asset/html/credits/" + HGBaseText.getText(R.string.credits_page_name));
        return webView;
    }
}
