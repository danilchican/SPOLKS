package com.bsuir.danilchican.command;

import com.bsuir.danilchican.exception.WrongCommandFormatException;

import java.util.Map;

public interface ICommand {

    /**
     * Execute command.
     */
    void execute();

    /**
     * Put token to command.
     *
     * @param name
     * @param value
     */
    void putToken(String name, String value);

    /**
     * Get all command tokens.
     *
     * @return hash map
     */
    Map<String, String> getTokens();

    /**
     * Verify inputted tokens.
     */
    void verifyTokens() throws WrongCommandFormatException;
}
