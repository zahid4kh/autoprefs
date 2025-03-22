package com.zahid.autoprefs

import android.content.SharedPreferences

class MockSharedPreferences : SharedPreferences {
    private val valueMap = mutableMapOf<String, Any?>()
    private val mockEditor = MockEditor()

    override fun edit(): SharedPreferences.Editor = mockEditor

    override fun getAll(): Map<String, *> = valueMap

    override fun getBoolean(key: String, defValue: Boolean): Boolean =
        valueMap[key] as? Boolean ?: defValue

    override fun getFloat(key: String, defValue: Float): Float =
        valueMap[key] as? Float ?: defValue

    override fun getInt(key: String, defValue: Int): Int =
        valueMap[key] as? Int ?: defValue

    override fun getLong(key: String, defValue: Long): Long =
        valueMap[key] as? Long ?: defValue

    override fun getString(key: String, defValue: String?): String? =
        valueMap[key] as? String ?: defValue

    override fun getStringSet(key: String, defValues: MutableSet<String>?): MutableSet<String>? =
        valueMap[key] as? MutableSet<String> ?: defValues

    override fun contains(key: String): Boolean = valueMap.containsKey(key)

    override fun registerOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?
    ) {}

    override fun unregisterOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?
    ) {}

    inner class MockEditor : SharedPreferences.Editor {
        private val tempMap = mutableMapOf<String, Any?>()

        override fun clear(): SharedPreferences.Editor {
            tempMap.clear()
            return this
        }

        override fun commit(): Boolean {
            valueMap.putAll(tempMap)
            tempMap.clear()
            return true
        }

        override fun apply() {
            commit()
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            tempMap[key] = value
            return this
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            tempMap[key] = value
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            tempMap[key] = value
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            tempMap[key] = value
            return this
        }

        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            tempMap[key] = value
            return this
        }

        override fun putStringSet(
            key: String,
            values: MutableSet<String>?
        ): SharedPreferences.Editor {
            tempMap[key] = values
            return this
        }

        override fun remove(key: String): SharedPreferences.Editor {
            tempMap.remove(key)
            return this
        }
    }
}