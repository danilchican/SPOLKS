package com.bsuir.danilchican.command;

import com.bsuir.danilchican.connection.Connection;
import com.bsuir.danilchican.controller.Controller;
import com.bsuir.danilchican.exception.AvailableTokenNotPresentException;
import com.bsuir.danilchican.exception.WrongCommandFormatException;
import com.bsuir.danilchican.util.Printer;
import org.apache.logging.log4j.Level;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

class DownloadCommand extends AbstractCommand {

    private static final String SUCCESS = "success";
    private static final String START_TRANSFER = "start";

    private static final int BUFF_SIZE = 5096;

    DownloadCommand() {
        Arrays.stream(AvailableToken.values()).forEach(t -> availableTokens.put(t.getName(), t.getRegex()));
    }

    /**
     * Execute command.
     */
    @Override
    public void execute() {
        try {
            validateRequired();
            validateTokens();

            Map<String, String> toks = getTokens();

            String firstKey = String.valueOf(toks.keySet().toArray()[0]);
            AvailableToken currentToken = AvailableToken.find(firstKey);

            switch (currentToken) {
                case HELP:
                    executeHelp();
                    break;
                default:
                    executeDownload();
                    break;
            }
        } catch (WrongCommandFormatException | AvailableTokenNotPresentException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
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

    private void validateRequired() throws WrongCommandFormatException {
        Map<String, String> tokens = getTokens();

        if (tokens.size() > 2) {
            throw new WrongCommandFormatException("This command should have only one or two tokens.");
        }

        if (tokens.containsKey(AvailableToken.HELP.getName())) {
            return;
        }

        for (AvailableToken t : AvailableToken.values()) {
            if (t.isRequired()) {
                String value = tokens.get(t.getName());

                if (value == null || value.isEmpty()) {
                    throw new WrongCommandFormatException("'" + t.getName() + "' token required. Check -help.");
                }
            }
        }
    }

    private void executeHelp() {
        Printer.println("Command format:");
        Printer.println("   download -path='path to file' -name='file name' [-help]");
    }

    private void executeDownload() {
        Connection connection = Controller.getInstance().getConnection();

        if (connection != null) {
            if (connection.sendMessage(cmd)) {
                String[] confirmation = connection.receive().split(" ");

                if (SUCCESS.equals(confirmation[0])) {
                    final long fileSize = Long.parseLong(confirmation[1]);
                    LOGGER.log(Level.INFO, "File size: " + fileSize + " bytes");

                    if (connection.sendMessage(START_TRANSFER)) {
                        long receivedBytes = 0;

                        try {
                            FileOutputStream fos = new FileOutputStream(getTokens().get(AvailableToken.NAME.getName()));

                            byte[] buff = new byte[BUFF_SIZE];
                            int count;

                            while ((count = connection.receive(buff)) != -1) {
                                receivedBytes += count;
                                fos.write(Arrays.copyOfRange(buff, 0, count));

                                LOGGER.log(Level.DEBUG, "Received " + receivedBytes + " bytes.");

                                if(receivedBytes == fileSize) {
                                    break;
                                }
                            }

                            fos.close();
                            LOGGER.log(Level.INFO, "File is downloaded. Total size: " + receivedBytes + " bytes.");
                        } catch (IOException e) {
                            LOGGER.log(Level.ERROR, e.getMessage());
                        }
                    }
                }
            }
        } else {
            LOGGER.log(Level.WARN, "You're not connected to server.");
        }
    }

    private enum AvailableToken {
        PATH("path", "^[\\w .-:\\\\]+$", true),
        NAME("name", "^[\\w .-:\\\\]+$", true),
        HELP("help", null, false);

        private String name;
        private String regex;
        private boolean required;

        AvailableToken(String name, String regex, boolean required) {
            this.name = name;
            this.regex = regex;
            this.required = required;
        }

        public static AvailableToken find(String name) throws AvailableTokenNotPresentException {
            for (AvailableToken t : values()) {
                if (t.getName().equals(name)) {
                    return t;
                }
            }

            throw new AvailableTokenNotPresentException("Token '" + name + "' is not available.");
        }

        public String getName() {
            return name;
        }

        public String getRegex() {
            return regex;
        }

        public boolean isRequired() {
            return required;
        }
    }
}
