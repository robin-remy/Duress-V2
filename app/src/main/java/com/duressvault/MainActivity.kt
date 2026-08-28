package com.duressvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.duressvault.ui.LockScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LockScreen(
                        onSetupClick = {
                            startActivity(android.content.Intent(this, com.duressvault.ui.SetupActivity::class.java))
                        }
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        // No permitir salir para mantener la fachada
        if (!isFinishing) {
            // Opcional: mostrar un mensaje o simplemente ignorar
        }
    }
}
