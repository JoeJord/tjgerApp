package com.tjger.net;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * A network client. For managing a client on the server or client side.
 * Not all fields must be used, depending if this object is used as the 
 * fasade of a client connection or as one of the clients to be managed by the server.
 * 
 * @author hagru
 */
public class NetworkClient {

    private String serverIP;
    private String clientName;
    private int clientId;
    private int clientNr;
    private String clientIP;
    private int state; // see NetworkConnection states (DISCONNECT, CONNECTED, ...)
    private Socket socket=null;
    private PrintStream outStream=null;
    private BufferedReader inStream=null;
    
    public NetworkClient() {
        super();
        resetClient();
    }

    /**
     * Resets all client information.
     */
    public void resetClient() {
        setServerIP("");
        setClientName("");
        setClientId(-1);
        setClientNr(-1);
        setClientIP("");
        setState(NetworkConnection.DISCONNECTED);
        setSocket(null);
        setOutStream(null);
        setInStream(null);
    }

    /**
     * @return The unique id of the client.
     */
    public int getClientId() {
        return clientId;
    }
    /**
     * @param clientId The unique id of the client.
     */
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
    /**
     * @return The client's ip address.
     */
    public String getClientIP() {
        return clientIP;
    }
    /**
     * @param clientIP The client's ip address.
     */
    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }
    /**
     * @return The player name of the client.
     */
    public String getClientName() {
        return clientName;
    }
    /**
     * @param clientName The player name of the client.
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    /**
     * @return The client number as seen from the server side.
     */
    public int getClientNr() {
        return clientNr;
    }
    /**
     * @param clientNr he client number as seen from the server side.
     */
    public void setClientNr(int clientNr) {
        this.clientNr = clientNr;
    }
    /**
     * @return The input stream.
     */
    public BufferedReader getInStream() {
        return inStream;
    }
    /**
     * @param inStream The input stream.
     */
    public void setInStream(BufferedReader inStream) {
        this.inStream = inStream;
    }
    /**
     * @return The output stream.
     */
    public PrintStream getOutStream() {
        return outStream;
    }
    /**
     * @param outStream The output stream.
     */
    public void setOutStream(PrintStream outStream) {
        this.outStream = outStream;
    }
    /**
     * @return The server's ip address.
     */
    public String getServerIP() {
        return serverIP;
    }
    /**
     * @param serverIP The server's ip address.
     */
    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }
    /**
     * @return The client socket.
     */
    public Socket getSocket() {
        return socket;
    }
    /**
     * @param sock The client socket.
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    /**
     * @return The connection state (see NetworkConnection).
     */
    public int getState() {
        return state;
    }
    /**
     * @param state The connection state (see NetworkConnection).
     */
    public void setState(int state) {
        this.state = state;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        return getClientId()+": "+getClientName();
    }
    
}
