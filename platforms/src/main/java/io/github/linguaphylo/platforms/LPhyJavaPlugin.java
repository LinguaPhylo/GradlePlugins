package io.github.linguaphylo.platforms;

import org.gradle.api.*;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;

import java.util.List;

/**
 * Define Java conventions using Java 16
 * and the Java Platform Module System (JPMS).
 * Overwrite Java related tasks to use module-path.
 *
 * @author Walter Xie
 */
public class LPhyJavaPlugin implements Plugin<Project> {

    public static final JavaVersion MIN_JAVA_VERSION = JavaVersion.VERSION_16;

    public void apply(final Project project) {

        /* java {
              sourceCompatibility = JavaVersion.VERSION_16
              targetCompatibility = JavaVersion.VERSION_16
              withSourcesJar()
              withJavadocJar()  } */
        project.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
            JavaPluginExtension extension = project.getExtensions().getByType(JavaPluginExtension.class);
            extension.setSourceCompatibility(MIN_JAVA_VERSION);
            extension.setTargetCompatibility(MIN_JAVA_VERSION);
            extension.withSourcesJar();
            extension.withJavadocJar();
        });

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
            jc.doFirst(new Action<Task>() {
                @Override
                public void execute(Task task) {
                    // JVM version convention
                    if ( !JavaVersion.toVersion(jc.getSourceCompatibility()).isCompatibleWith(MIN_JAVA_VERSION)
                            || !JavaVersion.toVersion(jc.getTargetCompatibility()).isCompatibleWith(MIN_JAVA_VERSION)
                            || !JavaVersion.current().isCompatibleWith(MIN_JAVA_VERSION) )
                        throw new GradleException("Require Java version >= " + MIN_JAVA_VERSION +
                                " ! But the current version = " +  JavaVersion.current() +
                                ", source = " + jc.getSourceCompatibility() + ", target = " + jc.getTargetCompatibility());
                    System.out.println("Current Java version = " + JavaVersion.current() + ", sourceCompatibility = " +
                            jc.getSourceCompatibility() + ", targetCompatibility = " + jc.getTargetCompatibility() + ".");

                    FileCollection classpath = jc.getClasspath();
                    jc.getOptions().setCompilerArgs(List.of("--module-path", classpath.getAsPath()));
//                jc.setClasspath();
                }
            });
            jc.doLast(new Action<Task>() {
                @Override
                public void execute(Task task) {
                    System.out.println(project.getName() + " Java compiler args = " +
                            jc.getOptions().getAllCompilerArgs().toString());
                }
            });
        });

        /* tasks.javadoc {
            (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
            doFirst {
                options.modulePath = classpath.files.toList()
                options.classpath = listOf()
            }
        } */
        project.getTasks().withType(org.gradle.api.tasks.javadoc.Javadoc.class).configureEach(doc -> {
            // avoid error
            ((StandardJavadocDocletOptions) doc.getOptions()).addBooleanOption("html5", true);
            // turn off warnings
            ((StandardJavadocDocletOptions) doc.getOptions()).addStringOption("Xdoclint:none", "-quiet");
            doc.doFirst(new Action<Task>() {
                @Override
                public void execute(Task task) {
                    doc.getOptions().setModulePath(doc.getClasspath().getFiles().stream().toList());
                    doc.getOptions().setClasspath(List.of());
                }
            });
        });
    }

}
