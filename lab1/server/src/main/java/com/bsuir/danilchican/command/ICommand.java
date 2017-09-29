package com.bsuir.danilchican.command;

public interface ICommand {

    /**
     * Execute command.
     */
    void execute();

    /**
     * Set command type.
     */
    void setType(CommandType type);

    /**
     * Get command type.
     *
     * @return type
     */
    CommandType getType();
}
