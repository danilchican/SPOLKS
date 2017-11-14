package com.bsuir.danilchican.pool;

import com.bsuir.danilchican.connection.ClientConnection;
import com.bsuir.danilchican.connection.Connection;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public final class ConnectionPool {

    /**
     * Logger to write logs.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Instance of ConnectionPool.
     */
    private static ConnectionPool instance;

    public static final int POOL_SIZE = 2;
    public static int lastConnectionIndex = POOL_SIZE;
    private int availableConnectionsCount = POOL_SIZE;

    private static boolean createdInstance = false;

    private Map<ClientConnection, Thread> connections;
    private Connection serverConnection;

    private ConnectionPool() {
        connections = new HashMap<>();
        serverConnection = new Connection();

        if (serverConnection.open()) {
            ReentrantLock lock = new ReentrantLock();

            for (int i = 0; i < POOL_SIZE; i++) {
                ClientConnection connection = new ClientConnection(i + 1, lock);
                connections.put(connection, new Thread(connection));
            }
        } else {
            LOGGER.log(Level.FATAL, "Can't open server connection.");
        }
    }

    /**
     * Get instance of connection pool.
     *
     * @return instance
     */
    public static ConnectionPool getInstance() {
        if (!createdInstance) {
            if (instance == null) {
                instance = new ConnectionPool();
                createdInstance = true;

                LOGGER.log(Level.INFO, ConnectionPool.class + " instance created!");
            }
        }

        return instance;
    }

    public Connection getServerConnection() {
        return serverConnection;
    }

    /**
     * Increment.
     */
    public void incAvailableConnections() {
        ++availableConnectionsCount;
    }

    /**
     * Decrement.
     */
    public void decAvailableConnections() {
        --availableConnectionsCount;
    }

    /**
     * Check if we're have some available connection.
     *
     * @return boolean
     */
    public boolean hasAvailableConnection() {
        LOGGER.log(Level.DEBUG, "Available threads: " + availableConnectionsCount);
        return availableConnectionsCount > 0;
    }

    /**
     * Start connections to listen accepting a new client.
     */
    public void runListeners() {
        connections.forEach((c, t) -> t.start());
    }

    /**
     * Add a new free connection to list
     * if it doesn't contain available connections
     * and start it right now.
     *
     * @param connection
     */
    public void addFreeConnection(ClientConnection connection) {
        Thread connectionThread = new Thread(connection);
        connections.put(connection, connectionThread);
        connectionThread.start();
    }

    /**
     * Remove connection from pool.
     *
     * @param connection
     */
    public void removeConnection(ClientConnection connection) {
        connections.remove(connection);
    }

    /**
     * Get actual pool size.
     *
     * @return size
     */
    public int getActualPoolSize() {
        return connections.size();
    }
}
