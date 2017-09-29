package test.com.bsuir.danilchican.parser;

import com.bsuir.danilchican.command.CommandType;
import com.bsuir.danilchican.command.ICommand;
import com.bsuir.danilchican.parser.IParser;
import com.bsuir.danilchican.parser.Parser;
import org.junit.Assert;
import org.junit.Test;

public class ParserTest {

    @Test
    public void parse() {
        String cmd = "help";
        IParser parser = new Parser();
        ICommand command = parser.parse(cmd);

        Assert.assertEquals(CommandType.HELP, command.getType());
    }
}
