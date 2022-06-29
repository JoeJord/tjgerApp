package com.tjger.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.tjger.MainFrame;
import com.tjger.game.completed.GameManager;
import com.tjger.lib.ConstantValue;

import at.hagru.hgbase.lib.HGBaseTools;

/**
 * An abstract class that manages a network connection.
 *
 * @author hagru
 */
abstract public class NetworkConnection {

    final public static int DISCONNECTED = 0;
    final public static int CONNECTED    = 1;
    final public static int CONNECTING   = 2;
    final public static int NOTUSED      = 9;

    private boolean wasClosed;
    private List<String> msgBuffer = new ArrayList<>();

    public NetworkConnection() {
        super();
    }

    /**
     * @return The main frame.
     */
    protected MainFrame getMainFrame() {
        return GameManager.getInstance().getMainFrame();
    }

    /**
     * Resets all values.
     */
    public void reset() {
        this.msgBuffer.clear();
        this.wasClosed = false;
    }

    /**
     * Wait for a network action
     */
    public static void netWait() {
        HGBaseTools.sleep(ConstantValue.NETWORK_WAITINTERVAL);
    }

    /**
     * Starts a connection. A thread is reading until connection is closed.
     *
     * @return 0 if successful or < 0 if there is an error.
     */
    protected int start() {
        reset();
        // start a thread that reads from the socket(s)
        Thread t = new Thread() {
            @Override
            public void run() {
                while (!wasClosed) {
                    String msg = getMessage();
                    if (HGBaseTools.hasContent(msg)) {
                        msgBufInsert(msg);
                        handleMessage(msg);
                    }
                    netWait();
                }
            }
        };
        t.start();
        return 0;
    }

    /**
     * Returns the client id or -1 if it's a server
     *
     * @return client id or -1
     */
    abstract public int getId();

    /**
     * @return The ip address of this connection.
     */
    public String getIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) { }
        return "";
    }

    /**
     * Closes the current connection
     */
    public void close() {
        wasClosed = true;
    }

    /**
     * Puts a message to the output stream
     *
     * @param out output steam
     * @param message message text
     * @return 0 if there was no error, otherwise the error code
     */
    protected int putMessage(PrintStream out, String message) {
        if (out != null) {
            out.println(message);
            return (out.checkError()) ? -30224 : 0;
        }
        return -30221;
    }

    /**
     * Gets a message from an input stream, must be overridden
     */
    abstract protected String getMessage();


    /**
     * Gets a message from the given input stream.
     * This message is appended on the string buffer.
     *
     * @param in Input stream.
     * @param message The buffer to append the message.
     * @return 0 if successful.
     */
    protected int getMessage(BufferedReader in, StringBuffer message) {
        if (in != null) {
            try {
                if (in.ready()) {
                    String str = in.readLine();
                    if (str != null) {
                        message.append(str);
                    }
                    return 0;
                }
            } catch (IOException e) {
                return -30225;
            }
        }
        return 0;
    }

    /**
     * Handles the given message
     *
     * @return true if this messages was handled
     */
    abstract protected boolean handleMessage(String msg);

    /**
     * Removes all messages from the message buffer
     *
     */
    protected void msgBufClear() {
        if (msgBuffer != null) {
            msgBuffer.clear();
        }
    }

    /**
     * Inserts a new message at the end of the message buffer
     *
     * @param msg new message
     */
    protected void msgBufInsert(String msg) {
        if (msgBuffer != null && msg != null) {
            msgBuffer.add(msg);
        }
    }

    /**
     * Returns if the given message has the given type
     *
     * @param msg message
     * @param type message type
     * @param param messsage parameter
     */
    protected boolean isMessageType(String msg, String type, String param) {
        return (type.equals(getMessagePart(0, msg)) && (param==null || param.equals(getMessagePart(1, msg))));
    }

    /**
     * If a message with the given type is in the buffer,
     * the index of this message is returned
     *
     * @param type message type
     * @param param message parameter
     * @return index of message or -1
     */
    protected int getIndexOfMessage(String type, String param) {
        for (int i=0; i<msgBuffer.size(); i++) {
            String msg = msgBuffer.get(i);
            if (isMessageType(msg, type, param)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the next message with the given type and removes it
     * from the buffer
     *
     * @param type message type
     * @param param message parameter
     * @return message or null
     */
    protected String getNextMessage(String type, String param) {
        String msg=null;
        synchronized (msgBuffer) {
            int idx = getIndexOfMessage(type, param);
            if (idx>=0) {
                msg = msgBuffer.remove(idx);
            }
        }
        return msg;
    }

    /**
     * Returns the next message with the given type and client id and removes it
     * from the buffer
     *
     * @param type message type
     * @param param message parameter
     * @param id id of the client
     * @return message or null
     */
    protected String getNextMessage(String type, String param, int id) {
        String msg=null;
        synchronized (msgBuffer) {
            for (int i=0; i<msgBuffer.size() && msg==null; i++) {
                String m = msgBuffer.get(i);
                if (isMessageType(m, type, param) && getClientIdOfMessage(m)==id) {
                    msg = msgBuffer.remove(i);
                }
            }
        }
        return msg;
    }

    /**
     * Returns the client id in the given message or -1.
     *
     * @param msg message.
     * @return client id or -1.
     */
    protected int getClientIdOfMessage(String msg) {
        StringTokenizer st = new StringTokenizer(msg, ConstantValue.NETWORK_SEPARATE);
        String lastPart = "";
        while (st.hasMoreTokens()) {
            lastPart=st.nextToken();
        }
        int id = HGBaseTools.toInt(lastPart);
        return (id==HGBaseTools.INVALID_INT)? -1 : id;
    }

    /**
     * Removes all messages with the given id from the buffer.
     *
     * @param id id of the client.
     * @return number of removed messages.
     */
    protected int removeMessagesWithClientId(int id) {
        int num=0;
        synchronized (msgBuffer) {
            for (int i=msgBuffer.size()-1;i>=0;i--) {
                String m = msgBuffer.get(i);
                if (getClientIdOfMessage(m)==id) {
                    msgBuffer.remove(i);
                    num++;
                }
            }
        }
        return num;
    }

    /**
     * Returns the given part of the message.
     *
     * @param index index of the part.
     * @param msg the whole message, seperated by ConstantValue.NETWORK_SEPARATE.
     * @return part of the message or null.
     */
    protected String getMessagePart(int index, String msg) {
        if (msg!=null) {
            StringTokenizer st = new StringTokenizer(msg, ConstantValue.NETWORK_SEPARATE);
            int i = 0;
            while (st.hasMoreTokens()) {
                String part = st.nextToken();
                if (i == index) {
                	return part;
                }
                i++;
            }
        }
        return null;
    }

    /**
     * Returns the number of mesasge parts that are seperated by the separator.
     *
     * @param msg The message to test.
     * @return The number of parts.
     */
    protected int getNumberMessageParts(String msg) {
        return msg.split(ConstantValue.NETWORK_SEPARATE).length;
    }

    /**
     * Returns the message part representing the move information or null if no move information received.
     *
     * @return String representing move information or null.
     */
    public String messageMoveInformation() {
        return messageGameInformation(NetworkMessage.MSG_MOVE);
    }

    /**
     * Returns the message part representing the game information or null if no game information received.
     * The game information can be the game state or a move information.
     *
     * @param msgType The message type (game state, move).
     * @return String representing game information or null.
     */
    protected String messageGameInformation(String msgType) {
        String msg = getNextMessage(msgType, null);
        return (msg==null)? null : msg.replaceFirst(msgType+ConstantValue.NETWORK_SEPARATE, "");
    }

}
