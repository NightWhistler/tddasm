package net.nightwhistler.tddasm.mos65xx;

import org.junit.jupiter.api.Test;

import static net.nightwhistler.ByteUtils.bytes;
import static org.junit.jupiter.api.Assertions.*;

class ProcessorTest {

    /**
     * Very basic test: load a value in the Accumulator and then check it.
     */
    @Test
    public void testLDAValue() {
        var staOperation = new Operation(OpCode.LDA, AddressingMode.Value, (byte) 0x03);
        var processor = new Processor();

        processor.performOperation(staOperation);
        assertEquals((byte) 0x03, processor.getAccumulatorValue());
    }

    @Test
    public void testLDXValue() {
        var staOperation = new Operation(OpCode.LDX, AddressingMode.Value, (byte) 0x03);
        var processor = new Processor();

        processor.performOperation(staOperation);
        assertEquals((byte) 0x03, processor.getXRegisterValue());
    }

    @Test
    public void testLDYValue() {
        var staOperation = new Operation(OpCode.LDY, AddressingMode.Value, (byte) 0x03);
        var processor = new Processor();

        processor.performOperation(staOperation);
        assertEquals((byte) 0x03, processor.getYRegisterValue());
    }


    @Test
    public void testLDAAddress() {

        //Load the accumulator from $4030
        var staOperation = new Operation(OpCode.LDA, AddressingMode.AbsoluteAddress, bytes(0x30, 0x40));
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
        var staOperation = new Operation(OpCode.LDY, AddressingMode.Value, (byte) 0x03);
        var processor = new Processor();

        processor.performOperation(staOperation);
        assertEquals((byte) 0x03, processor.getYRegisterValue());

        assertFalse(processor.isZeroFlagSet());

        //Read from an empty address
        processor.performOperation(new Operation(OpCode.LDY, AddressingMode.AbsoluteAddress, bytes(0x22, 0x11)));
        assertEquals((byte) 0x00, processor.getYRegisterValue());
        assertTrue(processor.isZeroFlagSet());
    }


}
