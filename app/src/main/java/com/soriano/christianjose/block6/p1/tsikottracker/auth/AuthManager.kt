package com.soriano.christianjose.block6.p1.tsikottracker.auth

import android.content.Context

class AuthManager(private val context : Context) {
    fun storeToken(token: String) {
        val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("authToken", token).apply()
    }

    fun getStoredToken(): String? {
        val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return sharedPreferences.getString("authToken", null)
    }
}