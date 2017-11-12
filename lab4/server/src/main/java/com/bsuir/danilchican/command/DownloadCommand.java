package com.bsuir.danilchican.command;

import com.bsuir.danilchican.connection.Connection;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
    public void execute(Connection connection) {
        try {
            String path = getTokens().get(AvailableToken.PATH.getName());

            if (path != null) {
                executeDownload(path, connection);
            }
        } catch (IOException | InterruptedException e) {
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

    private void executeDownload(String path, Connection connection) throws IOException, InterruptedException {
        if (connection != null) {
            File file = new File(path);

            final long fileSize = file.length();

            if (file.exists() && !file.isDirectory()) {
                connection.write(SUCCESS + " " + fileSize);

                if (START_TRANSFER.equals(connection.read())) {
                    FileInputStream fin = new FileInputStream(file);

                    int receivedBytes;
                    byte fileContent[] = new byte[BUFF_SIZE];

                    Date start = new Date();

                    while ((receivedBytes = fin.read(fileContent, 0, BUFF_SIZE)) != -1) {
                        connection.write(fileContent, receivedBytes);
                        Thread.sleep(1);
                        LOGGER.log(Level.DEBUG, "Sent " + receivedBytes + " bytes.");
                    }

                    Date end = new Date();
                    long resultTime = end.getTime() - start.getTime();

                    LOGGER.log(Level.INFO, "File is transferred.");
                    long resultTimeInSeconds = TimeUnit.SECONDS.convert(resultTime, TimeUnit.MILLISECONDS);
                    LOGGER.log(Level.INFO, "Transfer time: " + ((resultTimeInSeconds > 0) ? resultTimeInSeconds + "s" : resultTime + "ms"));
                } else {
                    LOGGER.log(Level.ERROR, START_TRANSFER + " flag not founded...");
                }
            } else {
                final String message = "File does not exists or something went wrong.";
                connection.write(message);
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
