package net.nightwhistler.tddasm.mos65xx;

import org.junit.jupiter.api.Test;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;
import static net.nightwhistler.tddasm.mos65xx.Operand.absolute;
import static net.nightwhistler.tddasm.mos65xx.Operand.value;
import static org.junit.jupiter.api.Assertions.*;

class ProgramTest {

    Program testProgram = new Program(
            Operand.absolute(0x8000),
            new ProgramElementsBuilder()
                    .label("load_data") //Address 0x8000
                    .lda(value(0x33))  //lda 2 bytes
                    .cmp(absolute(0x3300))  //cmp 3 bytes
                    .label("check")  //Address 0x8005
                    .bne("load_data") //bne 2 bytes
                    .inx() // 1 byte
                    .jmp("load_data") //jmp 3 bytes
                    .build()
    );


    @Test
    public void testResolveLabel() {
        assertEquals(none(), testProgram.resolveLabel("Non-existant"));
        assertEquals(some(absolute(0x8000)), testProgram.resolveLabel("load_data"));
    }

    @Test
    public void testCompile() {
        byte[] compiledProgram = testProgram.compile();
        assertEquals(11, compiledProgram.length);
    }

}
