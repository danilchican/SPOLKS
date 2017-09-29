package com.bsuir.danilchican.parser;

import com.bsuir.danilchican.command.ICommand;

public interface IParser {

    /**
     * Parse command from String and
     * return the CommandType instance.
     *
     * @param command as string
     * @return command instance
     */
    ICommand parse(String command);
}
