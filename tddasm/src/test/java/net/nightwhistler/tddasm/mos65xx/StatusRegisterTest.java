package net.nightwhistler.tddasm.mos65xx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusRegisterTest {

    @Test
    public void testToByte() {
        StatusRegister statusRegister = new StatusRegister();
        statusRegister.setCarryFlag(true); //1
        statusRegister.setNegativeFlag(true); //128

        assertEquals(161, Byte.toUnsignedInt(statusRegister.toByte()));
    }

    @Test
    public void testFromByte() {
        StatusRegister statusRegister = new StatusRegister();
        statusRegister.setFrom((byte) 161);

        assertTrue(statusRegister.isCarryFlagSet());
        assertTrue(statusRegister.isNegativeFlagSet());
        assertFalse(statusRegister.isBreakCommandFlagSet());
    }

}
