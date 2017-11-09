package com.bsuir.danilchican.request;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

import static com.bsuir.danilchican.command.DownloadCommand.BUFF_SIZE;
import static com.bsuir.danilchican.command.DownloadCommand.START_TRANSFER;

public class Request {

    /**
     * Logger to getCommand logs.
     */
    static final Logger LOGGER = LogManager.getLogger();

    private boolean isFree = true;
    private int step = 0;

    private SocketChannel channel;
    private SelectionKey key;
    private String cmd;

    private FileInputStream fin;
    private int receivedBytes = 0;

    public Request(SelectionKey key, SocketChannel channel) {
        this.key = key;
        this.channel = channel;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;

        if(isFree()) {
            this.step = 0;
            this.receivedBytes = 0;
            this.fin = null;
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    public void nextStep() {
        step++;
    }

    public void setFile(File file) throws FileNotFoundException {
        this.fin = new FileInputStream(file);
    }

    public void execute(String cmd) throws IOException {
        this.cmd = cmd;

        switch (step) {
            case 1:
                checkStartRequest(); // find "start" in client request
                step++;
                break;
            case 2:
                executeDownload(); // send partial of file
                break;
            default:
                LOGGER.log(Level.ERROR, "Step is incorrect!");
                break;
        }
    }

    private void checkStartRequest() throws IOException {
        if (START_TRANSFER.equals(cmd)) {
            key.interestOps(SelectionKey.OP_WRITE);
        } else {
            setFree(true);
            LOGGER.log(Level.ERROR, START_TRANSFER + " flag not founded...");
        }
    }

    private void executeDownload() throws IOException {
        byte fileContent[] = new byte[BUFF_SIZE];
        int count;

        if ((count = fin.read(fileContent, 0, BUFF_SIZE)) != -1) {
            receivedBytes += count;
            fileContent = Arrays.copyOfRange(fileContent, 0, count);
            ByteBuffer buffToWrite = ByteBuffer.wrap(fileContent);
            channel.write(buffToWrite); // change channel
            LOGGER.log(Level.DEBUG, "Sent " + receivedBytes + " bytes.");
        } else {
            setFree(true);
            LOGGER.log(Level.INFO, "File is transferred.");
        }
    }
}
