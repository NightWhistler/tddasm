package net.nightwhistler.tddasm.mos65xx;

public sealed interface ProgramElement permits Data, Label, Operation, StartingAddress {
    byte[] bytes();

    default int length() {
        return bytes().length;
    }
}
