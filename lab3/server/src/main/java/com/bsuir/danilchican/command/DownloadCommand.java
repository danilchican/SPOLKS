package com.bsuir.danilchican.command;

import com.bsuir.danilchican.connection.Connection;
import com.bsuir.danilchican.controller.Controller;
import com.bsuir.danilchican.util.SocketBuffer;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.bsuir.danilchican.connection.ClientSession.GLOBAL_USER_REQUEST;

public class DownloadCommand extends AbstractCommand {

    private static final String SUCCESS = "success";
    public static final String START_TRANSFER = "start";

    DownloadCommand() {
        Arrays.stream(AvailableToken.values()).forEach(t -> availableTokens.put(t.getName(), t.getRegex()));
    }

    /**
     * Execute command.
     */
    @Override
    public void execute() {
        try {
            String path = getTokens().get(AvailableToken.PATH.getName());

            if (path != null) {
                executeDownload(path);
            }
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Error: " + e.getMessage());
        }
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

    private void executeDownload(String path) throws IOException {
        Connection connection = Controller.getInstance().getConnection();

        if (connection != null) {
            File file = new File(path);

            final long fileSize = file.length();

            if (file.exists() && !file.isDirectory()) {
                String message = SUCCESS + " " + fileSize;
                ByteBuffer buff = ByteBuffer.wrap(message.getBytes());

                GLOBAL_USER_REQUEST.setFile(file);
                GLOBAL_USER_REQUEST.setFree(false);
                GLOBAL_USER_REQUEST.nextStep();

                channel.write(buff);
            } else {
                final String message = "File does not exists or something went wrong.";
                ByteBuffer buff = ByteBuffer.wrap(message.getBytes());
                channel.write(buff);

                GLOBAL_USER_REQUEST.setFree(true);
                LOGGER.log(Level.ERROR, message);
            }
        } else {
            LOGGER.log(Level.WARN, "You're not connected to server.");
        }
    }

    public static final int BUFF_SIZE = 236_608;

    private enum AvailableToken {
        PATH("path", "^[\\w .-:\\\\]+$"),
        NAME("name", "^[\\w .-:\\\\]+$");

        private String name;
        private String regex;

        AvailableToken(String name, String regex) {
            this.name = name;
            this.regex = regex;
        }

        public String getName() {
            return name;
        }

        public String getRegex() {
            return regex;
        }
    }
}
