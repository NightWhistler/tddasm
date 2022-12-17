package net.nightwhistler.tddasm.examples;

import net.nightwhistler.tddasm.mos65xx.Processor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class HelloWorldTest {

    @Test
    public void testHelloWorldCompiles() {
        try {
            HelloWorld.main().compile();
        } catch (Exception e) {
            fail("Should compile without error", e);
        }
    }

//    @Test
    public void testShouldRun() {
        Processor processor = new Processor();
        processor.load(HelloWorld.main());

        fail("Unfinished test");
    }
}
