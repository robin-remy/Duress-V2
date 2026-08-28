package com.duressvault.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duressvault.DuressApp
import com.duressvault.ui.SetupActivity
import kotlinx.coroutines.delay

@Composable
fun LockScreen(onSetupClick: () -> Unit, viewModel: LockViewModel = viewModel()) {
    var pin by remember { mutableStateOf("") }
    var showFakeProgress by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var needsSetup by remember { mutableStateOf(true) }  // Inicialmente asumimos que falta config

    val context = LocalContext.current
    val app = context.applicationContext as DuressApp
    val pinRepository = app.pinRepository

    // Comprobar si hay PINs configurados al entrar
    LaunchedEffect(Unit) {
        needsSetup = !pinRepository.hasPinsConfigured()
    }

    // Si no hay PINs, mostrar botón de configuración
    if (needsSetup) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("No hay PIN configurado", color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onSetupClick) {
                Text("Configurar")
            }
        }
        return
    }

    // Efecto para mostrar progreso falso y luego verificar
    LaunchedEffect(showFakeProgress) {
        if (showFakeProgress) {
            delay(1200)
            val result = viewModel.verifyPin(pin)
            if (result == PinResult.DURESS) {
                // El ViewModel ya ejecuta el wipe en background
                // No hacemos nada aquí; la app se cerrará o seguirá
            } else if (result == PinResult.NORMAL) {
                // Desbloqueo normal: cerrar actividad
                (context as? android.app.Activity)?.finish()
            } else {
                // PIN incorrecto
                error = true
                pin = ""
            }
            showFakeProgress = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Introduce el PIN",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = pin,
            onValueChange = { newPin ->
                pin = newPin.take(6)
                if (pin.length == 6) {
                    showFakeProgress = true
                }
            },
            modifier = Modifier.fillMaxWidth(0.6f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            isError = error,
            label = { Text("PIN", color = Color.White) },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.DarkGray,
                unfocusedContainerColor = Color.DarkGray,
                cursorColor = Color.White,
                errorCursorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White
            )
        )
        if (error) {
            Text("PIN incorrecto", color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
        if (showFakeProgress) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(color = Color.White)
            Text("Desbloqueando...", color = Color.White)
        }
    }
}
