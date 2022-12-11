package net.nightwhistler.tddasm.mos65xx;

import org.junit.jupiter.api.Test;

import static net.nightwhistler.ByteUtils.bytes;
import static net.nightwhistler.tddasm.mos65xx.Operation.operation;
import static org.junit.jupiter.api.Assertions.*;

class ProcessorTest {

    /**
     * Very basic test: load a value in the Accumulator and then check it.
     */
    @Test
    public void testLDAValue() {
        var operation = operation(OpCode.LDA, AddressingMode.Value, 0x03);
        var processor = new Processor();

        processor.performOperation(operation);
        assertEquals((byte) 0x03, processor.getAccumulatorValue());
    }

    @Test
    public void testLDXValue() {
        var operation = operation(OpCode.LDX, AddressingMode.Value, 0x03);
        var processor = new Processor();

        processor.performOperation(operation);
        assertEquals((byte) 0x03, processor.getXRegisterValue());
    }

    @Test
    public void testLDYValue() {
        var operation = operation(OpCode.LDY, AddressingMode.Value, 0x03);
        var processor = new Processor();

        processor.performOperation(operation);
        assertEquals((byte) 0x03, processor.getYRegisterValue());
    }

    @Test
    public void testSTAValue() {
        var processor = new Processor();
        var ldaOperation = operation(OpCode.LDA, AddressingMode.Value, 0x03);
        var staOperation = operation(OpCode.STA, AddressingMode.AbsoluteAddress, 0x0C69);

        processor.performOperation(ldaOperation);
        processor.performOperation(staOperation);

        assertEquals(0x03, processor.peekValue(0x0c69));
    }


    @Test
    public void testLDAAddress() {

        //Load the accumulator from $4030
        var staOperation = operation(OpCode.LDA, AddressingMode.AbsoluteAddress, 0x4030);
        var processor = new Processor();

        //Assure the address was 0
        assertEquals(0, processor.peekValue(0x4030));

        //Manually set the address to the expected value
        processor.pokeValue(0x4030, (byte) 0x99);
        processor.performOperation(staOperation);

        assertEquals(0x99, Byte.toUnsignedInt(processor.getAccumulatorValue()));
    }

    @Test
    public void testZeroFlag() {
        var staOperation = operation(OpCode.LDY, AddressingMode.Value, 0x03);
        var processor = new Processor();

        processor.performOperation(staOperation);
        assertEquals((byte) 0x03, processor.getYRegisterValue());

        assertFalse(processor.isZeroFlagSet());

        //Read from an empty address
        processor.performOperation(operation(OpCode.LDY, AddressingMode.AbsoluteAddress, 0x1122));
        assertEquals((byte) 0x00, processor.getYRegisterValue());
        assertTrue(processor.isZeroFlagSet());
    }


}
