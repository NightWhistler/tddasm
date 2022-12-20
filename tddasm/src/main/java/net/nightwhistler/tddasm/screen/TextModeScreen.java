package net.nightwhistler.tddasm.screen;

import net.nightwhistler.tddasm.mos65xx.Operand;
import net.nightwhistler.tddasm.mos65xx.Processor;

import static net.nightwhistler.tddasm.screen.ScreenCode.fromScreenCodes;

public record TextModeScreen(Processor processor) {

    private static Operand.TwoByteAddress SCREEN_START = Operand.address(0x400);
    private static final int COLS = 40;

    private static final int ROWS = 25;
    private static final int size = ROWS*COLS;

    public byte[] getLine(int line) {
        if (line > ROWS-1) {
            throw new IllegalArgumentException("Out of range: " +line);
        }
        int base= line * COLS;

        return processor.readMemory(SCREEN_START.plus(base), SCREEN_START.plus(base).plus(COLS));
    }

    public String getLineAsString(int line) {
        byte[] data = getLine(line);
        return new String(fromScreenCodes(data));
    }


}
