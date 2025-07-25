package uz.kabir.irregularverbs.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.kabir.irregularverbs.data.remote.model.UserProfile
import uz.kabir.irregularverbs.presentation.ui.state.AppLanguage
import uz.kabir.irregularverbs.presentation.ui.state.TextMode
import uz.kabir.irregularverbs.presentation.ui.state.ThemeMode
import uz.kabir.irregularverbs.domain.model.Profile
import javax.inject.Inject


class SettingParameterDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val THEME_KEY = stringPreferencesKey("theme_mode")
    private val TEXT_SIZE_KEY = stringPreferencesKey("text_size")
    private val LANGUAGE_KEY = stringPreferencesKey("language_code")
    private val SOUND_KEY = booleanPreferencesKey("sound_state")

    object ProfileKeys {
        val USER_GENDER = stringPreferencesKey("profile")
        val USER_NAME = stringPreferencesKey("user_name")
    }

    suspend fun saveThemeMode(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeMode.name
        }
    }

    val themeModeFlow: Flow<ThemeMode> = dataStore.data.map { preferences ->
        val themeName = preferences[THEME_KEY] ?: ThemeMode.LIGHT.name
        return@map ThemeMode.valueOf(themeName)
    }

    suspend fun saveTextSize(textMode: TextMode) {
        dataStore.edit { preferences ->
            preferences[TEXT_SIZE_KEY] = textMode.name
        }
    }

    val textSizeFlow: Flow<TextMode> = dataStore.data.map { preferences ->
        val textSize = preferences[TEXT_SIZE_KEY] ?: TextMode.MEDIUM.name
        return@map TextMode.valueOf(textSize)
    }

    suspend fun saveLanguageCode(languageCode: AppLanguage) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode.name
        }
    }

    val languageCode: Flow<AppLanguage> = dataStore.data.map { preferences ->
        val languageCode = preferences[LANGUAGE_KEY] ?: AppLanguage.AUTO.name
        return@map AppLanguage.valueOf(languageCode)
    }

    suspend fun saveProfile(profile: UserProfile) {
        dataStore.edit { preferences ->
            preferences[ProfileKeys.USER_NAME] = profile.userName
            preferences[ProfileKeys.USER_GENDER] = profile.userGender
        }
    }

    val getProfile: Flow<UserProfile> = dataStore.data.map { preferences ->
        val userName = preferences[ProfileKeys.USER_NAME] ?: ""
        val userGender = preferences[ProfileKeys.USER_GENDER] ?: ""
        return@map UserProfile(userName = userName, userGender = userGender)
    }

    suspend fun saveSoundState(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[SOUND_KEY] = isEnabled
        }
    }

    val getSoundState: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SOUND_KEY] ?: false
    }
}
