package uz.kabir.irregularverbs.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.kabir.irregularverbs.presentation.ui.state.AppLanguage
import uz.kabir.irregularverbs.presentation.ui.state.TextMode
import uz.kabir.irregularverbs.presentation.ui.state.ThemeMode
import uz.kabir.irregularverbs.data.datastore.SettingParameterDataStore
import uz.kabir.irregularverbs.data.datastore.UpdateVersionDataStore
import uz.kabir.irregularverbs.data.mapper.toData
import uz.kabir.irregularverbs.data.mapper.toDomain
import uz.kabir.irregularverbs.domain.repository.SettingsRepository
import uz.kabir.irregularverbs.domain.model.Profile
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingParameterDataStore: SettingParameterDataStore,
    private val updateVersionDataStore: UpdateVersionDataStore
) : SettingsRepository {
    override fun getThemeMode(): Flow<ThemeMode> {
        return settingParameterDataStore.themeModeFlow
    }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        settingParameterDataStore.saveThemeMode(themeMode)
    }

    override fun getTextSize(): Flow<TextMode> {
        return settingParameterDataStore.textSizeFlow
    }

    override suspend fun setTextSize(textMode: TextMode) {
        settingParameterDataStore.saveTextSize(textMode)
    }

    override fun getLanguageCode(): Flow<AppLanguage> {
        return settingParameterDataStore.languageCode
    }

    override suspend fun setLanguageCode(languageCode: AppLanguage) {
        settingParameterDataStore.saveLanguageCode(languageCode)
    }

    override fun getUpdateVersion(): Flow<Int> {
        return updateVersionDataStore.updateVersion
    }

    override suspend fun setUpdateVersion(version: Int) {
        updateVersionDataStore.setUpdateVersion(version)
    }

    override fun getProfile(): Flow<Profile> {
        return settingParameterDataStore.getProfile.map { it.toDomain() }
    }

    override suspend fun setProfile(profile: Profile) {
        settingParameterDataStore.saveProfile(profile.toData())
    }

    override fun getSoundState(): Flow<Boolean> {
        return settingParameterDataStore.getSoundState
    }

    override suspend fun setSoundState(isEnabled: Boolean) {
        settingParameterDataStore.saveSoundState(isEnabled)
    }
}