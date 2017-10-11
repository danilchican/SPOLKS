package test.com.bsuir.danilchican.command;

import com.bsuir.danilchican.command.ConnectCommand;
import com.bsuir.danilchican.command.ICommand;
import com.bsuir.danilchican.exception.CommandNotFoundException;
import com.bsuir.danilchican.exception.WrongCommandFormatException;
import com.bsuir.danilchican.parser.Parser;
import org.junit.Assert;
import org.junit.Test;

public class ConnectCommandTest {

    @Test
    public void validateTokenSuccess() throws WrongCommandFormatException, CommandNotFoundException {
        final String tokenValue = "8033";
        final String cmd = "connect -" + ConnectCommand.AvailableToken.PORT.getName() + "='" + tokenValue + "'";
        final ICommand command = new Parser().handle(cmd);

        boolean actual = command.validateToken(tokenValue, ConnectCommand.AvailableToken.PORT.getRegex());
        Assert.assertTrue(actual);
    }

    @Test
    public void validateNullToken() throws WrongCommandFormatException, CommandNotFoundException {
        final String cmd = "connect -" + ConnectCommand.AvailableToken.PORT.getName();
        final ICommand command = new Parser().handle(cmd);

        boolean actual = command.validateToken(null, ConnectCommand.SERVER_PORT_REGEX);
        Assert.assertFalse(actual);
    }

    @Test
    public void validateNullTokenAndNullRegex() throws WrongCommandFormatException, CommandNotFoundException {
        final String cmd = "connect -" + ConnectCommand.AvailableToken.PORT.getName();
        final ICommand command = new Parser().handle(cmd);

        boolean actual = command.validateToken(null, null);
        Assert.assertTrue(actual);
    }

    @Test
    public void validateTokensSuccess() throws WrongCommandFormatException, CommandNotFoundException {
        final String tokenValue = "8033";
        final String cmd = "connect -" + ConnectCommand.AvailableToken.PORT.getName() + "='" + tokenValue + "'";
        final ICommand command = new Parser().handle(cmd);

        command.validateTokens();
    }

    @Test(expected = WrongCommandFormatException.class)
    public void validateTokens() throws WrongCommandFormatException, CommandNotFoundException {
        final String cmd = "connect -" + ConnectCommand.AvailableToken.PORT.getName();
        final ICommand command = new Parser().handle(cmd);

        command.validateTokens();
    }
}
