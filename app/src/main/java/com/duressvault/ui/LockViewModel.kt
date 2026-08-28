package com.duressvault.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duressvault.DuressApp
import com.duressvault.admin.WipeManager
import kotlinx.coroutines.launch

class LockViewModel(application: Application) : AndroidViewModel(application) {

    fun onPinEntered(pin: String) {
        viewModelScope.launch {
            val app = getApplication<DuressApp>()
            val pinChars = pin.toCharArray()
            val isDuress = app.pinRepository.verifyPin(pinChars, isDuress = true)
            if (isDuress) {
                // DURESS: Borrar datos (totales o locales)
                WipeManager.performWipe(app)
            } else {
                val isNormal = app.pinRepository.verifyPin(pinChars, isDuress = false)
                if (isNormal) {
                    // Desbloqueo normal: cerrar actividad (puedes emitir un evento)
                } else {
                    // PIN incorrecto
                }
            }
            pinChars.fill('\u0000')
        }
    }
}
