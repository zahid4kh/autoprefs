package com.zahid.autoprefs.sample

import android.content.Context
import com.zahid.autoprefs.AutoPrefs

class UserSettings(context: Context) {
    private val prefs = AutoPrefs.create(context, "user_settings")

    private inner class Preferences {
        var username by prefs.string("username", "Guest")
        var isDarkMode by prefs.boolean("dark_mode", false)
        var fontSize by prefs.int("font_size", 14)
        var lastLoginTimestamp by prefs.stringAsync("last_login", "")
    }

    private val preferences = Preferences()

    fun getUsername(): String = preferences.username

    fun setUsername(name: String) {
        preferences.username = name
    }

    fun isDarkModeEnabled(): Boolean = preferences.isDarkMode

    fun setDarkMode(enabled: Boolean) {
        preferences.isDarkMode = enabled
    }

    fun getFontSize(): Int = preferences.fontSize

    fun setFontSize(size: Int) {
        preferences.fontSize = size
    }

    fun recordLogin() {
        preferences.lastLoginTimestamp = java.time.Instant.now().toString()
    }

    fun getLastLoginTime(): String = preferences.lastLoginTimestamp
}