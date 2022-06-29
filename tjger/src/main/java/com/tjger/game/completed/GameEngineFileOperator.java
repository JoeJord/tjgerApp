package com.tjger.game.completed;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.tjger.game.GamePlayer;
import com.tjger.lib.XmlUtil;

import at.hagru.hgbase.lib.HGBaseTools;
import at.hagru.hgbase.lib.xml.ChildNodeIterator;
import at.hagru.hgbase.lib.xml.HGBaseXMLTools;

/**
 * Helper class to read/write the game engine information from/to file.
 *
 * @author hagru
 */
class GameEngineFileOperator {

    private static boolean loadPlayers;
    private static int loadNumPlayers;
    private static boolean loadMtrg;

    /**
     * Write the file
     */
    public static void write(final Document doc, final Element root, final GameEngine engine) {
        // save the players information
        Element players = doc.createElement("players");
        GamePlayer[] player = engine.getActivePlayers();
        players.setAttribute("number", String.valueOf(player.length));
        players.setAttribute("start", String.valueOf(engine.getPlayerStartRound()));
        players.setAttribute("current", String.valueOf(engine.getCurrentPlayerIndex()));
        players.setAttribute("cyclic", String.valueOf(engine.getCyclingFirstGamePlayerIndex()));
        for (int i=0; i<player.length; i++) {
            Element pi = XmlUtil.savePlayer(doc, "p"+i, player[i]);
            players.appendChild(pi);
        }
        root.appendChild(players);
        // save move/turn/round and game information
        Element mtrg = doc.createElement("mtrg");
        mtrg.setAttribute("move", String.valueOf(engine.getCurrentMove()));
        mtrg.setAttribute("turn", String.valueOf(engine.getCurrentTurn()));
        mtrg.setAttribute("round", String.valueOf(engine.getCurrentRound()));
        mtrg.setAttribute("activeround", String.valueOf(engine.isActiveRound()));
        mtrg.setAttribute("activegame", String.valueOf(engine.isActiveGame()));
        root.appendChild(mtrg);
    }

    /**
     * Read the file.
     */
    public static boolean read(final Node root, final GameEngine engine) {
        loadPlayers = false;
        loadNumPlayers = 0;
        loadMtrg = false;
        ChildNodeIterator.run(new ChildNodeIterator(root, "gameengine", engine) {
            @Override
            public void performNode(Node node, int index, Object obj) {
                // load the players
                if (node.getNodeName().equals("players")) {
                    int number = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, "number"));
                    int start = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, "start"));
                    int current = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, "current"));
                    int cyclic = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, "cyclic"));
                    if (number>0 && start>=-1 && current>=-1) {
                        loadPlayers = true;
                        engine.numberPlayers = number;
                        engine.playerStartRound = start;
                        engine.currentPlayer = current;
                        engine.cyclingPlayerStartGame = Math.max(cyclic, 0);
                        engine.activePlayers = new GamePlayer[engine.numberPlayers];
                        ChildNodeIterator.run(new ChildNodeIterator(node, "players", engine) {
                            @Override
                            public void performNode(Node node, int index, Object obj) {
                                if (node.getNodeName().startsWith("p")) {
                                    int i = HGBaseTools.toInt(node.getNodeName().replaceFirst("p", ""));
                                    if (i>=0) {
                                        // get the player at this position for image and piece color
                                        GamePlayer player = XmlUtil.loadPlayer(node);
                                        if (player!=null) {
                                            // set as active player
                                            engine.activePlayers[loadNumPlayers] = player;
                                            loadNumPlayers++;
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
                // load the move/turn/round/game information
                if (node.getNodeName().equals("mtrg")) {
                    int move = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, "move"));
                    int turn = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, "turn"));
                    int round = HGBaseTools.toInt(HGBaseXMLTools.getAttributeValue(node, "round"));
                    if (move>=0 && turn>=0 && round>=0) {
                        loadMtrg = true;
                        engine.currentMove.set(move);
                        engine.currentTurn = turn;
                        engine.currentRound = round;
                        engine.activeRound = HGBaseXMLTools.getAttributeValue(node, "activeround").equals("true");
                        engine.activeGame = HGBaseXMLTools.getAttributeValue(node, "activegame").equals("true");
                        engine.stoppedGame = false;
                    }
                }
            }
        });
        return (loadMtrg && loadPlayers && loadNumPlayers>0 && loadNumPlayers==engine.getNumberPlayers());
    }
}