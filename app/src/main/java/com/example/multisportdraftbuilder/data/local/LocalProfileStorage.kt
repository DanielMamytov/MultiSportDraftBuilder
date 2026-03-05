package com.example.multisportdraftbuilder.data.local

import com.example.multisportdraftbuilder.data.model.ProfileDraft
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocalProfileStorage {

    private val profilesFlow = MutableStateFlow(
        listOf(
            ProfileDraft(
                id = 1,
                name = "Velocity Prime",
                disciplines = listOf("Football", "Basketball"),
                accent = "Attacker",
                season = "Summer Peak",
                skills = mapOf("Speed" to 84, "Reaction" to 78, "Accuracy" to 72, "Endurance" to 65, "Tactics" to 70, "Ball Control" to 80)
            ),
            ProfileDraft(
                id = 2,
                name = "Iron Wall",
                disciplines = listOf("Hockey", "Rugby", "Wrestling"),
                accent = "Defender",
                season = "Winter Core",
                skills = mapOf("Speed" to 58, "Reaction" to 75, "Accuracy" to 54, "Endurance" to 91, "Tactics" to 81, "Ball Control" to 61)
            )
        )
    )

    fun observeProfiles(): StateFlow<List<ProfileDraft>> = profilesFlow.asStateFlow()

    fun saveProfile(profileDraft: ProfileDraft) {
        val newId = (profilesFlow.value.maxOfOrNull { it.id } ?: 0) + 1
        profilesFlow.value = profilesFlow.value + profileDraft.copy(id = newId)
    }

    fun clearAll() {
        profilesFlow.value = emptyList()
    }
}
