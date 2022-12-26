package net.nightwhistler.tddasm.c64.kernal;

import net.nightwhistler.tddasm.c64.C64Constants;
import net.nightwhistler.tddasm.c64.screen.ScreenCode;
import net.nightwhistler.tddasm.mos65xx.JavaRoutine;
import net.nightwhistler.tddasm.mos65xx.OpCode;
import net.nightwhistler.tddasm.mos65xx.Operand;
import net.nightwhistler.tddasm.mos65xx.Operation;
import net.nightwhistler.tddasm.mos65xx.Processor;

import static net.nightwhistler.tddasm.mos65xx.Operand.address;
import static net.nightwhistler.tddasm.mos65xx.Operand.noValue;

/**
 * Java implementation of the CHROUT kernal routine.
 *
 * This routine is located at $F1CA with a jump table entry
 * at $FFD2
 */
public class ChrOut implements JavaRoutine {

    //The cursor offset, starts at 0 which is the top-left of the screen.
    int offset = 0;

    public static final Operand.TwoByteAddress CHROUT_ADDRESS = address(0xFFD2);

    /**
     * Sets up the jump table vector.
     *
     * This isn't strictly necessary, but allows for overriding the vector
     * in a realistic way.
     *
     * @param processor
     */
    @Override
    public void onLoad(Processor processor) {
        processor.storeOperationAt(CHROUT_ADDRESS, new Operation(OpCode.JMP, address(0xF1CA)));
    }

    @Override
    public Operand.TwoByteAddress location() {
        return address(0xF1CA);
    }

    @Override
    public int size() {
        return 128; //Somewhat randomly chosen
    }

    @Override
    public void execute(Processor processor) {
        processor.pokeValue(C64Constants.TEXT_MODE_SCREEN_MEMORY_START.plus(offset), ScreenCode.toScreenCode(processor.getAccumulatorValue()));
        offset++;
    }

    @Override
    public Operation endWith() {
        return new Operation(OpCode.RTS, noValue());
    }
}
