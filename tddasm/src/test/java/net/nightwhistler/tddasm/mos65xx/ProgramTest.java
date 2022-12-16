package net.nightwhistler.tddasm.mos65xx;

import io.vavr.collection.List;
import net.nightwhistler.ByteUtils;
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
                    .lda(value(0x33))  //lda 2 bytes 0xA9 0x33
                    .cmp(absolute(0x3300))  //cmp 3 bytes  0xCD 0x00 0x33
                    .label("check")  //Address 0x8005
                    .bne("load_data") //bne 2 bytes  0xD0 0xFB (-5)
                    .inx() // 1 byte   // 0xE8
                    .jmp("load_data") //jmp 3 bytes 0x4C 0x00 0x80
                    .rts() //rts 1 byte 0x60
                    .build()

    );

    @Test
    public void testOffsets() {
        assertEquals(absolute(0x8000), testProgram.addressOfElement(0)); //label
        assertEquals(absolute(0x8000), testProgram.addressOfElement(1)); //lda
        assertEquals(absolute(0x8002), testProgram.addressOfElement(2)); //cmp
        assertEquals(absolute(0x8005), testProgram.addressOfElement(3)); //label
        assertEquals(absolute(0x8005), testProgram.addressOfElement(4)); //bne
        assertEquals(absolute(0x8007), testProgram.addressOfElement(5)); //inx
        assertEquals(absolute(0x8008), testProgram.addressOfElement(6)); //jmp
        assertEquals(absolute(0x800B), testProgram.addressOfElement(7)); //jmp
    }

    @Test
    public void tesFindElements() {
        assertEquals(List.empty(), testProgram.elementsForLocation(absolute(0x4000)));
        List<ProgramElement> elements = testProgram.elementsForLocation(absolute(0x8005));
        assertEquals(2, elements.size());
        assertEquals(new Label("check"), elements.get(0));
        assertEquals(new Operation(OpCode.BNE, new Operand.LabelOperand("load_data", true)),
                elements.get(1));
    }

    public void testResolveLabel() {
        assertEquals(none(), testProgram.resolveLabelAbsolute("Non-existant"));
        assertEquals(some(absolute(0x8000)), testProgram.resolveLabelAbsolute("load_data"));
        assertEquals(some(absolute(0x8005)), testProgram.resolveLabelAbsolute("check"));
    }


    @Test
    public void testCompile() {
        byte[] compiledProgram = testProgram.compile();
        assertEquals(12, compiledProgram.length);

        byte[] expected = ByteUtils.bytes(
                0xA9, 0x33, 0xCD, 0x00, 0x33, 0xD0, 0xFB, 0xE8, 0x4C, 0x00, 0x80, 0x60
        );

        assertArrayEquals(expected, compiledProgram);
    }

}
