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

}