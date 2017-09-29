package com.bsuir.danilchican.parser;

import com.bsuir.danilchican.command.ICommand;
import com.bsuir.danilchican.exception.CommandNotFoundException;
import com.bsuir.danilchican.exception.WrongCommandFormatException;

abstract class AbstractParser implements IParser {

    /**
     * Handle parse text from cmd.
     *
     * @param cmd
     * @return command instance
     */
    public abstract ICommand handle(String cmd) throws WrongCommandFormatException, CommandNotFoundException;

    /**
     * Chain the data by handlers.
     *
     * @param cmd
     */
    public ICommand parse(String cmd) throws WrongCommandFormatException, CommandNotFoundException {
        return handle(cmd);
    }
}
