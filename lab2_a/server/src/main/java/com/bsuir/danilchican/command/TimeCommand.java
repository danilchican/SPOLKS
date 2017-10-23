package com.bsuir.danilchican.command;

import com.bsuir.danilchican.connection.Connection;
import com.bsuir.danilchican.controller.Controller;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class TimeCommand extends AbstractCommand {

    private static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

    /**
     * Execute command.
     */
    @Override
    public void execute() {
        Connection connection = Controller.getInstance().getConnection();

        if(connection != null) {
            try {
                DateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
                Date date = new Date();

                connection.write(dateFormat.format(date));
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, "Error: " + e.getMessage());
            }
        }
    }

    /**
     * Build command instance.
     *
     * @return instance
     */
    @Override
    public ICommand build() {
        return new TimeCommand();
    }
}
