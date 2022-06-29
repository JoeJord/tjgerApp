package com.tjger.game.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.tjger.game.GamePlayer;
import com.tjger.game.completed.GameConfig;
import com.tjger.game.completed.GameManager;
import com.tjger.lib.ConstantValue;
import com.tjger.lib.PlayerUtil;
import com.tjger.lib.ScoreUtil;

import at.hagru.hgbase.lib.HGBaseConfig;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * Holds all statistical information like scores of the players and played/won games.
 *
 * @author hagru
 */
public class GameStatistics {

    final private static int SCORE_TURN = GamePlayer.SCORE_TURN;
    final private static int SCORE_ROUND = GamePlayer.SCORE_ROUND;
    final private static int SCORE_GAME = GamePlayer.SCORE_GAME;

    private static GameStatistics statistics = new GameStatistics();

    final private Map<String,Integer> scoreGame; // for current game score, PlayerKey -> Integer
    final private Map<String,Integer>  scoreRound; // for current round score, PlayerKey -> Integer
    final private Map<String,Integer>  scoreTurn; // for current turn score, PlayerKey -> Integer
    final private Map<String,WonPlayedGames>  gameMap;  // for won/played games PlayerKey -> WonPlayedGames
    final private List<HighScoreElement> highScoreList; // list for highscores, contains HighScoreElement

    GamePlayer[] gamePlayerList; // players of the current game
    int[] gameRankingList; // the players' ranking of the current game
    List<ScoreRankElement[]> roundScoreRankingList;  // the scores and ranks per round of the current game
                                                     // every list entry is an array of ScoreRankElement
    final private boolean rememberScores;
    final private boolean rememberGames;
    final private int highScoreLength;
    final private boolean onlyFirstHighScore;
    /**
     * Flag if lower score is better.
     */
    final private boolean isLowerScoreBetter;

    private GameStatistics() {
        super();
        scoreGame = new HashMap<>();
        scoreRound = new HashMap<>();
        scoreTurn = new HashMap<>();
        gameMap = new HashMap<>();
        highScoreList = new ArrayList<>();
        resetRanking();
        // Read values from GameConfig ...
        GameConfig config = GameConfig.getInstance();
        rememberScores = config.isRememberScores();
        rememberGames = config.isRememberGames();
        highScoreLength = config.getHighScoreLength();
        onlyFirstHighScore = config.isOnlyFirstHighScore();
        this.isLowerScoreBetter = config.isLowerScoreBetter();
        // ... and init with the configuration file
        loadHighScore();
        loadGameStatistics();
    }

    /**
     * Returns the score map, depending on the score type.
     *
     * @param scoreType the score type defined in {@link GamePlayer}
     * @return the according player/score mape or null
     */
    private Map<String,Integer> getScoreMap(int scoreType) {
        switch (scoreType) {
            case SCORE_TURN: return scoreTurn;
            case SCORE_ROUND: return scoreRound;
            case SCORE_GAME: return scoreGame;
            default: return null;
        }
    }

    /**
     * @return The one and only game statistics object.
     */
    public static GameStatistics getInstance() {
        return statistics;
    }

    /**
     * Reset the score, this is 0 or the old score, if scores are remembered.
     *
     * @param player The player whose score shall be reseted.
     * @param scoreTypess The type of some scores (see GamePlayer).
     */
    public void resetScore(GamePlayer player, int scoreTypes) {
        resetScore(player.getName(), player.getType().getId(), scoreTypes);
    }

    /**
     * Reset the score, this is 0 or the old score, if scores are remembered.
     *
     * @param playerName Name of the player.
     * @param playerType The Player type.
     * @param scoreTypes The type of some scores (see GamePlayer).
     */
    public void resetScore(String playerName, String playerType, int scoreTypes) {
        PlayerKey key = new PlayerKey(playerName, playerType);
        if ((scoreTypes & SCORE_GAME) == SCORE_GAME && getScoreMap(SCORE_GAME) != null) {
            if (rememberScores && getScoreMap(SCORE_GAME).containsKey(key.toString())) {
                // do nothing and keep the player's score
            } else {
                getScoreMap(SCORE_GAME).put(key.toString(), Integer.valueOf(0));
            }
            saveScores();
        }
        if ((scoreTypes & SCORE_ROUND) == SCORE_ROUND && getScoreMap(SCORE_ROUND) != null) {
            getScoreMap(SCORE_ROUND).put(key.toString(), Integer.valueOf(0));
        }
        if ((scoreTypes & SCORE_TURN) == SCORE_TURN && getScoreMap(SCORE_TURN) != null) {
            getScoreMap(SCORE_TURN).put(key.toString(), Integer.valueOf(0));
        }
    }


    /**
     * Removes all game scores of all players (profile).
     * For internal use only!
     */
    public void removeGameScores() {
        PlayerProfiles profiles = GameManager.getInstance().getPlayerProfiles();
        for (GamePlayer player : profiles.getPlayers()) {
            removeGameScore(player);
        }
    }

    /**
     * Removes the save game scores of a player.
     * For inernal use only!
     *
     * @param player
     */
    public void removeGameScore(GamePlayer player) {
        PlayerKey key = new PlayerKey(player);
        HGBaseConfig.remove(ConstantValue.CONFIG_PLAYERSCORE + "." + key.toString());
        if (getScoreMap(SCORE_GAME) != null) {
            getScoreMap(SCORE_GAME).remove(key.toString());
        }
    }

    /**
     * @param player The player whose score shall be reseted.
     * @param scoreType The type of the score (see GamePlayer).
     * @return The current score of the given player.
     */
    public int getScore(GamePlayer player, int scoreType) {
        return getScore(player.getName(), player.getType().getId(), scoreType);
    }

    /**
     * @param playerName Name of the player.
     * @param playerType The Player type.
     * @param scoreType The type of the score (see GamePlayer).
     * @return The current score of the given player.
     */
    public int getScore(String playerName, String playerType, int scoreType) {
        if (getScoreMap(scoreType) != null) {
            PlayerKey key = new PlayerKey(playerName, playerType);
            Integer value = getScoreMap(scoreType).get(key.toString());
            return (value == null) ? 0 : value.intValue();
        }
        return 0;
    }

    /**
     * @param player The player which score shall be changed.
     * @param score The new score to add to the old one.
     * @param scoreTypes The score types (see GamePlayer).
     */
    public void addScore(GamePlayer player, int score, int scoreTypes) {
        addScore(player.getName(), player.getType().getId(), score, scoreTypes);
    }

    /**
     * @param playerName Name of the player.
     * @param playerType The player type.
     * @param score The new score to add to the old one.
     * @param scoreTypes The score types (see GamePlayer).
     */
    public void addScore(String playerName, String playerType, int score, int scoreTypes) {
        if ((scoreTypes & SCORE_GAME) == SCORE_GAME) {
            addScoreToMap(playerName, playerType, score, SCORE_GAME);
            saveScores();
        }
        if ((scoreTypes & SCORE_ROUND) == SCORE_ROUND) {
            addScoreToMap(playerName, playerType, score, SCORE_ROUND);
        }
        if ((scoreTypes & SCORE_TURN) == SCORE_TURN) {
            addScoreToMap(playerName, playerType, score, SCORE_TURN);
        }
    }

    /**
     * @param playerName Name of the player.
     * @param playerType The player type.
     * @param score The new score to add to the old one.
     * @param scoreType The score types (see GamePlayer).
     */
    private void addScoreToMap(String playerName, String playerType, int score, int scoreType) {
        PlayerKey key = new PlayerKey(playerName, playerType);
        Map<String, Integer> scoreMap = getScoreMap(scoreType);
        if (scoreMap != null) {
            if (!scoreMap.containsKey(key.toString())) {
                resetScore(playerName, playerType, scoreType);
            }
            int newScore = scoreMap.get(key.toString()).intValue() + score;
            scoreMap.put(key.toString(), Integer.valueOf(newScore));
        }
    }

    /**
     * @param player The player which score shall be changed.
     * @param score The new score.
     * @param scoreTypes The score types (see GamePlayer).
     */
    public void setScore(GamePlayer player, int score, int scoreTypes) {
        setScore(player.getName(), player.getType().getId(), score, scoreTypes);
    }

    /**
     * @param playerName Name of the player.
     * @param playerType The player type.
     * @param score The new score.
     * @param scoreTypes The score types (see GamePlayer).
     */
    public void setScore(String playerName, String playerType, int score, int scoreTypes) {
        if ((scoreTypes & SCORE_GAME) == SCORE_GAME) {
            setScoreToMap(playerName, playerType, score, SCORE_GAME);
            saveScores();
        }
        if ((scoreTypes & SCORE_ROUND) == SCORE_ROUND) {
            setScoreToMap(playerName, playerType, score, SCORE_ROUND);
        }
        if ((scoreTypes & SCORE_TURN) == SCORE_TURN) {
            setScoreToMap(playerName, playerType, score, SCORE_TURN);
        }
    }

    /**
     * @param playerName Name of the player.
     * @param playerType The player type.
     * @param score The new score.
     * @param scoreType The score types (see GamePlayer).
     */
    private void setScoreToMap(String playerName, String playerType, int score, int scoreType) {
        PlayerKey key = new PlayerKey(playerName, playerType);
        Map<String, Integer> scoreMap = getScoreMap(scoreType);
        if (scoreMap != null) {
            if (!scoreMap.containsKey(key.toString())) {
                resetScore(playerName, playerType, scoreType);
            }
            scoreMap.put(key.toString(), Integer.valueOf(score));
        }
    }

    /**
     * Resets the current round and game ranking.
     * This method is called, when a new game starts.
     */
    public void resetRanking() {
        gamePlayerList = null;
        gameRankingList = null;
        roundScoreRankingList = new ArrayList<>();
        testFirstCall(GameManager.getInstance().getGameEngine().getActivePlayers());
    }

    /**
     * At the first call (round or game ranking) init the player list.
     *
     * @param players The list with the players.
     */
    private void testFirstCall(GamePlayer[] players) {
		if (gamePlayerList == null && players != null) {
			gamePlayerList = players.clone();
			saveScoreAndGames();
		}
    }

    /**
     * Does the round ranking.
     *
     * @param players A list with players.
     * @param ranking A list wiht the players' rankings.
     */
    public void doRoundRanking(GamePlayer[] players, int[] ranking) {
        testFirstCall(players);
        // add the new round rank for every player, including score
        int numPlayers = gamePlayerList.length;
        ScoreRankElement[] sr = new ScoreRankElement[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            int score = gamePlayerList[i].getScore(GamePlayer.SCORE_ROUND);
            int rank = (ranking != null && ranking.length > i) ? ranking[i] : 0;
            sr[i] = new ScoreRankElement(score, rank);
        }
        roundScoreRankingList.add(sr);
    }

    /**
     * Does the game ranking.
     *
     * @param players A list with players.
     * @param ranking A list wiht the players' rankings.
     */
    public void doGameRanking(GamePlayer[] players, int[] ranking) {
        testFirstCall(players);
        if (ranking != null) {
            // adopt the game ranking, craete a rank for every existing player
            int numPlayers = gamePlayerList.length;
            gameRankingList = new int[numPlayers];
            for (int i = 0; i < numPlayers; i++) {
                gameRankingList[i] = (ranking.length > i) ? ranking[i] : 0;
            }
        }
        checkWonPlayedGames(ranking);
        checkHighScore();
    }

    /**
     * Check for won and played games of the players.
     *
     * @param ranking The ranking's list.
     */
    private void checkWonPlayedGames(int[] ranking) {
        int numPlayers = gamePlayerList.length;
        for (int i = 0; i < numPlayers; i++) {
            PlayerKey key = new PlayerKey(gamePlayerList[i]);
            int won = 0;
            int played = 0;
            if (gameMap.containsKey(key.toString())) {
                WonPlayedGames wp = gameMap.get(key.toString());
                won = wp.getWonGames();
                played = wp.getPlayedGames();
            }
            played++;
            if (ranking != null && ranking[i] == 1 && ScoreUtil.numberOfRanks(ranking, 1) != numPlayers) {
                won++;
            }
            gameMap.put(key.toString(), new WonPlayedGames(won, played));
        }
        saveScoreAndGames();
    }

    /**
     * Saves scores and games for the players.
     * This method is called after a game is finished or when a game was loaded.
     * For internal use only!
     */
    public void saveScoreAndGames() {
        saveScores();
        saveGames();
    }

    /**
     * Saves the scores of the player permanently.
     */
    private void saveScores() {
        if (rememberScores && gamePlayerList != null) {
            for (int i = 0; i < gamePlayerList.length; i++) {
                GamePlayer gp = gamePlayerList[i];
                if (gp != null && !gp.getType().isNetwork()) {
                    String key = ConstantValue.CONFIG_PLAYERSCORE + "." + new PlayerKey(gp).toString();
                    String score = String.valueOf(getScore(gp, GamePlayer.SCORE_GAME));
                    HGBaseConfig.set(key, score);
                }
            }
        }
    }

    /**
     * Saves the information about played and won games of the players.
     */
    private void saveGames() {
        if (rememberGames && gamePlayerList != null) {
            for (int i = 0; i < gamePlayerList.length; i++) {
                GamePlayer gp = gamePlayerList[i];
                if (gp != null && !gp.getType().isNetwork()) {
                    String key = ConstantValue.CONFIG_GAMESPLAYED + "." + new PlayerKey(gp).toString();
                    String games = gp.getGamesPlayed() + ";" + gp.getGamesWon();
                    HGBaseConfig.set(key, games);
                }
            }
        }
    }

    /**
     * Load the statistical information like scores and/or played games.
     *
     */
    private void loadGameStatistics() {
        if (rememberScores || rememberGames) {
            PlayerProfiles profiles = GameManager.getInstance().getPlayerProfiles();
            GamePlayer[] player = profiles.getPlayers();
            for (int i = 0; i < player.length; i++) {
                PlayerKey key = new PlayerKey(player[i]);
                // load scores
                if (rememberScores) {
                    int score = HGBaseConfig.getInt(ConstantValue.CONFIG_PLAYERSCORE + "." + key.toString());
                    if (score != HGBaseTools.INVALID_INT) {
                        addScore(key.getPlayerName(), key.getPlayerType(), score, GamePlayer.SCORE_GAME);
                    }
                }
                // load games
                if (rememberGames) {
                    String value = HGBaseConfig.get(ConstantValue.CONFIG_GAMESPLAYED + "." + key.toString());
                    if (HGBaseTools.hasContent(value)) {
                        String[] s = value.split(";");
                        if (s.length == 2) {
                            int played = HGBaseTools.toInt(s[0]);
                            int won = HGBaseTools.toInt(s[1]);
                            if (played != HGBaseTools.INVALID_INT && won != HGBaseTools.INVALID_INT) {
                                gameMap.put(key.toString(), new WonPlayedGames(won, played));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param player A player.
     * @return The number of won games.
     */
    public int getGamesWon(GamePlayer player) {
        return getGamesWon(player.getName(), player.getType().getId());
    }

    /**
     * @param playerName The player's name.
     * @param playerType The player's type.
     * @return The number of won games.
     */
    public int getGamesWon(String playerName, String playerType) {
        PlayerKey key = new PlayerKey(playerName, playerType);
        WonPlayedGames wp = gameMap.get(key.toString());
        return (wp==null)? 0 : wp.getWonGames();
    }

    /**
     * @param player A player.
     * @return The number of played games.
     */
    public int getGamesPlayed(GamePlayer player) {
        return getGamesPlayed(player.getName(), player.getType().getId());
    }

    /**
     * @param playerName The player's name.
     * @param playerType The player's type.
     * @return The number of played games.
     */
    public int getGamesPlayed(String playerName, String playerType) {
        PlayerKey key = new PlayerKey(playerName, playerType);
        WonPlayedGames wp = gameMap.get(key.toString());
        return (wp==null)? 0 : wp.getPlayedGames();
    }

    /**
     * Sets the won and played games for a specific player.
     * For internal use only!
     *
     * @param player The player to set the games.
     * @param played Number of played games.
     * @param won Number of won games.
     */
    public void setGamesPlayedWon(GamePlayer player, int played, int won) {
        PlayerKey key = new PlayerKey(player);
        gameMap.put(key.toString(), new WonPlayedGames(won, played));
    }

    /**
     * Removes the information about played and won games from the statistics.
     *
     */
    public void removeGamesPlayedWon() {
        PlayerProfiles profiles = GameManager.getInstance().getPlayerProfiles();
        for (GamePlayer player : profiles.getPlayers()) {
            removeGamesPlayedWon(player);
        }
    }

    /**
     * Removes the information about played and won games for a single player.
     *
     * @param player The player whose information shall be removed.
     */
    public void removeGamesPlayedWon(GamePlayer player) {
        PlayerKey key = new PlayerKey(player);
        HGBaseConfig.remove(ConstantValue.CONFIG_GAMESPLAYED+"."+key.toString());
        gameMap.remove(key.toString());
    }

    /**
     * Check for new high scores. Only the one(s) with the highest points get a high score.
     */
    private void checkHighScore() {
        if (highScoreLength > 0) {
            int scores[] = ScoreUtil.getScore(gamePlayerList, GamePlayer.SCORE_GAME);
            int maxScore = ScoreUtil.getMaximumScore(scores);
            for (int i = 0; i < scores.length; i++) {
                if (scores[i] > 0 && (scores[i] == maxScore || !onlyFirstHighScore)) {
                    boolean found = false;
                    int insertScore = highScoreList.size();
                    for (int h = 0; h < highScoreList.size() && !found; h++) {
                        HighScoreElement hi = highScoreList.get(h);
                        boolean scoreIsBetter = (this.isLowerScoreBetter) ? scores[i] < hi.getScore()
                                : scores[i] > hi.getScore();
                        if (scoreIsBetter) {
                            insertScore = h;
                            found = true;
                        }
                    }
                    if (insertScore < 0) {
                        insertScore = 0;
                    }
                    String playerName = PlayerUtil.getPlayerNameType(gamePlayerList[i]);
                    highScoreList.add(insertScore, new HighScoreElement(playerName, scores[i], new Date()));
                }
            }
            while (highScoreList.size() > highScoreLength) {
                highScoreList.remove(highScoreList.size() - 1);
            }
            saveHighScore();
        }
    }

    /**
     * Saves the current high score to the configuration file.
     */
    private void saveHighScore() {
        for (int i = 0; i < highScoreList.size(); i++) {
            HighScoreElement hi = highScoreList.get(i);
            String key = ConstantValue.CONFIG_HIGHSCORE + i;
            String value = hi.getName() + ";" + hi.getScore() + ";" + HGBaseTools.convertDate2String(hi.getDay());
            HGBaseConfig.set(key, value);
        }
    }

    /**
     * Loads the high scores from the configuration file.
     */
    private void loadHighScore() {
        boolean found = true;
        for (int i = 0; i < highScoreLength && found; i++) {
            found = false;
            String value = HGBaseConfig.get(ConstantValue.CONFIG_HIGHSCORE + i);
            if (HGBaseTools.hasContent(value)) {
                String[] s = value.split(";");
                if (s.length == 2 || s.length == 3) {
                    String name = s[0];
                    int score = HGBaseTools.toInt(s[1]);
                    Date day = (s.length == 3) ? HGBaseTools.convertString2Date(s[2]) : null;
                    if (score != HGBaseTools.INVALID_INT) {
                        highScoreList.add(new HighScoreElement(name, score, day));
                        found = true;
                    }
                }

            }
        }
    }

    /**
     * Removes the current high score from the configuration file.
     */
    public void removeHighScore() {
        for (int i = 0; i < highScoreList.size(); i++) {
            String key = ConstantValue.CONFIG_HIGHSCORE + i;
            HGBaseConfig.remove(key);
        }
        highScoreList.clear();
    }

    /**
     * Saves the statistics information for the current game.
     *
     * @return 0 if saving was successful.
     */
    public int saveStatistics(Document doc, Element root) {
        GameStatisticsFileOperator.write(doc, root, this);
        return 0;
    }

    /**
     * Load the statistics information for the current game.
     *
     * @return 0 if loading was successfull.
     */
    public int loadStatistics(Node root) {
        if (!GameStatisticsFileOperator.read(root, this)) {
            return -10706;
        } else {
            return 0;
        }
    }

    /**
     * @return A list with the high score.
     */
    public HighScoreElement[] getHighScore() {
        return highScoreList.toArray(new HighScoreElement[highScoreList.size()]);
    }

    /**
     * @return A list with all players of the current game.
     */
    public GamePlayer[] getGamePlayerList() {
        return HGBaseTools.clone(gamePlayerList);
    }
    /**
     * @return The current game ranking if there exists one.
     */
    public int[] getGameRankingList() {
        return HGBaseTools.clone(gameRankingList);
    }

    /**
     * @return An unmodifiable list where every entry holds an array of ScoreRankElements.
     */
    public List<ScoreRankElement[]> getRoundScoreRankingList() {
        return Collections.unmodifiableList(roundScoreRankingList);
    }

    /**
     * Returns the scores for the given round of the active game or null if the
     * round is not valid.
     *
     * @param round The round of the active game (starts with 1).
     * @return An array with scores or null.
     */
    public int[] getRoundScores(int round) {
        List<ScoreRankElement[]> listScore = GameManager.getInstance().getGameStatistics().getRoundScoreRankingList();
        int indexList = round - 1;
        if (indexList >= 0 && indexList < listScore.size()) {
            ScoreRankElement[] sre = listScore.get(indexList);
            int[] roundScore = new int[sre.length];
            for (int i = 0; i < roundScore.length; i++) {
                roundScore[i] = sre[i].getScore();
            }
            return roundScore;
        }
        return null;
    }
}
