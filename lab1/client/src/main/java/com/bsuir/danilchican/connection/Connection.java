package com.bsuir.danilchican.connection;

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

    /**
     * Default constructor.
     */
    public Connection() {
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
            LOGGER.log(Level.INFO, "Connected to server.");

            this.initStream();
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Couldn't listen to port " + PORT);
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
    public void receive() {
        try {
            int countBytes = is.read(clientMessage);

            String data = new String(clientMessage, 0, countBytes);
            LOGGER.log(Level.DEBUG, "Received data from server: " + data);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Error: " + e.getMessage());
        }
    }

    private void initStream() throws IOException {
        is = socket.getInputStream();
        os = socket.getOutputStream();
    }

    private void close() throws IOException {
        is.close();
        os.close();
        socket.close();
    }
}
