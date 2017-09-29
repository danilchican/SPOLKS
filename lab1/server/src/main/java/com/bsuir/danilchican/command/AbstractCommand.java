package com.bsuir.danilchican.command;

public abstract class AbstractCommand implements ICommand {

    private CommandType type;

    /**
     * Set command type.
     */
    @Override
    public final void setType(CommandType type) {
        this.type = type;
    }

    /**
     * Return command type;
     *
     * @return type
     */
    @Override
    public final CommandType getType() {
        return type;
    }
}
