# Gradle Plugins

Define lphy extension conventions and share the build logic.

This is derived from the example of 
[sharing build logic](https://docs.gradle.org/current/samples/sample_publishing_convention_plugins.html).

## Usage

```kotlin
plugins {
    id("lphy.platforms.lphy-publish") version "0.0.1-SNAPSHOT"
}
```

Then, use the following command to publish,
where the backslash is used to break lines :

```bash
./gradlew publish \
    -Psigning.secretKeyRingFile=/path/to/mysecr.gpg \
    -Psigning.password=mypswd -Psigning.keyId=last8chars \
    -Possrh.user=myuser -Possrh.pswd=mypswd
```

The first three properties for signing by [GPG](https://central.sonatype.org/publish/requirements/gpg/).
The last two are used to login your JIRA account in
[Sonatype](https://central.sonatype.org/publish/publish-guide/).

See also [lphy developer's note](https://github.com/LinguaPhylo/linguaPhylo/blob/master/DEV_NOTE.md).