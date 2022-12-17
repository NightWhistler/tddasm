package net.nightwhistler.tddasm.mos65xx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperandTest {

    @Test
    public void testAbsoluteAddress() {
        Operand.TwoByteAddress absolute = Operand.address(0x2345);
        assertEquals(absolute, new Operand.TwoByteAddress(AddressingMode.AbsoluteAddress, (byte) 0x45, (byte) 0x23));
    }

}
