package test.com.bsuir.danilchican.parser;

import com.bsuir.danilchican.command.HelpCommand;
import com.bsuir.danilchican.command.ICommand;
import com.bsuir.danilchican.exception.CommandNotFoundException;
import com.bsuir.danilchican.exception.WrongCommandFormatException;
import com.bsuir.danilchican.parser.IParser;
import com.bsuir.danilchican.parser.Parser;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

public class ParserTest {

    private static final String cmd = "help";
    private static IParser parser;
    private ICommand command;

    @BeforeClass
    public static void setUp() {
        parser = new Parser();
    }

    @Test
    public void parseTest() throws WrongCommandFormatException, CommandNotFoundException {
        command = parser.parse(cmd);

        assertThat(command, instanceOf(HelpCommand.class));
    }
}
