package com.example.multisportdraftbuilder

import android.content.Context
import com.example.multisportdraftbuilder.data.local.LocalProfileStorage
import com.example.multisportdraftbuilder.data.local.SettingsStore
import com.example.multisportdraftbuilder.data.repository.ProfileRepository
import com.example.multisportdraftbuilder.data.repository.SportsNetworkClient

object AppModules {

    private val networkClient by lazy { SportsNetworkClient() }

    fun provideProfileRepository(context: Context): ProfileRepository {
        val appContext = context.applicationContext
        return ProfileRepository(LocalProfileStorage(appContext), networkClient)
    }

    fun provideSettingsStore(context: Context): SettingsStore {
        return SettingsStore(context.applicationContext)
    }
}
