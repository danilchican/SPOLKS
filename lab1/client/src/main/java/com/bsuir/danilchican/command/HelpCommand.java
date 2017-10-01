package com.bsuir.danilchican.command;

import com.bsuir.danilchican.util.Printer;

import java.util.HashMap;

public class HelpCommand extends AbstractCommand {

    /**
     * Execute command.
     */
    @Override
    public void execute() {
        HashMap<String, String> commands = new HashMap<>();

        for (CommandType type : CommandType.values()) {
            commands.put(type.getName(), type.getDescription());
        }

        Printer.println("The most commonly used client commands are:");
        commands.forEach((k, v) -> {
            Printer.println("  " + k + " - " + v);
        });
    }

    /**
     * Build command instance.
     *
     * @return instance
     */
    @Override
    public ICommand build() {
        return new HelpCommand();
    }
}
