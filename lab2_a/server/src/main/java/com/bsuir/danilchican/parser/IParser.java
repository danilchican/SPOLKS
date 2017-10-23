package com.bsuir.danilchican.parser;

import com.bsuir.danilchican.command.ICommand;
import com.bsuir.danilchican.exception.CommandNotFoundException;
import com.bsuir.danilchican.exception.WrongCommandFormatException;

public interface IParser {

    /**
     * Parse command from String and
     * return the CommandType instance.
     *
     * @param command as string
     * @return command instance
     */
    ICommand parse(String command) throws WrongCommandFormatException, CommandNotFoundException;
}
