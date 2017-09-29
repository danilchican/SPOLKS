package com.bsuir.danilchican.command;

import com.bsuir.danilchican.util.Printer;

public class ConnectCommand extends AbstractCommand {

    private static final String AVAILABLE_SERVER_IP = "ip";

    ConnectCommand() {
        availableTokens.add(AVAILABLE_SERVER_IP);
    }

    /**
     * Execute command.
     */
    @Override
    public void execute() {
        Printer.println(getTokens().toString());
        // TODO implement
    }
}
