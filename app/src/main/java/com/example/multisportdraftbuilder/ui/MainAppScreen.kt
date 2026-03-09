package com.example.multisportdraftbuilder.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.multisportdraftbuilder.data.model.ProfileDraft
import com.example.multisportdraftbuilder.ui.navigation.AppPhase
import com.example.multisportdraftbuilder.ui.navigation.MainTab
import com.example.multisportdraftbuilder.ui.theme.CardDark
import com.example.multisportdraftbuilder.ui.viewmodel.MainUiState
import com.example.multisportdraftbuilder.ui.viewmodel.MainViewModel
import com.example.multisportdraftbuilder.utils.ProfileValidators

@Composable
fun MainAppScreen(
    viewModel: MainViewModel,
    notificationsPermissionGranted: Boolean,
    onRequestNotificationPermission: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState.phase) {
        AppPhase.PRELOADER -> PreloaderScreen(uiState.preloadProgress)
        AppPhase.ONBOARDING -> OnboardingScreen(uiState.onboardingPage, viewModel::nextOnboarding, viewModel::previousOnboarding)
        AppPhase.APP -> AppContent(uiState, viewModel, notificationsPermissionGranted, onRequestNotificationPermission)
    }
}

@Composable
private fun AppContent(
    uiState: MainUiState,
    viewModel: MainViewModel,
    notificationsPermissionGranted: Boolean,
    onRequestNotificationPermission: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFF181828),
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF23233A)) {
                listOf(
                    MainTab.HOME to Icons.Default.Home,
                    MainTab.PROFILES to Icons.Default.Person,
                    MainTab.DRAFT to Icons.Default.Build,
                    MainTab.ANALYTICS to Icons.Default.Search,
                    MainTab.SETTINGS to Icons.Default.Settings
                ).forEach { (tab, icon) ->
                    NavigationBarItem(
                        selected = uiState.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        icon = { Icon(icon, contentDescription = tab.title) },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (uiState.selectedTab) {
                MainTab.HOME -> HomeScreen(uiState.profiles)
                MainTab.PROFILES -> ProfilesScreen(uiState, viewModel)
                MainTab.DRAFT -> DraftScreen(uiState, viewModel)
                MainTab.ANALYTICS -> AnalyticsScreen(uiState.profiles)
                MainTab.SETTINGS -> SettingsScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    notificationsPermissionGranted = notificationsPermissionGranted,
                    onRequestNotificationPermission = onRequestNotificationPermission
                )
            }
        }
    }
}

@Composable
private fun PreloaderScreen(progress: Float) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF181828)).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(progress = { progress }, color = Color(0xFFA259FF))
        Spacer(Modifier.height(16.dp))
        Text("Initializing theme, local storage and dependencies", color = Color.White)
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun OnboardingScreen(page: Int, onNext: () -> Unit, onBack: () -> Unit) {
    val pages = listOf(
        "Create a hybrid athlete from multiple sports disciplines.",
        "Choose 2-3 sports and distribute points between key skills.",
        "Compare builds and run outcome simulations with analytics."
    )

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF181828)).padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("MultiSport Draft Builder", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            AnimatedContent(page, label = "onboarding") { index ->
                Card(colors = CardDefaults.cardColors(containerColor = CardDark)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Step ${index + 1} / 3", color = Color(0xFFC7BFFF))
                        Spacer(Modifier.height(8.dp))
                        Text(pages[index], color = Color.White)
                    }
                }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(enabled = page > 0, onClick = onBack) { Text("Back") }
            Button(onClick = onNext) { Text(if (page == 2) "Finish" else "Next") }
        }
    }
}

@Composable
private fun HomeScreen(profiles: List<ProfileDraft>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFF181828)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Home", color = Color.White, fontWeight = FontWeight.Bold)
            Text("Recent drafts", color = Color(0xFFC7BFFF))
        }
        items(profiles.takeLast(3).reversed()) { profile -> ProfileCard(profile) }
    }
}

@Composable
private fun DraftScreen(uiState: MainUiState, viewModel: MainViewModel) {
    val disciplineOptions = listOf("Football", "Basketball", "Tennis", "Hockey", "Rugby", "Volleyball")
    val accents = listOf("Attacker", "Universal", "Defender", "Custom")
    val pointsLeft = uiState.totalPoints - uiState.skills.values.sum()

    Column(Modifier.fillMaxSize().background(Color(0xFF181828)).padding(16.dp)) {
        Text("Draft", color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            listOf("Disciplines", "Skills", "Accent").forEachIndexed { index, label ->
                SegmentedButton(
                    selected = uiState.draftStep == index,
                    onClick = { viewModel.setDraftStep(index) },
                    shape = SegmentedButtonDefaults.itemShape(index, 3)
                ) { Text(label) }
            }
        }
        Spacer(Modifier.height(12.dp))

        when (uiState.draftStep) {
            0 -> {
                Text("Choose 2-3 disciplines", color = Color.White)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    disciplineOptions.forEach { option ->
                        FilterChip(
                            selected = uiState.selectedDisciplines.contains(option),
                            onClick = { viewModel.toggleDiscipline(option) },
                            label = { Text(option) }
                        )
                    }
                }
                if (!ProfileValidators.hasValidDisciplines(uiState.selectedDisciplines)) {
                    Text("Select 2-3 disciplines", color = Color(0xFFFF4F64))
                }
            }

            1 -> {
                Text("Skill distribution", color = Color.White)
                Text("Points left: $pointsLeft", color = if (pointsLeft < 0) Color(0xFFFF4F64) else Color(0xFF3DDC97))
                uiState.skills.forEach { (skill, value) ->
                    Text("$skill: $value", color = Color.White)
                    Slider(value = value.toFloat(), onValueChange = { viewModel.updateSkill(skill, it) }, valueRange = 0f..60f)
                }
            }

            else -> {
                Text("Accent and save", color = Color.White)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    accents.forEach { accent ->
                        FilterChip(
                            selected = uiState.selectedAccent == accent,
                            onClick = { viewModel.setAccent(accent) },
                            label = { Text(accent) }
                        )
                    }
                }
                OutlinedTextField(
                    value = uiState.draftName,
                    onValueChange = viewModel::updateDraftName,
                    label = { Text("Build name") },
                    isError = uiState.draftName.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = uiState.draftSeason,
                    onValueChange = viewModel::updateSeason,
                    label = { Text("Season") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = viewModel::saveDraft,
                    enabled = ProfileValidators.isValidName(uiState.draftName) &&
                        ProfileValidators.hasValidDisciplines(uiState.selectedDisciplines) &&
                        ProfileValidators.hasValidPoints(pointsLeft)
                ) {
                    Text("Save profile")
                }
            }
        }
    }
}

@Composable
private fun ProfilesScreen(uiState: MainUiState, viewModel: MainViewModel) {
    val seasons = listOf("All") + uiState.profiles.map { it.season }.distinct()
    val filtered = uiState.profiles.filter {
        (uiState.searchQuery.isBlank() || it.name.contains(uiState.searchQuery, true)) &&
            (uiState.seasonFilter == "All" || it.season == uiState.seasonFilter)
    }

    Column(Modifier.fillMaxSize().background(Color(0xFF181828)).padding(16.dp)) {
        Text("Profiles", color = Color.White, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = viewModel::updateSearch,
            label = { Text("Search") },
            modifier = Modifier.fillMaxWidth()
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            seasons.forEach { season ->
                FilterChip(
                    selected = uiState.seasonFilter == season,
                    onClick = { viewModel.updateSeasonFilter(season) },
                    label = { Text(season) }
                )
            }
        }
        if (filtered.isEmpty()) {
            Text("No profiles yet. Create one in Draft tab.", color = Color(0xFFC7BFFF))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered) { profile -> ProfileCard(profile) }
            }
        }
    }
}

@Composable
private fun ProfileCard(profile: ProfileDraft) {
    val index = profile.skills.values.average().toInt()
    Card(colors = CardDefaults.cardColors(containerColor = CardDark), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(profile.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text("Index $index", color = Color(0xFFA259FF))
            }
            Text(profile.disciplines.joinToString(" • "), color = Color(0xFFC7BFFF))
            Text("${profile.accent} • ${profile.season}", color = Color(0xFFC7BFFF))
            Divider(color = Color(0xFF373758))
            profile.skills.entries.take(3).forEach { Text("${it.key}: ${it.value}", color = Color.White) }
        }
    }
}

@Composable
private fun AnalyticsScreen(profiles: List<ProfileDraft>) {
    val skillAverages = profiles.flatMap { it.skills.entries }
        .groupBy { it.key }
        .mapValues { (_, entries) -> entries.map { it.value }.average().toInt() }

    Column(
        Modifier.fillMaxSize().background(Color(0xFF181828)).padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Analytics", color = Color.White, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = CardDark)) {
            Column(Modifier.padding(12.dp)) {
                Text("Average skills", color = Color.White)
                skillAverages.forEach { Text("${it.key}: ${it.value}", color = Color(0xFFC7BFFF)) }
            }
        }
        if (profiles.size >= 2) {
            val left = profiles[0]
            val right = profiles[1]
            val probability = simulationProbability(left, right)
            Card(colors = CardDefaults.cardColors(containerColor = CardDark)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Simulation: ${left.name} vs ${right.name}", color = Color.White)
                    Text("Success chance for first profile: $probability%", color = Color(0xFFA259FF))
                    left.skills.keys.forEach { skill ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(skill, color = Color.White, modifier = Modifier.width(90.dp))
                            Canvas(Modifier.width((left.skills[skill] ?: 0).dp).height(8.dp)) { drawRect(Color(0xFFA259FF)) }
                            Spacer(Modifier.width(6.dp))
                            Canvas(Modifier.width((right.skills[skill] ?: 0).dp).height(8.dp)) { drawRect(Color(0xFF7F7FFF)) }
                        }
                    }
                }
            }
        }
    }
}

private fun simulationProbability(left: ProfileDraft, right: ProfileDraft): Int {
    val leftPower = left.skills.values.sum() + if (left.accent == "Attacker") 15 else 0
    val rightPower = right.skills.values.sum() + if (right.accent == "Defender") 10 else 0
    return ((leftPower.toFloat() / (leftPower + rightPower).toFloat()) * 100).toInt()
}

@Composable
private fun SettingsScreen(
    uiState: MainUiState,
    viewModel: MainViewModel,
    notificationsPermissionGranted: Boolean,
    onRequestNotificationPermission: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().background(Color(0xFF181828)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Settings", color = Color.White, fontWeight = FontWeight.Bold)
        SettingSwitch("Dark theme", uiState.darkThemeEnabled, viewModel::setDarkTheme)
        SettingSwitch(
            title = "Seasonal notifications",
            checked = uiState.notificationsEnabled && notificationsPermissionGranted,
            onChanged = { enabled ->
                if (enabled && !notificationsPermissionGranted) {
                    onRequestNotificationPermission()
                } else {
                    viewModel.setNotifications(enabled)
                }
            }
        )

        Card(colors = CardDefaults.cardColors(containerColor = CardDark)) {
            Column(Modifier.padding(12.dp)) {
                TextButton(onClick = viewModel::clearLocalData) { Text("Clear local data") }
                TextButton(onClick = {}) { Text("Reset settings") }
                TextButton(onClick = {}) { Text("Rate app") }
                TextButton(onClick = {}) { Text("Share app") }
                Text("Version 1.0", color = Color(0xFFC7BFFF))
            }
        }
    }
}

@Composable
private fun SettingSwitch(title: String, checked: Boolean, onChanged: (Boolean) -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = CardDark), shape = RoundedCornerShape(16.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = Color.White)
            Switch(checked = checked, onCheckedChange = onChanged)
        }
    }
}
