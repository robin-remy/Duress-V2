package com.duressvault.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duressvault.DuressApp
import com.duressvault.admin.WipeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class PinResult {
    NORMAL,
    DURESS,
    INCORRECT
}

class LockViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Verifica el PIN y devuelve el resultado.
     * Si es DURESS, ejecuta el wipe en background.
     */
    suspend fun verifyPin(pin: String): PinResult {
        val app = getApplication<DuressApp>()
        val pinChars = pin.toCharArray()
        return try {
            val isDuress = withContext(Dispatchers.IO) {
                app.pinRepository.verifyPin(pinChars, isDuress = true)
            }
            if (isDuress) {
                // DURESS: Ejecutar wipe (local o total)
                WipeManager.performWipe(app)
                PinResult.DURESS
            } else {
                val isNormal = withContext(Dispatchers.IO) {
                    app.pinRepository.verifyPin(pinChars, isDuress = false)
                }
                if (isNormal) {
                    PinResult.NORMAL
                } else {
                    PinResult.INCORRECT
                }
            }
        } finally {
            pinChars.fill('\u0000')
        }
    }
}
