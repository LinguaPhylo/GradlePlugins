import java.nio.file.Files
import java.nio.file.Paths

plugins {
    `kotlin-dsl`
    `maven-publish`
}
// TODO
//apply(from = "src/main/kotlin/lphy.platforms.lphy-publish.gradle.kts")

group = "io.github.linguaphylo"
version = "0.1.0-SNAPSHOT"

//compileJava and compileKotlin should be set to the same Java version.
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions.jvmTarget = "16"
}

// Projects should use Maven Central for external dependencies
// This could be the organization's private repository
repositories {
    gradlePluginPortal() // so that external plugins can be resolved in dependencies section
}

dependencies {
//    implementation("gradle.plugin.com.github.spotbugs.snom:spotbugs-gradle-plugin:4.6.2")
    testImplementation("junit:junit:4.13")
}

publishing {
    repositories {
        maven {
            // to local build/plugins
            url = uri(layout.buildDirectory.dir("plugins"))
            val path: java.nio.file.Path = Paths.get(url.path)
            if (Files.exists(path)) {
                println("Delete the existing previous release : ${path.toAbsolutePath()}")
                project.delete(path)
            }
        }
    }
}




