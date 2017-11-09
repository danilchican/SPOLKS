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
        // String IP = "192.168.10.10";
        String IP = "127.0.0.1";
        String cmd = "connect -" + ConnectCommand.AvailableToken.IP.getName() + "='" + IP + "'";
        ICommand command = new Parser().handle(cmd);

        boolean actual = command.validateToken(IP, ConnectCommand.AvailableToken.IP.getRegex());
        Assert.assertTrue(actual);

        command.execute();
        cmd = "download -path='d:/allin.zip' -name='d:/spolks/test_3_lab_first.zip'";
//        cmd = "download -path='d:/test.rar' -name='d:/spolks/test_1_big_final.rar'";
        command = new Parser().handle(cmd);
        command.execute();
    }
}
