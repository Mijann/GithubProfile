package com.mijan.dev.githubprofile.manager

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.mijan.dev.githubprofile.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferenceManager @Inject constructor(
    @ApplicationContext context: Context,
) {

    private val sharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREFERENCE_PASSPHRASE, MODE_PRIVATE)

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

}