package net.nightwhistler.tddasm.screen;

import io.vavr.collection.List;
import net.nightwhistler.tddasm.mos65xx.Data;
import net.nightwhistler.tddasm.mos65xx.ProgramBuilder;
import net.nightwhistler.tddasm.mos65xx.ProgramElement;
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
