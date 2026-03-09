package com.example.multisportdraftbuilder.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            MultiSportDraftBuilderTheme(darkTheme = uiState.darkThemeEnabled) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}