package com.duressvault.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PinRepository(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("duress_pins", Context.MODE_PRIVATE)

    suspend fun setPin(pin: CharArray, isDuress: Boolean) = withContext(Dispatchers.IO) {
        val result = PinHasher.hashPin(pin)
        val editor = prefs.edit()
        val prefix = if (isDuress) "duress" else "normal"
        editor.putString("${prefix}_hash", Base64.encodeToString(result.hash, Base64.NO_WRAP))
        editor.putString("${prefix}_salt", Base64.encodeToString(result.salt, Base64.NO_WRAP))
        editor.commit()
        pin.fill('\u0000')
    }

    suspend fun verifyPin(pin: CharArray, isDuress: Boolean): Boolean = withContext(Dispatchers.IO) {
        val prefix = if (isDuress) "duress" else "normal"
        val hashB64 = prefs.getString("${prefix}_hash", null) ?: return@withContext false
        val saltB64 = prefs.getString("${prefix}_salt", null) ?: return@withContext false
        val hash = Base64.decode(hashB64, Base64.NO_WRAP)
        val salt = Base64.decode(saltB64, Base64.NO_WRAP)
        val result = PinHasher.verifyPin(pin, salt, hash)
        pin.fill('\u0000')
        result
    }

    fun hasPinsConfigured(): Boolean {
        return prefs.contains("normal_hash") && prefs.contains("duress_hash")
    }
}
