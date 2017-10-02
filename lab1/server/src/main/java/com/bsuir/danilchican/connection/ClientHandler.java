package com.bsuir.danilchican.connection;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class ClientHandler implements Runnable {

    /**
     * Logger to getCommand logs.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int SIZE_BUFF = 100;
    private final int clientId;

    private final Socket socket;

    private InputStream is;
    private OutputStream os;

    private byte clientMessage[];

    /**
     * Constructor.
     *
     * @param socket
     * @param clientId
     */
    ClientHandler(Socket socket, int clientId) {
        this.socket = socket;
        this.clientId = clientId;

        clientMessage = new byte[SIZE_BUFF];
    }

    @Override
    public void run() {
        try {
            this.initStream();

            while (true) {
                try {
                    int countBytes = is.read(clientMessage);

                    String data = new String(clientMessage, 0, countBytes);
                    LOGGER.log(Level.DEBUG, "Received from client #" + clientId + ": " + data);

                    os.write("some string".getBytes()); // TODO change
                } catch (Exception e) {
                    LOGGER.log(Level.INFO, "Client stopped working with server.");
                    break;
                }
            }

            this.closeConnection();
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, "Error: " + e.getMessage());
        }
    }

    private void initStream() throws IOException {
        is = socket.getInputStream();
        os = socket.getOutputStream();
    }

    private void closeConnection() throws IOException {
        is.close();
        os.close();
        socket.close();

        LOGGER.log(Level.INFO, "Client #" + clientId + " disconnected!");
    }
}
