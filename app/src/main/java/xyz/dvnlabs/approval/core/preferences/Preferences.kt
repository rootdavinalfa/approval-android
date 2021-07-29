/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import xyz.dvnlabs.approval.core.data.local.User

class Preferences(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "local_pref")

    companion object {
        val USER_TOKEN = stringPreferencesKey(name = "user_token")
        val USER_NAME = stringPreferencesKey(name = "user_name")
    }

    suspend fun savePreference(user: User) {
        context.dataStore.edit {
            it[USER_TOKEN] = user.token
            it[USER_NAME] = user.userName
        }
    }

    val getUserPref: Flow<User> = context.dataStore.data
        .catch {
            println(it.message)
        }
        .map {
            val userName = it[USER_NAME] ?: ""
            val token = it[USER_TOKEN] ?: ""
            return@map User(
                userName = userName,
                token = token
            )
        }.distinctUntilChanged()
}