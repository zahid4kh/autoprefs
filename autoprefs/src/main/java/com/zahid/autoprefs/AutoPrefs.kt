package com.zahid.autoprefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import androidx.core.content.edit

class AutoPrefs private constructor(private val prefs: SharedPreferences) {

    companion object {
        fun create(context: Context, name: String = "AutoPrefs"): AutoPrefs {
            val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            return AutoPrefs(prefs)
        }
    }

    private val gson = Gson()

    // String Delegate
    fun string(key: String, default: String = ""): ReadWriteProperty<Any, String> =
        object : ReadWriteProperty<Any, String> {
            override fun getValue(thisRef: Any, property: KProperty<*>): String {
                return prefs.getString(key, default) ?: default
            }
            override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
                prefs.edit { putString(key, value) }
            }
        }

    // Int Delegate
    fun int(key: String, default: Int = 0): ReadWriteProperty<Any, Int> =
        object : ReadWriteProperty<Any, Int> {
            override fun getValue(thisRef: Any, property: KProperty<*>): Int {
                return prefs.getInt(key, default)
            }
            override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
                prefs.edit { putInt(key, value) }
            }
        }

    // Boolean Delegate
    fun boolean(key: String, default: Boolean = false): ReadWriteProperty<Any, Boolean> =
        object : ReadWriteProperty<Any, Boolean> {
            override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
                return prefs.getBoolean(key, default)
            }
            override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
                prefs.edit { putBoolean(key, value) }
            }
        }

    // Custom Object Delegate
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

    // Async String Delegate
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