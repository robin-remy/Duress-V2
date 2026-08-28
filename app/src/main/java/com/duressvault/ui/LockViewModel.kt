package com.duressvault.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duressvault.DuressApp
import com.duressvault.admin.WipeManager
import kotlinx.coroutines.launch

class LockViewModel : ViewModel() {

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
                    // Desbloqueo normal: cerrar actividad
                    // (Se puede enviar evento a la Activity)
                } else {
                    // PIN incorrecto
                }
            }
            pinChars.fill('\u0000')
        }
    }
}
