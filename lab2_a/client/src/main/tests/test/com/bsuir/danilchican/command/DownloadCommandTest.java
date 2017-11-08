package test.com.bsuir.danilchican.command;

import com.bsuir.danilchican.command.ConnectCommand;
import com.bsuir.danilchican.command.ICommand;
import com.bsuir.danilchican.exception.CommandNotFoundException;
import com.bsuir.danilchican.exception.WrongCommandFormatException;
import com.bsuir.danilchican.parser.Parser;
import org.junit.Assert;
import org.junit.Test;

public class DownloadCommandTest {

    @Test
    public void download() throws WrongCommandFormatException, CommandNotFoundException {
        final String tokenValue = "127.0.0.1";
        String cmd = "connect -" + ConnectCommand.AvailableToken.IP.getName() + "='" + tokenValue + "'";
        ICommand command = new Parser().handle(cmd);

        boolean actual = command.validateToken(tokenValue, ConnectCommand.AvailableToken.IP.getRegex());
        Assert.assertTrue(actual);

        command.execute();

        cmd = "download -path='d:/spolks/text.txt' -name='d:/spolks/test_2_a.txt'";
        command = new Parser().handle(cmd);
        command.execute();
    }
}
