package net.nightwhistler.tddasm.examples;

import net.nightwhistler.tddasm.c64.kernal.Kernal;
import net.nightwhistler.tddasm.mos65xx.Processor;
import net.nightwhistler.tddasm.mos65xx.Program;
import net.nightwhistler.tddasm.c64.screen.TextModeScreen;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloWorldTest {

    @Test
    public void shouldDisplayHelloWorld() {
        Processor processor = new Processor();
        TextModeScreen screen = new TextModeScreen(processor);
        Program helloWorld = HelloWorld.usingPureASM();
        processor.load(helloWorld);

        processor.run(helloWorld.startAddress());
        assertEquals("             Hello world!               ",
                screen.getLineAsString(12));

    }

    @Test
    public void shouldDisplayHelloWorldUsingKernal() {
        Processor processor = new Processor();
        Kernal.registerKernalRoutines(processor);

        TextModeScreen screen = new TextModeScreen(processor);
        Program helloWorld = HelloWorld.usingKernal();
        processor.load(helloWorld);

        processor.run(helloWorld.startAddress());
        assertEquals("HELLO WORLD",
                screen.getLineAsString(0).trim());

    }

}
