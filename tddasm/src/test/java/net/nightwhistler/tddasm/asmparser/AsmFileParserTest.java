package net.nightwhistler.tddasm.asmparser;

import io.vavr.collection.List;
import net.nightwhistler.tddasm.mos65xx.AddressingMode;
import net.nightwhistler.tddasm.mos65xx.OpCode;
import net.nightwhistler.tddasm.mos65xx.Operand;
import net.nightwhistler.tddasm.mos65xx.OperationProvider;
import net.nightwhistler.tddasm.mos65xx.ProgramElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AsmFileParserTest {

    @Test
    public void testLoadValue() {
        List<ProgramElement> element = new AsmFileParser().parseLine("lda #$03");
        assertEquals(1, element.size());
        assertEquals(new OperationProvider(OpCode.LDA, Operand.value(3)), element.get(0));
    }

    @Test
    public void testZeroPage() {
        List<ProgramElement> element = new AsmFileParser().parseLine("lda $F3");
        assertEquals(1, element.size());
        assertEquals(
                new OperationProvider(OpCode.LDA,
                    new Operand.OneByteAddress(AddressingMode.ZeroPageAddress, (byte) 0xF3)
                )
                , element.get(0));
    }

    @Test
    public void testAddress() {
        List<ProgramElement> element = new AsmFileParser().parseLine("lda $33F3");
        assertEquals(1, element.size());
        assertEquals(
                new OperationProvider(OpCode.LDA,
                        new Operand.TwoByteAddress(AddressingMode.AbsoluteAddress, (byte) 0xF3, (byte) 0x33)
                )
                , element.get(0));
    }
}
