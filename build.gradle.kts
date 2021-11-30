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
    var calendar: java.util.Calendar? = java.util.Calendar.getInstance()
    var formatter = java.text.SimpleDateFormat("dd-MMM-yyyy HH:mm:ss")

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
            from (rootDir) {
                include("LICENSE")
            }
        }
    }
}