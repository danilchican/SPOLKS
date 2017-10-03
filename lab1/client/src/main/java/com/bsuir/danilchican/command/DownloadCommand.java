package com.bsuir.danilchican.command;

public class DownloadCommand extends AbstractCommand {

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
        return new DownloadCommand();
    }
}
