package net.nightwhistler.tddasm.c64.kernal;

import net.nightwhistler.tddasm.c64.screen.TextModeScreen;
import net.nightwhistler.tddasm.mos65xx.JavaRoutine;
import net.nightwhistler.tddasm.mos65xx.OpCode;
import net.nightwhistler.tddasm.mos65xx.Operand;
import net.nightwhistler.tddasm.mos65xx.Operation;
import net.nightwhistler.tddasm.mos65xx.Processor;

import static net.nightwhistler.tddasm.mos65xx.Operand.noValue;

public class ClearScreen implements JavaRoutine {
    public static final Operand.TwoByteAddress CLR_SCREEN_ADDRESS = Operand.address(0xE544);
    @Override
    public Operand.TwoByteAddress location() {
        return CLR_SCREEN_ADDRESS;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public void execute(Processor processor) {
        TextModeScreen.clearScreen(processor);
    }

    @Override
    public Operation endWith() {
        return new Operation(OpCode.RTS, noValue());
    }
}
