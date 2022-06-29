package com.tjger.lib;

import java.util.HashSet;
import java.util.Set;

import com.tjger.game.GamePlayer;
import com.tjger.game.completed.GameConfig;

import at.hagru.hgbase.lib.HGBaseTools;

/**
 * Some methods that helps for scoring.
 *
 * @author hagru
 */
public class ScoreUtil {

	private ScoreUtil() {
		super();
	}

    /**
     * Get an array of score for the given score type.
     *
     * @param players An array with players.
     * @param scoreType The score type to look for.
     * @return An array with scores of the given type.
     */
    public static int[] getScore(GamePlayer[] players, int scoreType) {
        return getScore(players, scoreType, ConstantValue.INCLUDE_DROPOUT);
    }

    /**
     * Get an array of score for the given score type.
     *
     * @param players An array with players.
     * @param scoreType The score type to look for.
     * @return An array with scores of the given type, has at least 0 size.
     */
    public static int[] getScore(GamePlayer[] players, int scoreType, boolean withoutDropOut) {
        GamePlayer[] playersToTest = players;
        if (playersToTest != null && withoutDropOut) {
            playersToTest = PlayerUtil.getAdvancedPlayers(playersToTest);
        }
        int score[] = new int[(playersToTest == null) ? 0 : playersToTest.length];
        for (int i = 0; i < score.length; i++) {
            score[i] = playersToTest[i].getScore(scoreType);
        }
        return score;
    }

    /**
     * Returns the maximum score of an array with scores.
     *
     * @param scores An array with scores.
     * @return The maximum score.
     */
    public static int getMaximumScore(int[] scores) {
        return HGBaseTools.getMaximum(scores);
    }

    /**
     * Returns the minimum score of an array with scores.
     *
     * @param scores An array with scores.
     * @return The minimum score.
     */
    public static int getMinimumScore(int[] scores) {
        return HGBaseTools.getMinimum(scores);
    }

    /**
     * Does the ranking depending on the players' scores.
     *
     * @param players The players to rank.
     * @param scoreType The score type to use.
     * @return A list with the ranking.
     */
    public static int[] getScoreRanking(GamePlayer[] players, int scoreType) {
        return getScoreRanking(players, scoreType, GameConfig.getInstance().isLowerScoreBetter());
    }

    /**
     * Does the ranking depending on the players' scores.
     *
     * @param players The players to rank.
     * @param scoreType The score type to use.
     * @param lessIsBetter Set true to indicate that less points are a better rank.
     * @return A list with the ranking.
     */
    public static int[] getScoreRanking(GamePlayer[] players, int scoreType, boolean lessIsBetter) {
        return getScoreRanking(getScore(players, scoreType), lessIsBetter);
    }

    /**
     * Does the ranking depending on the given scores.
     *
     * @param scores A list with the players' scores.
     * @return A list with the ranking.
     */
    public static int[] getScoreRanking(int[] scores) {
        return getScoreRanking(scores, GameConfig.getInstance().isLowerScoreBetter());
    }

    /**
     * Does the ranking depending on the given scores.
     *
     * @param scores A list with the players' scores.
     * @param lessIsBetter Set true to indicate that less points are a better rank.
     * @return A list with the ranking.
     */
    public static int[] getScoreRanking(int[] scores, boolean lessIsBetter) {
        Set<Integer> listScores = new HashSet<>();
        for (int i=0; i<scores.length; i++) {
            listScores.add(Integer.valueOf(scores[i]));
        }
        Integer[] diffScores = listScores.toArray(new Integer[listScores.size()]);
        HGBaseTools.orderList(diffScores, lessIsBetter);
        int[] rank = new int[scores.length];
        for (int i=0; i<rank.length; i++) {
            boolean found = false;
            for (int j=0; j<diffScores.length && !found; j++) {
                if (diffScores[j].intValue()==scores[i]) {
                    found = true;
                    rank[i] = diffScores.length-j;
                }
            }
        }
        return rank;
    }

    /**
     * Returns a list with the player so that the are in the rankings order.
     *
     * @param player The original player list.
     * @param ranks The list with the ranking.
     * @return The player in ranking order.
     */
    public static GamePlayer[] getPlayersInRankingOrder(GamePlayer[] player, int[] ranks) {
        GamePlayer[] rankedPlayer = player.clone();
        if (ranks!=null) {
	        int[] ranks2 = ranks.clone();
	        for (int i=0; i<rankedPlayer.length-1; i++) {
	            int minIndex = i;
	            int minRank = ranks2[i];
	            for (int j=i; j<rankedPlayer.length; j++) {
	                if (ranks2[j]>0 && (ranks2[j]<minRank || minRank<=0)) {
	                    minIndex = j;
	                    minRank = ranks2[j];
	                }
	            }
	            if (minIndex!=i) {
	                GamePlayer h = rankedPlayer[i];
	                rankedPlayer[i] = rankedPlayer[minIndex];
	                rankedPlayer[minIndex] = h;
	                int h2 = ranks2[i];
	                ranks2[i] = ranks2[minIndex];
	                ranks2[minIndex] = h2;
	            }
	        }
        }
        return rankedPlayer;
    }

    /**
     * Returns how often a given rank exists in the ranking.
     * @param ranking A list with ranks.
     * @param rank The rank to look for.
     * @return The number how often the given rank exists.
     */
    public static int numberOfRanks(int[] ranking, int rank) {
        int num = 0;
        for (int i=0; i<ranking.length; i++) {
            if (ranking[i]==rank) {
            	num++;
            }
        }
        return num;
    }

}
