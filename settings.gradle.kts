rootProject.name = "GradlePlugins"

include("platforms")

pluginManagement {
    repositories {
        mavenCentral()
        // uncomment if loading plugin from the local repo
//        maven {
//            name = "local-release"
//            // to local build/plugins
//            url = uri("platforms/build/releases/")
//            println("Temp repo : ${url}")
//        }
        // uncomment if loading plugin from the maven snapshots repository
//        maven {
//            url=uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
//        }
        gradlePluginPortal()
    }
}
