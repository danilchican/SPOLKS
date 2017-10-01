package com.bsuir.danilchican.controller;

import com.bsuir.danilchican.command.ICommand;
import com.bsuir.danilchican.connection.Connection;
import com.bsuir.danilchican.exception.CommandNotFoundException;
import com.bsuir.danilchican.exception.WrongCommandFormatException;
import com.bsuir.danilchican.util.InputManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.Pack200;

public final class Controller {

    /**
     * Logger to getCommand logs.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Instance of Controller.
     */
    private static Controller instance;

    /**
     * Instance creation flag.
     */
    private static AtomicBoolean createdInstance = new AtomicBoolean(false);

    /**
     * Reentrant lock.
     */
    private static ReentrantLock lock = new ReentrantLock();

    private Connection connection;

    /**
     * Get instance of controller.
     *
     * @return instance
     */
    public static Controller getInstance() {
        if (!createdInstance.get()) {
            try {
                lock.lock();

                if (instance == null) {
                    instance = new Controller();
                    createdInstance.set(true);

                    LOGGER.log(Level.INFO, Controller.class.getName() + " instance created!");
                }
            } finally {
                lock.unlock();
            }
        }

        return instance;
    }

    /**
     * Start working controller.
     */
    public void work() {
        InputManager keyboard = new InputManager();

        do {
            try {
                ICommand command = keyboard.getCommand();
                command.execute();
            } catch (WrongCommandFormatException | CommandNotFoundException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
        } while (!keyboard.wantExit());
    }

    /**
     * Set connection.
     *
     * @param c connection instance
     */
    public void setConnection(Connection c) {
        if(this.connection == null) {
            this.connection = c;
        } else {
            LOGGER.log(Level.WARN, "Can't create new connection. It already exist.");
        }
    }

    /**
     * Get opened connection;
     *
     * @return connection
     */
    public Connection getConnection() {
        return connection;
    }
}
