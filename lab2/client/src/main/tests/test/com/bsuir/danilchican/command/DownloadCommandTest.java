package test.com.bsuir.danilchican.command;

import com.bsuir.danilchican.command.DownloadCommand;
import com.bsuir.danilchican.command.ICommand;
import com.bsuir.danilchican.command.InitCommand;
import com.bsuir.danilchican.exception.CommandNotFoundException;
import com.bsuir.danilchican.exception.WrongCommandFormatException;
import com.bsuir.danilchican.parser.Parser;
import org.junit.Test;

public class DownloadCommandTest {

    @Test
    public void downloadTest() throws WrongCommandFormatException, CommandNotFoundException {
        final String tokenPort = "8033";
        final String tokenPath = "d:/psp_lab2.rar";
        final String tokenName = "d:/down.rar";

        final String initCmd = "init -" + InitCommand.AvailableToken.PORT.getName() + "='" + tokenPort + "'";
        final String downloadCmd = "download -" + DownloadCommand.AvailableToken.PATH.getName() + "='" + tokenPath + "' -"
                + DownloadCommand.AvailableToken.NAME.getName() + "='" + tokenName + "'";

        ICommand command = new Parser().handle(initCmd);
        command.execute();

        command = new Parser().handle(downloadCmd);
        command.execute();
    }
}
