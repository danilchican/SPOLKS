package com.bsuir.danilchican.command;

import com.bsuir.danilchican.exception.WrongCommandFormatException;
import org.apache.logging.log4j.Level;

public class ConnectCommand extends AbstractCommand {

    public static final String AVAILABLE_SERVER_IP = "ip";
    public static final String SERVER_IP_REGEX = "^(\\d{1,3}\\.){3}\\d{1,3}$";

    ConnectCommand() {
        availableTokens.put(AVAILABLE_SERVER_IP, SERVER_IP_REGEX);
    }

    /**
     * Execute command.
     */
    @Override
    public void execute() {
        try {
            validateTokens();
            // TODO implement
        } catch (WrongCommandFormatException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
    }

    /**
     * Build command instance.
     *
     * @return instance
     */
    @Override
    public ICommand build() {
        return new ConnectCommand();
    }
}
