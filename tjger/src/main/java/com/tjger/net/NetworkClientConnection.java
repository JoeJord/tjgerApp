package com.tjger.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tjger.game.NetworkPlayer;
import com.tjger.game.completed.GameConfig;
import com.tjger.lib.ConstantValue;

import at.hagru.hgbase.gui.HGBaseDialog;
import at.hagru.hgbase.lib.HGBaseFileTools;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * A client connection.
 *
 * @author hagru
 */
public class NetworkClientConnection extends NetworkConnection {

    private static NetworkClientConnection connection = new NetworkClientConnection();

    final private NetworkClient client;

    private NetworkClientConnection() {
        super();
        client = new NetworkClient();
    }

    /**
     * @return The one and only client instance.
     */
    public static NetworkClientConnection getInstance() {
        return connection;
    }

    /**
     * Starts a new thread for connecting the client to the server.
     *
     * @param serverIp The server ip to connect to.
     * @param name The name of this client.
     */
    public void startClient(final String serverIp, final String name) {
        super.start();
        client.setServerIP(serverIp);
        client.setClientName(name);
        client.setState(CONNECTING);
        // start a thread to build up a connection
        Thread t=new Thread() {
            @Override
            public void run() {
                try {
                    Socket sock = new Socket(serverIp, GameConfig.getInstance().getNetworkPort());
                    client.setSocket(sock);
                    client.setOutStream(new PrintStream(sock.getOutputStream(), false, Charset.defaultCharset().displayName()));
                    client.setInStream(new BufferedReader(new InputStreamReader(sock.getInputStream(),
                            Charset.defaultCharset())));
                    // ask the server to connect
                    client.setClientIP(sock.getLocalAddress().getHostAddress());
                    // ask the server to connect and wait for the server to
                    List<Integer> alOut = new ArrayList<>();
                    boolean succ = (putMessage(NetworkMessage.msgShakehandC(client.getClientName(), getIp()))==0);
                    while (succ && !messageServerAccepted(alOut)) {
                        succ = (!messageServerAbort());
                        netWait();
                    }
                    if (succ) {
                        if (!alOut.isEmpty()) {
                            client.setClientId(alOut.get(0).intValue());
                            client.setState(CONNECTED);
                            putMessage(NetworkMessage.msgAcknowledgeC());
                        } else {
                            client.setState(DISCONNECTED);
                        }
                    } else {
                        client.setState(DISCONNECTED);
                    }
                } catch(IOException e) {
                    int oldState = client.getState();
                    client.setState(DISCONNECTED);
                    if (oldState==CONNECTING) {
                        HGBaseDialog.printError(-30216, getMainFrame());
                    }
                    close();
                }
            }
        };
        t.start();
    }

    /**
     * Closes the current connection
     */
    @Override
    public void close() {
        super.close();
        int state=getState();
        if (state==CONNECTED || state==CONNECTING) {
            putMessage(NetworkMessage.msgAbort());
        }
        closeSockets();
        client.resetClient();
    }

    /**
     * Closes all sockets and sets them to null
     */
    private void closeSockets() {
        HGBaseFileTools.closeStream(client.getSocket());
        HGBaseFileTools.closeStream(client.getOutStream());
        HGBaseFileTools.closeStream(client.getInStream());
    }

    /* (non-Javadoc)
     * @see tjger.net.NetworkConnection#getId()
     */
    @Override
    public int getId() {
        return client.getClientId();
    }

    /* (non-Javadoc)
     * @see tjger.net.NetworkConnection#getIp()
     */
    @Override
    public String getIp() {
        String ip = client.getClientIP();
        if (HGBaseTools.hasContent(ip)) {
            return ip;
        } else {
            return super.getIp();
        }
    }
    /**
     * Returns the client name
     *
     * @return client name
     */
    public String getName() {
        return client.getClientName();
    }

    /**
     * Returns the state of the connection. If the connection has established,
     * it isn't tested any more if it is still available.
     *
     * @return state can be DISCONNECTED, CONNECTED or CONNECTING
     */
    public int getState() {
        return client.getState();
    }

    /**
     * Returns the index of this client from the sight of the server
     *
     * @return index of client or -1
     */
    public int getClientNr() {
        return client.getClientNr();
    }

    /**
     * Sets the index of this client from the sight of the server
     *
     * @param nr index of client
     */
    public void setClientNr(int nr) {
        client.setClientNr(nr);
    }

    /**
     * @return True if the client is connected to a server.
     */
    public boolean isConnected() {
        return (getState()==CONNECTED);
    }

    /**
     * Sends a message to the server
     *
     * @param msg message to send
     * @return 0 if there was no error, otherwise the error code
     */
    public int putMessage(String msg) {
        if (client.getOutStream()!=null) {
            return putMessage(client.getOutStream(),msg);
        } else {
            return -30221;
        }
    }

    /* (non-Javadoc)
     * @see tjger.net.NetworkConnection#getMessage()
     */
    @Override
    protected String getMessage() {
        StringBuffer msg = new StringBuffer("");
        if (getMessage(client.getInStream(), msg)!=0) {
            //  if there was an error, close this client
            close();
            return null;
        }
        return msg.toString();
    }

    /* (non-Javadoc)
     * @see tjger.net.NetworkConnection#handleMessage(java.lang.String)
     */
    @Override
    protected boolean handleMessage(String msg) {
        String type=getMessagePart(0,msg);
        String param=getMessagePart(1,msg);
        if (type!=null) {
            // handle the next game message
            if (type.equals(NetworkMessage.MSG_ERROR) && param!=null && param.equals(NetworkMessage.ERROR_NEXTGAME)) {
                // wait for other threads to stop, do this in an own thread otherwise the handleMessage-Thread is blocked
                Thread t=new Thread() {
                    @Override
                    public void run() {
                        netWait();
                        netWait();
                        // get the next game message if they were not taken away
                        getNextMessage(NetworkMessage.MSG_ENGINE, NetworkMessage.PARAM_NEXTGAME);
                        getNextMessage(NetworkMessage.MSG_ERROR, NetworkMessage.ERROR_NEXTGAME);
                        //FIXME GameEngine.getInstance().nextNetworkGame(); network currently not supported
                    }
                };
                t.start();
                return true;
            } else if (type.equals(NetworkMessage.MSG_ERROR)) {
                // abort at alle other errors at once
                close();
                return true;
            }
        }
        return false;
    }

    /**
     * Returns if an abort was sent, after the method is called
     * the value will be reseted
     *
     * @return true if there was an abort message
     */
    public boolean messageServerAbort() {
        String msg=getNextMessage(NetworkMessage.MSG_ERROR, null);
        if (msg!=null) {
            String err = getMessagePart(1, msg);
            if (!err.equals(NetworkMessage.ERROR_ABORT) && !err.equals(NetworkMessage.ERROR_NEXTGAME)) {
                // display a message only if it's not a simple abort or it's not the next game error
                String param = getMessagePart(2, msg); // can be null
                HGBaseDialog.printError(err, new String[] {param}, getMainFrame());
            }
            return true;
        }
        return false;
    }

    /**
     * Returns true if a message that says the server accepted was received
     *
     * @param al array with datas, that shall be filled
     * @return true if this message was received, else false
     */
    public boolean messageServerAccepted(List<Integer> al) {
        al.clear();
        String msg = getNextMessage(NetworkMessage.MSG_CTRL, NetworkMessage.PARAM_SERVERACC);
        if (msg!=null) {
            al.add(Integer.valueOf(HGBaseTools.toInt(getMessagePart(2,msg))));
            return true;
        }
        return false;
    }

    /**
     * Returns true if a message with player names has received.<p>
     * This method also receives player color to set the same color for all players in network games.
     *
     * @param listPlayer A list that will be filled with player names.
     * @param listColor A list that will be filled with player colors.
     * @return True if message received.
     */
    public boolean messageServerPlayerNames(List<String> listPlayer, List<String> listColor) {
        listPlayer.clear();
        String msg = getNextMessage(NetworkMessage.MSG_ENGINE, NetworkMessage.PARAM_PLAYERNAME);
        if (msg!=null) {
            String[] data = getMessagePart(2,msg).split(ConstantValue.NETWORK_DIVIDEPART);
            for (int i = 0; i < data.length; i++) {
                String[] nameColor = data[i].split(ConstantValue.NETWORK_DIVIDEPAIR);
                listPlayer.add(nameColor[0]);
                if (nameColor.length > 1) {
                    listColor.add(nameColor[1]);
                } else {
                    listColor.add("");
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Returns true if a message with the new game information has received.
     *
     * @param clientNr An array with the length one for getting the client's number.
     * @param cyclicStartPlayer An array with the length one for getting the current cyclic start player of the server
     * @param mapInfo A map with the new game information.
     * @return True if message received.
     */
    public boolean messageServerNewGameInfo(int[] clientNr, int[] cyclicStartPlayer, Map<String,String> mapInfo) {
        if (clientNr.length>0) {
            mapInfo.clear();
            String msg = getNextMessage(NetworkMessage.MSG_ENGINE, NetworkMessage.PARAM_NEWGAME);
            if (msg!=null) {
                clientNr[0] = HGBaseTools.toInt(getMessagePart(2, msg));
                if (clientNr[0] < 0 || cyclicStartPlayer[0] < 0) {
                    return false;
                }
                cyclicStartPlayer[0] = HGBaseTools.toInt(getMessagePart(3, msg));
                if (clientNr[0] < 0 || cyclicStartPlayer[0] < 0) {
                    return false;
                }
                String[] info = getMessagePart(4, msg).split(ConstantValue.NETWORK_DIVIDEPART);
                for (int i=0; i<info.length; i++) {
                    String[] value = info[i].split(ConstantValue.NETWORK_DIVIDEPAIR);
                    if (value.length==2) {
                        mapInfo.put(value[0], value[1]);
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the server has put a next game message
     *
     * @param al array with datas, that shall be filled
     * @return true if this message was received, else false
     */
    public boolean messageServerNextGame() {
        String msg = getNextMessage(NetworkMessage.MSG_ENGINE, NetworkMessage.PARAM_NEXTGAME);
        return (msg!=null);
    }

    /**
     * Returns the message part representing the game state or null if no game state received.
     *
     * @return String representing game state or null.
     */
    public String messageServerGameState() {
        return messageGameInformation(NetworkMessage.MSG_STATE);
    }

    /**
     * This method waits until a game information (game state, move) was received or the server terminated the game.
     * If a message was received, a ok is sent back to the server.
     *
     * @param msgType The message type (game state, move).
     * @param msgReceived Buffer to get the game information.
     * @param player The client player that is waiting, can be null if not a current player is waiting.
     * @return True if successful or false if game was terminated.
     */
    public boolean waitForServerGameInformation(String msgType, StringBuffer msgReceived, NetworkPlayer player) {
        String msg = null;
        // try to get new game information and player names
        while ((player==null || player.isPlaying()) && !messageServerAbort() && msg==null) {
            msg = messageGameInformation(msgType);
            NetworkConnection.netWait();
        }
        if (msg != null) {
            msgReceived.append(msg);
            return (putMessage(NetworkMessage.msgClientGameOkC(msgType)) == 0);
        } else {
            return false;
        }
    }

}
