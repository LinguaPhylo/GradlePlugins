package io.github.linguaphylo.platforms;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository;
import org.gradle.api.publish.plugins.PublishingPlugin;
import org.gradle.authentication.http.BasicAuthentication;
import org.gradle.plugins.signing.SigningExtension;
import org.gradle.plugins.signing.SigningPlugin;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Define maven publishing conventions.
 * Require signing and maven-publish plugins.
 * Java conventions are defined separately.
 * @see LPhyJavaPlugin
 *
 * @author Walter Xie
 */
public class LPhyPublishPlugin implements Plugin<Project> {

    public static final String STR_SNAPSHOT = "SNAPSHOT";
    public static final String OSSRH_USER = "ossrh.user";
    public static final String OSSRH_PSWD = "ossrh.pswd";
    public static final String OSS_SNAPSHOT ="https://s01.oss.sonatype.org/content/repositories/snapshots/";
    public static final String OSS_RELEASE ="https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/";
    public static final String PUBLISH_TO_MAVEN_CENTRAL = "maven";
    public static final String PUBLISH_TO_LOCAL = "local";
    public static final String LOCAL_RELEASE_DIR = "releases";

    public void apply(final Project project) {

        // -Possrh.user=myuser -Possrh.pswd=mypswd
        boolean hasOSSRHCredentials = project.hasProperty(OSSRH_USER) && project.hasProperty(OSSRH_PSWD);
        System.out.println("OSSRH credentials are " + (hasOSSRHCredentials?"":"NOT") + " provided.");

        // -Psigning.secretKeyRingFile=/path/to/mysecr.gpg -Psigning.password=mypswd -Psigning.keyId=last8chars
        boolean isSigning = project.hasProperty("signing.password")
                && project.hasProperty("signing.keyId");

        // config repos
        project.getPlugins().withType(PublishingPlugin.class, pubPlugin -> {
            PublishingExtension pubExt = project.getExtensions().getByType(PublishingExtension.class);
            /* repositories {
              maven {
                name = "maven"
                val ossrhUser = findProperty("ossrh.user")
                val ossrhPswd = findProperty("ossrh.pswd")
                credentials {
                    username = "$ossrhUser"
                    password = "$ossrhPswd"
                }
                authentication {
                    create<BasicAuthentication>("basic")
                }
                url = ...
              maven {
                name = "local"
                url = uri(layout.buildDirectory.dir("releases"))
              }
            } */
            pubExt.repositories(repo -> {
                if (hasOSSRHCredentials) {
                    String ossrhUser = String.valueOf(project.property(OSSRH_USER));
                    String ossrhPswd = String.valueOf(project.property(OSSRH_PSWD));
                    // publish to maven central
                    repo.maven(mvn -> {
                        mvn.setName(PUBLISH_TO_MAVEN_CENTRAL);
                        mvn.credentials(cred -> {
                            cred.setUsername(ossrhUser);
                            cred.setPassword(ossrhPswd);
                        });
                        mvn.authentication(auth -> {
                            auth.create("basic", BasicAuthentication.class);
                        });

                        try {
                            // set to SNAPSHOT as default, update in tasks.withType(PublishToMavenRepository.class)
                            mvn.setUrl(new URI(OSS_SNAPSHOT));
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    // if "ossrh.user" not provided, then publish to local
                    repo.maven(mvn -> {
                        mvn.setName(PUBLISH_TO_LOCAL);
                        DirectoryProperty buildDir = project.getLayout().getBuildDirectory();
                        mvn.setUrl(buildDir.dir(LOCAL_RELEASE_DIR));
                    });
                }
            });

        });


        // update url, and signing MavenPublication
        project.getTasks().withType(PublishToMavenRepository.class)
                .configureEach(pubTo -> {
                    pubTo.doFirst(task -> {
                        // project.getVersion() not working in config
                        boolean isReleaseVersion = !project.getVersion().toString().endsWith(STR_SNAPSHOT);
                        MavenArtifactRepository repo = pubTo.getRepository();
                        // if repo name sets to "maven" and is release version, then use deploy url
                        if (PUBLISH_TO_MAVEN_CENTRAL.equalsIgnoreCase(repo.getName())) {
                            if (isReleaseVersion) {
                                try {
                                    repo.setUrl(new URI(OSS_RELEASE));
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        /* signing { sign(publishing.publications.matching{ it!!.name.toLowerCase().contains("lphy") }) } */
                        // if signing properties are provided
                        if (isSigning) {
                            project.getPlugins().apply(SigningPlugin.class);
                            SigningExtension signExt = project.getExtensions().getByType(SigningExtension.class);

                            MavenPublication mavPub = pubTo.getPublication();
                            System.out.println("Signing MavenPublication " + mavPub.getName());
                            signExt.sign( mavPub );
                        }
                        System.out.println("Publishing the " + (isReleaseVersion?"release":STR_SNAPSHOT.toLowerCase()) +
                                " version " + project.getVersion());
                    });
                });

    }

}