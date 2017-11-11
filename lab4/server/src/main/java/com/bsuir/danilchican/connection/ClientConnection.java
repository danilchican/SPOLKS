package com.bsuir.danilchican.connection;

import com.bsuir.danilchican.pool.ConnectionPool;

import static com.bsuir.danilchican.pool.ConnectionPool.lastConnectionIndex;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ClientConnection implements Runnable {

    /**
     * Logger to write logs.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int TIMEOUT_MS = 500; // ms
    private final int index;
    private boolean isRepeated = false;

    private ReentrantLock lock;

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

    private void acceptClient(ConnectionPool connectionPool) throws InterruptedException {
        connectionPool.decAvailableConnections();

        if (!connectionPool.hasAvailableConnection() && !isRepeated) {
            int index = ++lastConnectionIndex;

            ClientConnection connection = new ClientConnection(index, lock);
            connectionPool.addFreeConnection(connection);

            connectionPool.incAvailableConnections();
            LOGGER.log(Level.INFO, "Added new available connection. Pool size: " + connectionPool.getActualPoolSize());
        }

        // TODO accept TCP here
        TimeUnit.SECONDS.sleep(1);
        LOGGER.log(Level.DEBUG, "ClientConnection " + index + " captured lock.");
    }

    private void execute(ConnectionPool connectionPool) {
        try {
            // TODO execute client commands here
            LOGGER.log(Level.INFO, "Execute client " + index + " commands.");
            TimeUnit.SECONDS.sleep(2);
            // TODO client commands
        } catch (InterruptedException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            ConnectionPool connectionPool = ConnectionPool.getInstance();

            try {
                LOGGER.log(Level.INFO, "ClientConnection " + index + " started.");

                lock.lock();
                this.acceptClient(connectionPool);
            } catch (InterruptedException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            } finally {
                lock.unlock();
            }

            execute(connectionPool);

            try {
                while (true) {
                    if (connectionPool.getActualPoolSize() <= ConnectionPool.POOL_SIZE) {
                        break;
                    }

                    LOGGER.log(Level.INFO, "Waiting timeout: " + TIMEOUT_MS + "ms to get lock.");

                    if (!lock.tryLock(TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                        throw new InterruptedException("Client " + index + ": can't get lock access. Timeout exceed.");
                    }

                    LOGGER.log(Level.INFO, "I got lock access!");

                    isRepeated = true;
                    this.acceptClient(connectionPool);
                    lock.unlock();

                    execute(connectionPool);
                }
            } catch (InterruptedException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
                connectionPool.removeConnection(this);
                LOGGER.log(Level.INFO, "Finished ClientConnection " + index + ". Pool size: " + connectionPool.getActualPoolSize());
                break;
            }
        }
    }
}
