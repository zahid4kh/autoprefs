import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
    id("signing")
}

val versionPropsFile = file("version.properties")
val versionProps = Properties()
if (versionPropsFile.exists()) {
    versionProps.load(FileInputStream(versionPropsFile))
}

val majorVersion = versionProps.getProperty("majorVersion", "1")
val minorVersion = versionProps.getProperty("minorVersion", "0")
val patchVersion = versionProps.getProperty("patchVersion", "0")
val libraryVersion = "$majorVersion.$minorVersion.$patchVersion"

android {
    namespace = "com.zahid.autoprefs"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        buildConfigField("String", "VERSION_NAME", "\"$libraryVersion\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = false
        buildConfig = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(libs.gson)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

group = "com.zahid"
version = libraryVersion

val secretPropsFile = rootProject.file("local.properties")
val secretProps = Properties()
if (secretPropsFile.exists()) {
    secretProps.load(FileInputStream(secretPropsFile))
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.zahid"
            artifactId = "autoprefs"
            version = libraryVersion

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set("AutoPrefs")
                description.set("A lightweight, Kotlin-idiomatic library to simplify working with SharedPreferences in Android apps.")
                url.set("https://github.com/zahid4kh/autoprefs")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("zahid4kh")
                        name.set("Zahid Khalilov")
                        email.set("funroboticshere@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:github.com/zahid4kh/autoprefs.git")
                    developerConnection.set("scm:git:ssh://github.com/zahid4kh/autoprefs.git")
                    url.set("https://github.com/zahid4kh/autoprefs/tree/main")
                }
            }
        }
    }

    repositories {
        maven {
            name = "localRelease"
            url = uri("${layout.buildDirectory}/repos/releases")
        }

        maven {
            name = "OSSRH"
            url = uri(
                if (version.toString().endsWith("SNAPSHOT")) {
                    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                } else {
                    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                }
            )

            credentials {
                username = secretProps.getProperty("ossrhUsername", "")
                password = secretProps.getProperty("ossrhPassword", "")
            }
        }
    }
}

signing {
    setRequired({ gradle.taskGraph.hasTask("publish") })

    val signingKey = secretProps.getProperty("signing.key", "")
    val signingPassword = secretProps.getProperty("signing.password", "")

    if (signingKey.isNotEmpty() && signingPassword.isNotEmpty()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }

    sign(publishing.publications["release"])
}