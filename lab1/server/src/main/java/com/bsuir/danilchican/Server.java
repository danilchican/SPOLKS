package com.bsuir.danilchican;

import com.bsuir.danilchican.command.HelpCommand;
import com.bsuir.danilchican.command.ICommand;

public class Server {

    public static void main(String[] args) {
        ICommand command = new HelpCommand();
        command.execute();
    }
}
