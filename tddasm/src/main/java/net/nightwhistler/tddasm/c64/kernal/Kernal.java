package net.nightwhistler.tddasm.c64.kernal;

import net.nightwhistler.tddasm.mos65xx.Processor;

public class Kernal {
    public static void registerKernalRoutines(Processor processor) {
        processor.registerJavaRoutine(new ChrOut());
        processor.registerJavaRoutine(new ClearScreen());
    }
}
