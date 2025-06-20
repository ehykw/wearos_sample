
package com.example.sotuken.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun Mobile_sensor_receiverTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
