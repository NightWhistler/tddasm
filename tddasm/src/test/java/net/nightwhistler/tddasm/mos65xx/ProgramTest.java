package net.nightwhistler.tddasm.mos65xx;

import io.vavr.collection.List;
import net.nightwhistler.ByteUtils;
import org.junit.jupiter.api.Test;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;
import static net.nightwhistler.tddasm.mos65xx.Operand.address;
import static net.nightwhistler.tddasm.mos65xx.Operand.value;
import static org.junit.jupiter.api.Assertions.*;

class ProgramTest {

    Program testProgram = new Program(
            Operand.address(0x8000),
            new ProgramBuilder()
                    .label("load_data") //Address 0x8000
                    .lda(value(0x33))  //lda 2 bytes 0xA9 0x33
                    .cmp(address(0x3300))  //cmp 3 bytes  0xCD 0x00 0x33
                    .label("check")  //Address 0x8005
                    .bne("load_data") //bne 2 bytes  0xD0 0xFB (-5)
                    .inx() // 1 byte   // 0xE8
                    .jmp("load_data") //jmp 3 bytes 0x4C 0x00 0x80
                    .rts() //rts 1 byte 0x60
                    .buildElements()

    );

    @Test
    public void testOffsets() {
        assertEquals(address(0x8000), testProgram.addressOfElement(0)); //label
        assertEquals(address(0x8000), testProgram.addressOfElement(1)); //lda
        assertEquals(address(0x8002), testProgram.addressOfElement(2)); //cmp
        assertEquals(address(0x8005), testProgram.addressOfElement(3)); //label
        assertEquals(address(0x8005), testProgram.addressOfElement(4)); //bne
        assertEquals(address(0x8007), testProgram.addressOfElement(5)); //inx
        assertEquals(address(0x8008), testProgram.addressOfElement(6)); //jmp
        assertEquals(address(0x800B), testProgram.addressOfElement(7)); //jmp
    }

    @Test
    public void tesFindElements() {
        assertEquals(List.empty(), testProgram.elementsForLocation(address(0x4000)));
        List<ProgramElement> elements = testProgram.elementsForLocation(address(0x8005));
        assertEquals(2, elements.size());
        assertEquals(new Label("check"), elements.get(0));
        assertEquals(new OperationProvider(OpCode.BNE, new Operand.LabelOperand("load_data", AddressingMode.Relative)),
                elements.get(1));
    }

    public void testResolveLabel() {
        assertEquals(none(), testProgram.resolveLabelAbsolute("Non-existant"));
        assertEquals(some(address(0x8000)), testProgram.resolveLabelAbsolute("load_data"));
        assertEquals(some(address(0x8005)), testProgram.resolveLabelAbsolute("check"));
    }


    @Test
    public void testCompile() {
        byte[] compiledProgram = testProgram.compile();
        assertEquals(12, compiledProgram.length);

        byte[] expected = ByteUtils.bytes(
                0xA9, 0x33, 0xCD, 0x00, 0x33, 0xD0, 0xF9, 0xE8, 0x4C, 0x00, 0x80, 0x60
        );

        assertArrayEquals(expected, compiledProgram);
    }

    @Test
    public void relativeJump() {
        Program tiny = new Program(address(0x4000),
                new ProgramBuilder()
                        .label("start")
                        .ldx(value(0x0A))
                        .dex()
                        .bne("start")
                        .rts()
                        .buildElements()
        );
        byte[] compiledProgram = tiny.compile();
        assertEquals(6, compiledProgram.length);

        byte[] expected = ByteUtils.bytes(
                0xA2, 0x0A, 0xCA, 0xD0, 0xFB, 0x60
        );

        assertArrayEquals(expected, compiledProgram);
    }


}
