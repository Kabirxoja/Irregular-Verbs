package uz.kabir.irregularverbs.presentation.ui.screens.learn

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.kabir.irregularverbs.domain.model.IrregularVerbTranslated
import uz.kabir.irregularverbs.domain.usecase.GetLanguageUseCase
import uz.kabir.irregularverbs.domain.usecase.GetSoundStateUseCase
import uz.kabir.irregularverbs.domain.usecase.GetVerbsByLevelUseCase
import uz.kabir.irregularverbs.presentation.ui.state.AppLanguage
import uz.kabir.irregularverbs.presentation.ui.utils.SoundManager
import uz.kabir.irregularverbs.presentation.ui.utils.TTSManager
import javax.inject.Inject

@HiltViewModel
class LearnViewModel @Inject constructor(
    private val getVerbsByLevelUseCase: GetVerbsByLevelUseCase,
    private val getLanguageUseCase: GetLanguageUseCase,
    private val ttsManager: TTSManager,
    getSoundStateUseCase: GetSoundStateUseCase,

    ) : ViewModel() {
    private val _verbs = MutableStateFlow<List<IrregularVerbTranslated>>(emptyList())
    val verbs: StateFlow<List<IrregularVerbTranslated>> = _verbs.asStateFlow()

    private val _language = MutableStateFlow<AppLanguage>(AppLanguage.AUTO)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    val soundState: StateFlow<Boolean> = getSoundStateUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    private val _playClick = MutableSharedFlow<Unit>()
    val playClick = _playClick.asSharedFlow()

    fun playSound() {
        viewModelScope.launch {
            _playClick.emit(Unit)
        }
    }


    fun getVerbsByLevel(level: String) {
        viewModelScope.launch {
            _verbs.value = getVerbsByLevelUseCase(level = level)
        }
    }

    fun getLanguage() {
        viewModelScope.launch {
            _language.value = getLanguageUseCase()
        }
    }

    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady = _isTtsReady.asStateFlow()

    fun initTTS() {
        ttsManager.init {
            _isTtsReady.value = true
        }
    }

    fun speak(text: String) {
        ttsManager.speak(text)
    }

    fun shutDown() {
        ttsManager.shutdown()
    }


}