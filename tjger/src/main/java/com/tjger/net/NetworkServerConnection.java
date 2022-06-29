package com.tjger.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.tjger.game.NetworkPlayer;
import com.tjger.game.completed.GameConfig;
import com.tjger.lib.ConstantValue;

import at.hagru.hgbase.android.HGBaseAppTools;
import at.hagru.hgbase.gui.HGBaseDialog;
import at.hagru.hgbase.lib.HGBaseFileTools;
import at.hagru.hgbase.lib.HGBaseTools;

/**
 * A server connection.
 *
 * @author hagru
 */
public class NetworkServerConnection extends NetworkConnection {

    private static NetworkServerConnection connection = new NetworkServerConnection();

    private List<NetworkClient> listClients; // list with NetworkClients.
    private ServerSocket servSock;
    private int lastClient;
    private int numInitialClients;

    private NetworkServerConnection() {
        super();
    }

    /**
     * @return The one and only server instance.
     */
    public static NetworkServerConnection getInstance() {
        return connection;
    }

    /**
     * Starts a server.
     *
     * @return 0 if it was successful.
     */
    public int startServer() {
        listClients = new ArrayList<>();
        lastClient = 0;
        numInitialClients = 0;
        // try to start the server
        try {
            servSock = new ServerSocket(GameConfig.getInstance().getNetworkPort());
        } catch (IOException e) {
            servSock = null;
            HGBaseDialog.printError(-30218, getMainFrame());
            return -30218;
        }
        super.start();
        return 0;
    }

    /* (non-Javadoc)
     * @see tjger.net.NetworkConnection#getId()
     */
    @Override
    public int getId() {
        return -1;
    }

    /**
     * @return True if the server is started.
     */
    public boolean isStarted() {
        return (servSock!=null);
    }



    /**
     * @return The number of clients that should be, must be set by the user.
     */
    public int getNumInitialClients() {
        return numInitialClients;
    }
    /**
     * @param numInitialClients The number of clients that should be.
     */
    public void setNumInitialClients(int numInitialClients) {
        this.numInitialClients = numInitialClients;
    }

    /**
     * Closes all of the current connections
     */
    @Override
    public void close() {
        super.close();
        if (servSock != null) {
            for (int i = getNumClients() - 1; i >= 0; i--) {
                disconnect(i);
            }
            HGBaseFileTools.closeStream(servSock);
            servSock = null;
        }
    }

    /**
     * Closes the connection to the given client and removes the client from the list.
     *
     * @param index Index of the client.
     * @param True if disconnect was successful.
     */
    synchronized private boolean disconnect(int index) {
        NetworkClient client = getClient(index);
        if (client!=null) {
            int id = client.getClientId();
            if (id>=0) {
                removeMessagesWithClientId(id);
            }
            Socket sock = client.getSocket();
            PrintStream outStream = client.getOutStream();
            BufferedReader inStream = client.getInStream();
            putMessage(outStream, NetworkMessage.msgAbort());
            HGBaseFileTools.closeStream(sock);
            HGBaseFileTools.closeStream(outStream);
            HGBaseFileTools.closeStream(inStream);
            return removeClient(client);
        }
        return false;
    }

    /**
     * Puts a message to all clients.
     *
     * @param msg The message text.
     * @return 0 if there was no error, otherwise the error code.
     */
    public int putMessageAll(String msg) {
        return putMessageAll(msg, -1);
    }

    /**
     * Puts a message to all clients.
     *
     * @param msg The message text.
     * @param except Index of client that gets no message or -1.
     * @return 0 if there was no error, otherwise the error code.
     */
    public int putMessageAll(String msg, int except) {
        int succ=0;
        for (int i=0;i<getNumClients();i++) {
            if (i!=except) {
                int res=putMessage(i,msg);
                if (res!=0) {
                    succ=res;
                }
            }
        }
        return succ;
    }

    /**
     * Puts a message to all clients and wait for ok.
     *
     * @param msg The message to send.
     * @param msgTypeToWait The message type to wait for (engine, game state, move information).
     * @return True if everything was ok.
     */
    public boolean putMessageAllAndWait(String msg, String msgTypeToWait) {
        return putMessageAllAndWait(msg, msgTypeToWait, -1);
    }

    /**
     * Puts a message to all clients except one and wait for their ok.
     *
     * @param msg The message to send.
     * @param msgTypeToWait The message type to wait for (engine, game state, move information).
     * @param except Index of client that gets no message or -1.
     * @return True if everything was ok.
     */
    public boolean putMessageAllAndWait(String msg, String msgTypeToWait, int except) {
        if (putMessageAll(msg, except) == 0) {
            return waitForClientsGameOk(msgTypeToWait, except);
        } else {
            return false;
        }
    }

    /**
     * Puts a message to the given clients
     *
     * @param index index of client
     * @param msg message text
     * @return 0 if there was no error, otherwise the error code
     */
    public int putMessage(int index, String msg) {
        int state=getClientState(index);
        if (state==CONNECTED || state==CONNECTING) {
            PrintStream outStream = getClientOutput(index);
            if (outStream!=null) {
                return putMessage(outStream,msg);
            }
        }
        return -30221;
    }

    /* (non-Javadoc)
     * @see tjger.net.NetworkConnection#getMessage()
     */
    @Override
    protected String getMessage() {
        if (getNumClients() > 0) {
            lastClient = (lastClient + 1) % getNumClients();
            NetworkClient client = getClient(lastClient);
            if (client != null && (client.getState() == CONNECTED || client.getState() == CONNECTING)) {
                int clientId = client.getClientId();
                BufferedReader inStream = client.getInStream();
                if (inStream != null) {
                    StringBuffer msg = new StringBuffer(32);
                    if (getMessage(inStream, msg) != 0) {
                        // if there was an error, disconnect this client
                        disconnect(getIndexOfId(clientId));
                    } else if (msg.length() > 0) {
                        // add the clients id
                        msg = msg.append(ConstantValue.NETWORK_SEPARATE + clientId);
                        return msg.toString();
                    }
                }
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see tjger.net.NetworkConnection#handleMessage(String)
     */
    @Override
    protected boolean handleMessage(String msg) {
        String type = getMessagePart(0, msg);
        String param = getMessagePart(1, msg);
        if (type != null && type.equals(NetworkMessage.MSG_ERROR)) {
            // abort message from a client, disconnect this client
            if (param != null && param.equals(NetworkMessage.ERROR_ABORT)) {
                int id = HGBaseTools.toInt(getMessagePart(2, msg));
                if (id >= 0) {
                    disconnect(getIndexOfId(id));
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the number of network clients
     *
     * @return number of network clients
     */
    public int getNumClients() {
        if (listClients==null) {
            return 0;
        }
        return listClients.size();
    }

    /**
     * @param index The index of the client (starting with 0).
     * @return The client or null.
     */
    public NetworkClient getClient(int index) {
        if (index>=0 && index<getNumClients()) {
            return listClients.get(index);
        } else {
            return null;
        }
    }

    /**
     * @param index Index of the client.
     * @return The client's state or INVALID.
     */
    public int getClientState(int index) {
        NetworkClient client = getClient(index);
        if (client!=null) {
            return client.getState();
        } else {
            return HGBaseTools.INVALID_INT;
        }
    }

    /**
     * Returns the output stream of the given network client
     *
     * @param index index of the client
     * @return output stream of the client
     */
    public PrintStream getClientOutput(int index) {
        NetworkClient client = getClient(index);
        if (client!=null) {
            return client.getOutStream();
        } else {
            return null;
        }
    }

    /**
     * Returns the number of clients that are already connected.
     *
     * @return Number of connected clients.
     */
    public int getNumConnected() {
        int conn=0;
        for (int i=0;i<getNumClients();i++) {
            if (getClientState(i)==CONNECTED) {
                conn++;
            }
        }
        return conn;
    }

    /**
     * Returns the next possible id.
     *
     * @return A new client id.
     */
    synchronized private int getNextClientId() {
        int id=10;
        boolean found=false;
        while (!found) {
            if (getIndexOfId(id)==-1) {
                found=true;
            } else {
                id=id+10;
            }
        }
        return id;
    }

    /**
     * Returns the index of the given id or -1 if this id is not available.
     *
     * @param id Id of the client
     * @return Index of the client.
     */
    synchronized public int getIndexOfId(int id) {
        if (id<0) {
            return -1;
        }
        for (int i=0;i<getNumClients();i++) {
            NetworkClient client = getClient(i);
            if (client!=null && id==client.getClientId()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the index of the given client number (this is the number as the client is seen as player)
     * or -1 if this number is not available.
     *
     * @param clientNr Number of the client
     * @return Index of the client.
     */
    synchronized public int getIndexOfNr(int clientNr) {
        if (clientNr<0) {
            return -1;
        }
        for (int i=0;i<getNumClients();i++) {
            NetworkClient client = getClient(i);
            if (client!=null && clientNr==client.getClientNr()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Indicates that a connection to a new client shall be established
     */
    public NetworkClient addClient() {
        int id = getNextClientId();
        NetworkClient client = new NetworkClient();
        client.setClientId(id);
        listClients.add(client);
        startConnecting(id);
        return client;
    }

    /**
     * @param client The client to remove.
     * @return True if a client was removed.
     */
    private boolean removeClient(NetworkClient client) {
        if (client!=null) {
            client.setClientId(-1);
            return listClients.remove(client);
        } else {
            return false;
        }
    }

    /**
     * @param index Index of the client to remove.
     * @return True if a client was removed.
     */
    public boolean removeClient(int index) {
        return disconnect(index);
    }

    /**
     * Removes the last client with the given state.
     *
     * @param state The state of a client.
     * @return True if a client was removed.
     */
    private boolean removeLastClientByState(int state) {
        for (int i=getNumClients()-1;i>=0;i--) {
            if (state==-1 || getClientState(i)==state) {
                if (removeClient(i)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Removes a client. The least important clients is removed first.
     * If a client index is given, than this client is disconnected if all clients are CONNECTED.
     *
     * @param index Index of the choosen client (or -1).
     */
    synchronized public void removeBestFitClient(int index) {
        if (!removeLastClientByState(NOTUSED)) {
            if (!removeLastClientByState(DISCONNECTED)) {
                if (!removeLastClientByState(CONNECTING)) {
                    if (index<0 || !removeClient(index)) {
	                    if (!removeLastClientByState(CONNECTED)) {
	                        removeLastClientByState(-1);
	                    }
                    }
                }
            }
        }
    }

    /**
     * Starts the connecting process for client with the given id.
     * The client has to be DISCONNECTED.
     *
     * @param clientId Id of a disconnected client.
     */
    public void startConnecting(final int clientId) {
        // start a new thread to accept a connection
        Thread t = new Thread() {
            @Override
            public void run() {
                Socket sock=null;
                try {
                    int index = getIndexOfId(clientId);
                    NetworkClient client = getClient(index);
                    if (client!=null && client.getState()==DISCONNECTED) {
                        boolean releaseSocket=false;
                        client.setState(CONNECTING);
                        sock = servSock.accept();
                        // test if this client is still connecting
                        if (sock!=null && client.getState()==CONNECTING && client.getClientId()==clientId) {
                            PrintStream outStream = new PrintStream(sock.getOutputStream(), false, Charset.defaultCharset().displayName());
                            BufferedReader inStream = new BufferedReader(new InputStreamReader(sock.getInputStream(),
                                                                                               Charset.defaultCharset()));
                            // set the client's datas
                            client.setSocket(sock);
                            client.setOutStream(outStream);
                            client.setInStream(inStream);
                            // wait for the client
                            List<String> alOut=new ArrayList<>();
                            boolean wasAbort = false;
                            while (!wasAbort && !messageClientData(alOut, clientId)) {
                                wasAbort = (messageClientAbort(clientId) || client.getClientId()!=clientId || putMessage(outStream, NetworkMessage.msgTestS())!=0);
                                netWait();
                            }
                            // got message of a client
                            if (!wasAbort && !alOut.isEmpty()) {
                                String name = "";
                                String ip = "";
                                if (alOut.size()>=1) {
                                    name=alOut.get(0);
                                }
                                if (alOut.size()>=2) {
                                    ip=alOut.get(1);
                                }
                                if (HGBaseTools.hasContent(name)) {
                                    if (nameAlreadyExists(name)) {
                                        putMessage(outStream, NetworkMessage.msgNameExistsS());
                                        releaseSocket=true;
                                    } else {
	                                    // replies to the client and wait for an ok of the client
	                                    wasAbort = (putMessage(outStream, NetworkMessage.msgShakehandS(clientId))!=0);
	                                    while (!wasAbort && !messageClientAcknowledge(clientId)) {
	                                        wasAbort = (messageClientAbort(clientId) || client.getClientId()!=clientId || putMessage(outStream, NetworkMessage.msgTestS())!=0);
	                                        netWait();
	                                    }
	                                    if (!wasAbort) {
	                                        if (nameAlreadyExists(name)) {
	                                            // test a second time if name still does not exist
	                                            putMessage(outStream, NetworkMessage.msgNameExistsS());
	                                            releaseSocket=true;
	                                        } else {
		                                        client.setState(CONNECTED);
		                                        client.setClientName(name);
		                                        client.setClientIP(ip);
		                                        client.setServerIP(getIp());
	                                        }
	                                    } else {
                                            releaseSocket=true;
                                        }
                                    }
                                }
                            } else {
                                releaseSocket=true;
                            }
                        } else {
                            releaseSocket=true;
                        }
                        if (releaseSocket){
                            // release the socket
                            disconnect(index);
                        }
                        // end the critical sections for all branches
                    }
                } catch(IOException e) {
                    if (clientId>=0) {
                        int idx = getIndexOfId(clientId);
                        if (idx>=0) {
                            disconnect(idx);
                        }
                    }
                }
            }
        };
        t.start();
    }

    /**
     * Tests if one of the connected clients has the same name as
     * the new one.
     *
     * @param name Name of the new client.
     * @return
     */
    synchronized private boolean nameAlreadyExists(String name) {
        for (int i=0; i<getNumClients(); i++) {
            NetworkClient client = getClient(i);
            if (client!=null && client.getState()==CONNECTED && client.getClientName().equals(name)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Removes the client id from the message (the last part).
     *
     * @return The message without the client id (without the last part).
     */
    protected String removeClientIdFromMessage(String msg) {
        int index = msg.lastIndexOf(';');
        return msg.substring(0, index);
    }

    /**
     * Returns true if an abort was sent.
     * After the method is called the value will be reseted
     *
     * @param id id of the client
     * @return true if there was an abort message
     */
    public boolean messageClientAbort(int id) {
        String msg = getNextMessage(NetworkMessage.MSG_ERROR, NetworkMessage.ERROR_ABORT, id);
        if (msg!=null) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if a message from a client that acknowledges the connection
     * was received
     *
     * @param clientId id of the client
     * @return true if this message was received, else false
     */
    public boolean messageClientAcknowledge(int clientId) {
        String msg=getNextMessage(NetworkMessage.MSG_CTRL, NetworkMessage.PARAM_CLIENTACC, clientId);
        return (msg!=null);
    }

    /**
     * Returns true if a message that got client datas was received
     *
     * @param al array with datas, that shall be filled
     * @param clientId id of the client
     * @return true if this message was received, else false
     */
    public boolean messageClientData(List<String> al, int clientId) {
        al.clear();
        String msg=getNextMessage(NetworkMessage.MSG_CTRL, NetworkMessage.PARAM_CLIENTCONN, clientId);
        if (msg!=null) {
            // control, if it's the correct application
            String appName = getMessagePart(2,msg);
            if (appName!=null && appName.equals(HGBaseAppTools.getAppName())) {
	            al.add(getMessagePart(3,msg));
	            al.add(getMessagePart(4,msg));
	            return true;
            } else {
                putMessage(getIndexOfId(clientId), NetworkMessage.msgWrongApplicationS());
                // the server recognizes an error because the list "al" is empty
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if an ok message for a game from any client received.
     *
     * @param msgType The message type (engine, game state, move information).
     * @return True if this message was received.
     */
    public boolean messageClientsGameOk(String msgType) {
        String msg=getNextMessage(msgType, NetworkMessage.PARAM_GAMEOK);
        return (msg!=null);
    }

    /**
     * Returns true if an ok message for a game from a given client is received.
     *
     * @param msgType The message type (engine, game state, move information).
     * @param clientId The id of a client.
     * @return True if this message was received.
     */
    public boolean messageClientsGameOk(String msgType, int clientId) {
        String msg=getNextMessage(msgType, NetworkMessage.PARAM_GAMEOK, clientId);
        return (msg!=null);
    }

    /**
     * This method waits until all clients have returned an ok or any client got lost.
     *
     * @param msgType The message type (engine, game state, move information).
     * @return True if all clients acknowledged or false if a client disconnected.
     */
    public boolean waitForClientsGameOk(String msgType) {
        return waitForClientsGameOk(msgType, -1);
    }

    /**
     * This method waits until the given number clients have returned an ok or any client got lost.
     *
     * @param msgType The message type (engine, game state, move information).
     * @param except The index of the client that is excepted or -1.
     * @return True if the given number of clients acknowledged or false if a client disconnected.
     */
    public boolean waitForClientsGameOk(String msgType, int except) {
        int numClients = getNumInitialClients();
        if (except>=0) {
            numClients--;
        }
        int numOk = 0;
        while (getNumConnected()==getNumInitialClients() && numOk<numClients) {
            while (messageClientsGameOk(msgType)) {
                numOk++;
            }
            NetworkConnection.netWait();
        }
        return (numOk==numClients);
    }


    /**
     * This method waits until a move information was received or a client terminated the game.
     *
     * @param msgReceived Buffer to get the game information.
     * @param player The server player that waits.
     * @return True if the information received.
     */
    public boolean waitForClientMoveInformation(StringBuffer msgReceived, NetworkPlayer player) {
        String msg = null;
        while (player.isPlaying() && getNumConnected() == getNumInitialClients() && msg == null) {
            msg = messageGameInformation(NetworkMessage.MSG_MOVE);
            NetworkConnection.netWait();
        }
        if (msg != null) {
            msgReceived.append(removeClientIdFromMessage(msg));
            return true;
        } else {
            return false;
        }
    }

}
