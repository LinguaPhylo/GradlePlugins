plugins {
    //`kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.18.0"
//    id("io.github.linguaphylo.platforms.lphy-java") version "0.1.0"
//    id("io.github.linguaphylo.platforms.lphy-publish") version "0.1.0"
}

//base.archivesName.set("lphy-platforms")
group = "io.github.linguaphylo"
version = "0.1.0"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
//    testImplementation("junit:junit:4.13")
}

publishing {
    repositories {
        maven {
            // to local build/plugins
            url = uri(layout.buildDirectory.dir("plugins"))
        }
    }
}

gradlePlugin {
    plugins {
        create("lphy-java") {
            id = "io.github.linguaphylo.platforms.lphy-java"
            implementationClass = "io.github.linguaphylo.platforms.LPhyConventionJavaPlugin"
        }
        create("lphy-publish") {
            id = "io.github.linguaphylo.platforms.lphy-publish"
            implementationClass = "io.github.linguaphylo.platforms.LPhyPublishPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/LinguaPhylo/GradlePlugins"
    vcsUrl = "https://github.com/LinguaPhylo/GradlePlugins"
    description = "Gradle plugins for LPhy conventions"

    (plugins) {
        "lphy-java" {
            // id is captured from java-gradle-plugin configuration
            displayName = "Gradle LPhy Java Plugin"
            description = "Use Java 16 and overwrite java related tasks to use module-path."
            tags = listOf("gradle", "lphy", "java")
            version = project.version.toString()
        }
        "lphy-publish" {
            // id is captured from java-gradle-plugin configuration
            displayName = "Gradle Maven Publish Plugin"
            description = "Plugin that provides conventions for building and publishing Docker images for Java applications."
            tags = listOf("gradle", "lphy", "maven-publish")
            version = project.version.toString()
        }
    }
//    mavenCoordinates {
//        groupId = project.group.toString()
//        artifactId = project.name
//        version = project.version.toString()
//    }
}


