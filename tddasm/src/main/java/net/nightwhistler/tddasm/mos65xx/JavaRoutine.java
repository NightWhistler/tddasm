package net.nightwhistler.tddasm.mos65xx;

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
     * The address that this routine should be located at.
     *
     * @return
     */
    public Operand.TwoByteAddress location();

    /**
     * How many bytes in memory this routine should take up.
     *
     * @return
     */
    int size();

    public void execute(Processor processor);
}
