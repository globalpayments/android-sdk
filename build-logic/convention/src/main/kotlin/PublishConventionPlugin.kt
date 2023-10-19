import com.globalpayments.android.sdk.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import java.net.URI
import java.util.Properties

class PublishConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("maven-publish")
                apply(SigningPlugin::class.java)
            }
            val sdkGroupId = "com.heartlandpaymentsystems"
            val sdkArtifactId = "globalpayments-android-sdk"
            val sdkVersion: String = libs.findVersion("globalPayments-android").get().toString()

            val publishProperties = Properties()
            val publishPropertiesFile = file("publish.properties")
            if (publishPropertiesFile.exists()) {
                publishProperties.load(publishPropertiesFile.inputStream())
            }
            extra["signing.keyId"] = publishProperties["signing.keyId"]
            extra["signing.password"] = publishProperties["signing.password"]
            extra["signing.secretKeyRingFile"] = publishProperties["signing.secretKeyRingFile"]
            extra["mavenCentralRepoUsername"] = publishProperties["mavenCentralRepoUsername"]
            extra["mavenCentralRepoPassword"] = publishProperties["mavenCentralRepoPassword"]

            afterEvaluate {
                extensions.getByType(PublishingExtension::class.java).apply {
                    publications {
                        create<MavenPublication>("release") {
                            from(components["release"])

                            groupId = sdkGroupId
                            artifactId = sdkArtifactId
                            version = sdkVersion

                            pom {
                                name.set(sdkArtifactId)
                                description.set("The official Android SDK for Global Payments API.")
                                url.set("https://developer.globalpay.com")

                                licenses {
                                    license {
                                        name.set("The MIT License (MIT)")
                                        url.set("https://opensource.org/licenses/MIT")
                                    }
                                }

                                developers {
                                    developer {
                                        id.set("globalpayments")
                                        name.set("Global Payments")
                                        email.set("developers@globalpay.com")
                                    }
                                }

                                scm {
                                    connection.set("scm:git:git://github.com/globalpayments/android-sdk.git")
                                    developerConnection.set("scm:git:ssh://github.com/globalpayments/android-sdk.git")
                                    url.set("https://github.com/globalpayments/android-sdk")
                                }
                            }
                        }
                    }

                    repositories {
                        maven {
                            name = "MavenCentral"
                            url = URI.create("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                            credentials {
                                username = extra["mavenCentralRepoUsername"] as? String
                                password = extra["mavenCentralRepoPassword"] as? String
                            }
                        }
                    }
                }
            }
            extensions.getByType(SigningExtension::class.java).apply {
                sign(extensions.getByType(PublishingExtension::class).publications)
            }
        }
    }
}
