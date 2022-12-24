package net.nightwhistler.tddasm.annotation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnnotatedClassesCompilerTest {

    @Test
    public void testRunCompilation() throws Exception {
        AnnotatedClassesCompiler.compileAnnotatedClasses();
    }
}
