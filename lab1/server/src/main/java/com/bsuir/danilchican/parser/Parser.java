package com.bsuir.danilchican.parser;

import com.bsuir.danilchican.command.ICommand;

public class Parser extends AbstractParser {

    /**
     * Default constructor.
     */
    public Parser() {
        parser = new TokenParser();
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
