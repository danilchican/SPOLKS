package com.bsuir.danilchican.connection;

import com.bsuir.danilchican.command.ICommand;
import com.bsuir.danilchican.exception.CommandNotFoundException;
import com.bsuir.danilchican.exception.WrongCommandFormatException;
import com.bsuir.danilchican.parser.Parser;
import com.bsuir.danilchican.request.Request;
import com.bsuir.danilchican.util.SocketBuffer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ClientSession {

    /**
     * Logger to getCommand logs.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    public static final int SIZE_BUFF = 256;

    private SelectionKey readSelkey;
    private SocketChannel channel;
    private SocketBuffer buffer;

    private boolean disconnected = false;

    public static Request GLOBAL_USER_REQUEST;

    ClientSession(SelectionKey readSelkey, SocketChannel channel) throws IOException {
        this.readSelkey = readSelkey;
        this.channel = (SocketChannel) channel.configureBlocking(false); // asynchronous/non-blocking
        this.buffer = new SocketBuffer();
    }

    public boolean isUserDisconnected() {
        return disconnected;
    }

    private void disconnect() {
        Connection.clientMap.remove(readSelkey);
        Connection.requests.remove(this);
        disconnected = true;

        try {
            if (readSelkey != null) {
                readSelkey.cancel();
            }

            if (channel == null) {
                LOGGER.log(Level.ERROR, "Can't disconnect from server. Channel is null.");
                return;
            }

            LOGGER.log(Level.INFO, "User " + channel.getRemoteAddress() + " is disconnected!");
            channel.close();
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
    }

    void read() {
        try {
            int countBytes;

            if ((countBytes = channel.read((ByteBuffer) buffer.clear())) == -1) {
                disconnect();
            }

            if(countBytes < 1) {
                return;
            }

            byte[] tempData = buffer.read(countBytes);

            String cmd = new String(tempData, 0, countBytes);
            LOGGER.log(Level.DEBUG, "Client: " + cmd);

            Request request = Connection.requests.get(this);

            if(request != null) {
                GLOBAL_USER_REQUEST = request;

                if(request.isFree()) {
                    ICommand command = new Parser().handle(cmd);
                    command.setChannel(channel);
                    command.execute();
                } else {
                    request.execute(cmd);
                }
            } else {
                LOGGER.log(Level.ERROR, "User request is empty.");
            }
        } catch (IOException e) {
            //LOGGER.log(Level.ERROR, e.getMessage());
            disconnect();
        } catch (WrongCommandFormatException | CommandNotFoundException e) {
            LOGGER.log(Level.ERROR, "Error: " + e.getMessage());
        }
    }
}
