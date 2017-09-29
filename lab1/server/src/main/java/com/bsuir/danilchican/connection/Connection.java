package com.bsuir.danilchican.connection;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Connection {

    /**
     * Logger to getCommand logs.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    private ServerSocket socket;

    private static final int PORT = 1024;
    private static final int BACKLOG = 10;

    private int countClients = 0;

    /**
     * Run server.
     *
     * @return boolean
     */
    public boolean open() {
        try {
            socket = new ServerSocket(PORT, BACKLOG);
            LOGGER.log(Level.INFO, "Server started.");

            return true;
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Couldn't listen to port " + PORT);
            return false;
        }
    }

    /**
     * Close connection.
     */
    public void close() {
        try {
            socket.close();
            LOGGER.log(Level.INFO, "Server stopped.");
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Can't close connection.");
        }
    }

    /**
     * Listen for clients.
     */
    public void listen() {
        while (true) {
            try {
                Socket client = socket.accept();

                countClients++;
                LOGGER.log(Level.INFO, "Client #" + countClients + " connected!");

                new Thread(new ClientHandler(client, countClients)).start();
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, "Can't close connection.");
            }
        }
    }
}
