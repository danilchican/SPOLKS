package com.bsuir.danilchican.command;

import com.bsuir.danilchican.connection.Connection;
import com.bsuir.danilchican.controller.Controller;
import com.bsuir.danilchican.util.Cache;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.util.Arrays;
import java.util.Map;

class DownloadCommand extends AbstractCommand {

    private static final String SUCCESS = "success";
    private static final String START_TRANSFER = "start";

    private static final int BUFF_SIZE = 4096;
    private static int receivedBytes = 0;

    Cache cache = new Cache();

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
                connection.write(SUCCESS + " " + fileSize);

                if (START_TRANSFER.equals(connection.read())) {
                    FileInputStream fin = new FileInputStream(file);

                    receivedBytes = 0;

                    /* Start transfer file */

                    int cacheIndex = 1;

                    LOGGER.log(Level.INFO, "File transter started.");
                    do {
                        byte indexPacket = 1;

                        byte[] fileContent = new byte[BUFF_SIZE + 1];
                        int onceCacheSize = 0;
                        int onceReceivingCount;

                        while ((onceReceivingCount = fin.read(fileContent, 1, BUFF_SIZE)) != -1 && !cache.isFull()) {
                            fileContent[0] = indexPacket;
                            onceCacheSize += onceReceivingCount;

                            cache.add(indexPacket, Arrays.copyOfRange(fileContent, 0, onceReceivingCount + 1));
                            indexPacket++;

                            if (indexPacket > 20) {
                                indexPacket = 1;
                            }

                            fileContent = new byte[BUFF_SIZE + 1];
                        }


                        sendClientCache(onceCacheSize, cacheIndex);
                        fin = new FileInputStream(file);
                        fin.skip(receivedBytes);

                        cacheIndex++;
                    } while (receivedBytes < fileSize);

                    /* End transfer file */

                    LOGGER.log(Level.INFO, "File is transferred.");
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

    private void sendClientCache(int oneCacheSize, int cacheIndex) throws IOException {
        Connection connection = Controller.getInstance().getConnection();
        //LOGGER.log(Level.INFO, "Starting to send cache " + cacheIndex + " to client.");

        for (Map.Entry<Byte, byte[]> entry : cache.get().entrySet()) {
            connection.write(entry.getValue(), entry.getValue().length);
            //LOGGER.log(Level.DEBUG, "Sent cache item[" + entry.getKey() + "] = " + entry.getValue().length + " bytes.");

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARN, "Error: " + e.getMessage());
            }
        }

        String resultClientCache = connection.read();

        if (SUCCESS.equals(resultClientCache)) {
            ///LOGGER.log(Level.INFO, "Cache " + cacheIndex + " sent successfully. Size: " + oneCacheSize);
            cache.clear();
            receivedBytes += oneCacheSize;
        } else {
           // LOGGER.log(Level.ERROR, "Trying to send cache " + cacheIndex + " once again.");
            sendClientCache(oneCacheSize, cacheIndex);
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
