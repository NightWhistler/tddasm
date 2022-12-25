package net.nightwhistler.tddasm;

import io.vavr.Tuple2;
import net.nightwhistler.tddasm.annotation.CompileProgram;
import net.nightwhistler.tddasm.mos65xx.Program;
import net.nightwhistler.tddasm.util.ProgramWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Mojo(name = "assembly-compile", defaultPhase = LifecyclePhase.COMPILE)
public class AssemblyCompilerMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Override
    public void execute() {
        getLog().info("Scanning for methods annotated with @CompileProgram");
        String outputDir = project.getBuild().getOutputDirectory();
        Path javaOutputPath = Path.of(outputDir);
        Path mosPath = javaOutputPath.resolveSibling("mos62xx");

        getLog().debug("Got build dir: " + outputDir);
        ClassLoader projectClassloader = getClassLoader(project);

        try (Stream<Path> stream = Files.walk(javaOutputPath)) {
            if (! Files.exists(mosPath)) {
                Files.createDirectory(mosPath);
            }

            for( Path classFile: stream.filter(Files::isRegularFile).toList()) {
                getLog().debug("Got file " + classFile.toAbsolutePath());

                if (classFile.getFileName().toString().endsWith(".class")) {
                    String className = className(javaOutputPath.relativize(classFile));
                    Class<?> clz = projectClassloader.loadClass(className);

                    for (Method m: clz.getDeclaredMethods()) {
                        Optional<Tuple2<String, Program>> maybeProgram = attemptCompile(m);
                        maybeProgram.ifPresent(t -> writeProgram(mosPath, t._1, t._2));
                    }
                }
            }
        } catch (IOException io) {
            getLog().error("Could not scan files", io);
        } catch (ClassNotFoundException cle) {
            getLog().error("Could not load class: ", cle);
        }
    }

    private void writeProgram(Path toPath, String programName, Program program) {
        Path filePath = toPath.resolve(programName);
        try(OutputStream outputStream = Files.newOutputStream(filePath)) {
            ProgramWriter.writeProgram(program, outputStream);
            getLog().info("Successfully compiled " + programName);
        } catch (IOException io) {
            getLog().error("Could not write program to file " + filePath);
        }
    }

    private Optional<Tuple2<String, Program>> attemptCompile(Method method) {
        CompileProgram compileProgram = method.getAnnotation(CompileProgram.class);
        if (compileProgram != null) {
            String name = compileProgram.value();

            try {
                Program program = (Program) method.invoke(null, new Object[0]);
                return Optional.of(new Tuple2<>(name, program));
            } catch (Exception i) {
                getLog().error("Error compiling " + name, i);
            }
        } else {
            getLog().debug("Method " + method.getName() + " is not annotated. Skipping.");
        }

        return Optional.empty();
    }

    private String className(Path file) {
        String asString = file.toString()
                .replaceAll(FileSystems.getDefault().getSeparator(), ".")
                .replaceAll(".class", "");

        getLog().debug("Constructed classname " + asString);
        return asString;
    }

    //https://stackoverflow.com/questions/49737706/access-project-classes-from-a-maven-plugin
    private ClassLoader getClassLoader(MavenProject project) {
        try {
            List<String> classpathElements = (List<String>) project.getCompileClasspathElements();
            classpathElements.add( project.getBuild().getOutputDirectory() );
//            classpathElements.add( project.getBuild().getTestOutputDirectory() );
            URL urls[] = new URL[classpathElements.size()];
            for ( int i = 0; i < classpathElements.size(); ++i ) {
                urls[i] = new File(classpathElements.get( i )).toURI().toURL();
            }
            return new URLClassLoader( urls, this.getClass().getClassLoader() );
        }
        catch (Exception e) {
            getLog().error( "Couldn't get the classloader." );
            return this.getClass().getClassLoader();
        }
    }
}
