# AutoPrefs

[![](https://jitpack.io/v/zahid4kh/autoprefs.svg)](https://jitpack.io/#zahid4kh/autoprefs)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.zahid4kh/autoprefs)](https://central.sonatype.org/artifact/com.zahid/autoprefs)
[![GitHub release](https://img.shields.io/github/v/release/zahid4kh/autoprefs)](https://github.com/zahid4kh/autoprefs/releases)
[![License](https://img.shields.io/github/license/zahid4kh/autoprefs)](https://github.com/zahid4kh/autoprefs/blob/main/LICENSE)

A lightweight, Kotlin-idiomatic library to simplify working with `SharedPreferences` in Android apps. AutoPrefs eliminates boilerplate by using property delegation, supports type-safe operations, and offers advanced features like custom object serialization and asynchronous writes.

## Features

- **Delegated Properties**: Bind preferences to variables with `by` syntax
- **Type Safety**: Built-in support for String, Int, Boolean, and custom objects
- **Default Values**: Specify defaults to avoid manual null/missing value handling
- **Custom Objects**: Serialize/deserialize complex objects with Gson
- **Async Support**: Write preferences asynchronously using coroutines
- **Easy Setup**: Minimal configuration with sensible defaults

## Installation

### Option 1: Maven Central

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.zahid:autoprefs:1.0.2")
}
```

## Option 2: JitPack

Add JitPack repository to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Then add the dependency:

```kotlin
dependencies {
    implementation("com.github.zahid4kh:autoprefs:1.0.2")
}
```
