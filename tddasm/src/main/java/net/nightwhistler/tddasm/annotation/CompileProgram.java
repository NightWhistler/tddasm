package net.nightwhistler.tddasm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
public @interface CompileProgram {
    String value();

    //TODO Rename to CompiledTo
}
