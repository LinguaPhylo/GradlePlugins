# Gradle Plugins

Define lphy extension conventions and share the build logic.

The released versions are available in the Maven Central Repository:
https://search.maven.org/artifact/io.github.linguaphylo/platforms.

## 1) lphy-java

### Usage

```kotlin
plugins {
    id("io.github.linguaphylo.lphy-java") version "0.1.0"
}
```

### Features

1. Constrain to Java 16 or higher, and overwrite java related tasks to use module-path.
2. Configure `compileJava` task to use module-path to compile classes;
3. Configure `javadoc` task to use html5 which solves the issue using >= Java 9, and also turn off warnings.


## 2) lphy-publish

### Usage

```kotlin
plugins {
    id("io.github.linguaphylo.lphy-publish") version "0.1.0"
}
```

### Features

1. Configure publishing repositories;

If there are OSSRH properties provided, then publish to the Maven releases repository
if version does not end with "SNAPSHOT",
otherwise publish to the Maven snapshots repository. 
If no OSSRH properties, then publish to the local folder "releases" under the build directory.

2. Signing the publications defined in the project, if there are signing properties provided.

### Command line

Then, use the following command to publish, where `-P` defines the Gradle properties,
and where the backslash is used to break lines :

```bash
./gradlew clean
./gradlew publish \
    -Psigning.secretKeyRingFile=/path/to/mysecr.gpg \
    -Psigning.password=mypswd -Psigning.keyId=last8chars \
    -Possrh.user=myuser -Possrh.pswd=mypswd
```

The first three properties for signing by [GPG](https://central.sonatype.org/publish/requirements/gpg/).
The last two are used to login your JIRA account in
[Sonatype](https://central.sonatype.org/publish/publish-guide/).

See also [lphy developer's note](https://github.com/LinguaPhylo/linguaPhylo/blob/master/DEV_NOTE.md).

## How to develop Gradle plugins 

- [Developing Custom Gradle Plugins](https://docs.gradle.org/current/userguide/custom_plugins.html)
