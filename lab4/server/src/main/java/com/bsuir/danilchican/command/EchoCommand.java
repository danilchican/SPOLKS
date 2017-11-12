package com.bsuir.danilchican.command;

import com.bsuir.danilchican.connection.Connection;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.Arrays;

public class EchoCommand extends AbstractCommand {

    EchoCommand() {
        Arrays.stream(AvailableToken.values()).forEach(t -> availableTokens.put(t.getName(), t.getRegex()));
    }

    /**
     * Execute command.
     */
    @Override
    public void execute(Connection connection) {
        try {
            String content = getTokens().get(AvailableToken.CONTENT.getName());

            if (content != null) {
                executeEcho(content, connection);
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
        return new EchoCommand();
    }

    private void executeEcho(String content, Connection connection) throws IOException {
        connection.write(content);
    }

    private enum AvailableToken {
        CONTENT("content", null);

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
