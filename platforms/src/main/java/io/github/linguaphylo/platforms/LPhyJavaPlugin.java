package io.github.linguaphylo.platforms;

import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;

import java.util.List;

/**
 * @author Walter Xie
 */
public class LPhyJavaPlugin implements Plugin<Project> {
    public void apply(Project project) {

        // plugins {  `java-library` }
        project.getPlugins().apply(JavaLibraryPlugin.class);

        /* java {
              sourceCompatibility = JavaVersion.VERSION_16
              targetCompatibility = JavaVersion.VERSION_16
              withSourcesJar()
              withJavadocJar()  } */
        JavaPluginExtension extension = project.getExtensions().getByType(JavaPluginExtension.class);
        extension.setSourceCompatibility(JavaVersion.VERSION_16);
        extension.setTargetCompatibility(JavaVersion.VERSION_16);
        extension.withSourcesJar();
        extension.withJavadocJar();

        /* tasks.compileJava {
            options.javaModuleVersion.set(provider { project.version as String })
            doFirst {
                println("Java version used is ${JavaVersion.current()}.")
                options.compilerArgs = listOf("--module-path", classpath.asPath)
                classpath = files()
            }
            doLast {
                println("${project.name} compiler args = ${options.compilerArgs}")
            }
        } */
        project.getTasks().withType(JavaCompile.class).configureEach(jc -> {
            // use the project's version or define one directly
            jc.getOptions().getJavaModuleVersion().set(project.getVersion().toString());
            jc.doFirst(task -> {
                System.out.println("Java version used is ${JavaVersion.current()}.");
                FileCollection classpath = jc.getClasspath();
                jc.getOptions().setCompilerArgs(List.of("--module-path", classpath.getAsPath()));
//                jc.setClasspath();
            });
            jc.doLast(task -> System.out.println("${project.name} compiler args = ${options.compilerArgs}"));
        });

        /* tasks.javadoc {
            (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
            doFirst {
                options.modulePath = classpath.files.toList()
                options.classpath = listOf()
            }
        } */
        project.getTasks().withType(org.gradle.api.tasks.javadoc.Javadoc.class).configureEach(doc -> {
            ((StandardJavadocDocletOptions) doc.getOptions()).addBooleanOption("html5", true);
            doc.doFirst(task -> {
                doc.getOptions().setModulePath(doc.getClasspath().getFiles().stream().toList());
                doc.getOptions().setClasspath(List.of());
            });
        });
    }

}
