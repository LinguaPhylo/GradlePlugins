rootProject.name = "GradlePlugins"

include("platforms")

pluginManagement {
    repositories {
        maven {
            // to local build/plugins
            url = uri("platforms/build/plugins/")
            println("Temp repo : ${url}")
        }
        gradlePluginPortal()
    }
}
