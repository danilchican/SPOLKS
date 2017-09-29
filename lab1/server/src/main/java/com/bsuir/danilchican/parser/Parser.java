package com.bsuir.danilchican.parser;

import com.bsuir.danilchican.command.CommandType;
import com.bsuir.danilchican.command.ICommand;
import com.bsuir.danilchican.exception.CommandNotFoundException;
import com.bsuir.danilchican.exception.WrongCommandFormatException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser extends AbstractParser {

    private static final String CMD_COMMON_REGEX = "^([a-z]+)( -[a-z]+((?==)=[A-Za-z\\d]+)*)*$";
    private static final int COMMAND_GROUP_INDEX = 1;

    /**
     * Handle parse text from cmd.
     *
     * @param cmd
     * @return command instance
     */
    @Override
    public ICommand handle(String cmd) throws WrongCommandFormatException, CommandNotFoundException {
        Pattern pattern = Pattern.compile(CMD_COMMON_REGEX);
        Matcher matcher = pattern.matcher(cmd);

        if(!matcher.find()) {
            throw new WrongCommandFormatException();
        }

        final String commandName = matcher.group(COMMAND_GROUP_INDEX);

        if(CommandType.hasCommand(commandName)) {
            return new TokenParser(commandName).handle(cmd);
        }

        throw new CommandNotFoundException("Cannot find command by name[=" + commandName + "]");
    }
}
