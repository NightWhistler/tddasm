package net.nightwhistler.tddasm.annotation;

import io.vavr.collection.Stream;
import net.nightwhistler.tddasm.mos65xx.Program;
import net.nightwhistler.tddasm.mos65xx.ProgramBuilder;
import org.reflections.Reflections;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class AnnotatedClassesCompiler {

    public static void main(String argv[]) {
        try {
            compileAnnotatedClasses();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void compileAnnotatedClasses() throws ClassNotFoundException {

        Reflections reflections = new Reflections("net.nightwhistler.tddasm");

        Set<Class<?>> classes = reflections
                .getTypesAnnotatedWith(CompileProgram.class);

        System.out.println("Found " + classes.size() + " classes.");

        Set<Method> methods = reflections
                .getMethodsAnnotatedWith(CompileProgram.class);

        System.out.println("Found " + methods.size() + " methods.");

        for (Method method: methods) {
            System.out.println("Found class: " + method.getDeclaringClass().getSimpleName());
            try {
                Program program = getProgram(method);
                compileProgram(program);
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static void compileProgram(Program program) {
        System.out.println("Compiling program: ");
        program.printASM(new PrintWriter(System.out), true);
    }

    private static Program getProgram(Method method) throws InvocationTargetException, IllegalAccessException {
        return (Program) method.invoke(null, new Object[0]);
    }

}
