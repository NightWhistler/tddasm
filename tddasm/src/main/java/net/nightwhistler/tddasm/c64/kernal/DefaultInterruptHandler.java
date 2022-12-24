package net.nightwhistler.tddasm.c64.kernal;

import net.nightwhistler.tddasm.mos65xx.JavaRoutine;
import net.nightwhistler.tddasm.mos65xx.Operand;
import net.nightwhistler.tddasm.mos65xx.Operation;
import net.nightwhistler.tddasm.mos65xx.Processor;

import static net.nightwhistler.tddasm.mos65xx.OpCode.RTI;

public class DefaultInterruptHandler implements JavaRoutine  {
    @Override
    public void onLoad(Processor processor) {
        //Set up the default jump for interrupts to this address
        processor.pokeValue(0x0314, location().lowByte());
        processor.pokeValue(0x0315, location().highByte());
    }

    @Override
    public Operand.TwoByteAddress location() {
        return Operand.address(0xEA31);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public void execute(Processor processor) {
    }

    @Override
    public Operation endWith() {
        return Operation.operation(RTI);
    }
}
