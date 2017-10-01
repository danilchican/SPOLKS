package com.bsuir.danilchican.command;

public class ExitCommand extends AbstractCommand {

    /**
     * Execute command.
     */
    @Override
    public void execute() {
        // TODO implement
    }

    /**
     * Build command instance.
     *
     * @return instance
     */
    @Override
    public ICommand build() {
        return new ExitCommand();
    }
}
