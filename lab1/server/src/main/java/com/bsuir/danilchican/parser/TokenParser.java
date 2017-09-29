package com.bsuir.danilchican.parser;

import com.bsuir.danilchican.command.ICommand;

public class TokenParser extends AbstractParser {

    /**
     * Default constructor.
     */
    public TokenParser() {
        parser = new Parser();
    }

    /**
     * Handle parse text from cmd.
     *
     * @param cmd
     * @return command instance
     */
    @Override
    public ICommand handle(String cmd) {
        return null;
    }
}