package com.example.weatherapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.sp

@Composable
fun InforAppDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "App Information", fontSize = 20.sp)
        },
        text = {
            Column {
                Text(text = "📱 This is a current weather app for Android mobile app class assignment.")
                Text(text = "🔄 Data fetched from Open-Meteo API")
                Text(text = "🌍 https://api.open-meteo.com/v1/")
                Text(text = "👨‍💻 Created by HaNguyen")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
