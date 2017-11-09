package com.bsuir.danilchican.connection;

import com.bsuir.danilchican.command.ICommand;
import com.bsuir.danilchican.parser.Parser;
import com.bsuir.danilchican.request.Request;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Connection {

    /**
     * Logger to getCommand logs.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String ADDRESS = "localhost";

    private static final int PORT = 1024;
    private static final int LISTEN_PERIOD_TIME_MS = 500;

    static HashMap<SelectionKey, ClientSession> clientMap = new HashMap<>();
    public static HashMap<ClientSession, Request> requests = new HashMap<>();

    private ServerSocketChannel serverChannel;
    private Selector selector;
    private SelectionKey serverKey;

    public Connection() {
    }

    /**
     * Run server.
     *
     * @return boolean
     */
    public boolean open() {
        try {
            InetSocketAddress sockAddr = new InetSocketAddress(ADDRESS, PORT);

            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverKey = serverChannel.register(selector = Selector.open(), SelectionKey.OP_ACCEPT);
            serverChannel.bind(sockAddr);

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
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                listenExecutor();
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
        }, 0, LISTEN_PERIOD_TIME_MS, TimeUnit.MILLISECONDS);
    }

    private void listenExecutor() throws IOException {
        selector.selectNow();

        for (SelectionKey key : selector.selectedKeys()) {
            try {
                if (!key.isValid()) {
                    LOGGER.log(Level.WARN, "Key is invalid...");
                    continue;
                }

                if (key == serverKey) {
                    SocketChannel acceptedChannel = serverChannel.accept();

                    if (acceptedChannel == null) {
                        LOGGER.log(Level.ERROR, "Accepted channel is null.");
                        continue;
                    }

                    acceptedChannel.configureBlocking(false);

                    SelectionKey readKey = acceptedChannel.register(selector, SelectionKey.OP_READ);
                    ClientSession newClientSession = new ClientSession(readKey, acceptedChannel);

                    clientMap.put(readKey, newClientSession);
                    requests.put(newClientSession, new Request(readKey, acceptedChannel));

                    LOGGER.log(Level.INFO, "Client " + acceptedChannel.getRemoteAddress() + " connected.");
                    LOGGER.log(Level.INFO, "Total clients: " + clientMap.size());
                }

                boolean isUserDisconnected = false;

                if (key.isReadable()) {
                    ClientSession session = clientMap.get(key);

                    if (session == null) {
                        LOGGER.log(Level.ERROR, "Client session not found.");
                        continue;
                    }

                    session.read();
                    isUserDisconnected = session.isUserDisconnected();
                }

                if (!isUserDisconnected) {
                    if (key.isWritable()) {
                        ClientSession session = clientMap.get(key);

                        if (session == null) {
                            LOGGER.log(Level.ERROR, "Client session not found.");
                            continue;
                        }

                        Request request = Connection.requests.get(session);

                        if (request != null) {
                            ClientSession.GLOBAL_USER_REQUEST = request;

                            if (!request.isFree()) {
                                request.execute(null);
                            } else {
                                LOGGER.log(Level.ERROR, "Operation WRITE is not available.");
                            }
                        } else {
                            LOGGER.log(Level.ERROR, "User request is empty.");
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
        }

        selector.selectedKeys().clear();
    }
}
