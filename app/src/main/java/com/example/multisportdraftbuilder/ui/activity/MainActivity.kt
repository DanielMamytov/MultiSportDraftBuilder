package com.example.multisportdraftbuilder.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.multisportdraftbuilder.AppModules
import com.example.multisportdraftbuilder.ui.MainAppScreen
import com.example.multisportdraftbuilder.ui.theme.MultiSportDraftBuilderTheme
import com.example.multisportdraftbuilder.ui.viewmodel.MainViewModel
import com.example.multisportdraftbuilder.ui.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(
            AppModules.provideProfileRepository(this),
            AppModules.provideSettingsStore(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var notificationsPermissionGranted by mutableStateOf(hasNotificationPermission())
        enableEdgeToEdge()

        setContent {
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                notificationsPermissionGranted = isGranted
                if (!isGranted) {
                    viewModel.setNotifications(false)
                }
            }

            MultiSportDraftBuilderTheme {
                MainAppScreen(
                    viewModel = viewModel,
                    notificationsPermissionGranted = notificationsPermissionGranted,
                    onRequestNotificationPermission = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                )
            }
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
