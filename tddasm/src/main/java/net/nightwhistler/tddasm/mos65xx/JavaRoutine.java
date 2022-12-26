package net.nightwhistler.tddasm.mos65xx;

import static net.nightwhistler.tddasm.mos65xx.OpCode.NOP;
import static net.nightwhistler.tddasm.mos65xx.OpCode.RTS;

/**
 * Interface for Java routines.
 *
 * Java routines can be registered in the Processor at a specific
 * address and will be executed when the Program Counter hits that address.
 *
 * Java routines can modify the Processor as they see fit.
 *
 * After execution of the routine, the Processor will automatically execute
 * an RTS instruction.
 *
 * Code in memory at this address will be ignored. Java routines can be used
 * to fake Kernal routines, but can also be useful in testing. It allows adding
 * mocks or stubs and verifying that subroutines are called properly.
 */
public interface JavaRoutine {

    /**
     * Called by the Processor when loading the routine.
     * Can be used for initialisation logic like setting
     * up jump tables.
     * @param processor
     */
    default void onLoad(Processor processor) {

    }

    /**
     * What the processor should execute after running this Java routine.
     * The default is NOP, should be RTI for interrupt handlers.
     * @return
     */
    default Operation endWith() {
        return Operation.operation(NOP);
    }

    /**
     * The address that this routine should be located at.
     *
     * @return
     */
    Operand.TwoByteAddress location();

    /**
     * How many bytes in memory this routine should take up.
     *
     * @return
     */
    int size();

    void execute(Processor processor);
}
