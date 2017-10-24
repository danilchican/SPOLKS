package com.bsuir.danilchican.connection;

import com.bsuir.danilchican.command.ICommand;
import com.bsuir.danilchican.exception.CommandNotFoundException;
import com.bsuir.danilchican.exception.WrongCommandFormatException;
import com.bsuir.danilchican.parser.Parser;
import com.bsuir.danilchican.util.SocketBuffer;
import com.sun.javaws.Main;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

class ClientSession {

    /**
     * Logger to getCommand logs.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int SIZE_BUFF = 256;

    private SelectionKey selkey;
    private SocketChannel channel;
    private SocketBuffer buffer;

    ClientSession(SelectionKey selkey, SocketChannel channel) throws IOException {
        this.selkey = selkey;
        this.channel = (SocketChannel) channel.configureBlocking(false); // asynchronous/non-blocking
        this.buffer = new SocketBuffer();
    }

    private void disconnect() {
        AsyncConnection.clientMap.remove(selkey);

        try {
            if (selkey != null) {
                selkey.cancel();
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

            ICommand command = new Parser().handle(cmd);
            command.setBuffer(buffer);
            LOGGER.log(Level.INFO, "Worked " + cmd);

            // use it!
//            buffer.flip(); // flip to clear and send to client
//            channel.write(buffer);

            command.execute();
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
            disconnect();
        } catch (WrongCommandFormatException | CommandNotFoundException e) {
            LOGGER.log(Level.ERROR, "Error: " + e.getMessage());
        }
    }
}
