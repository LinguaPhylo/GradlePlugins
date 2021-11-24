# Gradle Plugins

Define lphy extension conventions and share the build logic.

## lphy-java

Use Java 16 and overwrite java related tasks to use module-path.

```kotlin
plugins {
    id("io.github.linguaphylo.lphy-java") version "0.1.0"
}
```

## lphy-publish

```kotlin
plugins {
    id("io.github.linguaphylo.lphy-publish") version "0.1.0"
}
```

Then, use the following command to publish,
where the backslash is used to break lines :

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
