package net.nightwhistler.tddasm.asmparser;

import io.vavr.collection.List;
import net.nightwhistler.tddasm.mos65xx.OpCode;
import net.nightwhistler.tddasm.mos65xx.Operand;
import net.nightwhistler.tddasm.mos65xx.OperationProvider;
import net.nightwhistler.tddasm.mos65xx.ProgramElement;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AsmFileParserTest {

    @Disabled
    @Test
    public void testLoadValue() {
        List<ProgramElement> element = new AsmFileParser().parseLine("lda #3");
        assertEquals(1, element.size());
        assertEquals(new OperationProvider(OpCode.LDA, Operand.value(3)), element.get(0));
    }

}
