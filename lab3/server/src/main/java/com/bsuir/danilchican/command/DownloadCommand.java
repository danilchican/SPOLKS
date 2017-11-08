package com.bsuir.danilchican.command;

import com.bsuir.danilchican.connection.Connection;
import com.bsuir.danilchican.controller.Controller;
import com.bsuir.danilchican.util.SocketBuffer;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

class DownloadCommand extends AbstractCommand {

    private static final String SUCCESS = "success";
    private static final String START_TRANSFER = "start";

    private static final int BUFF_SIZE = 12288;

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
                channel.write(buff);

                SocketBuffer buffer = new SocketBuffer();
                int countBytes;

                // TODO error not wait
                if ((countBytes = channel.read((ByteBuffer) buffer.clear())) < 1) {
                    LOGGER.log(Level.ERROR, "Buffer is clear.");
                    return;
                }

                byte[] tempData = buffer.read(countBytes);
                String cmd = new String(tempData, 0, countBytes);

                if (START_TRANSFER.equals(cmd)) {
                    FileInputStream fin = new FileInputStream(file);

                    int receivedBytes;
                    byte fileContent[] = new byte[BUFF_SIZE];

                    while ((receivedBytes = fin.read(fileContent, 0, BUFF_SIZE)) != -1) {
                        ByteBuffer buffToWrite = ByteBuffer.wrap(fileContent);
                        channel.write(buffToWrite);
                        LOGGER.log(Level.DEBUG, "Sent " + receivedBytes + " bytes.");
                    }

                    LOGGER.log(Level.INFO, "File is transferred.");
                } else {
                    LOGGER.log(Level.ERROR, START_TRANSFER + " flag not founded...");
                }
            } else {
                final String message = "File does not exists or something went wrong.";
                ByteBuffer buff = ByteBuffer.wrap(message.getBytes());
                channel.write(buff);

                LOGGER.log(Level.ERROR, message);
            }
        } else {
            LOGGER.log(Level.WARN, "You're not connected to server.");
        }
    }

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
