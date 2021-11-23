/**
 * Created by Walter Xie
 * Copyright (c) 2022. All rights reserved.
 */

// Define Java conventions for this organization.
// Projects need to use the Java, Checkstyle and Spotbugs plugins.

plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
    withSourcesJar()
    withJavadocJar()
}

// overwrite compileJava to use module-path
tasks.compileJava {
    // use the project's version or define one directly
    options.javaModuleVersion.set(provider { project.version as String })

    doFirst {
        println("Java version used is ${JavaVersion.current()}.")
        options.compilerArgs = listOf("--module-path", classpath.asPath)
        classpath = files()
    }

    doLast {
        println("${project.name} compiler args = ${options.compilerArgs}")
    }
}

tasks.javadoc {
    // if (JavaVersion.current().isJava9Compatible)
    (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    doFirst {
        options.modulePath = classpath.files.toList()
        options.classpath = listOf()
    }
}

//TODO test
