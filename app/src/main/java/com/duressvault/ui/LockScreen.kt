package com.duressvault.ui

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
import com.duressvault.data.PinRepository
import kotlinx.coroutines.delay

@Composable
fun LockScreen(onSetupClick: () -> Unit, viewModel: LockViewModel = viewModel()) {
    var pin by remember { mutableStateOf("") }
    var showFakeProgress by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val pinRepository = (context.applicationContext as com.duressvault.DuressApp).pinRepository
    val hasPins = remember { pinRepository.hasPinsConfigured() }

    if (!hasPins) {
        // Mostrar botón para configurar
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

    LaunchedEffect(showFakeProgress) {
        if (showFakeProgress) {
            delay(1500)
            viewModel.onPinEntered(pin)
            showFakeProgress = false
            pin = ""
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
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.DarkGray,
                unfocusedContainerColor = Color.DarkGray,
                cursorColor = Color.White,
                errorCursorColor = Color.White
            )
        )
        if (showFakeProgress) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(color = Color.White)
            Text("Desbloqueando...", color = Color.White)
        }
    }
}
