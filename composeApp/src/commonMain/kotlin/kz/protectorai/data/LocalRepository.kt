package kz.protectorai.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath

object LocalRepository {
    internal const val FILENAME_DATA_STORE = "data.preferences_pb"
    private const val KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN"
    private val store by lazy { createPreferencesDataStore() }
    private val keyAccessToken by lazy { stringPreferencesKey(KEY_ACCESS_TOKEN) }

    suspend fun saveAccessToken(accessToken: String) {
        store.edit { preferences -> preferences[keyAccessToken] = accessToken }
    }

    fun getAccessTokenFlow() = store.data.map { it[keyAccessToken] }

    suspend fun clear() = store.edit { it.clear() }
}

fun getPreferencesDataStore(
    path: String
) = PreferenceDataStoreFactory.createWithPath { path.toPath() }

expect fun createPreferencesDataStore(): DataStore<Preferences>