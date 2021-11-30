plugins {
    //`java-library`
    `maven-publish`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.18.0"
    id("io.github.linguaphylo.platforms.lphy-java") version "0.1.0-SNAPSHOT"
    id("io.github.linguaphylo.platforms.lphy-publish") version "0.1.0-SNAPSHOT"
}

//base.archivesName.set("lphy-platforms")
group = "io.github.linguaphylo"
version = "0.1.0-SNAPSHOT"

repositories {
//    gradlePluginPortal()
    mavenCentral()
}

dependencies {
//    testImplementation("junit:junit:4.13")
}

publishing {
//    repositories {
//        maven {
//            // plugins to local build/releases
//            url = uri(layout.buildDirectory.dir("releases"))
//        }
//    }
    publications {
        afterEvaluate {
            val licenseName = "GNU Lesser General Public License, version 3"
            val licenseURL = "https://www.gnu.org/licenses/lgpl-3.0.txt"
            val pluginBundle = project.extensions.getByName("pluginBundle") as com.gradle.publish.PluginBundleExtension
            named<MavenPublication>("pluginMaven") {
                pom {
                    name.set("lphy-platforms")
                    description.set(pluginBundle.description)
                    url.set(pluginBundle.website)
                    developers {
                        developer {
                            name.set("Walter Xie")
                            id.set("walterxie")
                        }
                    }
                    licenses {
                        license {
                            name.set(licenseName)
                            url.set(licenseURL)
                        }
                    }
                }
            }
            named<MavenPublication>("lphy-javaPluginMarkerMaven") {
                pom {
                    val plugin = pluginBundle.plugins.getByName("lphy-java")
                    name.set(plugin.displayName)
                    description.set(plugin.description)
                    url.set(pluginBundle.website)
                    developers {
                        developer {
                            name.set("Walter Xie")
                            id.set("walterxie")
                        }
                    }
                    licenses {
                        license {
                            name.set(licenseName)
                            url.set(licenseURL)
                        }
                    }
                }
            }
            named<MavenPublication>("lphy-publishPluginMarkerMaven") {
                pom {
                    val plugin = pluginBundle.plugins.getByName("lphy-publish")
                    name.set(plugin.displayName)
                    description.set(plugin.description)
                    url.set(pluginBundle.website)
                    developers {
                        developer {
                            name.set("Walter Xie")
                            id.set("walterxie")
                        }
                    }
                    licenses {
                        license {
                            name.set(licenseName)
                            url.set(licenseURL)
                        }
                    }
                }
            }
        }
    }
}

gradlePlugin {
    plugins {
        create("lphy-java") {
            id = "io.github.linguaphylo.platforms.lphy-java"
            implementationClass = "io.github.linguaphylo.platforms.LPhyJavaPlugin"
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


