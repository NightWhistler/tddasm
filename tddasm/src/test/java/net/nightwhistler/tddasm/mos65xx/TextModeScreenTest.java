package net.nightwhistler.tddasm.mos65xx;

import net.nightwhistler.tddasm.c64.screen.TextModeScreen;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextModeScreenTest {


    @Test
    public void testGetLine() {
        TextModeScreen screen = new TextModeScreen(new Processor());
        assertEquals(40, screen.getLineAsString(0).length());
        assertEquals("", screen.getLineAsString(0).trim());

    }

}
