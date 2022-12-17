package net.nightwhistler.tddasm.mos65xx;

import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import static net.nightwhistler.ByteUtils.bytes;
import static net.nightwhistler.tddasm.mos65xx.Operand.address;
import static net.nightwhistler.tddasm.mos65xx.Operand.value;
import static org.junit.jupiter.api.Assertions.*;

class ProgramElementsBuilderTest {

    @Test
    public void testSingleInstruction() {
        var elements = new ProgramElementsBuilder()
                .lda(value(0x03))
                .build();

        assertEquals(1, elements.size());
        assertEquals(new Operation(OpCode.LDA, new Operand.ByteValue((byte) 0x03)), elements.get(0));
    }

    @Test
    public void testProgram() {
        var elements = new ProgramElementsBuilder()
                .lda(value(0x03))
                .label("some_label")
                .iny()
                .jsr("print_y")
                .jmp(address(0x4567))
                .build();

        assertEquals(5, elements.size());
        assertEquals(List.of(
                new Operation(OpCode.LDA, new Operand.ByteValue((byte) 0x03)),
                new Label("some_label"),
                new Operation(OpCode.INY, Operand.noValue()),
                new Operation(OpCode.JSR, new Operand.LabelOperand("print_y")),
                new Operation(OpCode.JMP, new Operand.TwoByteAddress(AddressingMode.AbsoluteAddress, (byte) 0x67, (byte) 0x45))
        ), elements);
    }

    @Test
    public void testText() {
        var elements = new ProgramElementsBuilder()
                .text("ABC!")
                .build();

        var dataElement = (Data) elements.head();
        assertEquals(4, dataElement.length());
        assertArrayEquals(bytes(65, 66, 67, 33), dataElement.bytes());
    }

    @Test
    public void testPETSCII() {
        var elements = new ProgramElementsBuilder()
                .screenCodes("abcABC!")
                .build();

        var dataElement = (Data) elements.head();
        assertEquals(7, dataElement.length());
        assertArrayEquals(bytes(01, 02, 03, 65, 66, 67, 33), dataElement.bytes());
    }

}
