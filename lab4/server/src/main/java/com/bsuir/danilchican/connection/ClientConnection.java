package com.bsuir.danilchican.connection;

import com.bsuir.danilchican.pool.ConnectionPool;

import static com.bsuir.danilchican.pool.ConnectionPool.lastConnectionIndex;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ClientConnection implements Runnable {

    /**
     * Logger to write logs.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int TIMEOUT_MS = 2_000; // ms
    private int index;
    private boolean isRepeated = false;

    private ReentrantLock lock;
    private Socket clientSocket;

    /**
     * Default constructor.
     *
     * @param index
     * @param lock
     */
    public ClientConnection(final int index, ReentrantLock lock) {
        this.index = index;
        this.lock = lock;
    }

    @Override
    public void run() {
        while (true) {
            ConnectionPool connectionPool = ConnectionPool.getInstance();

            try {
                lock.lock();

                LOGGER.log(Level.INFO, "Thread " + index + " has been locked.");
                this.acceptClient(connectionPool);
            } catch (InterruptedException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            } finally {
                lock.unlock();
            }

            workWithClient(connectionPool);

            try {
                while (true) {
                    if (connectionPool.getActualPoolSize() <= ConnectionPool.POOL_SIZE) {
                        break;
                    }

                    LOGGER.log(Level.INFO, "Waiting timeout: " + TIMEOUT_MS + "ms to get lock.");

                    if (!lock.tryLock(TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                        throw new InterruptedException("Lock timeout exceed...");
                    }

                    LOGGER.log(Level.INFO, "I got lock access!");

                    isRepeated = true;
                    this.acceptClient(connectionPool);
                    lock.unlock();

                    workWithClient(connectionPool);
                }
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARN, e.getMessage());

                connectionPool.decAvailableConnections();
                connectionPool.removeConnection(this);
                connectionPool.hasAvailableConnection();

                LOGGER.log(Level.INFO, "Pool size: " + connectionPool.getActualPoolSize());
                break;
            }
        }
    }

    private void acceptClient(ConnectionPool connectionPool) throws InterruptedException {
        clientSocket = connectionPool.getServerConnection().accept();
        connectionPool.decAvailableConnections();

        LOGGER.log(Level.INFO, "Thread " + index + " has been accepted.");

        if (!connectionPool.hasAvailableConnection() && !isRepeated) {
            int index = ++lastConnectionIndex;

            ClientConnection connection = new ClientConnection(index, lock);
            connectionPool.addFreeConnection(connection);

            connectionPool.incAvailableConnections();
            LOGGER.log(Level.INFO, "Added new thread to pool. Pool size: " + connectionPool.getActualPoolSize());
            connectionPool.hasAvailableConnection();
        }
    }

    private void workWithClient(ConnectionPool connectionPool) {
        Connection connection = new Connection();
        connection.listen(clientSocket, index);

        index = ++lastConnectionIndex;

        connectionPool.incAvailableConnections();
        connectionPool.hasAvailableConnection();
    }
}
