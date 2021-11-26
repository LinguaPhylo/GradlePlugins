rootProject.name = "GradlePlugins"

include("platforms")

pluginManagement {
    repositories {
//        maven {
//            // to local build/plugins
//            url = uri("platforms/build/releases/")
//            println("Temp repo : ${url}")
//        }
        // add sonatype snapshots repository
        maven {
            url=uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        }
        gradlePluginPortal()
    }
}
