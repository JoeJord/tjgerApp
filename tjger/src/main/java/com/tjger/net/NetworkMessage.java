package com.tjger.net;

import java.util.ArrayList;
import java.util.List;

import com.tjger.game.GamePlayer;
import com.tjger.game.GameState;
import com.tjger.game.MoveInformation;
import com.tjger.game.completed.GameManager;
import com.tjger.lib.ConstantValue;

import at.hagru.hgbase.android.HGBaseAppTools;
import at.hagru.hgbase.lib.HGBaseStringBuilder;

/**
 * Helper class to create the messages used for tjger.
 *
 * @author hagru
 */
public class NetworkMessage {

    public static final String MSG_CTRL   = "TJxCTRL";
    public static final String MSG_ERROR  = "TJxERR";
    public static final String MSG_ENGINE = "TJxGE";
    public static final String MSG_STATE  = "TJxGS";
    public static final String MSG_MOVE   = "TJxMI";

    public static final String PARAM_CLIENTCONN = "CLCON";
    public static final String PARAM_SERVERACC   = "SRACC";
    public static final String PARAM_CLIENTACC   = "CLACK";
    public static final String PARAM_NEWGAME     = "NEWGM";
    public static final String PARAM_NEXTGAME    = "NEXTGM";
    public static final String PARAM_GAMEOK      = "GMOK";
    public static final String PARAM_PLAYERNAME  = "PLNAM";
    public static final String PARAM_TEST        = "TEST";

    public static final String ERROR_ABORT  = "err.netabort";
    public static final String ERROR_NAME   = "err.netname";
    public static final String ERROR_NEXTGAME   = "err.nextgame";
    public static final String ERROR_APPLICATION   = "err.application";

    private NetworkMessage() {
        super();
    }

    /**
     * Creates a message string by the list.
     *
     * @param msg An array with message parts.
     * @return Message text.
     */
    private static String createString(List<String> msg) {
        StringBuffer message = new StringBuffer(128);
        if (msg!=null) {
            final int msgSize = msg.size();
            for (int i=0; i<msgSize; i++) {
                if (i>0) {
                    message.append(ConstantValue.NETWORK_SEPARATE);
                }
                message.append(msg.get(i));
            }
        }
        return message.toString();
    }

    /**
     * Create an error message.
     *
     * @param errMsg The error code.
     * @return Message text.
     */
    public static String msgError(String errMsg) {
        List<String> al=new ArrayList<>();
        al.add(MSG_ERROR);
        al.add(errMsg);
        return createString(al);
    }

    /**
     * Error that a name already exists.
     *
     * @return Message text.
     */
    public static String msgNameExistsS() {
        return msgError(ERROR_NAME);
    }

    /**
     * Abort a connection.
     *
     * @return Message text.
     */
    public static String msgAbort() {
        return msgError(ERROR_ABORT);
    }

    /**
     * Tells the client that there runs another application at the server.
     *
     * @return Message text.
     */
    public static String msgWrongApplicationS() {
        List<String> al=new ArrayList<>();
        al.add(MSG_ERROR);
        al.add(ERROR_APPLICATION);
        al.add(HGBaseAppTools.getAppName());
        return createString(al);
    }

    /**
     * Message to test the connection
     *
     * @return message text
     */
    public static String msgTestS() {
        List<String> al=new ArrayList<>();
        al.add(MSG_CTRL);
        al.add(PARAM_TEST);
        return createString(al);
    }

    /**
     * Server replies to shake-hands
     *
     * @param id Id of client
     * @return message text
     */
    public static String msgShakehandS(int id) {
        List<String> al=new ArrayList<>();
        al.add(MSG_CTRL);
        al.add(PARAM_SERVERACC);
        al.add(String.valueOf(id));
        return createString(al);
    }

    /**
     * Client asks for shake-hands
     *
     * @param name name of client
     * @param ip address of client
     * @return message text
     */
    public static String msgShakehandC(String name,String ip) {
        List<String> al=new ArrayList<>();
        al.add(MSG_CTRL);
        al.add(PARAM_CLIENTCONN);
        al.add(HGBaseAppTools.getAppName());
        al.add(name);
        al.add(ip);
        return createString(al);
    }

    /**
     * Client acknowledges the server's shake-hands
     *
     * @return message text
     */
    public static String msgAcknowledgeC() {
        List<String> al=new ArrayList<>();
        al.add(MSG_CTRL);
        al.add(PARAM_CLIENTACC);
        return createString(al);
    }

    /**
     * Server sends an individual message with the new game information to each client.
     *
     * @param clientNr The number of the player during the game.
     * @param manager The game manager to get the new game information.
     * @return The message text.
     */
    public static String msgNewGameInfoS(int clientNr, GameManager manager) {
        String keys[] = manager.getNewGameInformationKeys();
        StringBuffer ngi = new StringBuffer(128);
        for (int i=0; i<keys.length; i++) {
            if (i>0) {
                ngi.append(ConstantValue.NETWORK_DIVIDEPART);
            }
            ngi.append(keys[i]);
            ngi.append(ConstantValue.NETWORK_DIVIDEPAIR);
            ngi.append(manager.getNewGameInformationInt(keys[i]));
        }
        List<String> al=new ArrayList<>();
        al.add(MSG_ENGINE);
        al.add(PARAM_NEWGAME);
        al.add(String.valueOf(clientNr));
        al.add(String.valueOf(manager.getGameEngine().getCyclingFirstGamePlayerIndex()));
        al.add(ngi.toString());
        return createString(al);
    }

    /**
     * Send a message with all player names.<p>
     * Also add player color to have the same colors for players in network games.
     * The image for a player will not be saved and can differ on server and client game.
     *
     * @param players A list with the players.
     * @return The message text.
     */
    public static String msgPlayerNamesS(GamePlayer[] players) {
        HGBaseStringBuilder names = new HGBaseStringBuilder(ConstantValue.NETWORK_DIVIDEPART);
        for (GamePlayer player : players) {
            names.append(player.getName() + ConstantValue.NETWORK_DIVIDEPAIR + player.getPieceColor());
        }
        List<String> al = new ArrayList<>();
        al.add(MSG_ENGINE);
        al.add(PARAM_PLAYERNAME);
        al.add(names.toString());
        return createString(al);
    }

    /**
     * Client acknowledges the new game of the server.
     *
     * @param The message type (engine, game state, move).
     * @return The message text.
     */
    public static String msgClientGameOkC(String msgType) {
        List<String> al=new ArrayList<>();
        al.add(msgType);
        al.add(PARAM_GAMEOK);
        return createString(al);
    }

    /**
     * Create the message to transfer the game state.
     *
     * @param state The game state to transfer.
     * @return The message Text.
     */
    public static String msgGameStateS(GameState state) {
        List<String> al=new ArrayList<>();
        al.add(MSG_STATE);
        al.add(state.toNetworkString());
        return createString(al);
    }

    /**
     * Creates the message to transfer the move information.
     *
     * @param state The game state for converting.
     * @param move The move information to transfer.
     * @return The message text.
     */
    public static String msgMoveInformationS(GameState state, MoveInformation move) {
        List<String> al=new ArrayList<>();
        al.add(MSG_MOVE);
        al.add(state.toNetworkStringMove(move));
        return createString(al);
    }


    /**
     * Message to indicate a next game.
     *
     * @return The message text.
     */
    public static String msgNextGameS() {
        List<String> al=new ArrayList<>();
        al.add(MSG_ENGINE);
        al.add(PARAM_NEXTGAME);
        return createString(al);
    }

}
