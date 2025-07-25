package uz.kabir.irregularverbs.presentation.ui.screens.listen

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.kabir.irregularverbs.domain.model.IrregularVerbTranslated
import uz.kabir.irregularverbs.domain.model.UserProgress
import uz.kabir.irregularverbs.domain.usecase.GetProgressUseCase
import uz.kabir.irregularverbs.domain.usecase.GetSoundStateUseCase
import uz.kabir.irregularverbs.domain.usecase.GetVerbsByGroupIdUseCase
import uz.kabir.irregularverbs.domain.usecase.UpdateProgressUseCase
import uz.kabir.irregularverbs.presentation.ui.theme.DarkGreen
import uz.kabir.irregularverbs.presentation.ui.theme.DarkRed
import uz.kabir.irregularverbs.presentation.ui.utils.AudioHelper
import uz.kabir.irregularverbs.presentation.ui.utils.TTSManager
import uz.kabir.irregularverbs.presentation.ui.utils.toHighlightedColorText
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ListenViewModel @Inject constructor(
    private val getVerbsByGroupIdUseCase: GetVerbsByGroupIdUseCase,
    savedStateHandle: SavedStateHandle,
    private val updateProgressUseCase: UpdateProgressUseCase,
    private val getProgressUseCase: GetProgressUseCase,
    private val ttsManager: TTSManager,
    getSoundStateUseCase: GetSoundStateUseCase
) : ViewModel() {

    private val _groupId = mutableIntStateOf(savedStateHandle["groupId"] ?: 0)
    val groupId: Int get() = _groupId.intValue

    private val _phase = MutableStateFlow(1)

    private val _firstHalf = MutableStateFlow<List<ListenItem>>(emptyList())
    private val _secondHalf = MutableStateFlow<List<ListenItem>>(emptyList())

    private val _verb1List = MutableStateFlow<List<String>>(emptyList())
    val verb1List: StateFlow<List<String>> = _verb1List.asStateFlow()

    private val _verb2Or3List = MutableStateFlow<List<String>>(emptyList())
    val verb2Or3List: StateFlow<List<String>> = _verb2Or3List.asStateFlow()

    private val _selectedV1 = MutableStateFlow("")
    val selectedV1: StateFlow<String> = _selectedV1

    private val _selectedV2V3 = MutableStateFlow("")
    val selectedV2V3: StateFlow<String> = _selectedV2V3

    private val _matched = MutableStateFlow<Set<Pair<String, String>>>(emptySet())
    val matched: StateFlow<Set<Pair<String, String>>> = _matched

    private val _isBottomSheetVisible = MutableStateFlow(false)
    val isBottomSheetVisible: StateFlow<Boolean> = _isBottomSheetVisible

    private val _lastMatchedItem = MutableStateFlow<ListenItem?>(null)
    val lastMatchedItem: StateFlow<ListenItem?> = _lastMatchedItem

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished

    private val _results = MutableStateFlow<List<ListenItem>>(emptyList())
    val results: StateFlow<List<ListenItem>> = _results

    private val _timerStateFlow = MutableStateFlow(0)
    val timerStateFlow: StateFlow<Int> = _timerStateFlow
    private var timerJob: Job? = null

    private val _getUserProgress =
        MutableStateFlow<UserProgress>(UserProgress(0, 0, false, false, false))
    val getUserProgress: StateFlow<UserProgress> = _getUserProgress

    private val _definitionEnglish = MutableStateFlow(AnnotatedString(""))
    val definitionEnglish: StateFlow<AnnotatedString> = _definitionEnglish

    private val _navigationSharedFlow = MutableSharedFlow<ListenNavEvent>(extraBufferCapacity = 1)
    val navigationSharedFlow = _navigationSharedFlow.asSharedFlow()

    val visibleItems: StateFlow<List<ListenItem>> =
        combine(_phase, _firstHalf, _secondHalf) { phase, first, second ->
            if (phase == 1) first else second
        }.stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), emptyList())

    val soundState: StateFlow<Boolean> = getSoundStateUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    fun launchList() {
        viewModelScope.launch {
            val all = getVerbsByGroupIdUseCase(groupId).shuffled()
            val half = all.size / 2
            val firstHalf = all.take(half).map { createListenItem(it) }.shuffled()
            val secondHalf = all.drop(half).map { createListenItem(it) }.shuffled()

            _firstHalf.value = firstHalf
            _secondHalf.value = secondHalf


            Log.d("ListenViewModel", "firstHalf:    ${_firstHalf.value}")
            Log.d("ListenViewModel", "_secondHalf:    ${_secondHalf.value}")
            Log.d("VISSIBLE", "visibleItems:    ${visibleItems.value}")

            _verb1List.value = firstHalf.map { it.verb1 }.shuffled()

            for (item in firstHalf) {
                if (item.isVerb2Hidden) {
                    _verb2Or3List.value = (_verb2Or3List.value + item.verb2).shuffled()
                } else {
                    _verb2Or3List.value = (_verb2Or3List.value + item.verb3).shuffled()
                }
            }

            Log.d("ListenViewModel", "_verb1List:    ${_verb1List.value}")
            Log.d("ListenViewModel", "_verb2Or3List:    ${_verb2Or3List.value}")

        }
    }


    fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _timerStateFlow.value++
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }


    private fun createListenItem(verb: IrregularVerbTranslated): ListenItem {
        val isV2 = Random.Default.nextBoolean()
        val form = if (isV2) verb.verb2 else verb.verb3
        val translation = if (isV2) verb.verb2Translation else verb.verb3Translation
        return ListenItem(
            verb1 = verb.baseForm,
            verb2 = verb.pastSimple,
            verb3 = verb.pastParticiple,
            isVerb2Hidden = isV2,
            selected = "" to "",
            isCorrect = false,
            example = form,
            translationExample = translation
        )
    }

    fun selectV1(v1: String) {
        _selectedV1.value = v1
    }

    fun selectV2V3(text: String) {
        _selectedV2V3.value = text
    }

    fun checkAnswer() {
        val v1 = _selectedV1.value
        val v2v3 = _selectedV2V3.value
        if ((v1.isBlank() || v2v3.isBlank()) && _matched.value.any { it.first == v1 || it.second == v2v3 }) return

        val alreadyUsed = _matched.value.any { it.first == v1 || it.second == v2v3 }
        if (alreadyUsed) return

        val isCorrect =
            visibleItems.value.any { it.verb1 == v1 && (it.verb2 == v2v3 || it.verb3 == v2v3) }
        val updatedItem = visibleItems.value.find { it.verb1 == v1 }
            ?.copy(selected = v1 to v2v3, isCorrect = isCorrect)

        val color = if (isCorrect) DarkGreen else DarkRed
        _definitionEnglish.value =
            (updatedItem?.example?.toHighlightedColorText(color) ?: "") as AnnotatedString

        updatedItem?.let {
            _results.value = _results.value + it
            _matched.value = _matched.value + (v1 to v2v3)

            val updatedList = visibleItems.value.map { item ->
                if (item.verb1 == v1) it else item
            }

            if (_phase.value == 1) _firstHalf.value = updatedList
            else _secondHalf.value = updatedList

            _lastMatchedItem.value = it
            _isBottomSheetVisible.value = true
        }

        // Reset tanlovlar
        _selectedV1.value = ""
        _selectedV2V3.value = ""
    }

    fun continueToNextQuestion() {
        _isBottomSheetVisible.value = false
        _lastMatchedItem.value = null

        // Phase tugaganini tekshirish
        val remaining =
            visibleItems.value.any { it.selected.first.isBlank() || it.selected.second.isBlank() }
        if (!remaining) {
            if (_phase.value == 1) {
                _phase.value = 2

                _verb1List.value = emptyList()
                _verb2Or3List.value = emptyList()


                _verb1List.value = _secondHalf.value.map { it.verb1 }.shuffled()

                for (item in _secondHalf.value) {
                    if (item.isVerb2Hidden) {
                        _verb2Or3List.value = (_verb2Or3List.value + item.verb2).shuffled()
                    } else {
                        _verb2Or3List.value = (_verb2Or3List.value + item.verb3).shuffled()
                    }
                }


            } else {
                _isFinished.value = true
                Log.d("ListenViewModel", "_results.value:    ${_results.value}")

                val correctAnswerCount = _results.value.count { it.isCorrect }

                viewModelScope.launch {
                    val progressList = getProgressUseCase.getProgressFlow().first()
                    val currentProgress = progressList[_groupId.intValue - 1]
                    _getUserProgress.value = currentProgress
                    saveUserProgress(_groupId.intValue, correctAnswerCount, currentProgress)
                    _navigationSharedFlow.emit(ListenNavEvent.NavigateToListenResult(groupId)) // navigate
                }

            }
        }
    }


    fun saveUserProgress(groupId: Int, correctCount: Int, previousProgress: UserProgress) {

        val isListenStar = correctCount == _results.value.size
        val isWriteStar = previousProgress.writeTestStar
        val isOptionStar = previousProgress.optionTestStar

        viewModelScope.launch {
            val progressState = if (isListenStar && isWriteStar && isOptionStar) {
                1
            } else {
                previousProgress.testState
            }

            if (correctCount == _results.value.size) {
                updateProgressUseCase(
                    UserProgress(
                        groupId = groupId,
                        testState = progressState,
                        listenTestStar = isListenStar,
                        optionTestStar = isOptionStar,
                        writeTestStar = isWriteStar
                    )
                )
            }

            val allProgress = getProgressUseCase.getProgressFlow().first()
            val totalProgressGroups = allProgress.size
            val nextGroupId = groupId + 1

            if (isListenStar && isWriteStar && isOptionStar && nextGroupId <= totalProgressGroups) {
                val nextProgress = allProgress.find { it.groupId == nextGroupId }
                updateProgressUseCase(nextProgress?.copy(testState = 0) ?: return@launch)
            }
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

    fun toHomeScreen() {
        viewModelScope.launch {
            _navigationSharedFlow.emit(ListenNavEvent.NavigateToHome) // navigate
        }
    }

    fun playClickSound(context: Context){
        if(soundState.value){
            AudioHelper.playClick(context)
        }
    }

}