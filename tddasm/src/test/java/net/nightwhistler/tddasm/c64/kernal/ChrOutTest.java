package net.nightwhistler.tddasm.c64.kernal;

import net.nightwhistler.tddasm.c64.screen.TextModeScreen;
import net.nightwhistler.tddasm.mos65xx.Operand;
import net.nightwhistler.tddasm.mos65xx.Processor;
import net.nightwhistler.tddasm.mos65xx.Program;
import net.nightwhistler.tddasm.mos65xx.ProgramBuilder;
import org.junit.jupiter.api.Test;

import static net.nightwhistler.tddasm.c64.kernal.ChrOut.CHROUT_ADDRESS;
import static net.nightwhistler.tddasm.mos65xx.Operand.value;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ChrOutTest {

    @Test
    public void testChrOut() {

        Processor processor = new Processor();
        processor.registerJavaRoutine(new ChrOut());

        TextModeScreen screen = new TextModeScreen(processor);

        Program testProgram = new ProgramBuilder()
                .lda(value('a'))
                .jsr(CHROUT_ADDRESS)
                .lda(value('b'))
                .jsr(CHROUT_ADDRESS)
                .buildProgram();

        processor.load(testProgram);
        processor.run(testProgram.startAddress());

        assertEquals("ab", screen.getLineAsString(0).trim());
    }

}
