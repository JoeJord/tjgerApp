package com.tjger.game.internal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.tjger.game.GamePlayer;

import at.hagru.hgbase.lib.HGBaseTools;
import at.hagru.hgbase.lib.xml.ChildNodeIterator;
import at.hagru.hgbase.lib.xml.HGBaseXMLTools;

/**
 * Helper class to read/write the game statistics information for the current game from/to file.
 *
 * @author hagru
 */
class GameStatisticsFileOperator {

    private static int loadNumSre;
    private static int loadGameRanks;

    /**
     * Write the file
     */
    public static void write(final Document doc, final Element root, final GameStatistics statistics) {
        // save the players' statistics information
        Element playerStat = doc.createElement("playerstats");
        GamePlayer[] player = statistics.gamePlayerList;
        for (int i = 0; i < player.length; i++) {
            Element pE = doc.createElement("player");
            pE.setAttribute("index", String.valueOf(i));
            pE.setAttribute("st", String.valueOf(player[i].getScore(GamePlayer.SCORE_TURN)));
            pE.setAttribute("sr", String.valueOf(player[i].getScore(GamePlayer.SCORE_ROUND)));
            pE.setAttribute("sg", String.valueOf(player[i].getScore(GamePlayer.SCORE_GAME)));
            pE.setAttribute("gp", String.valueOf(player[i].getGamesPlayed()));
            pE.setAttribute("gw", String.valueOf(player[i].getGamesWon()));
            pE.setAttribute("do", String.valueOf(player[i].isDropOut()));
            playerStat.appendChild(pE);
        }
        root.appendChild(playerStat);
        // save the score/ranking information for the rounds
        Element rounds = doc.createElement("rounds");
        rounds.setAttribute("number", String.valueOf(statistics.roundScoreRankingList.size()));
        for (int i = 0; i < statistics.roundScoreRankingList.size(); i++) {
            Element ri = doc.createElement("round" + i);
            ScoreRankElement[] sre = statistics.roundScoreRankingList.get(i);
            for (int j = 0; j < sre.length; j++) {
                Element sreNode = doc.createElement("sre" + j);
                sreNode.setAttribute("score", String.valueOf(sre[j].getScore()));
                sreNode.setAttribute("rank", String.valueOf(sre[j].getRank()));
                ri.appendChild(sreNode);
            }
            rounds.appendChild(ri);
        }
        root.appendChild(rounds);
        // save the ranking information for the game
        if (statistics.gameRankingList != null) {
            Element rank = doc.createElement("gamerank");
            for (int i = 0; i < statistics.gameRankingList.length; i++) {
                Element grNode = doc.createElement("gr" + i);
                grNode.setAttribute("rank", String.valueOf(statistics.gameRankingList[i]));
                rank.appendChild(grNode);
            }
            root.appendChild(rank);
        }
    }

    /**
     * Read the file.
     */
    public static boolean read(final Node root, final GameStatistics statistics) {
        loadGameRanks = 0;
        statistics.resetRanking();
        ChildNodeIterator.run(new ChildNodeIterator(root, "gamestatistics", statistics) {
            @Override
            public void performNode(Node node, int index, Object obj) {
                // get the players' statistical information
                if (node.getNodeName().equals("playerstats")) {
                    ChildNodeIterator.run(new ChildNodeIterator(node, "playerstats", obj) {

                        @Override
                        public void performNode(Node node, int index, Object obj) {
                            if (node.getNodeName().equals("player")) {
                                int i = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, "index"));
                                if (i >= 0 && statistics.gamePlayerList.length > i) {
                                    GamePlayer player = statistics.gamePlayerList[i];
                                    if (player != null) {
                                        // set the score
                                        player.setScore(getScore(node, "st"), GamePlayer.SCORE_TURN);
                                        player.setScore(getScore(node, "sr"), GamePlayer.SCORE_ROUND);
                                        player.setScore(getScore(node, "sg"), GamePlayer.SCORE_GAME);
                                        // set played and won game
                                        int playedGames = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, "gp"));
                                        int wonGames = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, "gw"));
                                        if (playedGames >= 0 && wonGames >= 0) {
                                            statistics.setGamesPlayedWon(player, playedGames, wonGames);
                                        }
                                        // set drop out
                                        boolean dropOut = HGBaseXMLTools.getAttributeValue(node, "do").equals("true");
                                        player.setDropOut(dropOut);
                                    }
                                }
                            }
                        }

                        private int getScore(Node node, String scoreType) {
                            int score = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, scoreType));
                            if (score == HGBaseTools.INVALID_INT) {
                                score = 0;
                            }
                            return score;
                        }
                    });
                }
                // get the round score/ranking information
                if (node.getNodeName().equals("rounds")) {
                    // ignore the number attribute and go through the rounds
                    ChildNodeIterator.run(new ChildNodeIterator(node, "rounds", obj) {
                        @Override
                        public void performNode(Node node, int index, Object obj) {
                            if (node.getNodeName().startsWith("round")) {
                                int round = HGBaseTools.toInt(node.getNodeName().replaceFirst("round", ""));
                                if (round >= 0) {
                                    loadNumSre = 0;
                                    final ScoreRankElement[] sreList = new ScoreRankElement[statistics.gamePlayerList.length];
                                    ChildNodeIterator.run(new ChildNodeIterator(node, "round" + round, obj) {

                                        @Override
                                        public void performNode(Node node, int index, Object obj) {
                                            if (node.getNodeName().startsWith("sre")) {
                                                int sre = HGBaseTools.toInt(node.getNodeName().replaceFirst("sre", ""));
                                                if (sre >= 0) {
                                                    int score = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, "score"));
                                                    int rank = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, "rank"));
                                                    if (score != HGBaseTools.INVALID_INT && rank >= 0 && loadNumSre < sreList.length) {
                                                        sreList[loadNumSre] = new ScoreRankElement(score, rank);
                                                        loadNumSre++;
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    statistics.roundScoreRankingList.add(sreList);
                                }
                            }
                        }
                    });
                }
                // get the game ranking information
                if (node.getNodeName().equals("gamerank")) {
                    statistics.gameRankingList = new int[statistics.gamePlayerList.length];
                    ChildNodeIterator.run(new ChildNodeIterator(node, "gamerank", obj) {

                        @Override
                        public void performNode(Node node, int index, Object obj) {
                            if (node.getNodeName().startsWith("gr")) {
                                int gr = HGBaseTools.toInt(node.getNodeName().replaceFirst("gr", ""));
                                if (gr >= 0 && loadGameRanks < statistics.gameRankingList.length) {
                                    int rank = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, "rank"));
                                    if (rank >= 0) {
                                        statistics.gameRankingList[loadGameRanks] = rank;
                                        loadGameRanks++;
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
        return true;
    }
}