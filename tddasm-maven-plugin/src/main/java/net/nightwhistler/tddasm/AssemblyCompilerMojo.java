package net.nightwhistler.tddasm;

import net.nightwhistler.tddasm.annotation.CompileProgram;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mojo(name = "assembly-compile", defaultPhase = LifecyclePhase.COMPILE)
public class AssemblyCompilerMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("AssemblyCompilerMojo!!!!!!!!!!!");
        String outputDir = project.getBuild().getOutputDirectory();
        Path javaOutputPath = Path.of(outputDir);
        Path mosPath = javaOutputPath.resolveSibling("mos62xx");
//        Files.createDirectory(mosPath);

        getLog().info("Got build dir: " + outputDir);

        ClassLoader projectClassloader = getClassLoader(project);

        try (Stream<Path> stream = Files.walk(javaOutputPath)) {
            for( Path classFile: stream.filter(Files::isRegularFile).collect(Collectors.toList())) {
                getLog().info("Got file " + classFile.toAbsolutePath());

                if (classFile.getFileName().toString().endsWith(".class")) {
                    String className = className(javaOutputPath.relativize(classFile));
                    Class<?> clz = projectClassloader.loadClass(className);
                    if (clz.getAnnotationsByType(CompileProgram.class).length > 0) {
                        getLog().info("Class is annotated.");
                    } else {
                        getLog().info("Class is not annotated.");
                    }
                }
            }
        } catch (IOException io) {
            getLog().error("Could not scan files", io);
        } catch (ClassNotFoundException cle) {
            getLog().error("Could not load class: ", cle);
        }
    }

    private String className(Path file) {
        String asString = file.toString()
                .replaceAll(FileSystems.getDefault().getSeparator(), ".")
                .replaceAll(".class", "");

        getLog().info("Constructed classname " + asString);
        return asString;
    }

    //https://stackoverflow.com/questions/49737706/access-project-classes-from-a-maven-plugin
    private ClassLoader getClassLoader(MavenProject project) {
        try {
            List classpathElements = project.getCompileClasspathElements();
            classpathElements.add( project.getBuild().getOutputDirectory() );
//            classpathElements.add( project.getBuild().getTestOutputDirectory() );
            URL urls[] = new URL[classpathElements.size()];
            for ( int i = 0; i < classpathElements.size(); ++i ) {
                urls[i] = new File( (String) classpathElements.get( i ) ).toURL();
            }
            return new URLClassLoader( urls, this.getClass().getClassLoader() );
        }
        catch (Exception e) {
            getLog().error( "Couldn't get the classloader." );
            return this.getClass().getClassLoader();
        }
    }
}
