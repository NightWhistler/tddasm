package net.nightwhistler.tddasm;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "assembly-compile", defaultPhase = LifecyclePhase.COMPILE)
public class AssemblyCompilerMojo extends AbstractMojo {
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("AssemblyCompilerMojo!!!!!!!!!!!");
    }
}
