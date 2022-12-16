package net.nightwhistler.tddasm.mos65xx;

public sealed interface ProgramElement permits Data, Label, Operation {
    byte[] bytes();

    default int length() {
        return bytes().length;
    }
}
