package com.bsuir.danilchican.pool;

import com.bsuir.danilchican.connection.ClientConnection;
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

    public static final int POOL_SIZE = 3;
    public static int lastConnectionIndex = POOL_SIZE;
    private int availableConnectionsCount = POOL_SIZE;

    private static boolean createdInstance = false;

    private Map<ClientConnection, Thread> connections;

    private ConnectionPool() {
        connections = new HashMap<>();
        ReentrantLock lock = new ReentrantLock();

        for (int i = 0; i < POOL_SIZE; i++) {
            ClientConnection connection = new ClientConnection(i + 1, lock);
            connections.put(connection, new Thread(connection));
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
