package net.nightwhistler.tddasm.mos65xx;

import java.util.function.Consumer;

public record JavaElement(Consumer<Processor> javaRoutine) implements ProgramElement, ProgramElement.BytesElement {
    @Override
    public int length() {
        return 1;
    }

    public byte[] bytes() {
        return new Operation(OpCode.NOP, Operand.noValue()).bytes();
    }

    public JavaRoutine toJavaRoutine(Program program) {
        return new JavaRoutine() {
            @Override
            public Operand.TwoByteAddress location() {
                return program.addressOfElement(JavaElement.this)
                        .getOrElseThrow(() ->
                                new IllegalArgumentException("Could not find address for " + JavaElement.this)
                        );
            }

            @Override
            public int size() {
                return 1;
            }

            @Override
            public void execute(Processor processor) {
                javaRoutine.accept(processor);
            }
        };
    }
}
