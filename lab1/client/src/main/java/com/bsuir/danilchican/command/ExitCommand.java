package com.bsuir.danilchican.command;

import com.bsuir.danilchican.connection.Connection;
import com.bsuir.danilchican.controller.Controller;
import org.apache.logging.log4j.Level;

public class ExitCommand extends AbstractCommand {

    /**
     * Execute command.
     */
    @Override
    public void execute() {
        Connection connection = Controller.getInstance().getConnection();

        if(connection != null) {
            LOGGER.log(Level.WARN, "Connection is opened. Please, close connection to terminate program.");
        } else {
            Controller.getInstance().getKeyboard().wantExit(true);
        }
    }

    /**
     * Build command instance.
     *
     * @return instance
     */
    @Override
    public ICommand build() {
        return new ExitCommand();
    }
}
