package com.zahid.autoprefs

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import androidx.core.content.edit

/**
 * AutoPrefs is a lightweight, Kotlin-idiomatic library that simplifies working with SharedPreferences.
 * It uses Kotlin's property delegation to provide type-safe access to preferences with minimal boilerplate code.
 *
 * Usage example:
 * ```kotlin
 * // In an Activity or other context-aware class
 * class MainActivity : AppCompatActivity() {
 *
 *     // Create a nested class for preferences
 *     inner class Preferences {
 *         private val prefs = AutoPrefs.create(this@MainActivity, "UserPrefs")
 *
 *         // Delegate properties to SharedPreferences
 *         var username by prefs.string("username", "Guest")
 *         var loginCount by prefs.int("login_count", 0)
 *         var isPremium by prefs.boolean("is_premium", false)
 *         var userData by prefs.custom("user_data", UserData(), UserData::class.java)
 *         var lastSync by prefs.stringAsync("last_sync", "")
 *     }
 *
 *     // Initialize when context is available
 *     private lateinit var preferences: Preferences
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         preferences = Preferences()
 *
 *         // Use as regular properties
 *         preferences.loginCount++
 *         preferences.username = "John"
 *     }
 * }
 * ```
 */
class AutoPrefs private constructor(private val prefs: SharedPreferences) {

    /**
     * Factory methods for creating AutoPrefs instances.
     */
    companion object {
        /**
         * Creates an instance of AutoPrefs with the specified SharedPreferences name.
         *
         * @param context The Android context used to access SharedPreferences.
         * @param name The name of the SharedPreferences file. Defaults to "AutoPrefs".
         * @return A new AutoPrefs instance.
         */
        fun create(context: Context, name: String = "AutoPrefs"): AutoPrefs {
            val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            return AutoPrefs(prefs)
        }

        @VisibleForTesting
        fun createForTesting(sharedPreferences: SharedPreferences): AutoPrefs {
            return AutoPrefs(sharedPreferences)
        }

    }

    private val gson = Gson()

    /**
     * Creates a String property delegate that reads from and writes to SharedPreferences.
     *
     * @param key The key to use for storing the value in SharedPreferences.
     * @param default The default value to return if the key doesn't exist. Defaults to empty string.
     * @return A property delegate that handles getting and setting the value in SharedPreferences.
     *
     * Example:
     * ```
     * var username by prefs.string("username", "Guest")
     * ```
     */
    fun string(key: String, default: String = ""): ReadWriteProperty<Any, String> =
        object : ReadWriteProperty<Any, String> {
            override fun getValue(thisRef: Any, property: KProperty<*>): String {
                return prefs.getString(key, default) ?: default
            }
            override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
                prefs.edit { putString(key, value) }
            }
        }

    /**
     * Creates an Int property delegate that reads from and writes to SharedPreferences.
     *
     * @param key The key to use for storing the value in SharedPreferences.
     * @param default The default value to return if the key doesn't exist. Defaults to 0.
     * @return A property delegate that handles getting and setting the value in SharedPreferences.
     *
     * Example:
     * ```
     * var counter by prefs.int("counter", 0)
     * ```
     */
    fun int(key: String, default: Int = 0): ReadWriteProperty<Any, Int> =
        object : ReadWriteProperty<Any, Int> {
            override fun getValue(thisRef: Any, property: KProperty<*>): Int {
                return prefs.getInt(key, default)
            }
            override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
                prefs.edit { putInt(key, value) }
            }
        }

    /**
     * Creates a Boolean property delegate that reads from and writes to SharedPreferences.
     *
     * @param key The key to use for storing the value in SharedPreferences.
     * @param default The default value to return if the key doesn't exist. Defaults to false.
     * @return A property delegate that handles getting and setting the value in SharedPreferences.
     *
     * Example:
     * ```
     * var isFirstRun by prefs.boolean("is_first_run", true)
     * ```
     */
    fun boolean(key: String, default: Boolean = false): ReadWriteProperty<Any, Boolean> =
        object : ReadWriteProperty<Any, Boolean> {
            override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
                return prefs.getBoolean(key, default)
            }
            override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
                prefs.edit { putBoolean(key, value) }
            }
        }

    /**
     * Creates a custom object property delegate that reads from and writes to SharedPreferences.
     * The object is serialized to/from JSON using Gson.
     *
     * @param key The key to use for storing the value in SharedPreferences.
     * @param default The default value to return if the key doesn't exist.
     * @param clazz The Java Class of the object type for deserialization.
     * @return A property delegate that handles getting and setting the value in SharedPreferences.
     *
     * Example:
     * ```
     * data class UserProfile(val name: String, val age: Int)
     * var profile by prefs.custom("profile", UserProfile("Guest", 0), UserProfile::class.java)
     * ```
     */
    fun <T> custom(key: String, default: T, clazz: Class<T>): ReadWriteProperty<Any, T> =
        object : ReadWriteProperty<Any, T> {
            override fun getValue(thisRef: Any, property: KProperty<*>): T {
                val json = prefs.getString(key, null)
                return if (json != null) gson.fromJson(json, clazz) else default
            }
            override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
                val json = gson.toJson(value)
                prefs.edit { putString(key, json) }
            }
        }

    /**
     * Creates a String property delegate that reads from and writes to SharedPreferences asynchronously.
     * Write operations are performed on the IO dispatcher to avoid blocking the main thread.
     *
     * @param key The key to use for storing the value in SharedPreferences.
     * @param default The default value to return if the key doesn't exist. Defaults to empty string.
     * @return A property delegate that handles getting and setting the value in SharedPreferences.
     *
     * Example:
     * ```
     * var lastSyncTime by prefs.stringAsync("last_sync", "Never")
     * ```
     */
    fun stringAsync(key: String, default: String = ""): ReadWriteProperty<Any, String> =
        object : ReadWriteProperty<Any, String> {
            override fun getValue(thisRef: Any, property: KProperty<*>): String {
                return prefs.getString(key, default) ?: default
            }
            override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
                runBlocking {
                    withContext(Dispatchers.IO) {
                        prefs.edit(commit = true) { putString(key, value) }
                    }
                }
            }
        }
}