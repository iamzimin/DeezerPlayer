package com.evg.deezerplayer

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import com.evg.ui.theme.AppSize
import com.evg.ui.theme.AppStyle
import com.evg.ui.theme.DeezerPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0,
            )
        }

        enableEdgeToEdge()
        setContent {
            val currentStyle = remember { mutableStateOf(AppStyle.Green) }
            val currentFontSize = remember { mutableStateOf(AppSize.Medium) }

            DeezerPlayerTheme(
                style = currentStyle.value,
                textSize = currentFontSize.value,
            ) {
                MainScreen()
            }
        }
    }
}