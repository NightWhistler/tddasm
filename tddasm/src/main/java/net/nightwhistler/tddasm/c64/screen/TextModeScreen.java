package net.nightwhistler.tddasm.c64.screen;

import net.nightwhistler.tddasm.c64.C64Constants;
import net.nightwhistler.tddasm.mos65xx.Operand;
import net.nightwhistler.tddasm.mos65xx.Processor;

import static net.nightwhistler.tddasm.c64.C64Constants.TEXT_MODE_SCREEN_MEMORY_START;

public record TextModeScreen(Processor processor) {

    private static final int COLS = 40;

    private static final int ROWS = 25;
    private static final int size = ROWS*COLS;

    public TextModeScreen {
        clearScreen(processor);
    }

    public static void clearScreen(Processor processor) {
        byte[] emptyData = new byte[size+2];
        for (int i=0; i < emptyData.length; i++) {
            emptyData[i] = 0x20; //Fill with spaces
        }

        emptyData[0] = TEXT_MODE_SCREEN_MEMORY_START.lowByte();
        emptyData[1] = TEXT_MODE_SCREEN_MEMORY_START.highByte();

        //FIXME This abuses the Processor's load functionality a bit
        processor.loadBinary(emptyData);
    }

    public byte[] getLine(int line) {
        if (line > ROWS-1) {
            throw new IllegalArgumentException("Out of range: " +line);
        }
        int base= line * COLS;

        return processor.readMemory(TEXT_MODE_SCREEN_MEMORY_START.plus(base),
                TEXT_MODE_SCREEN_MEMORY_START.plus(base).plus(COLS));
    }

    public String getScreenContents() {
        StringBuilder stringBuilder = new StringBuilder();
        for ( int i=0; i < ROWS; i++) {
            stringBuilder.append(getLineAsString(i));
            stringBuilder.append('\n');
        }

        return stringBuilder.toString();
    }

    public String getLineAsString(int line) {
        byte[] data = getLine(line);
        return new String(ScreenCode.fromScreenCodes(data));
    }


}
