package com.bsuir.danilchican.command;

import com.bsuir.danilchican.exception.WrongCommandFormatException;
import com.bsuir.danilchican.util.SocketBuffer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractCommand implements ICommand {

    Map<String, String> availableTokens;
    private Map<String, String> tokens;

    private SocketBuffer buffer;

    /**
     * Logger to getCommand logs.
     */
    static final Logger LOGGER = LogManager.getLogger();

    /**
     * Default constructor.
     */
    AbstractCommand() {
        tokens = new HashMap<>();
        availableTokens = new HashMap<>();
    }

    /**
     * Verify inputted tokens.
     */
    @Override
    public final void verifyTokens() throws WrongCommandFormatException {
        LOGGER.log(Level.DEBUG, "Tokens: " + tokens);

        if (!tokens.isEmpty()) {
            for (Map.Entry<String, String> fl : tokens.entrySet()) {
                final String key = fl.getKey();

                if (!availableTokens.containsKey(key)) {
                    throw new WrongCommandFormatException("The command does not contain '" + key + "' token.");
                }
            }
        }
    }

    @Override
    public void setBuffer(SocketBuffer buffer) {
        this.buffer = buffer;
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
