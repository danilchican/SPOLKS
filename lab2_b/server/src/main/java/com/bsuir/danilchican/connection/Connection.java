package com.bsuir.danilchican.connection;

import com.bsuir.danilchican.command.ICommand;
import com.bsuir.danilchican.exception.CommandNotFoundException;
import com.bsuir.danilchican.exception.WrongCommandFormatException;
import com.bsuir.danilchican.parser.Parser;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Connection {

    /**
     * Logger to getCommand logs.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    private DatagramSocket socket;
    private DatagramPacket packet;

    private static final int SIZE_BUFF = 256;
    private static final int PORT = 8033;

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
        byte[] bytes = data.getBytes();

        this.write(bytes, bytes.length);
    }

    /**
     * Write to stream.
     *
     * @param bytes
     * @throws IOException
     */
    public void write(byte[] bytes, int length) throws IOException {
        DatagramPacket packet = new DatagramPacket(
                bytes, length,
                this.packet.getAddress(),
                this.packet.getPort()
        );

        socket.send(packet);
    }

    /**
     * Read stream data.
     *
     * @return data
     * @throws IOException
     */
    public String read() throws IOException {
        socket.receive(packet);
        return new String(packet.getData(), 0, packet.getLength());
    }

    /**
     * Run server.
     *
     * @return boolean
     */
    public boolean open() {
        try {
            socket = new DatagramSocket(PORT);
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
            try {
                packet = new DatagramPacket(clientMessage, SIZE_BUFF);

                String cmd = this.read();
                LOGGER.log(Level.DEBUG, "Client: " + cmd);

                ICommand command = new Parser().handle(cmd);
                command.execute();
            } catch (WrongCommandFormatException | CommandNotFoundException | IOException e) {
                LOGGER.log(Level.ERROR, "Error: " + e.getMessage());
            }
        }
    }

    public void close() throws IOException {
        socket.close();
    }
}
