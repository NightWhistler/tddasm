package net.nightwhistler.tddasm.mos65xx;

import net.nightwhistler.ByteUtils;

import static net.nightwhistler.ByteUtils.highByte;
import static net.nightwhistler.ByteUtils.lowByte;

public record StartingAddress(int address) implements ProgramElement {
    public static StartingAddress startingAddress(int address) {
        //Mask off anything except first 2 bytes
        return new StartingAddress(address & 0x0000FFFF);
    }

    @Override
    public byte[] bytes() {
        return new byte[]{lowByte(address), highByte(address)};
    }
}
