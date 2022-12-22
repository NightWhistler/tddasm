package net.nightwhistler.tddasm.screen;

import net.nightwhistler.tddasm.c64.screen.ScreenCode;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ScreenCodeTest {
    @Test
    public void testConversion() {
        String originalText = "ABCDabcd!@#";

        byte[] ascii = originalText.getBytes(StandardCharsets.US_ASCII);
        byte[] screenCodes = ScreenCode.toScreenCodes(ascii);

        String parsed = new String(ScreenCode.fromScreenCodes(screenCodes));

        assertEquals(originalText, parsed);
    }

}
