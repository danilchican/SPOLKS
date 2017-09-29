package com.bsuir.danilchican.command;

import java.util.Map;

public interface ICommand {

    /**
     * Execute command.
     */
    void execute();

    /**
     * Put flag to command.
     *
     * @param name
     * @param value
     */
    void putFlag(String name, String value);

    /**
     * Get all command flags.
     *
     * @return hash map
     */
    Map<String, String> getFlags();
}
