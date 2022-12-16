package net.nightwhistler.tddasm.mos65xx;

import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import static net.nightwhistler.tddasm.mos65xx.Operand.absolute;
import static net.nightwhistler.tddasm.mos65xx.Operand.value;
import static org.junit.jupiter.api.Assertions.*;

class ProgramBuilderTest {

    @Test
    public void testSingleInstruction() {
        var elements = new ProgramBuilder()
                .lda(value(0x03))
                .build();

        assertEquals(1, elements.size());
        assertEquals(new Operation(OpCode.LDA, new Operand.ByteValue((byte) 0x03)), elements.get(0));
    }

    @Test
    public void testProgram() {
        var elements = new ProgramBuilder()
                .lda(value(0x03))
                .label("some_label")
                .iny()
                .jsr("print_y")
                .jmp(absolute(0x4567))
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


}
