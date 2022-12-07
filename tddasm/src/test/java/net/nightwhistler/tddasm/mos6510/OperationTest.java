package net.nightwhistler.tddasm.mos6510;

import org.junit.jupiter.api.Test;

import static net.nightwhistler.ByteUtils.bytes;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OperationTest {

    @Test
    public void testLoadSTA() {
        byte[] values = bytes(0x8D, 0x33, 0x55);

        Operation operation = Operation.fromBytes(values);
        assertEquals(OpCode.STA, operation.opCode());
        assertEquals(AddressingMode.AbsoluteAddress, operation.addressingMode());
        assertArrayEquals(bytes(0x33, 0x55), operation.values());
    }
}
