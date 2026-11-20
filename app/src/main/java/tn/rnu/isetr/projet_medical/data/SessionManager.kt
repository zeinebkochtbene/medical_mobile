package tn.rnu.isetr.projet_medical.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("medirdv_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val USER_TOKEN = "user_token"
        private const val USER_NAME = "user_name"
        private const val USER_EMAIL = "user_email"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(USER_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(USER_TOKEN, null)

    fun isLoggedIn(): Boolean = getToken() != null

    fun saveUserName(name: String) {
        prefs.edit().putString(USER_NAME, name).apply()
    }

    fun getUserName(): String? = prefs.getString(USER_NAME, null)

    fun saveUserEmail(email: String) {
        prefs.edit().putString(USER_EMAIL, email).apply()
    }

    fun getUserEmail(): String? = prefs.getString(USER_EMAIL, null)

    fun clear() {
        prefs.edit().clear().apply()
    }
}