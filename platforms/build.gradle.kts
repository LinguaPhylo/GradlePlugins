plugins {
    //`java-library`
    signing
    `maven-publish`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.18.0"
    id("io.github.linguaphylo.platforms.lphy-java") version "0.1.2-SNAPSHOT"
    id("io.github.linguaphylo.platforms.lphy-publish") version "0.1.2-SNAPSHOT"
}

//base.archivesName.set("lphy-platforms")
group = "io.github.linguaphylo"
version = "0.1.2-SNAPSHOT" //-SNAPSHOT

dependencies {
//    testImplementation("junit:junit:4.13")
}

publishing {
    // uncomment below when publishing to local without using platform plugins
//    repositories {
//        maven {
//            name = "local-release"
//            // plugins to local build/releases
//            url = uri(layout.buildDirectory.dir("releases"))
//        }
//    }
    publications {
        afterEvaluate {
            val pluginBundle = project.extensions.getByName("pluginBundle") as com.gradle.publish.PluginBundleExtension
            named<MavenPublication>("pluginMaven") {
                pom {
                    name.set("lphy-platforms")
                    description.set(pluginBundle.description)
                }
            }
            named<MavenPublication>("lphy-javaPluginMarkerMaven") {
                pom {
                    val plugin = pluginBundle.plugins.getByName("lphy-java")
                    name.set(plugin.displayName)
                    description.set(plugin.description)
                }
            }
            named<MavenPublication>("lphy-publishPluginMarkerMaven") {
                pom {
                    val plugin = pluginBundle.plugins.getByName("lphy-publish")
                    name.set(plugin.displayName)
                    description.set(plugin.description)
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
    website = "${extra["web"]}"
    vcsUrl = "${extra["web"]}"
    description = "Gradle plugins for LPhy conventions."

    (plugins) {
        "lphy-java" {
            // id is captured from java-gradle-plugin configuration
            displayName = "Gradle LPhy Java Plugin"
            description = "Use Java 17 and overwrite java related tasks to use module-path."
            tags = listOf("gradle", "lphy", "java")
            version = project.version.toString()
        }
        "lphy-publish" {
            // id is captured from java-gradle-plugin configuration
            displayName = "Gradle Maven Publish Plugin"
            description = "Configure the repositories for maven publishing and proceed signing."
            tags = listOf("gradle", "lphy", "maven-publish", "signing")
            version = project.version.toString()
        }
    }
}

// uncomment below when publishing to local without using platform plugins
//signing {
//    sign(publishing.publications.matching {
//        it!!.name.toLowerCase().contains("lphy")
//                || it.name.toLowerCase().contains("pluginmaven")
//    })
//}

