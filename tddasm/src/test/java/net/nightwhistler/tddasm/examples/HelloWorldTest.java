package net.nightwhistler.tddasm.examples;

import net.nightwhistler.tddasm.mos65xx.Processor;
import net.nightwhistler.tddasm.mos65xx.Program;
import net.nightwhistler.tddasm.screen.TextModeScreen;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloWorldTest {

    @Test
    public void shouldDisplayHelloWorld() {
        Processor processor = new Processor();
        TextModeScreen screen = new TextModeScreen(processor);
        Program helloWorld = HelloWorld.main();
        processor.load(helloWorld);

        processor.run(helloWorld.startAddress());
        assertEquals("             Hello world!               ",
                screen.getLineAsString(12));

    }

}
