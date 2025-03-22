package com.zahid.autoprefs

import org.junit.Test
import org.junit.Assert.*

class AutoPrefsUnitTest {
    @Test
    fun testStringDelegateWorks() {
        val mockPrefs = MockSharedPreferences()
        val autoPrefs = AutoPrefs.createForTesting(mockPrefs)

        // Create a test object with a delegated property
        class TestObject {
            var testString by autoPrefs.string("test_key", "default")
        }

        val testObject = TestObject()

        // Test default value
        assertEquals("default", testObject.testString)

        // Test setting a value
        testObject.testString = "new value"
        assertEquals("new value", testObject.testString)

        // Test persistence (create a new object)
        val newObject = TestObject()
        assertEquals("new value", newObject.testString)
    }

    @Test
    fun testIntDelegateWorks() {
        val mockPrefs = MockSharedPreferences()
        val autoPrefs = AutoPrefs.createForTesting(mockPrefs)

        class TestObject {
            var testInt by autoPrefs.int("test_int", 42)
        }

        val testObject = TestObject()
        assertEquals(42, testObject.testInt)

        testObject.testInt = 100
        assertEquals(100, testObject.testInt)

        val newObject = TestObject()
        assertEquals(100, newObject.testInt)
    }
}