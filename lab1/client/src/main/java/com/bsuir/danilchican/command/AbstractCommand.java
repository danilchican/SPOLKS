package com.bsuir.danilchican.command;

import com.bsuir.danilchican.exception.WrongCommandFormatException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class AbstractCommand implements ICommand {

    private Map<String, String> flags;

    List<String> availableFlags;

    /**
     * Default constructor.
     */
    AbstractCommand() {
        flags = new HashMap<>();
        availableFlags = new ArrayList<>();
    }

    /**
     * Check inputted flags.
     */
    @Override
    public final void checkFlags() throws WrongCommandFormatException {
        if (!flags.isEmpty()) {
            for (Map.Entry<String, String> fl : flags.entrySet()) {
                final String key = fl.getKey();

                if (!availableFlags.contains(key)) {
                    throw new WrongCommandFormatException("The command does not contain '" + key + "' flag");
                }
            }
        }
    }

    /**
     * Get all command flags.
     *
     * @return hash map
     */
    @Override
    public final Map<String, String> getFlags() {
        return this.flags;
    }

    /**
     * Put flag to command.
     *
     * @param name
     * @param value
     */
    @Override
    public final void putFlag(String name, String value) {
        this.flags.put(name, value);
    }
}
