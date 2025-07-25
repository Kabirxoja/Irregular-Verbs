package uz.kabir.irregularverbs.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class UpdateVersionDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val UPDATE_VERSION = intPreferencesKey("update_version")
    }

    val updateVersion: Flow<Int> = dataStore.data.map { it[UPDATE_VERSION] ?: 0 }

    suspend fun setUpdateVersion(version: Int) {
        dataStore.edit {
            it[UPDATE_VERSION] = version
        }
    }
}
