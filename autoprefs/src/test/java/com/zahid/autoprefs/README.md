# AutoPrefs Unit Testing

This directory contains the unit test implementation for AutoPrefs library. Unit tests allow you to verify the functionality of AutoPrefs without requiring an Android device or emulator.

## Files

- `MockSharedPreferences.kt` - A custom implementation of the SharedPreferences interface that allows testing without Android dependencies
- `AutoPrefsUnitTest.kt` - Unit tests that verify the core functionality of AutoPrefs property delegates

## How Unit Testing Works

Unit tests run on your local JVM rather than on an Android device, which makes them:
- Much faster to execute
- Independent of Android framework availability
- Easier to integrate into continuous integration pipelines

## MockSharedPreferences

The `MockSharedPreferences` class provides an in-memory implementation of Android's SharedPreferences. It:
- Stores all preference values in a simple Map
- Simulates the Editor pattern used by SharedPreferences
- Handles all basic data types (String, Int, Boolean, etc.)

This allows the AutoPrefs delegate functionality to be tested without Android dependencies.

## How to Write Your Own Unit Tests

You can use this same approach to unit test your classes that use AutoPrefs:

```kotlin
@Test
fun testMyClassWithAutoPrefs() {
    // Create the mock and pass it to AutoPrefs
    val mockPrefs = MockSharedPreferences()
    val autoPrefs = AutoPrefs.createForTesting(mockPrefs)
    
    // Create your class, passing the test AutoPrefs instance
    val myClass = MyClass(autoPrefs)
    
    // Test your class functionality
    myClass.saveUserName("TestUser")
    assertEquals("TestUser", myClass.getUserName())
}