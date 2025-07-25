package uz.kabir.irregularverbs.presentation.ui.screens.setting

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.kabir.irregularverbs.domain.model.UserProgress
import uz.kabir.irregularverbs.domain.usecase.GetLanguageUseCase
import uz.kabir.irregularverbs.domain.usecase.GetProfileUseCase
import uz.kabir.irregularverbs.domain.usecase.GetProgressUseCase
import uz.kabir.irregularverbs.domain.usecase.GetTextSizeUseCase
import uz.kabir.irregularverbs.domain.usecase.GetThemeUseCase
import uz.kabir.irregularverbs.domain.usecase.SaveLanguageUseCase
import uz.kabir.irregularverbs.domain.usecase.SaveProfileUseCase
import uz.kabir.irregularverbs.domain.usecase.SaveTextSizeUseCase
import uz.kabir.irregularverbs.domain.usecase.SaveThemeUseCase
import uz.kabir.irregularverbs.presentation.ui.state.AppLanguage
import uz.kabir.irregularverbs.presentation.ui.state.LanguageState
import uz.kabir.irregularverbs.presentation.ui.state.TextMode
import uz.kabir.irregularverbs.presentation.ui.state.TextState
import uz.kabir.irregularverbs.presentation.ui.state.ThemeMode
import uz.kabir.irregularverbs.presentation.ui.state.ThemeState
import uz.kabir.irregularverbs.domain.model.Profile
import uz.kabir.irregularverbs.domain.usecase.GetSoundStateUseCase
import uz.kabir.irregularverbs.domain.usecase.SaveSoundStateUseCase
import uz.kabir.irregularverbs.presentation.ui.utils.AudioHelper
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val saveThemeUseCase: SaveThemeUseCase,
    private val getThemeUseCase: GetThemeUseCase,
    private val saveTextSizeUseCase: SaveTextSizeUseCase,
    private val getTextSizeUseCase: GetTextSizeUseCase,
    private val saveLanguageUseCase: SaveLanguageUseCase,
    private val getLanguageUseCase: GetLanguageUseCase,
    private val getProgressUseCase: GetProgressUseCase,
    private val saveProfileUseCase: SaveProfileUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val saveSoundStateUseCase: SaveSoundStateUseCase,
    private val getSoundStateUseCase: GetSoundStateUseCase,
) : ViewModel() {

    private val _languageCode = MutableStateFlow(AppLanguage.AUTO)
    val languageCode: StateFlow<AppLanguage> = _languageCode.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _textSizeMode = MutableStateFlow(TextMode.BIG)
    val textSizeMode: StateFlow<TextMode> = _textSizeMode.asStateFlow()

    private val _userProgressList = MutableStateFlow<List<UserProgress>>(emptyList())
    val userProgressList: StateFlow<List<UserProgress>> = _userProgressList

    private val _overallProgressPercentage = MutableStateFlow(0.0f)
    val overallProgressPercentage: StateFlow<Float> = _overallProgressPercentage.asStateFlow()


    val soundState: StateFlow<Boolean> = getSoundStateUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    val profile: StateFlow<Profile?> = getProfileUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5_000),
        initialValue = null
    )


    init {
        viewModelScope.launch {
            val textMode = getTextSizeUseCase()
            _textSizeMode.value = textMode
            TextState.currentTextSize = textMode

            val themeMode = getThemeUseCase.getOnce() // suspend *
            _themeMode.value = themeMode
            ThemeState.currentTheme = themeMode

            val language = getLanguageUseCase()
            _languageCode.value = language
            LanguageState.currentLanguage = language
        }

        viewModelScope.launch {
            getProgressUseCase.getProgressFlow()
                .collectLatest { progressItems ->
                    val completedTestsCount = progressItems.count { it.testState == 1 }
                    val totalTestsCount = progressItems.size

                    if (totalTestsCount > 0) {
                        _overallProgressPercentage.value =
                            completedTestsCount.toFloat() / totalTestsCount
                    } else {
                        _overallProgressPercentage.value = 0.0f
                    }

                    Log.d(
                        "SettingsViewModel",
                        "Completed tests: $completedTestsCount, Total tests: $totalTestsCount, Overall Percentage: ${_overallProgressPercentage.value}"
                    )
                }
        }
    }

    fun saveLanguage(languageCode: AppLanguage) {
        viewModelScope.launch {
            saveLanguageUseCase(languageCode)
            LanguageState.currentLanguage = languageCode
            _languageCode.value = languageCode
        }
    }

    fun getLanguage() {
        viewModelScope.launch {
            val saved = getLanguageUseCase()
            _languageCode.value = saved
            LanguageState.currentLanguage = saved
        }
    }

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            saveThemeUseCase(themeMode)
            _themeMode.value = themeMode
            ThemeState.currentTheme = themeMode
        }
    }

    fun setTextSizeMode(textSizeMode: TextMode) {
        viewModelScope.launch {
            saveTextSizeUseCase(textSizeMode)
            _textSizeMode.value = textSizeMode
            TextState.currentTextSize = textSizeMode
        }
    }

    fun setProfileInfo(profile: Profile) {
        viewModelScope.launch {
            saveProfileUseCase(profile)
        }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            saveSoundStateUseCase(enabled)
        }
    }

    fun playClickSound(context: Context) {
        viewModelScope.launch {
            if (soundState.value) {
                AudioHelper.playClick(context)
            }
        }
    }

}