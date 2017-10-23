package com.bsuir.danilchican.connection;

import com.bsuir.danilchican.controller.Controller;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;

public class Connection {

    /**
     * Logger to getCommand logs.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int SIZE_BUFF = 256;
    private int port = 8033;

    private DatagramSocket socket;

    private byte packetData[];

    /**
     * Default constructor.
     */
    private Connection() {
        packetData = new byte[SIZE_BUFF];
    }

    /**
     * Constructor with port.
     *
     * @param port
     */
    public Connection(int port) {
        this();
        this.port = port;
    }

    /**
     * Connect to server.
     *
     * @return boolean
     */
    public boolean init() {
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(1000);

            return true;
        } catch (SocketException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
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
        byte bytes[] = data.getBytes();

        try {
            DatagramPacket packetSend = new DatagramPacket(
                    bytes, bytes.length,
                    InetAddress.getLocalHost(), port
            );

            socket.send(packetSend);
            return true;
        } catch (UnknownHostException e) {
            LOGGER.log(Level.ERROR, "Couldn't send message. Unknown host.");
            return false;
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
            packetData = new byte[SIZE_BUFF];

            DatagramPacket packet = new DatagramPacket(packetData, packetData.length);
            socket.receive(packet);

            String data = new String(packet.getData(), 0, packet.getLength());
            LOGGER.log(Level.INFO, "Server: " + data);

            return data;
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Error: " + e.getMessage());
            return null;
        }
    }

    public int receive(byte[] buffer) {
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            return packet.getLength();
        } catch (IOException e) {
            LOGGER.log(Level.WARN, e.getMessage());
            return -1;
        }
    }

    /**
     * Close connection.
     */
    public void close() {
        socket.close();
        Controller.getInstance().setConnection(null);
    }
}
