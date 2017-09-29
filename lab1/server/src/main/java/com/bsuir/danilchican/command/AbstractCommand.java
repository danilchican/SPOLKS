package com.bsuir.danilchican.command;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractCommand implements ICommand {

    private Map<String, String> flags;

    /**
     * Default constructor.
     */
    AbstractCommand() {
        flags = new HashMap<>();
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
