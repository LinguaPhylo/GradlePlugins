/**
 * Created by Walter Xie
 * Copyright (c) 2022. All rights reserved.
 */

import java.nio.file.Files
import java.nio.file.Paths

// Define Java Library conventions for this organization.
// Projects need to use the organization's Java conventions and publish using Maven Publish

plugins {
    //`java-library`
    `maven-publish`
    signing
    id("lphy.platforms.lphy-java")
}

// Projects have the 'io.github.linguaphylo' group by convention
//group = "io.github.linguaphylo"

val isReleaseVersion: Boolean = !version.toString().endsWith("SNAPSHOT")
val hasOSSRHCredentials: Boolean = hasProperty("ossrh.user") && hasProperty("ossrh.pswd")

// if OSSRH properties are given, then publish to maven central
tasks.withType<PublishToMavenRepository>().configureEach {
    onlyIf {
        val toMaven = ( repository == publishing.repositories["maven"] && hasOSSRHCredentials )
        // otherwise publish to local
        val toLocal = ( repository == publishing.repositories["local"] && !(hasOSSRHCredentials) )
        toMaven || toLocal
    }
    // define the release folder according to version
    // must be here, otherwise setting url would not work
    doFirst {
        // why isReleaseVersion not working here
        val isRel = !project.version.toString().endsWith("SNAPSHOT")
        if (hasOSSRHCredentials) {
            if (isRel)
                repository.setUrl(uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"))
            else
                repository.setUrl(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        } else {
            if (isRel)
                repository.setUrl(uri(layout.buildDirectory.dir("releases")))
            else
                repository.setUrl(uri(layout.buildDirectory.dir("snapshots")))
            // del previous release folder
            val path: java.nio.file.Path = Paths.get(repository.url.path)
            if (Files.exists(path)) {
                println("Delete the existing previous release : ${path.toAbsolutePath()}")
                project.delete(path)
            }
        }
        println("Publish ${project.name}:${project.version} to ${repository?.name} repository, " +
                "URL = ${repository?.url}")
    }
}

publishing {
    publications {
        create<MavenPublication>("lphy-ext") {
            artifactId = project.base.archivesName.get()
            from(components["java"])
            // Configures the version mapping strategy
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set(project.name)
                description.set("A probabilistic model specification language to concisely and precisely define phylogenetic models.")
                url.set("https://linguaphylo.github.io/")
                packaging = "jar"
                properties.set(mapOf(
                    "maven.compiler.source" to java.sourceCompatibility.majorVersion,
                    "maven.compiler.target" to java.targetCompatibility.majorVersion
                ))
                licenses {
                    license {
                        name.set("GNU Lesser General Public License, version 3")
                        url.set("https://www.gnu.org/licenses/lgpl-3.0.txt")
                    }
                }
                developers {
                    developer {
                        name.set("LPhy developer team")
                    }
                }
                // https://central.sonatype.org/publish/requirements/
                scm {
                    connection.set("scm:git:git://github.com/LinguaPhylo/linguaPhylo.git")
                    developerConnection.set("scm:git:ssh://github.com/LinguaPhylo/linguaPhylo.git")
                    url.set("https://github.com/LinguaPhylo/linguaPhylo")
                }
            }
        }

    }
    repositories {
        maven {
            // publish to maven
            name = "maven"
            // -Possrh.user=myuser -Possrh.pswd=mypswd
            val ossrhUser = findProperty("ossrh.user")
            val ossrhPswd = findProperty("ossrh.pswd")
            credentials {
                username = "$ossrhUser"
                password = "$ossrhPswd"
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
//            val releaseOSSRH = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
//            val snapshotOSSRH = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
//            url = uri(if (project.version.toString().endsWith("SNAPSHOT")) snapshotOSSRH else releaseOSSRH)
        }
        maven {
            // if "ossrh.user" not provided, then publish to local
            name = "local"
//            val releaseLocal = uri(layout.buildDirectory.dir("releases"))
//            val snapshotLocal = uri(layout.buildDirectory.dir("snapshots"))
//            url = if (project.version.toString().endsWith("SNAPSHOT")) snapshotLocal else releaseLocal
//            val path: java.nio.file.Path = Paths.get(url.path)
//            if (Files.exists(path)) {
//                println("Delete the existing previous release : ${path.toAbsolutePath()}")
//                project.delete(path)
//            }
        }
    }
}

// -Psigning.secretKeyRingFile=/path/to/mysecr.gpg -Psigning.password=mypswd -Psigning.keyId=last8chars
signing {
    // Conditional Signing
    setRequired({
        isReleaseVersion && gradle.taskGraph.hasTask("publish")
    })
    sign(publishing.publications["lphy-ext"])
}


