package com.bsuir.danilchican.util;

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
        return new Parser().parse(cmd);
    }

    public void wantExit(boolean want) {
        isWantExit = want;
    }

    /**
     * Check if a user select exit option.
     *
     * @return boolean
     */
    public boolean enteredExit() {
        return isWantExit;
    }
}
