package com.bsuir.danilchican.connection;

import com.bsuir.danilchican.controller.Controller;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Connection {

    /**
     * Logger to getCommand logs.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int SIZE_BUFF = 100;
    private static final int PORT = 1024;

    /**
     * Default serverIP of server.
     */
    private String serverIP = "192.168.1.2";

    private Socket socket;

    private InputStream is;
    private OutputStream os;

    private byte clientMessage[];
    private int receivedBytesCount = 0;

    /**
     * Default constructor.
     */
    private Connection() {
        clientMessage = new byte[SIZE_BUFF];
    }

    /**
     * Constructor with server ip.
     *
     * @param serverIP
     */
    public Connection(String serverIP) {
        this();
        this.serverIP = serverIP;
    }

    /**
     * Connect to server.
     *
     * @return boolean
     */
    public boolean connect() {
        try {
            socket = new Socket(serverIP, PORT);
            socket.setKeepAlive(true);
            LOGGER.log(Level.INFO, "Connected to server.");

            this.initStream();
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Couldn't connect to server.");
            return false;
        }
    }
    /**
     * Send message to server.
     *
     * @param data message to server
     * @return boolean
     */
    public boolean sendMessage(String data) {
        try {
            os.write(data.getBytes());
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Couldn't send message. " + e.getMessage());
            return false;
        }
    }

    /**
     * Receive message from server.
     */
    public String receive() {
        try {
            int countBytes = is.read(clientMessage);

            String data = new String(clientMessage, 0, countBytes);
            LOGGER.log(Level.INFO, "Server: " + data);

            return data;
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Error: " + e.getMessage());
            return null;
        }
    }

    public byte[] receiveBuff(final int buffSize) {
        try {
            byte[] buffer = new byte[buffSize];
            int count = is.read(buffer);

            if(count != -1) {
                setReceivedBytesCount(count);
            } else {
                LOGGER.log(Level.ERROR, "Count = -1. Something went wrong");
            }

            return buffer;
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Error: " + e.getMessage());
            return null;
        }
    }

    public void setReceivedBytesCount(int receivedBytesCount) {
        this.receivedBytesCount = receivedBytesCount;
    }

    public int getReceivedBytesCount() {
        return receivedBytesCount;
    }

    /**
     * Close connection.
     */
    public void close() {
        try {
            is.close();
            os.close();

            socket.close();
            Controller.getInstance().setConnection(null);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Error: " + e.getMessage());
        }
    }

    private void initStream() throws IOException {
        is = socket.getInputStream();
        os = socket.getOutputStream();
    }
}
