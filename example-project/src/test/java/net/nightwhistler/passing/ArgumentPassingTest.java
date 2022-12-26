package net.nightwhistler.passing;

import net.nightwhistler.tddasm.mos65xx.Operand;
import net.nightwhistler.tddasm.mos65xx.Processor;
import net.nightwhistler.tddasm.mos65xx.Program;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArgumentPassingTest {

    @Test
    public void testOneByteAddition() {
        Processor processor = new Processor();
        processor.load(ArgumentPassing.oneByteAdditionArgPassing());
        processor.run();

        assertEquals(8, processor.getAccumulatorValue());
    }

    @Test
    public void testTwoByteAddition() {
        Processor processor = new Processor();
        var program = ArgumentPassing.twoByteAddition();
        processor.load(program);
        processor.run();

        Operand.TwoByteAddress resultLowByte = program.resolveLabelAbsolute("result").get();
        Operand.TwoByteAddress resultHighByte = program.resolveLabelAbsolute("result").get().plus(1);
        //Result should be 510 -> 0x01FE

        System.out.println("Result is located at " + resultLowByte);

        assertEquals((byte) 0xFE, processor.peekValue(resultLowByte));
        assertEquals((byte) 0x01, processor.peekValue(resultHighByte));

    }
}
