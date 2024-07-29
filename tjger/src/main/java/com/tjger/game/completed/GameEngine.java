package com.tjger.game.completed;

import com.tjger.MainFrame;
import com.tjger.game.GamePlayer;
import com.tjger.game.GameRules;
import com.tjger.game.GameState;
import com.tjger.game.GameStateListener;
import com.tjger.game.MoveInformation;
import com.tjger.game.internal.GameStatistics;
import com.tjger.game.internal.StateFactory;
import com.tjger.gui.GameDialogs;
import com.tjger.gui.internal.GameDialogFactory;
import com.tjger.lib.ConstantValue;
import com.tjger.lib.MoveUtil;
import com.tjger.lib.TimeAction;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import at.hagru.hgbase.gui.HGBaseDialog;
import at.hagru.hgbase.gui.ProgressState;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * Manages the state of a game, including starting and stopping it. The process is move > turn > round > game.
 *
 * @author hagru
 */
public final class GameEngine {

    public static final int ACTION_NEWGAME = 1;
    public static final int ACTION_NEWROUND = 2;
    public static final int ACTION_NEWTURN = 3;
    public static final int ACTION_GAMEFINISHED = 4;
    public static final int ACTION_GAMESTOPPED = 5;
    public static final int ACTION_BEFOREMOVE = 6;
    public static final int ACTION_AFTERMOVE = 7;
    public static final int RESET_CONTINUED_MOVE = -1;
    public static final int RESET_MOVE = 0;
    public static final int RESET_TURN = 1;
    public static final int RESET_ROUND = 2;
    public static final int RESET_GAME = 3;
    public static final int AFTER_TURN = 1;
    public static final int AFTER_ROUND = 2;
    public static final int AFTER_GAME = 3;

    private static final GameEngine engine = new GameEngine();
    private final List<GameStateListener> gameStateListeners; // array with the GameStateListeners that shall
    // be invoked
    private final GameState gameState;
    private final List<TimeAction> timeActionList;
    /*
     * All data needs to be package-protected for the game engine file reader/writer.
     */ int numberPlayers; // number of active players of the current game
    GamePlayer[] activePlayers; // array with the active players
    int playerStartRound; // number of the player, that started the current round
    int cyclingPlayerStartGame; // index of the player that starts the game if the start player shall cycle
    int currentPlayer; // the index of the current player
    int currentRound; // the current round (starts with 1)
    int currentTurn; // the current turn of the round (starts with 1)
    AtomicInteger currentMove = new AtomicInteger(0);// the current move within a turn
    boolean activeGame; // indicates whether the game is active or not
    boolean activeRound; // indicates whether the current round is active or not
    boolean stoppedGame = true; // to find out whether game was stopped or normal finish
    private boolean stopGameProcessing = false; // to prevent calling stop game two times
    private boolean nextGameProcessing = false; // to prevent error message on next game

    private GameEngine() {
        super();
        gameStateListeners = new ArrayList<>();
        timeActionList = new ArrayList<>();
        cyclingPlayerStartGame = 0;
        initGame();
        // create the game state class
        gameState = StateFactory.getInstance().createGameState();
    }

    /**
     * @return The one and only instance of the game state.
     */
    public static GameEngine getInstance() {
        return engine;
    }

    /**
     * Initializes the game variables.
     */
    private void initGame() {
        numberPlayers = 0;
        playerStartRound = -1;
        currentPlayer = -1;
        currentTurn = 0;
        currentRound = 0;
        currentMove.set(0);
        activeGame = false;
        activeRound = false;
        activePlayers = null;
    }

    /**
     * @return The game manager.
     */
    public GameManager getGameManager() {
        return GameManager.getInstance();
    }

    /**
     * @return The main frame.
     */
    public MainFrame getMainFrame() {
        return getGameManager().getMainFrame();
    }

    /**
     * @return The game configuration.
     */
    private GameConfig getGameConfig() {
        return getGameManager().getGameConfig();
    }

    /**
     * @return The player manager.
     */
    private PlayerManager getPlayerManager() {
        return getGameManager().getPlayerManager();
    }

    /**
     * @return The game's rules.
     */
    private GameRules getGameRules() {
        return getGameManager().getGameRules();
    }

    /**
     * @return The game's state.
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * @return The game's statistics.
     */
    private GameStatistics getGameStatistics() {
        return getGameManager().getGameStatistics();
    }

    /**
     * @param listener A new game state listener to add.
     */
    public void addGameStateListener(GameStateListener listener) {
        synchronized (gameStateListeners) {
            if (listener != null && !gameStateListeners.contains(listener)) {
                gameStateListeners.add(listener);
            }
        }
    }

    /**
     * @param listener The game state listener to remove.
     */
    public void removeGameStateListener(GameStateListener listener) {
        synchronized (gameStateListeners) {
            gameStateListeners.remove(listener);
        }
    }

    /**
     * Contributes the new game state for all listeners. Internal use only!
     *
     * @param action The action why the game state has to be contributed.
     */
    void contributeGameState(final int action) {
        // contribute the game state
        synchronized (gameStateListeners) {
            getMainFrame().runOnUiThread(() -> {
                GameState state = getGameState();
                for (GameStateListener gsListener : gameStateListeners) {
                    switch (action) {
                        case ACTION_NEWGAME:
                            gsListener.newGameStarted(state, GameEngine.this);
                            break;
                        case ACTION_NEWROUND:
                            gsListener.newRoundStarted(state, GameEngine.this);
                            break;
                        case ACTION_NEWTURN:
                            gsListener.newTurnStarted(state, GameEngine.this);
                            break;
                        case ACTION_GAMEFINISHED:
                            gsListener.gameFinished(true);
                            break;
                        case ACTION_GAMESTOPPED:
                            gsListener.gameFinished(false);
                            break;
                        case ACTION_BEFOREMOVE:
                            gsListener.gameStateBeforeMove(state, GameEngine.this);
                            break;
                        case ACTION_AFTERMOVE:
                            gsListener.gameStateAfterMove(state, GameEngine.this);
                            break;
                        default:
                            break;
                    }
                }
                updateGamePanel();
            });
        }
    }

    /**
     * Updates the game panel.
     */
    private void updateGamePanel() {
        getMainFrame().getGamePanel().repaint();
    }

    /**
     * @return The number of active players.
     */
    public int getNumberPlayers() {
        return this.numberPlayers;
    }

    /**
     * @param withoutDropOut True if players that are drop out shall not be counted.
     * @return The number of active players.
     * @see ConstantValue#INCLUDE_DROPOUT
     * @see ConstantValue#EXCLUDE_DROPOUT
     */
    public int getNumberPlayers(boolean withoutDropOut) {
        GamePlayer[] active = getActivePlayers(withoutDropOut);
        if (active == null) {
            return 0;
        } else {
            return active.length;
        }
    }

    /**
     * @return An array with the active players.
     */
    public GamePlayer[] getActivePlayers() {
        return getActivePlayers(ConstantValue.INCLUDE_DROPOUT);
    }

    /**
     * For internal use only! Used by MainMenu for next game.
     *
     * @param player New player to set.
     */
    public void setActivePlayers(GamePlayer[] player) {
        this.activePlayers = HGBaseTools.clone(player);
        this.numberPlayers = player.length;
    }

    /**
     * @param withoutDropOut True to get only player that aren't drop out.
     * @return An array with active players (inclusive or without players that are drop out).
     */
    public GamePlayer[] getActivePlayers(boolean withoutDropOut) {
        if (activePlayers == null) {
            return null;
        }
        if (withoutDropOut) {
            List<GamePlayer> listPlayer = new ArrayList<>();
            for (GamePlayer activePlayer : activePlayers) {
                if (!activePlayer.isDropOut()) {
                    listPlayer.add(activePlayer);
                }
            }
            return listPlayer.toArray(new GamePlayer[0]);
        } else {
            return activePlayers.clone();
        }
    }

    /**
     * @return The current player.
     */
    public GamePlayer getCurrentPlayer() {
        if (currentPlayer < 0) {
            return null;
        }
        return getActivePlayers()[currentPlayer];
    }

    /**
     * Returns the index of the current player.
     *
     * @return The index of the current player.
     */
    public int getCurrentPlayerIndex() {
        return currentPlayer;
    }

    /**
     * @return the index of the player who starts the round
     */
    int getPlayerStartRound() {
        return playerStartRound;
    }

    /**
     * @return The player that started the current round.
     */
    public GamePlayer getFirstPlayer() {
        if (playerStartRound < 0) {
            return null;
        } else {
            return getActivePlayers()[playerStartRound];
        }

    }

    /**
     * Returns the first player (of the next) game assuming that the start player cycles through all players.<p>
     * This method is intended to be used by {@link GameRules#getGameStartPlayer(GameState)} to define this
     * default behavior.
     *
     * @return the indented player to start the next game
     */
    public GamePlayer getCyclingFirstGamePlayer() {
        return getActivePlayers(ConstantValue.EXCLUDE_DROPOUT)[getCyclingFirstGamePlayerIndex()];
    }

    /**
     * @return the index of the indented player to start the next game
     */
    public int getCyclingFirstGamePlayerIndex() {
        return cyclingPlayerStartGame % numberPlayers;
    }

    /**
     * @param player An active player.
     * @return The previous player.
     * @see ConstantValue#INCLUDE_DROPOUT
     * @see ConstantValue#EXCLUDE_DROPOUT
     */
    public GamePlayer getPrevPlayer(GamePlayer player, boolean withoutDropOut) {
        GamePlayer prev = null;
        int index = getIndexOfPlayer(player, withoutDropOut);
        if (index != -1) {
            prev = getActivePlayers()[getPrevPlayerIndex(index)];
            if (prev.isDropOut() && withoutDropOut && getNumberPlayers(ConstantValue.EXCLUDE_DROPOUT) > 0) {
                return getPrevPlayer(prev, ConstantValue.EXCLUDE_DROPOUT);
            }
        }
        return prev;
    }

    /**
     * @see #getPrevPlayer(GamePlayer, boolean)
     */
    public GamePlayer getPrevPlayer(GamePlayer player) {
        return getPrevPlayer(player, ConstantValue.INCLUDE_DROPOUT);
    }

    /**
     * @param player An active player.
     * @return The next player.
     * @see ConstantValue#INCLUDE_DROPOUT
     * @see ConstantValue#EXCLUDE_DROPOUT
     */
    public GamePlayer getNextPlayer(GamePlayer player, boolean withoutDropOut) {
        GamePlayer next = null;
        int index = getIndexOfPlayer(player);
        if (index != -1) {
            final GamePlayer[] players = getActivePlayers();
            if (players != null) {
                next = players[getNextPlayerIndex(index)];
                if (next.isDropOut() && withoutDropOut && getNumberPlayers(withoutDropOut) > 0) {
                    return getNextPlayer(next, ConstantValue.EXCLUDE_DROPOUT);
                }
            }
        }
        return next;
    }

    /**
     * @see #getNextPlayer(GamePlayer, boolean)
     */
    public GamePlayer getNextPlayer(GamePlayer player) {
        return getNextPlayer(player, ConstantValue.INCLUDE_DROPOUT);
    }

    /**
     * @param player An active player.
     * @return The index of this player.
     */
    public int getIndexOfPlayer(GamePlayer player) {
        return getIndexOfPlayer(player, ConstantValue.INCLUDE_DROPOUT);
    }

    /**
     * @param player         An active player.
     * @param withoutDropOut When calculating the index ignore players that are drop out.
     * @return The index of this player.
     * @see ConstantValue#INCLUDE_DROPOUT
     * @see ConstantValue#EXCLUDE_DROPOUT
     */
    public int getIndexOfPlayer(GamePlayer player, boolean withoutDropOut) {
        int index = -1;
        GamePlayer[] active = getActivePlayers(withoutDropOut);
        for (int i = 0; active != null && i < active.length && index == -1; i++) {
            if (active[i].equals(player)) {
                index = i;
            }
        }
        return index;
    }

    /**
     * @param index An index.
     * @return The player with the given index or null.
     */
    public GamePlayer getPlayerWithIndex(int index) {
        return getPlayerWithIndex(index, ConstantValue.INCLUDE_DROPOUT);
    }

    /**
     * @param index An index.
     * @return The player with the given index or null.
     * @see ConstantValue#INCLUDE_DROPOUT
     * @see ConstantValue#EXCLUDE_DROPOUT
     */
    public GamePlayer getPlayerWithIndex(int index, boolean withoutDropOut) {
        GamePlayer[] active = getActivePlayers(withoutDropOut);
        if (active != null && index >= 0 && index < active.length) {
            return active[index];
        }
        return null;
    }

    /**
     * @param pieceColor A piece color, must not be null.
     * @return The player with the given color or null.
     */
    public GamePlayer getPlayerWithColor(String pieceColor) {
        return getPlayerWithColor(pieceColor, ConstantValue.INCLUDE_DROPOUT);
    }

    /**
     * @param pieceColor A piece color, must not be null.
     * @return The player with the given color or null.
     * @see ConstantValue#INCLUDE_DROPOUT
     * @see ConstantValue#EXCLUDE_DROPOUT
     */
    public GamePlayer getPlayerWithColor(String pieceColor, boolean withoutDropOut) {
        for (GamePlayer player : getActivePlayers(withoutDropOut)) {
            if (pieceColor.equals(player.getPieceColor())) {
                return player;
            }
        }
        return null;
    }

    /**
     * The current round is 0 before a game starts. On every new game the round is set to 1 and increased
     * after every round.
     *
     * @return The current round of this game.
     */
    public int getCurrentRound() {
        return currentRound;
    }

    /**
     * The current turn is 0 after game starts. On every new round the turn is set to 1 and increased after
     * every turn.
     *
     * @return The current turn of this round.
     */
    public int getCurrentTurn() {
        return currentTurn;
    }

    /**
     * The current move is reset to 0 after every turn and increased before a player starts his move.
     *
     * @return The current move of this turn.
     */
    public int getCurrentMove() {
        return currentMove.get();
    }

    /**
     * Starts a new game.
     *
     * @param numberPlayers Number of players that take part (minPlayer ... maxPlayer).
     * @return 0 if the game was started.
     */
    public int startGame(int numberPlayers) {
        getMainFrame().setCursorWait();
        int ret = initActivePlayers(numberPlayers);
        if (ret == 0) {
            cyclingPlayerStartGame = 0;
            ret = newGame();
        }
        if (ret != 0) {
            stopGame();
        }
        return ret;
    }

    /**
     * Starts a new game based on the last game. For internal use!
     */
    public int newGame() {
        if (numberPlayers > 0) {
            resetDelayActions();
            resetScore(GamePlayer.SCORE_GAME);
            if (getGameStatistics() != null) {
                getGameStatistics().resetRanking();
            }
            int ret = resetGameState(RESET_GAME);
            if (ret != 0) {
                return ret;
            }
            getGameDialogsInstance().hideAdvertisementDialog(getMainFrame());
            activeGame = true;
            stoppedGame = false;
            currentRound = 0;
            currentTurn = 0;
            contributeGameState(ACTION_NEWGAME);
            if (getGameConfig().isChangedOnNewGame()) {
                setGameStateChanged();
            }
            getMainFrame().setCursorDefault();
            getMainFrame().showHintsDialog(ConstantValue.HINTS_GAME);
            delayNewRound(0);
        }
        return 0;
    }

    /**
     * On a local game the game state's methods for game, round or turn is called. If it's the server the
     * server's game state is sent to the client afterwards and the server waits for ok. If it's a client the
     * game state is not reset but it's waited for the server message and ok is sent.
     *
     * @param reset Tells what to reset (RESET_CONTINUED_MOVE, RESET_MOVE, RESET_TURN, RESET_ROUND, RESET_GAME).
     * @return 0 if successful.
     */
    public int resetGameState(int reset) {
        if (getGameState() != null) {
            // reset the game state
            switch (reset) {
                case RESET_MOVE:
                case RESET_CONTINUED_MOVE:
                    getGameState().resetMove(this, (reset == RESET_CONTINUED_MOVE));
                    break;
                case RESET_TURN:
                    getGameState().resetTurn(this);
                    break;
                case RESET_ROUND:
                    getGameState().resetRound(this);
                    break;
                case RESET_GAME:
                    getGameState().resetGame(this);
                    break;
                default:
                    break;
            }
            updateGamePanel();
        }
        return 0;
    }

    /**
     * @return True if the game process is stopped at the moment.
     */
    public boolean isStopGameProcessing() {
        return stopGameProcessing;
    }

    /**
     * @return True if the next game is processing.
     */
    public boolean isNextGameProcessing() {
        return nextGameProcessing;
    }

    /**
     * Stops the current game.
     */
    public void stopGame() {
        if (stopGameProcessing) {
            return;
        }
        stopGameProcessing = true;
        stoppedGame = true;
        // call the stop in an own thread because of graphics problems
        Thread t = new Thread(() -> {
            // stop the game
            resetDelayActions();
            getMainFrame().setStatusProgress(ProgressState.STATE_NORMAL);
            GamePlayer player = getCurrentPlayer();
            if (player != null) {
                player.stopPlaying();
            }
            initGame();
            if (getGameState() != null) {
                getGameState().stopGame();
            }
            if (getGameStatistics() != null) {
                getGameStatistics().resetRanking();
            }
            contributeGameState(ACTION_GAMESTOPPED);
            getMainFrame().setCursorDefault();
        });
        t.start();
        try {
            while (t.isAlive()) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopGameProcessing = false;
    }

    /**
     * Starts a new round.
     */
    public void newRound() {
        currentTurn = 0;
        //getMainFrame().setCursorWait();
        resetScore(GamePlayer.SCORE_ROUND);
        int ret = resetGameState(RESET_ROUND);
        if (ret == 0) {
            currentRound++;
            currentTurn = 1;
            currentMove.set(0);
            activeRound = true;
            playerStartRound = getIndexOfPlayer(getGameRules().getStartPlayer(getGameState()));
            currentPlayer = playerStartRound;
            cyclingPlayerStartGame = (playerStartRound + 1) % numberPlayers;
            contributeGameState(ACTION_NEWROUND);
            if (getGameConfig().isChangedOnNewRound()) {
                setGameStateChanged();
            }
            //getMainFrame().setCursorDefault();
            getMainFrame().showHintsDialog(ConstantValue.HINTS_ROUND);
            delayNewTurn(0);
        } else {
            HGBaseDialog.printError("err_newround", getMainFrame());
        }
    }

    /**
     * Starts a new turn.
     */
    synchronized void newTurn() {
        //getMainFrame().setCursorWait();
        resetScore(GamePlayer.SCORE_TURN);
        int ret = resetGameState(RESET_TURN);
        if (ret == 0) {
            currentMove.set(1);
            contributeGameState(ACTION_NEWTURN);
            if (getGameConfig().isChangedOnNewTurn()) {
                setGameStateChanged();
            }
            //getMainFrame().setCursorDefault();
            getMainFrame().showHintsDialog(ConstantValue.HINTS_TURN);
            delayDoPlayerMove(0, false);
        } else {
            HGBaseDialog.printError("err_newturn", getMainFrame());
        }
    }

    /**
     * Performs the move of the current player.
     *
     * @param move The player's move to perform.
     */
    public synchronized void performMove(MoveInformation move) {
        getGameManager().getMainFrame().setStatusProgress(ProgressState.STATE_NORMAL);
        // change the game state
        GameState state = getGameState();
        state.changeState(getCurrentPlayer(), move, this);
        boolean isMoveComplete = testMoveComplete(move);
        updateGamePanel();
        // look for the rules and do the scoring
        GameRules rules = getGameRules();
        GameStatistics stats = getGameStatistics();
        GamePlayer[] active = getActivePlayers();
        if (active != null) {
            boolean turnFinished = false;
            if (isMoveComplete) {
                //getGameManager().getMainFrame().setCursorWait();
                // do the scoring after every move
                rules.doScoring(active, getGameState());
                // test if the turn is finished
                turnFinished = rules.isTurnFinished(getGameState());
                // test if round is finished
                if (rules.isRoundFinished(getGameState())) {
                    int[] ranks = rules.getRoundRanking(active);
                    stats.doRoundRanking(getActivePlayers(), ranks);
                    activeRound = false;
                }
                // test if game is finished
                if (rules.isGameFinished(getGameState())) {
                    int[] ranks = rules.getGameRanking(active);
                    stats.doGameRanking(getActivePlayers(), ranks);
                    activeGame = false;
                }
                //getGameManager().getMainFrame().setCursorDefault();
            }
            // inform all listeners that a move has finished
            contributeGameState(ACTION_AFTERMOVE);
            if (getGameConfig().isChangedAfterMove()) {
                setGameStateChanged();
            }
            if (isMoveComplete) {
                // look if the game is finished and inform the listeners in that case
                if (!isActiveGame()) {
                    contributeGameState(ACTION_GAMEFINISHED);
                    getGameDialogsInstance().showAdvertisementDialog(getMainFrame(), getGameConfig());
                }
                // show the dialogs after turn, round or game
                showDialogs(isMoveComplete, turnFinished, rules);
            } else {
                proceedGameAfterMove(isMoveComplete, turnFinished, rules);
            }
        }
    }

    /**
     * @param isMoveComplete true if move is complete
     * @param turnFinished   true if turn is finished
     * @param rules          the game rules
     */
    public void proceedGameAfterMove(boolean isMoveComplete, boolean turnFinished, GameRules rules) {
        // look if the game is still active or not and to the next move
        if (isActiveRound() && isActiveGame()) {
            if (isMoveComplete) {
                // get the next player
                currentPlayer = getIndexOfPlayer(rules.getNextPlayer(getCurrentPlayer(), getGameState()));
            }
            // if turn was finished, start a new turn
            if (turnFinished) {
                currentTurn++;
                currentMove.set(0);
            }
            // do the move of the next player
            if (currentMove.get() == 0) {
                delayNewTurn(getGameConfig().getDelayTurnWithSpeedFactor());
            } else {
                if (isMoveComplete) {
                    currentMove.incrementAndGet();
                }
                delayDoPlayerMove(getGameConfig().getDelayMoveWithSpeedFactor(), !isMoveComplete);
            }
        } else if (isActiveGame()) {
            // the round is finished, but the game is still active - check
            // if there shall be an interruption
            if (!getGameConfig().isInterruptAfterRound()) {
                // if there should not be an interruption, call newRound()
                delayNewRound(getGameConfig().getDelayRoundWithSpeedFactor());
            }
        }
    }

    /**
     * @param move The move to test if the move is complete.
     * @return True if the move is complete, otherwise false.
     */
    private boolean testMoveComplete(MoveInformation move) {
        return MoveUtil.isMoveComplete(move);
    }

    /**
     * Shows the dialogs after turn, round or/and game.
     *
     * @param isMoveComplete true if move is complete
     * @param turnFinished   true if turn is finished
     * @param rules          the game rules
     */
    private void showDialogs(boolean isMoveComplete, boolean turnFinished, GameRules rules) {
        showDialogsIntern(isMoveComplete, turnFinished, rules, 1);
    }

    /**
     * <b>Important:</b> This method must not be called from a client but only from the game state info panel!!!<br>
     * Shows the dialogs after turn, round or/and game depending on the given step.
     *
     * @param isMoveComplete true if move is complete
     * @param turnFinished   true if turn is finished
     * @param rules          the game rules
     * @param step           the current step to show
     */
    public void showDialogsIntern(boolean isMoveComplete, boolean turnFinished, GameRules rules, int step) {
        if (step == 1) {
            if (getGameConfig().showDialogAfterTurn() && rules.isTurnFinished(getGameState())) {
                // look if a dialog shall be displayed after every turn
                showDialog(AFTER_TURN, isMoveComplete, turnFinished, rules);
            } else {
                step++;
            }
        }
        if (step == 2) {
            if (getGameConfig().showDialogAfterRound() && rules.isRoundFinished(getGameState())) {
                // look if a dialog shall be displayed after a round
                showDialog(AFTER_ROUND, isMoveComplete, turnFinished, rules);
            } else {
                step++;
            }
        }
        if (step == 3) {
            if (getGameConfig().showDialogAfterGame() && rules.isGameFinished(getGameState())) {
                // look if a dialog shall be displayed after the game
                showDialog(AFTER_GAME, isMoveComplete, turnFinished, rules);
            } else {
                step++;
            }
        }
        if (step == 4) {
            proceedGameAfterMove(isMoveComplete, turnFinished, rules);
        }
    }

    /**
     * @param dialogMode     Says which dialog shall be displayed (AFTER_TURN, AFTER_ROUND, AFTER_GAME).
     * @param isMoveComplete true if move is complete
     * @param turnFinished   true if turn is finished
     * @param rules          The game rules.
     */
    private void showDialog(int dialogMode, boolean isMoveComplete, boolean turnFinished, GameRules rules) {
        getGameDialogsInstance().showGameStateInfoPanel(getMainFrame(), dialogMode, isMoveComplete, turnFinished, getGameState(), this, rules);
        updateGamePanel();
    }

    /**
     * @return the instance of the game dialogs class
     */
    private GameDialogs getGameDialogsInstance() {
        return GameDialogFactory.getInstance().createGameDialogs(getMainFrame());
    }

    /**
     * Invokes the move of the current player. Internal use only!
     *
     * @param continued If {@code true}, this move is a continuation of the previous move, which means, the previous move was not completed.
     */
    public void doPlayerMove(boolean continued) {
        int ret = resetGameState(continued ? RESET_CONTINUED_MOVE : RESET_MOVE);
        if (ret != 0) {
            HGBaseDialog.printError(ret, getMainFrame());
            return;
        }

        contributeGameState(ACTION_BEFOREMOVE);
        GamePlayer player = getCurrentPlayer();
        if (player != null) {
            if (player.isHuman()) {
                getMainFrame().showHintsDialog(ConstantValue.HINTS_MOVE);
            }
            player.startMove();
        }
    }

    /**
     * Call the method <code>newRound</code> with the defined delay.
     */
    private void delayNewRound(int delay) {
        if (delay < 0) {
            delay = 0;
        }
        TimeAction t = new TimeAction(delay) {

            @Override
            public void doAction() {
                //getMainFrame().setCursorWait();
            }

            @Override
            public void afterAction() {
                newRound();
                //getMainFrame().setCursorDefault();
            }

            @Override
            public String toString() {
                return super.toString() + ": delayNewRound";
            }
        };
        TimeAction.run(t);
    }

    /**
     * Call the method <code>newTurn</code> with the defined delay.
     */
    private void delayNewTurn(int delay) {
        if (delay > 0) {
            TimeAction t = new TimeAction(delay) {

                @Override
                public void doAction() {
                    //getMainFrame().setCursorWait();
                }

                @Override
                public void afterAction() {
                    newTurn();
                    //getMainFrame().setCursorDefault();
                }

                @Override
                public String toString() {
                    return super.toString() + ": delayNewTurn";
                }
            };
            TimeAction.run(t);
        } else {
            newTurn();
        }
    }

    /**
     * Call the method <code>doPlayerMove</code> with the defined delay.
     *
     * @param delay     The minimum time in milliseconds that the move should take to execute.
     * @param continued If {@code true}, this move is a continuation of the previous move, which means, the previous move was not completed.
     */
    private void delayDoPlayerMove(int delay, boolean continued) {
        if (delay > 0) {
            TimeAction t = new TimeAction(delay) {

                @Override
                public void doAction() {
                    //getMainFrame().setCursorWait();
                }

                @Override
                public void afterAction() {
                    doPlayerMove(continued);
                    //getMainFrame().setCursorDefault();
                }

                @Override
                public String toString() {
                    return super.toString() + ": delayDoPlayerMove";
                }
            };
            TimeAction.run(t);
        } else {
            doPlayerMove(continued);
        }
    }

    /**
     * Resets all delay actions.
     */
    private void resetDelayActions() {
        getMainFrame().setCursorWait();
        synchronized (timeActionList) {
            boolean notCancel;
            do {
                notCancel = false;
                for (TimeAction t : timeActionList) {
                    if (t.isAlive() && !t.isCancelAble()) {
                        notCancel = true;
                    } else {
                        t.interrupt();
                    }
                }
                if (notCancel) {
                    HGBaseTools.sleep(50);
                }
            } while (notCancel);
            timeActionList.clear();
        }
        getMainFrame().setCursorDefault();
    }

    /**
     * Adds a time action that will be interrupted if a games is stopped or started again without stopping.
     * After the action is finished, the object should be removed with <code>removeTimeAction</code>.
     *
     * @param ta The time action to announce to the game engine.
     * @see #removeTimeAction(TimeAction)
     */
    public void addTimeAction(TimeAction ta) {
        synchronized (timeActionList) {
            timeActionList.add(ta);
        }
    }

    /**
     * Removes the time action from the game engine's watch list. This method should be called, after a time
     * action is finished that was added with <code>addTimeAction</code>.
     *
     * @param ta The time action to remove from the game engine's watch list.
     * @see #addTimeAction(TimeAction)
     */
    public void removeTimeAction(TimeAction ta) {
        synchronized (timeActionList) {
            timeActionList.remove(ta);
        }
    }

    /**
     * Indicate that the game state has changed (for saving).
     */
    private void setGameStateChanged() {
        getMainFrame().setChanged(true);
    }

    /**
     * @return Whether this is an active game or not.
     */
    public boolean isActiveGame() {
        return activeGame;
    }

    /**
     * @return Whether this is an active round or not.
     */
    public boolean isActiveRound() {
        return activeRound;
    }

    /**
     * @return true if the game was stopped (manually), otherwise false
     */
    public boolean isStoppedGame() {
        return stoppedGame;
    }

    /**
     * @param index A player's index.
     * @return The index of the next player.
     */
    private int getNextPlayerIndex(int index) {
        int numPlayers = getNumberPlayers();
        return (numPlayers == 0) ? 0 : ((index + 1) % numPlayers);
    }

    /**
     * @param index A player's index.
     * @return The index of the previous player.
     */
    private int getPrevPlayerIndex(int index) {
        int numPlayers = getNumberPlayers();
        return (numPlayers == 0) ? 0 : ((index + numPlayers - 1) % numPlayers);
    }

    /**
     * @param numberPlayers Number of active players.
     * @return 0 if successful.
     */
    private int initActivePlayers(int numberPlayers) {
        int ret = 0;
        if (numberPlayers < getPlayerManager().getMinPlayers() || numberPlayers > getPlayerManager().getMaxPlayers()) {
            numberPlayers = getPlayerManager().getMinPlayers();
        }
        this.numberPlayers = numberPlayers;
        activePlayers = new GamePlayer[this.numberPlayers];
        GamePlayer[] allPlayers = getPlayerManager().getPlayers();
        Integer[] playerIndex = createPlayerIndexArray(this.numberPlayers);
        int playersOrder = getGameConfig().getPlayersOrder();
        Arrays.sort(playerIndex, new PlayerOrderComparator(playersOrder));
        for (int i = 0; i < activePlayers.length; i++) {
            int allPlayerIndex = playerIndex[i];
            activePlayers[i] = allPlayers[allPlayerIndex];
            getGameStatistics().resetScore(activePlayers[i], GamePlayer.SCORE_GAME);
        }
        return ret;
    }

    /**
     * @param numPlayers The number of (active) players.
     * @return A list with the indexes of the players for sorting with PlayerOrderComparator.
     */
    private Integer[] createPlayerIndexArray(int numPlayers) {
        Integer[] indexList = new Integer[numPlayers];
        for (int i = 0; i < indexList.length; i++) {
            indexList[i] = i;
        }
        return indexList;
    }

    /**
     * Reset the score from the given type for all active players.
     *
     * @param scoreType The score type.
     */
    private void resetScore(int scoreType) {
        GamePlayer[] player = getActivePlayers();
        for (int i = 0; player != null && i < player.length; i++) {
            getGameStatistics().resetScore(player[i], scoreType);
            if (scoreType == GamePlayer.SCORE_GAME) {
                // always when the game score is reset, reset the drop-out state of a player
                player[i].setDropOut(false);
            }
        }
    }

    /**
     * Reset the drop out state of the active players.
     */
    public void resetGamePlayersDropOut() {
        GamePlayer[] players = engine.getActivePlayers();
        for (GamePlayer player : players) {
            player.setDropOut(false);
        }
    }

    /**
     * Save the current game engine.
     *
     * @param doc  The document object.
     * @param root The root for the game engine.
     * @return 0 if saving was successful.
     */
    public int saveEngine(Document doc, Element root) {
        GameEngineFileOperator.write(doc, root, this);
        return 0;
    }

    /**
     * Loads the game engine.
     *
     * @param root The root of the game engine information.
     * @return 0 if loading was successful.
     */
    public int loadEngine(Node root) {
        if (!GameEngineFileOperator.read(root, this)) {
            return -10706;
        } else {
            return 0;
        }
    }
}
