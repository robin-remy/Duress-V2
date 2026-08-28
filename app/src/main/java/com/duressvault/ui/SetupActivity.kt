package com.duressvault.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.duressvault.DuressApp
import kotlinx.coroutines.launch

class SetupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SetupScreen()
        }
    }
}

@Composable
fun SetupScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as DuressApp
    var pin1 by remember { mutableStateOf("") }
    var pin2 by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Configuración de PINs", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = pin1,
            onValueChange = { pin1 = it.take(6) },
            label = { Text("PIN normal") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = pin2,
            onValueChange = { pin2 = it.take(6) },
            label = { Text("PIN duress") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (pin1.length < 4 || pin2.length < 4) {
                error = "El PIN debe tener al menos 4 dígitos"
            } else if (pin1 == pin2) {
                error = "Los PINs no pueden ser iguales"
            } else {
                scope.launch {
                    app.pinRepository.setPin(pin1.toCharArray(), isDuress = false)
                    app.pinRepository.setPin(pin2.toCharArray(), isDuress = true)
                    // Cerrar actividad
                    (context as? android.app.Activity)?.finish()
                }
            }
        }) {
            Text("Guardar")
        }
    }
}
