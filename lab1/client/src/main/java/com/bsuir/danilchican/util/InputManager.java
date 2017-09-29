package com.bsuir.danilchican.util;

import com.bsuir.danilchican.command.ExitCommand;
import com.bsuir.danilchican.command.ICommand;
import com.bsuir.danilchican.exception.CommandNotFoundException;
import com.bsuir.danilchican.exception.WrongCommandFormatException;
import com.bsuir.danilchican.parser.Parser;

import java.util.Scanner;

public class InputManager {

    private Scanner scanner;

    private boolean isWantExit;

    public InputManager() {
        scanner = new Scanner(System.in);
        isWantExit = false;
    }

    /**
     * Get command from user's input.
     *
     * @return command interface
     * @throws WrongCommandFormatException
     * @throws CommandNotFoundException
     */
    public ICommand getCommand() throws WrongCommandFormatException, CommandNotFoundException {
        String cmd = scanner.nextLine();
        ICommand command = new Parser().parse(cmd);

        if(command instanceof ExitCommand) {
            isWantExit = true;
        }

        return command;
    }

    /**
     * Check if a user want to exit.
     *
     * @return boolean
     */
    public boolean wantExit() {
        return isWantExit;
    }
}
