package com.example.multisportdraftbuilder.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.multisportdraftbuilder.data.local.SettingsStore
import com.example.multisportdraftbuilder.data.model.ProfileDraft
import com.example.multisportdraftbuilder.data.repository.ProfileRepository
import com.example.multisportdraftbuilder.ui.navigation.AppPhase
import com.example.multisportdraftbuilder.ui.navigation.MainTab
import com.example.multisportdraftbuilder.utils.ProfileValidators
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

class MainViewModel(
    private val repository: ProfileRepository,
    private val settingsStore: SettingsStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        observeData()
        runPreloader()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                repository.observeProfiles(),
                settingsStore.darkThemeEnabled,
                settingsStore.notificationsEnabled
            ) { profiles, darkTheme, notifications ->
                Triple(profiles, darkTheme, notifications)
            }.collect { (profiles, darkTheme, notifications) ->
                _uiState.update {
                    it.copy(profiles = profiles, darkThemeEnabled = darkTheme, notificationsEnabled = notifications)
                }
            }
        }
    }

    private fun runPreloader() {
        viewModelScope.launch {
            repeat(10) {
                delay(150)
                _uiState.update { state -> state.copy(preloadProgress = (state.preloadProgress + 0.1f).coerceAtMost(1f)) }
            }
            _uiState.update { it.copy(phase = AppPhase.ONBOARDING) }
        }
    }

    fun nextOnboarding() {
        _uiState.update {
            if (it.onboardingPage >= 2) it.copy(phase = AppPhase.APP)
            else it.copy(onboardingPage = it.onboardingPage + 1)
        }
    }

    fun previousOnboarding() {
        _uiState.update { it.copy(onboardingPage = max(it.onboardingPage - 1, 0)) }
    }

    fun selectTab(tab: MainTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun toggleDiscipline(discipline: String) {
        _uiState.update { state ->
            val selected = state.selectedDisciplines.toMutableList()
            if (selected.contains(discipline)) selected.remove(discipline)
            else if (selected.size < 3) selected.add(discipline)
            state.copy(selectedDisciplines = selected)
        }
    }

    fun updateSkill(skill: String, value: Float) {
        _uiState.update { state ->
            val updated = state.skills.toMutableMap()
            updated[skill] = value.toInt()
            state.copy(skills = updated)
        }
    }

    fun setDraftStep(step: Int) {
        _uiState.update { it.copy(draftStep = step) }
    }

    fun setAccent(accent: String) {
        _uiState.update { it.copy(selectedAccent = accent) }
    }

    fun updateDraftName(input: String) {
        _uiState.update { it.copy(draftName = ProfileValidators.sanitizeName(input)) }
    }

    fun updateSeason(season: String) {
        _uiState.update { it.copy(draftSeason = season) }
    }

    fun saveDraft() {
        val state = _uiState.value
        val pointsLeft = state.totalPoints - state.skills.values.sum()
        val canSave = ProfileValidators.isValidName(state.draftName) &&
            ProfileValidators.hasValidDisciplines(state.selectedDisciplines) &&
            ProfileValidators.hasValidPoints(pointsLeft)

        if (!canSave) return

        repository.saveProfile(
            ProfileDraft(
                id = 0,
                name = state.draftName,
                disciplines = state.selectedDisciplines,
                accent = state.selectedAccent,
                season = state.draftSeason,
                skills = state.skills
            )
        )

        _uiState.update {
            it.copy(
                draftName = "",
                selectedDisciplines = emptyList(),
                draftStep = 0,
                selectedTab = MainTab.PROFILES
            )
        }
    }

    fun updateSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateSeasonFilter(season: String) {
        _uiState.update { it.copy(seasonFilter = season) }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch { settingsStore.setDarkTheme(enabled) }
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch { settingsStore.setNotifications(enabled) }
    }

    fun clearLocalData() {
        repository.clearProfiles()
    }
}

class MainViewModelFactory(
    private val repository: ProfileRepository,
    private val settingsStore: SettingsStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository, settingsStore) as T
    }
}

data class MainUiState(
    val phase: AppPhase = AppPhase.PRELOADER,
    val preloadProgress: Float = 0f,
    val onboardingPage: Int = 0,
    val selectedTab: MainTab = MainTab.HOME,
    val profiles: List<ProfileDraft> = emptyList(),
    val draftStep: Int = 0,
    val selectedDisciplines: List<String> = emptyList(),
    val selectedAccent: String = "Universal",
    val draftName: String = "",
    val draftSeason: String = "Summer Peak",
    val skills: Map<String, Int> = mapOf(
        "Speed" to 20,
        "Reaction" to 20,
        "Accuracy" to 20,
        "Endurance" to 20,
        "Tactics" to 20,
        "Ball Control" to 20
    ),
    val totalPoints: Int = 120,
    val searchQuery: String = "",
    val seasonFilter: String = "All",
    val darkThemeEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true
)
