package net.nightwhistler.tddasm.mos65xx;

public sealed interface ProgramElement permits Data, Label, OperationProvider, Comment, JavaElement {
    int length();

    interface BytesElement {
        byte[] bytes();
    }
}

