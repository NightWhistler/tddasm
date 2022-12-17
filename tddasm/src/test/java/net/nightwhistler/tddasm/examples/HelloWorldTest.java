package net.nightwhistler.tddasm.examples;

import net.nightwhistler.tddasm.mos65xx.Program;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HelloWorldTest {

    @Test
    public void testHelloWorldCompiles() {
        try {
            HelloWorld.main().compile();
        } catch (Exception e) {
            Assertions.fail("Should compile without error", e);
        }
    }
}
