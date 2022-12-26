package net.nightwhistler.tddasm.mos65xx;

public record Comment(String contents) implements ProgramElement {
    @Override
    public int length() {
        return 0;
    }

    @Override
    public String toString() {
        return "; " + contents;
    }
}
