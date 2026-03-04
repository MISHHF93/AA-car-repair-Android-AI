package com.aa.carrepair

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.aa.carrepair.navigation.AppNavGraph
import com.aa.carrepair.ui.theme.AACarRepairTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach { (permission, granted) ->
            Timber.d("Permission %s granted: %b", permission, granted)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Keep the splash screen visible until the start destination is determined from DataStore.
        splashScreen.setKeepOnScreenCondition {
            appViewModel.startDestination.value == null
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        requestAppPermissions()

        setContent {
            AACarRepairTheme {
                val startDestination by appViewModel.startDestination.collectAsState()

                Surface(modifier = Modifier.fillMaxSize()) {
                    // Only render the nav graph once we know where to start (avoids a flash).
                    startDestination?.let { dest ->
                        AppNavGraph(
                            startDestination = dest,
                            deepLinkVin = intent?.data?.host
                        )
                    }
                }
            }
        }
    }

    private fun requestAppPermissions() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )
    }
}
