package com.zahid.autoprefs.sample


import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import android.content.Context

@RunWith(AndroidJUnit4::class)
class UserSettingsTest {
    private lateinit var userSettings: UserSettings
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext

        // Clear existing preferences
        context.getSharedPreferences("user_settings", Context.MODE_PRIVATE)
            .edit().clear().apply()

        // Create a fresh UserSettings instance
        userSettings = UserSettings(context)
    }

    @Test
    fun testUsernameDefaultValue() {
        assertEquals("Guest", userSettings.getUsername())
    }

    @Test
    fun testUsernameStorage() {
        userSettings.setUsername("JohnDoe")
        assertEquals("JohnDoe", userSettings.getUsername())
        val newInstance = UserSettings(context)
        assertEquals("JohnDoe", newInstance.getUsername())
    }

    @Test
    fun testDarkModeToggle() {
        assertFalse(userSettings.isDarkModeEnabled())
        userSettings.setDarkMode(true)
        assertTrue(userSettings.isDarkModeEnabled())
        val newInstance = UserSettings(context)
        assertTrue(newInstance.isDarkModeEnabled())
    }

    @Test
    fun testFontSizeSettings() {
        assertEquals(14, userSettings.getFontSize())
        userSettings.setFontSize(18)
        assertEquals(18, userSettings.getFontSize())
        val newInstance = UserSettings(context)
        assertEquals(18, newInstance.getFontSize())
    }

    @Test
    fun testMultipleChanges() {
        // Change multiple settings
        userSettings.setUsername("AliceSmith")
        userSettings.setDarkMode(true)
        userSettings.setFontSize(16)

        // Verify all changes
        assertEquals("AliceSmith", userSettings.getUsername())
        assertTrue(userSettings.isDarkModeEnabled())
        assertEquals(16, userSettings.getFontSize())

        // Verify all changes actually persist
        val newInstance = UserSettings(context)
        assertEquals("AliceSmith", newInstance.getUsername())
        assertTrue(newInstance.isDarkModeEnabled())
        assertEquals(16, newInstance.getFontSize())
    }

    @Test
    fun testAsyncStorageWorks() {
        userSettings.recordLogin()

        val loginTime = userSettings.getLastLoginTime()

        // Should not be empty
        assertFalse(loginTime.isEmpty())
        assertNotNull(java.time.Instant.parse(loginTime))

        val newInstance = UserSettings(context)
        assertEquals(loginTime, newInstance.getLastLoginTime())
    }
}