# AutoPrefs

[![](https://jitpack.io/v/zahid4kh/autoprefs.svg)](https://jitpack.io/#zahid4kh/autoprefs)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.zahid4kh/autoprefs)](https://central.sonatype.com/artifact/io.github.zahid4kh/autoprefs)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)


A lightweight, Kotlin-idiomatic library to simplify working with `SharedPreferences` in Android apps. AutoPrefs eliminates boilerplate by using property delegation, supports type-safe operations, and offers advanced features like custom object serialization and asynchronous writes.

## Features

- **Delegated Properties**: Bind preferences to variables with `by` syntax
- **Type Safety**: Built-in support for String, Int, Boolean, and custom objects
- **Default Values**: Specify defaults to avoid manual null/missing value handling
- **Custom Objects**: Serialize/deserialize complex objects with Gson
- **Async Support**: Write preferences asynchronously using coroutines
- **Easy Setup**: Minimal configuration with sensible defaults

## Installation [](#installation)

### Option 1: Maven Central

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.zahid4kh:autoprefs:1.0.3")
}
```

### Option 2: JitPack

Add JitPack repository to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {  
   repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) repositories {  
     mavenCentral() maven { url = uri("https://jitpack.io") }  
   }
}  
```

Then add the dependency:

```kotlin
dependencies {  
    implementation("com.github.zahid4kh:autoprefs:v1.0.3")
}  
```

## Usage [](#usage)

### Example android app

- `MainActivity.kt`

**Create inner class** `Preferences`:

```kotlin
inner class Preferences() {  
    private val prefs = AutoPrefs.create(this@MainActivity, "UserPrefs")  

    //examples
    var username by prefs.string("username", "Guest")  // string property
    var loginCount by prefs.int("login_count", 0)  // int pproperty
    var isFirstRun by prefs.boolean("first_run", true)  // boolean property
    var userProfile by prefs.custom("profile", UserProfile("Guest", 18, false), UserProfile::class.java)  // custom property
    var lastLoginTime by prefs.stringAsync("last_login", "Never")  // async property
}
```

Create a data class for **custom property** example:

```kotlin
data class UserProfile(
    val name: String,
    val age: Int,
    val isPremium: Boolean
)
```

**Declare a variable, and initialize it later in `onCreate`**

```kotlin
private lateinit var preferences: Preferences
```

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
     super.onCreate(savedInstanceState)
     enableEdgeToEdge()

     // Initializing preferences when Context is available
     preferences = Preferences()

     // Check if first run and update values accordingly
    // use like normal variables
     if (preferences.isFirstRun) {
         preferences.loginCount += 1
         preferences.isFirstRun = false
         preferences.lastLoginTime = Instant.now().toString()
     }

     setContent {
         AutoPrefsTheme {
             Surface(
                 modifier = Modifier.fillMaxSize(),
                 color = MaterialTheme.colorScheme.background
             ) {
                 PreferencesScreen(
                     initialUsername = preferences.username,
                     initialLoginCount = preferences.loginCount,
                     initialIsFirstRun = preferences.isFirstRun,
                     initialUserProfile = preferences.userProfile,
                     initialLastLoginTime = preferences.lastLoginTime,

                     // triggered when save button is clicked
                     onSavePreferences = { newUsername, newLoginCount, newIsFirstRun, newUserProfile, newLastLoginTime ->
                         preferences.username = newUsername
                         preferences.loginCount = newLoginCount
                         preferences.isFirstRun = newIsFirstRun
                         preferences.userProfile = newUserProfile
                         preferences.lastLoginTime = newLastLoginTime 
                     }
                 ) 
             } 
         } 
     } 
}
```

- `appdemo/PrefsScreen.kt` :

  **Save Changes button**:

```kotlin
onClick = {
  // Updates all preferences
  val now = Instant.now().toString()
  onSavePreferences(
      userNameState,
      loginCountState,
      isFirstRunState,
      UserProfile(
          profileNameState,
          profileAgeState.toIntOrNull() ?: 0,
          profileIsPremiumState
      ),
      now
  )
  lastLoginState = formatInstant(now)
  showSavedMessage = true
}
```



UI file: [appdemo/PrefsScreen.kt](appdemo/PrefsScreen.kt)

demo apk file: [appdemo/autoprefsdemo.apk](appdemo/autoprefsdemo.apk)



## Testing [](#testing)

AutoPrefs is designed to be easily testable in both unit tests and instrumented tests.

### Testing Options

1. **Unit Tests** - Run on the JVM without needing an Android device
   - Located in `autoprefs/src/test/java/com/zahid/autoprefs/`
   - Uses a mock implementation of SharedPreferences
   - Faster execution, ideal for testing business logic

2. **Instrumented Tests** - Run on an Android device or emulator
   - Located in `autoprefs/src/androidTest/java/com/zahid/autoprefs/sample/`
   - Uses actual SharedPreferences implementation
   - Verifies real-world behavior on Android

### Example Code

- [Unit Tests](autoprefs/src/test/java/com/zahid/autoprefs/AutoPrefsUnitTest.kt): Shows how to test AutoPrefs using a [mock implementation](autoprefs/src/test/java/com/zahid/autoprefs/MockSharedPreferences.kt)
- [Instrumented Tests](autoprefs/src/androidTest/java/com/zahid/autoprefs/sample/UserSettingsTest.kt): Shows how to test a [class that uses AutoPrefs](autoprefs/src/androidTest/java/com/zahid/autoprefs/sample/UserSettings.kt) on an Android device

### Making Your Code Testable

For best testability, structure your classes to accept an AutoPrefs instance in the constructor:

```kotlin
class UserSettings {
    private val prefs: AutoPrefs
    
    // production use
    constructor(context: Context) {
        prefs = AutoPrefs.create(context, "user_settings")
    }
    
    // testing
    constructor(testPrefs: AutoPrefs) {
        prefs = testPrefs
    }
    
    // Rest of your class...
}
```


## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.