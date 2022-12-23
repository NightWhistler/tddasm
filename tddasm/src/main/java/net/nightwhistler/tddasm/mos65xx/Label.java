package net.nightwhistler.tddasm.mos65xx;

public record Label(String name) implements ProgramElement {
    public static Label label(String name){
        return new Label(name);
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public String toString() {
        return name + ":";
    }
}
