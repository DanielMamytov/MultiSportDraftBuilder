package com.example.multisportdraftbuilder.ui.navigation

enum class AppPhase { PRELOADER, ONBOARDING, APP }

enum class MainTab(val title: String) {
    HOME("Home"),
    PROFILES("Profiles"),
    DRAFT("Draft"),
    ANALYTICS("Analytics"),
    SETTINGS("Settings")
}
