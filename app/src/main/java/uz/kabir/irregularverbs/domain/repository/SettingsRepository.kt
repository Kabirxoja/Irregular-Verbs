package uz.kabir.irregularverbs.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.kabir.irregularverbs.presentation.ui.state.AppLanguage
import uz.kabir.irregularverbs.presentation.ui.state.TextMode
import uz.kabir.irregularverbs.presentation.ui.state.ThemeMode
import uz.kabir.irregularverbs.domain.model.Profile

interface SettingsRepository {
    fun getThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(themeMode: ThemeMode)

    fun getTextSize():Flow<TextMode>
    suspend fun setTextSize(textMode: TextMode)

    fun getLanguageCode(): Flow<AppLanguage>
    suspend fun setLanguageCode(languageCode: AppLanguage)

    fun getUpdateVersion(): Flow<Int>
    suspend fun setUpdateVersion(version: Int)

    fun getProfile(): Flow<Profile>
    suspend fun setProfile(profile: Profile)

    fun getSoundState():Flow<Boolean>
    suspend fun setSoundState(isEnabled:Boolean)
}