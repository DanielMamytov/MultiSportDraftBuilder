package com.example.multisportdraftbuilder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.multisportdraftbuilder.ui.theme.CardDark
import com.example.multisportdraftbuilder.ui.theme.MultiSportDraftBuilderTheme
import kotlinx.coroutines.delay
import kotlin.math.max

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultiSportDraftBuilderTheme {
                DraftBuilderApp()
            }
        }
    }
}

enum class AppPhase { PRELOADER, ONBOARDING, APP }
enum class MainTab(val title: String) { HOME("Home"), PROFILES("Profiles"), DRAFT("Draft"), ANALYTICS("Analytics"), SETTINGS("Settings") }

data class AthleteProfile(
    val id: Int,
    val name: String,
    val disciplines: List<String>,
    val accent: String,
    val season: String,
    val skills: Map<String, Int>
)

@Composable
fun DraftBuilderApp() {
    var phase by remember { mutableStateOf(AppPhase.PRELOADER) }
    var onboardingPage by remember { mutableIntStateOf(0) }
    var selectedTab by remember { mutableStateOf(MainTab.HOME) }

    val profiles = remember {
        mutableStateListOf(
            AthleteProfile(1, "Velocity Prime", listOf("Football", "Basketball"), "Attacker", "Summer Peak", mapOf("Speed" to 84, "Reaction" to 78, "Accuracy" to 72, "Endurance" to 65, "Tactics" to 70, "Ball Control" to 80)),
            AthleteProfile(2, "Iron Wall", listOf("Hockey", "Rugby", "Wrestling"), "Defender", "Winter Core", mapOf("Speed" to 58, "Reaction" to 75, "Accuracy" to 54, "Endurance" to 91, "Tactics" to 81, "Ball Control" to 61))
        )
    }

    when (phase) {
        AppPhase.PRELOADER -> PreloaderScreen { phase = AppPhase.ONBOARDING }
        AppPhase.ONBOARDING -> OnboardingScreen(
            page = onboardingPage,
            onNext = {
                if (onboardingPage < 2) onboardingPage++ else phase = AppPhase.APP
            },
            onBack = { onboardingPage = max(onboardingPage - 1, 0) }
        )
        AppPhase.APP -> Scaffold(
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
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = { Icon(icon, contentDescription = tab.title) },
                            label = { Text(tab.title) }
                        )
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (selectedTab) {
                    MainTab.HOME -> HomeScreen(profiles)
                    MainTab.PROFILES -> ProfilesScreen(profiles)
                    MainTab.DRAFT -> DraftFlowScreen(onSaveProfile = { profiles.add(it.copy(id = profiles.size + 1)) })
                    MainTab.ANALYTICS -> AnalyticsScreen(profiles)
                    MainTab.SETTINGS -> SettingsScreen()
                }
            }
        }
    }
}

@Composable
fun PreloaderScreen(onDone: () -> Unit) {
    var progress by remember { mutableStateOf(0f) }
    val animated by animateFloatAsState(progress, animationSpec = tween(500), label = "loader")

    LaunchedEffect(Unit) {
        repeat(10) {
            delay(180)
            progress += 0.1f
        }
        delay(300)
        onDone()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF181828)).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(progress = { animated }, strokeCap = StrokeCap.Round, color = Color(0xFFA259FF))
        Spacer(Modifier.height(24.dp))
        Text("Initializing theme, storage and dependencies…", color = Color.White)
        Spacer(Modifier.height(12.dp))
        LinearProgressIndicator(progress = { animated }, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun OnboardingScreen(page: Int, onNext: () -> Unit, onBack: () -> Unit) {
    val pages = listOf(
        "Hybrid athlete: combine disciplines to design a unique sports identity.",
        "Pick 2-3 disciplines and distribute points to tune strengths and weaknesses.",
        "Compare profiles and run simulations to evaluate strategy outcomes."
    )
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF181828)).padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Welcome to MultiSport Draft Builder", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(18.dp))
            AnimatedContent(page, label = "slides") { index ->
                Card(colors = CardDefaults.cardColors(containerColor = CardDark)) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Step ${index + 1}/3", color = Color(0xFFC7BFFF))
                        Spacer(Modifier.height(8.dp))
                        Text(pages[index], color = Color.White)
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onBack, enabled = page > 0) { Text("Back") }
            Button(onClick = onNext) { Text(if (page == 2) "Finish" else "Next") }
        }
    }
}

@Composable
fun HomeScreen(profiles: List<AthleteProfile>) {
    LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFF181828)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Home", color = Color.White, fontWeight = FontWeight.Bold)
            Text("Build, track and compare your hybrid athletes", color = Color(0xFFC7BFFF))
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = CardDark)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Create new profile", color = Color.White)
                    IconButton(onClick = {}) { Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFFA259FF)) }
                }
            }
        }
        items(profiles.takeLast(3).reversed()) { profile ->
            ProfileCard(profile)
        }
    }
}

@Composable
fun DraftFlowScreen(onSaveProfile: (AthleteProfile) -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    val disciplineOptions = listOf("Football", "Basketball", "Tennis", "Hockey", "Rugby", "Volleyball")
    val selectedDisciplines = remember { mutableStateListOf<String>() }
    val skillPool = 120
    val skills = remember {
        mutableStateMapOf(
            "Speed" to 20,
            "Reaction" to 20,
            "Accuracy" to 20,
            "Endurance" to 20,
            "Tactics" to 20,
            "Ball Control" to 20
        )
    }
    val accents = listOf("Attacker", "Universal", "Defender", "Custom")
    var accent by remember { mutableStateOf(accents[1]) }
    var name by remember { mutableStateOf("") }
    var season by remember { mutableStateOf("Summer Peak") }
    val pointsUsed = skills.values.sum()
    val pointsLeft = skillPool - pointsUsed

    Column(Modifier.fillMaxSize().background(Color(0xFF181828)).padding(16.dp)) {
        Text("Draft Builder", color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            listOf("Disciplines", "Skills", "Accent & Save").forEachIndexed { index, label ->
                SegmentedButton(
                    selected = step == index,
                    onClick = { step = index },
                    shape = SegmentedButtonDefaults.itemShape(index, 3)
                ) { Text(label) }
            }
        }
        Spacer(Modifier.height(16.dp))

        when (step) {
            0 -> {
                Text("Choose 2–3 disciplines", color = Color.White)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    disciplineOptions.forEach { option ->
                        FilterChip(
                            selected = selectedDisciplines.contains(option),
                            onClick = {
                                if (selectedDisciplines.contains(option)) selectedDisciplines.remove(option)
                                else if (selectedDisciplines.size < 3) selectedDisciplines.add(option)
                            },
                            label = { Text(option) }
                        )
                    }
                }
                AnimatedVisibility(selectedDisciplines.size !in 2..3) {
                    Text("Select between 2 and 3 disciplines", color = Color(0xFFFF4F64))
                }
            }
            1 -> {
                Text("Distribute skill points", color = Color.White)
                Text("Points left: $pointsLeft", color = if (pointsLeft < 0) Color(0xFFFF4F64) else Color(0xFF3DDC97))
                skills.forEach { (skill, value) ->
                    Text("$skill: $value", color = Color.White)
                    Slider(value = value.toFloat(), onValueChange = { newValue -> skills[skill] = newValue.toInt() }, valueRange = 0f..60f)
                }
            }
            else -> {
                Text("Profile accent and metadata", color = Color.White)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    accents.forEach {
                        FilterChip(selected = accent == it, onClick = { accent = it }, label = { Text(it) })
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 32) name = it.filter { ch -> ch.isLetterOrDigit() || ch == ' ' } },
                    label = { Text("Build name") },
                    supportingText = {
                        if (name.isBlank()) Text("Name required")
                    },
                    isError = name.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(value = season, onValueChange = { season = it }, label = { Text("Season") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                val canSave = name.isNotBlank() && selectedDisciplines.size in 2..3 && pointsLeft >= 0
                Button(onClick = {
                    onSaveProfile(AthleteProfile(0, name, selectedDisciplines.toList(), accent, season, skills.toMap()))
                    name = ""
                }, enabled = canSave) {
                    Text("Save profile")
                }
            }
        }
    }
}

@Composable
fun ProfilesScreen(profiles: List<AthleteProfile>) {
    var search by remember { mutableStateOf("") }
    var seasonFilter by remember { mutableStateOf("All") }
    val seasons = listOf("All") + profiles.map { it.season }.distinct()
    val filtered = profiles.filter {
        (search.isBlank() || it.name.contains(search, true)) &&
            (seasonFilter == "All" || it.season == seasonFilter)
    }

    Column(Modifier.fillMaxSize().background(Color(0xFF181828)).padding(16.dp)) {
        Text("Profiles", color = Color.White, fontWeight = FontWeight.Bold)
        OutlinedTextField(value = search, onValueChange = { search = it }, label = { Text("Search") }, modifier = Modifier.fillMaxWidth())
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            seasons.forEach { season ->
                FilterChip(selected = seasonFilter == season, onClick = { seasonFilter = season }, label = { Text(season) })
            }
        }
        Spacer(Modifier.height(8.dp))
        if (filtered.isEmpty()) {
            Text("No builds found. Create a new one in Draft tab.", color = Color(0xFFC7BFFF))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filtered) { profile ->
                    ProfileCard(profile)
                }
            }
        }
    }
}

@Composable
fun ProfileCard(profile: AthleteProfile) {
    val powerIndex = profile.skills.values.average().toInt()
    Card(colors = CardDefaults.cardColors(containerColor = CardDark)) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(profile.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text("Index $powerIndex", color = Color(0xFFA259FF))
            }
            Text(profile.disciplines.joinToString(" • "), color = Color(0xFFC7BFFF))
            Text("Accent: ${profile.accent} | Season: ${profile.season}", color = Color(0xFFC7BFFF))
            Divider(color = Color(0xFF373758))
            profile.skills.entries.take(3).forEach { (skill, value) ->
                Text("$skill $value", color = Color.White)
            }
        }
    }
}

@Composable
fun AnalyticsScreen(profiles: List<AthleteProfile>) {
    val skillAverages = if (profiles.isNotEmpty()) {
        profiles.flatMap { it.skills.entries }
            .groupBy { it.key }
            .mapValues { (_, entries) -> entries.map { it.value }.average() }
    } else emptyMap()

    var leftProfile by remember { mutableStateOf(profiles.firstOrNull()) }
    var rightProfile by remember { mutableStateOf(profiles.getOrNull(1)) }

    Column(
        Modifier.fillMaxSize().background(Color(0xFF181828)).padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Analytics", color = Color.White, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = CardDark)) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Average skill values", color = Color.White)
                skillAverages.forEach { (skill, avg) -> Text("$skill: ${avg.toInt()}", color = Color(0xFFC7BFFF)) }
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = CardDark)) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Comparison simulator", color = Color.White)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    profiles.forEach {
                        FilterChip(selected = leftProfile?.id == it.id, onClick = { leftProfile = it }, label = { Text("A: ${it.name}") })
                    }
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    profiles.forEach {
                        FilterChip(selected = rightProfile?.id == it.id, onClick = { rightProfile = it }, label = { Text("B: ${it.name}") })
                    }
                }
                val probability = simulationProbability(leftProfile, rightProfile)
                Text("Success chance for A: $probability%", color = Color(0xFFA259FF))
                ComparisonBars(leftProfile, rightProfile)
            }
        }
    }
}

private fun simulationProbability(left: AthleteProfile?, right: AthleteProfile?): Int {
    if (left == null || right == null) return 50
    val leftPower = left.skills.values.sum() + if (left.accent == "Attacker") 15 else 0
    val rightPower = right.skills.values.sum() + if (right.accent == "Defender") 10 else 0
    return ((leftPower.toFloat() / (leftPower + rightPower).toFloat()) * 100).toInt()
}

@Composable
fun ComparisonBars(left: AthleteProfile?, right: AthleteProfile?) {
    if (left == null || right == null) return
    val skills = left.skills.keys.intersect(right.skills.keys)
    skills.forEach { skill ->
        val leftV = left.skills[skill] ?: 0
        val rightV = right.skills[skill] ?: 0
        Text(skill, color = Color.White)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Canvas(Modifier.width((leftV * 2).dp).height(8.dp)) { drawRect(Color(0xFFA259FF)) }
            Spacer(Modifier.width(8.dp))
            Canvas(Modifier.width((rightV * 2).dp).height(8.dp)) { drawRect(Color(0xFF7F7FFF)) }
        }
    }
}

@Composable
fun SettingsScreen() {
    var notifications by remember { mutableStateOf(true) }
    var darkTheme by remember { mutableStateOf(true) }

    Column(
        Modifier.fillMaxSize().background(Color(0xFF181828)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Settings", color = Color.White, fontWeight = FontWeight.Bold)
        SettingItem("Dark theme", darkTheme) { darkTheme = it }
        SettingItem("Seasonal notifications", notifications) { notifications = it }
        Card(colors = CardDefaults.cardColors(containerColor = CardDark)) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Privacy & Data", color = Color.White)
                TextButton(onClick = {}) { Text("Clear local data") }
                TextButton(onClick = {}) { Text("Reset settings") }
                TextButton(onClick = {}) { Text("Rate app") }
                TextButton(onClick = {}) { Text("Share app") }
                Text("Version 1.0", color = Color(0xFFC7BFFF))
            }
        }
    }
}

@Composable
fun SettingItem(title: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = CardDark), shape = RoundedCornerShape(16.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = Color.White)
            Switch(checked = checked, onCheckedChange = onToggle)
        }
    }
}
