package com.bsuir.danilchican.parser;

import com.bsuir.danilchican.command.ICommand;

abstract class AbstractParser implements IParser {

    /**
     * Abstract parser for all protected parsers.
     */
    AbstractParser parser;

    /**
     * Handle parse text from cmd.
     *
     * @param cmd
     * @return command instance
     */
    public abstract ICommand handle(String cmd);

    /**
     * Chain the data by handlers.
     *
     * @param cmd
     */
    public ICommand parse(String cmd) {
        return handle(cmd);
    }
}
