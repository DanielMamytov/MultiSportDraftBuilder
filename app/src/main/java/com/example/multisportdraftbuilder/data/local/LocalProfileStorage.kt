package com.example.multisportdraftbuilder.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.multisportdraftbuilder.data.model.ProfileDraft
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

private val Context.profileDataStore by preferencesDataStore(name = "draft_builder_profiles")

class LocalProfileStorage(private val context: Context) {

    private val storageScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val profilesKey = stringPreferencesKey("profiles_json")
    private val defaultProfiles = listOf(
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

    private val profilesFlow = MutableStateFlow(defaultProfiles)

    init {
        storageScope.launch {
            val rawJson = context.profileDataStore.data.first()[profilesKey]
            val persistedProfiles = decodeProfiles(rawJson)
            if (persistedProfiles != null) {
                profilesFlow.value = persistedProfiles
            }
        }
    }

    fun observeProfiles(): StateFlow<List<ProfileDraft>> = profilesFlow.asStateFlow()

    fun saveProfile(profileDraft: ProfileDraft) {
        val newId = (profilesFlow.value.maxOfOrNull { it.id } ?: 0) + 1
        profilesFlow.value = profilesFlow.value + profileDraft.copy(id = newId)
        persistProfiles(profilesFlow.value)
    }

    fun clearAll() {
        profilesFlow.value = emptyList()
        persistProfiles(emptyList())
    }

    private fun persistProfiles(profiles: List<ProfileDraft>) {
        storageScope.launch {
            context.profileDataStore.edit { preferences ->
                preferences[profilesKey] = encodeProfiles(profiles)
            }
        }
    }

    private fun encodeProfiles(profiles: List<ProfileDraft>): String {
        val array = JSONArray()
        profiles.forEach { profile ->
            val skills = JSONObject()
            profile.skills.forEach { (key, value) -> skills.put(key, value) }

            array.put(
                JSONObject()
                    .put("id", profile.id)
                    .put("name", profile.name)
                    .put("disciplines", JSONArray(profile.disciplines))
                    .put("accent", profile.accent)
                    .put("season", profile.season)
                    .put("skills", skills)
            )
        }
        return array.toString()
    }

    private fun decodeProfiles(rawJson: String?): List<ProfileDraft>? {
        if (rawJson.isNullOrBlank()) return null

        return runCatching {
            val array = JSONArray(rawJson)
            buildList {
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    val disciplinesArray = item.getJSONArray("disciplines")
                    val skillsObject = item.getJSONObject("skills")
                    val skills = mutableMapOf<String, Int>()

                    val skillKeys = skillsObject.keys()
                    while (skillKeys.hasNext()) {
                        val key = skillKeys.next()
                        skills[key] = skillsObject.getInt(key)
                    }

                    add(
                        ProfileDraft(
                            id = item.getInt("id"),
                            name = item.getString("name"),
                            disciplines = List(disciplinesArray.length()) { index -> disciplinesArray.getString(index) },
                            accent = item.getString("accent"),
                            season = item.getString("season"),
                            skills = skills
                        )
                    )
                }
            }
        }.getOrNull()
    }
}
