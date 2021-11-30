import java.text.SimpleDateFormat
import java.util.Calendar

plugins {
    `java-library`
    `maven-publish`
//    signing
}

allprojects {
    repositories {
        mavenCentral()
        // add sonatype snapshots repository
//        maven {
//            url=uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
//        }
        // Managing plugin versions via pluginManagement in settings.gradle.kts
//        mavenLocal() // only for testing
    }
}

subprojects {
    var calendar: java.util.Calendar? = Calendar.getInstance()
    var formatter = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss")

    tasks.withType<Jar>() {
        manifest {
            attributes(
                "Implementation-Version" to archiveVersion,
                "Implementation-Title" to "LPhy platforms",
                "Implementation-Vendor" to "Walter Xie",
                "Built-By" to "Walter Xie",
                "Build-Jdk" to JavaVersion.current().majorVersion.toInt(),
                "Built-Date" to formatter.format(calendar?.time)
            )
        }
        // copy LICENSE to META-INF
        metaInf {
            from(rootDir) {
                include("LICENSE")
            }
        }
    }

    extra["webSteam"] = "github.com/LinguaPhylo/GradlePlugins"
    extra["web"] = "https://${extra["webSteam"]}"

    // configure the shared contents in MavenPublication especially POM
    afterEvaluate {
        extensions.configure<PublishingExtension> {
            publications {
                withType<MavenPublication>().all() {
                    pom {
                        // ...
                        url.set("${extra["web"]}")
                        licenses {
                            license {
                                name.set("GNU Lesser General Public License, version 3")
                                url.set("https://www.gnu.org/licenses/lgpl-3.0.txt")
                            }
                        }
                        developers {
                            developer {
                                name.set("Walter Xie")
                                id.set("walterxie")
                            }
                        }
                        // https://central.sonatype.org/publish/requirements/
                        scm {
                            connection.set("scm:git:git://${extra["webSteam"]}.git")
                            developerConnection.set("scm:git:ssh://${extra["webSteam"]}.git")
                            url.set(extra["web"].toString())
                        }
                    }

                }
            }
        }

        // only sign lphy related or main plugin publication
//        extensions.configure<SigningExtension> {
//            sign(publishing.publications.matching{ it!!.name.toLowerCase().contains("lphy")
//                    || it.name.toLowerCase().contains("pluginmaven")  })
//        }
    }

}