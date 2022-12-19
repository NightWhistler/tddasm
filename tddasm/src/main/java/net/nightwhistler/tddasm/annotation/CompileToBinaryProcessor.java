package net.nightwhistler.tddasm.annotation;

import net.nightwhistler.tddasm.mos65xx.Program;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.reflect.Method;
import java.util.Set;

@SupportedAnnotationTypes("net.nightwhistler.tddasm.annotation.CompileProgram")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class CompileToBinaryProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for ( TypeElement annotation : annotations ) {
            for ( Element element : roundEnv.getElementsAnnotatedWith(annotation) ) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                        "found @CompileProgram at " + element + " of type " + element.getClass().getName());
                try {
                    Class<?> cls = Class.forName(element.toString());
                    for (Method m: cls.getMethods()) {
                        if (m.getReturnType().equals(Program.class)) {
                            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                                    "Found method: " + m.getName());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

}
