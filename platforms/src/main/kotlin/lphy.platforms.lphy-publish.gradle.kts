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

//TODO Projects have the 'io.github.linguaphylo' group by convention
//group = "io.github.linguaphylo"

val isReleaseVersion: Boolean = !version.toString().endsWith("SNAPSHOT")
val hasOSSRHCredentials: Boolean = hasProperty("ossrh.user") && hasProperty("ossrh.pswd")

// if OSSRH credentials are given in CMD, then publish to maven central
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

// create your own MavenPublication in your build
publishing {
//    publications {
//        create<MavenPublication>("lphy-ext") {
//  ...
    repositories {
        // both urls are defined in tasks.withType<PublishToMavenRepository>().configureEach
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
        }
        maven {
            // if "ossrh.user" not provided, then publish to local
            name = "local"
        }
    }
}

// -Psigning.secretKeyRingFile=/path/to/mysecr.gpg -Psigning.password=mypswd -Psigning.keyId=last8chars
// only signing publications whose names contain "lphy"
signing {
    // Conditional Signing
    setRequired({
        isReleaseVersion && gradle.taskGraph.hasTask("publish")
    })
    sign(publishing.publications.matching{ it!!.name.toLowerCase().contains("lphy") })
}


