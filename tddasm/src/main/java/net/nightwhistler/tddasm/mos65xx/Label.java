package net.nightwhistler.tddasm.mos65xx;

public record Label(String name) implements ProgramElement {
    public static Label label(String name){
        return new Label(name);
    }

    @Override
    public byte[] bytes() {
        //Labels don't get translated to bytes
        return new byte[0];
    }
}
