package com.bsuir.danilchican.command;

import com.bsuir.danilchican.exception.WrongCommandFormatException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class AbstractCommand implements ICommand {

    private Map<String, String> tokens;

    List<String> availableTokens;

    /**
     * Default constructor.
     */
    AbstractCommand() {
        tokens = new HashMap<>();
        availableTokens = new ArrayList<>();
    }

    /**
     * Verify inputted tokens.
     */
    @Override
    public final void verifyTokens() throws WrongCommandFormatException {
        if (!tokens.isEmpty()) {
            for (Map.Entry<String, String> fl : tokens.entrySet()) {
                final String key = fl.getKey();

                if (!availableTokens.contains(key)) {
                    throw new WrongCommandFormatException("The command does not contain '" + key + "' token");
                }
            }
        }
    }

    /**
     * Get all command tokens.
     *
     * @return hash map
     */
    public final Map<String, String> getTokens() {
        return this.tokens;
    }

    /**
     * Put token to command.
     *
     * @param name
     * @param value
     */
    @Override
    public final void putToken(String name, String value) {
        this.tokens.put(name, value);
    }
}
