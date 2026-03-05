package com.example.multisportdraftbuilder.data.repository

import com.example.multisportdraftbuilder.data.local.LocalProfileStorage
import com.example.multisportdraftbuilder.data.model.ProfileDraft
import kotlinx.coroutines.flow.StateFlow

class ProfileRepository(
    private val localProfileStorage: LocalProfileStorage,
    private val networkClient: SportsNetworkClient
) {

    fun observeProfiles(): StateFlow<List<ProfileDraft>> = localProfileStorage.observeProfiles()

    fun saveProfile(profileDraft: ProfileDraft) {
        localProfileStorage.saveProfile(profileDraft)
        networkClient.trackProfileCreated(profileDraft.name)
    }

    fun clearProfiles() = localProfileStorage.clearAll()
}

class SportsNetworkClient {
    fun trackProfileCreated(profileName: String) {
        // Stub network client module for analytics/event sync.
        profileName.length
    }
}
