import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

val secretPropsFile = rootProject.file("local.properties")
val secretProps = Properties()
if (secretPropsFile.exists()) {
    secretProps.load(FileInputStream(secretPropsFile))
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://central.sonatype.com/api/v1/publisher"))
            username.set(secretProps.getProperty("sonatype.username", ""))
            password.set(secretProps.getProperty("sonatype.password", ""))
            stagingProfileId.set(secretProps.getProperty("sonatype.stagingProfileId", ""))
        }
    }
}