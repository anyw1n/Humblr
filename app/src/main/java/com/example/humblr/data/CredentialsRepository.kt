package com.example.humblr.data

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CredentialsRepository @Inject constructor(
    private val prefs: SharedPreferences
) {

    private var _token: String? = null
    var token: String?
        get() {
            if (_token == null) {
                _token = prefs.getString(Key, null)
            }
            println(_token)
            return _token
        }
        set(value) {
            _token = value
            prefs.edit { putString(Key, value) }
        }

    private companion object {
        const val Key = "token"
    }
}
