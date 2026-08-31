package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.presentation.navigation.HarmonyNavHost
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.HarmonyTheme

class MainActivity : ComponentActivity() {
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    @com.google.accompanist.permissions.ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as HarmonyApp).appContainer

        setContent {
            var permissionGranted by androidx.compose.runtime.mutableStateOf(false)

            com.example.domain.usecase.RequestLibraryPermissions(
                onPermissionGranted = {
                    permissionGranted = true
                }
            )

            androidx.compose.runtime.LaunchedEffect(permissionGranted) {
                if (permissionGranted) {
                    appContainer.libraryScanner.scanLibrary().collect {}
                }
            }

            HarmonyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundDark
                ) {
                    HarmonyNavHost(appContainer = appContainer)
                }
            }
        }
    }
}

