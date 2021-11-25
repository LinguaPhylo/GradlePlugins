package io.github.linguaphylo.platforms;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository;
import org.gradle.api.publish.plugins.PublishingPlugin;

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

    private boolean hasOSSRHCredentials;

    public void apply(Project project) {
        // plugins {  `maven-publish` signing }
        project.getPlugins().apply(PublishingPlugin.class);

        hasOSSRHCredentials = project.hasProperty(OSSRH_USER) && project.hasProperty(OSSRH_PSWD);
        System.out.println("OSSRH credentials are " + (hasOSSRHCredentials?"":"NOT") + " provided.");

        project.getTasks().withType(PublishToMavenRepository.class)
                .configureEach(pubTo -> {
                    pubTo.doFirst(task -> {
                        // project.getVersion() not working in config
                        boolean isReleaseVersion = !project.getVersion().toString().endsWith(STR_SNAPSHOT);
                        MavenArtifactRepository repo = pubTo.getRepository();
                        if (repo.getName().equalsIgnoreCase("maven")) {
                            if (isReleaseVersion) {
                                try {
                                    repo.setUrl(new URI(OSS_RELEASE));
                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        System.out.println("It is the " + (isReleaseVersion?"release":STR_SNAPSHOT.toLowerCase()) +
                                " version " + project.getVersion() + " publishing to " + pubTo.getName());
                    });
                });

//        PublishingExtension pubExt = project.getExtensions().getByType(PublishingExtension.class);

        // only signing publications whose names contain "lphy"
//        DomainObjectCollection<Publication> lphyPubs = pubExt.getPublications();
//        System.out.println("Find " + lphyPubs.size() + " publication(s) : " + lphyPubs);

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
        // -Possrh.user=myuser -Possrh.pswd=mypswd
//        pubExt.repositories(repo -> {
//            if (hasOSSRHCredentials) {
//                String ossrhUser = String.valueOf(project.property(OSSRH_USER));
//                String ossrhPswd = String.valueOf(project.property(OSSRH_PSWD));
//                // publish to maven cental
//                repo.maven(mvn -> {
//                    mvn.setName("maven");
//                    mvn.credentials(cred -> {
//                        cred.setUsername(ossrhUser);
//                        cred.setPassword(ossrhPswd);
//                    });
//                    mvn.authentication(auth -> {
//                        auth.create("basic", BasicAuthentication.class);
//                    });
//
//                    try {
//                        // set to SNAPSHOT as default,
//                        // if release, then change in withType(PublishToMavenRepository.class)
//                        mvn.setUrl(new URI(OSS_SNAPSHOT));
//                    } catch (URISyntaxException e) {
//                        e.printStackTrace();
//                    }
//                });
//            } else {
//                // if "ossrh.user" not provided, then publish to local
//                repo.maven(mvn -> {
//                    mvn.setName("local");
//                    DirectoryProperty buildDir = project.getLayout().getBuildDirectory();
//                    mvn.setUrl(buildDir.dir("releases"));
//                });
//            }
//        });

        /* signing {
             setRequired({
                isReleaseVersion && gradle.taskGraph.hasTask("publish")
             })
             sign(publishing.publications.matching{ it!!.name.toLowerCase().contains("lphy") })
        } */
        // -Psigning.secretKeyRingFile=/path/to/mysecr.gpg -Psigning.password=mypswd -Psigning.keyId=last8chars
        boolean isSigning = project.hasProperty("signing.password")
                && project.hasProperty("signing.keyId");
//        if (isSigning && lphyPubs.size() > 0) {
//            project.getPlugins().apply(SigningPlugin.class);
//
//            SigningExtension signExt = project.getExtensions().getByType(SigningExtension.class);
//            // only signing publications whose names contain "lphy"
//            signExt.sign( lphyPubs.matching(pub -> pub.getName().toLowerCase().contains("lphy")) );
//        }
    }



}