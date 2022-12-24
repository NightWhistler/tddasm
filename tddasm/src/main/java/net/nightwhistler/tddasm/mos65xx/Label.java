package net.nightwhistler.tddasm.mos65xx;

public record Label(String name) implements ProgramElement {

    @Override
    public int length() {
        return 0;
    }

    @Override
    public String toString() {
        return name + ":";
    }
}
