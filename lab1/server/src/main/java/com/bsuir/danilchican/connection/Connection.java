package com.bsuir.danilchican.connection;

import com.bsuir.danilchican.command.ICommand;
import com.bsuir.danilchican.exception.CommandNotFoundException;
import com.bsuir.danilchican.exception.WrongCommandFormatException;
import com.bsuir.danilchican.parser.Parser;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Connection {

    /**
     * Logger to getCommand logs.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    private ServerSocket socket;

    private static final int SIZE_BUFF = 256;
    private static final int PORT = 1024;
    private static final int BACKLOG = 10;

    private InputStream is;
    private OutputStream os;

    private byte clientMessage[];

    public Connection() {
        clientMessage = new byte[SIZE_BUFF];
    }

    /**
     * Write to stream.
     *
     * @param data
     * @throws IOException
     */
    public void write(String data) throws IOException {
        os.write(data.getBytes());
    }

    /**
     * Write to stream.
     *
     * @param bytes
     * @throws IOException
     */
    public void write(byte[] bytes, int length) throws IOException {
        os.write(Arrays.copyOfRange(bytes, 0, length));
    }

    /**
     * Read stream data.
     *
     * @return data
     * @throws IOException
     */
    public String read() throws IOException {
        int countBytes = is.read(clientMessage);

        return new String(clientMessage, 0, countBytes);
    }

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
     * Listen for clients.
     */
    public void listen() {
        while (true) {
            Socket client;

            try {
                client = socket.accept();

                LOGGER.log(Level.INFO, "Client is connected!");
                this.initStream(client);

                while (true) {
                    try {
                        int countBytes;

                        if ((countBytes = is.read(clientMessage)) == -1) {
                            break;
                        }

                        String cmd = new String(clientMessage, 0, countBytes);
                        LOGGER.log(Level.DEBUG, "Client: " + cmd);

                        ICommand command = new Parser().handle(cmd);
                        command.execute();
                    } catch (IOException e) {
                        LOGGER.log(Level.ERROR, "Client stopped working with server.");
                        break;
                    } catch (WrongCommandFormatException | CommandNotFoundException e) {
                        LOGGER.log(Level.ERROR, "Error: " + e.getMessage());
                    }
                }

                this.closeClientConnection(client);
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, "Can't close connection.");
            }
        }
    }

    public void close() throws IOException {
        is.close();
        os.close();
        socket.close();
    }

    private void initStream(Socket s) throws IOException {
        is = s.getInputStream();
        os = s.getOutputStream();
    }

    private void closeClientConnection(Socket s) throws IOException {
        is.close();
        os.close();
        s.close();
        System.out.println("Client has been disconnected!");
    }
}
