# AutoPrefs Testing Examples

This directory contains examples showing how to test code that uses the AutoPrefs library.

## Files

- `UserSettings.kt` - A sample class that uses AutoPrefs to manage application settings
- `UserSettingsTest.kt` - A comprehensive test suite for the UserSettings class

## Testing Your Code with AutoPrefs

AutoPrefs can be easily tested using standard Android instrumented tests. To test your own code that uses AutoPrefs:

1. Create test classes in your project's `src/androidTest` directory
2. Use `InstrumentationRegistry.getInstrumentation().targetContext` to get a valid Context
3. Initialize your classes that use AutoPrefs with this context
4. Write assertions to verify that preferences are correctly saved and retrieved

### Example Test Structure

```kotlin
@RunWith(AndroidJUnit4::class)
class YourClassTest {
    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Clear existing preferences before each test
        context.getSharedPreferences("your_prefs_name", Context.MODE_PRIVATE)
            .edit().clear().apply()
        
        // Initialize your class with the context
        yourClass = YourClass(context)
    }
    
    @Test
    fun testPreferenceSaving() {
        // Set a preference
        yourClass.setSomeSetting("value")
        
        // Verify it's saved
        assertEquals("value", yourClass.getSomeSetting())
        
        // Create a new instance to verify persistence
        val newInstance = YourClass(context)
        assertEquals("value", newInstance.getSomeSetting())
    }
}