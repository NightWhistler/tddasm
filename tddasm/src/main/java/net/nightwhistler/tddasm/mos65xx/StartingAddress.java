package net.nightwhistler.tddasm.mos65xx;

public record StartingAddress(int address) implements ProgramElement {
    public static StartingAddress startingAddress(int address) {
        //Mask off anything except first 2 bytes
        return new StartingAddress(address & 0x0000FFFF);
    }
}
