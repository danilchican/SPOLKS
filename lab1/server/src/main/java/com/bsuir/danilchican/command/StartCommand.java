package com.bsuir.danilchican.command;

public class StartCommand extends AbstractCommand {

    /**
     * Execute command.
     */
    @Override
    public void execute() {

    }

    /**
     * Build command instance.
     *
     * @return instance
     */
    @Override
    public ICommand build() {
        return new StartCommand();
    }
}
