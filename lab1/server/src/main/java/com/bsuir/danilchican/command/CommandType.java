package com.bsuir.danilchican.command;

public enum CommandType {
    HELP("help", "Display help information about available commands");

    private String commandName;

    private String description;

    /**
     * Constructor.
     *
     * @param commandName
     */
    CommandType(String commandName, String description) {
        this.commandName = commandName;
        this.description = description;
    }

    /**
     * Get command name as String.
     *
     * @return command name
     */
    public String getName() {
        return commandName;
    }

    /**
     * Get command description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }
}
