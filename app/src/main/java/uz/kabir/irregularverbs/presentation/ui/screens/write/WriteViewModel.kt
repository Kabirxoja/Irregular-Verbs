package uz.kabir.irregularverbs.presentation.ui.screens.write

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
import uz.kabir.irregularverbs.presentation.ui.utils.toHighlightedColorText
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val getVerbsByGroupIdUseCase: GetVerbsByGroupIdUseCase,
    private val updateProgressUseCase: UpdateProgressUseCase,
    private val getProgressUseCase: GetProgressUseCase,
    savedStateHandle: SavedStateHandle,
    private val getSoundStateUseCase: GetSoundStateUseCase
) : ViewModel() {

    private val _verbs = MutableStateFlow<List<IrregularVerbTranslated>>(emptyList())
    val verbs: StateFlow<List<IrregularVerbTranslated>> = _verbs

    private val _result = MutableStateFlow<List<WriteItem>>(emptyList())
    val result: StateFlow<List<WriteItem>> = _result

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    private val _currentQuestion = MutableStateFlow<WriteItem?>(null)
    val currentQuestion: StateFlow<WriteItem?> = _currentQuestion

    private val _isBottomSheetVisible = MutableStateFlow(false)
    val isBottomSheetVisible: StateFlow<Boolean> = _isBottomSheetVisible

    private val _correctCount = MutableStateFlow(0)
    val correctCount: StateFlow<Int> = _correctCount

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished

    private val _definitionEnglish = MutableStateFlow(AnnotatedString(""))
    val definitionEnglish: StateFlow<AnnotatedString> = _definitionEnglish

    private val _getUserProgress =
        MutableStateFlow<UserProgress>(UserProgress(0, 0, false, false, false))
    val getUserProgress: StateFlow<UserProgress> = _getUserProgress

    private val _groupId = mutableIntStateOf(savedStateHandle["groupId"] ?: 0)
    val groupId: Int get() = _groupId.intValue

    private val _timerStateFlow = MutableStateFlow(0)
    val timerStateFlow: StateFlow<Int> = _timerStateFlow
    private var timerJob: Job? = null

    private val _currentProgress = MutableStateFlow(0f)
    val currentProgress: StateFlow<Float> = _currentProgress

    private val _navigationSharedFlow = MutableSharedFlow<WriteNavEvent>(extraBufferCapacity = 1)
    val navigationSharedFlow = _navigationSharedFlow.asSharedFlow()

    val soundState: StateFlow<Boolean> = getSoundStateUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )


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

    fun getVerbsByGroupId() {
        viewModelScope.launch {
            val data = getVerbsByGroupIdUseCase(_groupId.intValue)
            _verbs.value = data

            _currentIndex.value = 0
            generateQuestion(0)
        }
        Log.d("Groupp", "Group ID: ${_groupId.intValue}")
    }

    fun generateQuestion(index: Int) {
        val verb = _verbs.value.getOrNull(index) ?: return
        val isHideV2 = Random.Default.nextBoolean()
        val correctAnswer: String = if (isHideV2) {
            verb.pastSimple
        } else {
            verb.pastParticiple
        }
        val question = WriteItem(
            verb1 = verb.baseForm,
            verb2 = verb.pastSimple,
            verb3 = verb.pastParticiple,
            isVerb2Hidden = isHideV2,
            translation = verb.translation,
            isCorrect = false,
            example = if (isHideV2) verb.verb2 else verb.verb3,
            translationExample = if (isHideV2) verb.verb2Translation else verb.verb3Translation
        )
        _currentQuestion.value = question
        _currentProgress.value = (index + 1).toFloat() / _verbs.value.size
    }

    fun checkAnswer(answer: String) {
        val currentQuestion = _currentQuestion.value ?: return

        val isCorrect = if (currentQuestion.isVerb2Hidden) {
            answer.trim().equals(currentQuestion.verb2, ignoreCase = true)
        } else {
            answer.trim().equals(currentQuestion.verb3, ignoreCase = true)
        }

        val color = if (isCorrect) DarkGreen else DarkRed
        _definitionEnglish.value = currentQuestion.example.toHighlightedColorText(color)

        if (isCorrect) {
            _correctCount.value++
        }

        val updatedQuestion = currentQuestion.copy(
            isCorrect = isCorrect,
            userAnswer = answer
        )

        Log.d("CHECKK", "Before size: ${_result.value.size}")
        _currentQuestion.value = updatedQuestion
        _result.value = _result.value + updatedQuestion
        Log.d("CHECKK", "After size: ${_result.value.size}")
        Log.d("CHECKK", "List content: ${_result.value}")

        _isBottomSheetVisible.value = true
    }

    fun continueNextQuestion() {
        _isBottomSheetVisible.value = false
        val nextIndex = _currentIndex.value + 1
        if (nextIndex < _verbs.value.size) {
            _currentIndex.value = nextIndex
            generateQuestion(nextIndex)
        } else {
            _isFinished.value = true

            viewModelScope.launch {
                val progressList = getProgressUseCase.getProgressFlow().first()
                val currentProgress = progressList[_groupId.intValue - 1]
                _getUserProgress.value = currentProgress
                saveUserProgress(_groupId.intValue, _correctCount.value, currentProgress)
                _navigationSharedFlow.emit(WriteNavEvent.NavigateToWriteResult(groupId))
            }
        }
    }

    fun saveUserProgress(groupId: Int, correctCount: Int, previousProgress: UserProgress) {

        val isWriteStar = correctCount == _verbs.value.size
        val isOptionStar = previousProgress.optionTestStar
        val isListenStar = previousProgress.listenTestStar

        Log.d("Groupp", "Group ID: ${groupId}")
        Log.d("TESTT", "write=$isWriteStar option=$isOptionStar listen=$isListenStar")
        Log.d("TESTT", "previous testState: ${previousProgress.testState}")

        viewModelScope.launch {
            val progressState = if (isWriteStar && isOptionStar && isListenStar) {
                1
            } else {
                previousProgress.testState
            }

            Log.d("TESTT", "new testState: $progressState")

            if (correctCount == _verbs.value.size) {
                updateProgressUseCase(
                    UserProgress(
                        groupId = groupId,
                        testState = progressState,
                        optionTestStar = isOptionStar,
                        listenTestStar = isListenStar,
                        writeTestStar = isWriteStar
                    )
                )
            }

            val allProgress = getProgressUseCase.getProgressFlow().first()
            val totalProgressGroups = allProgress.size
            val nextGroupId = groupId + 1
            if (isWriteStar && isOptionStar && isListenStar && nextGroupId <= totalProgressGroups) {
                val nextProgress = allProgress.find { it.groupId == nextGroupId }
                updateProgressUseCase(nextProgress?.copy(testState = 0) ?: return@launch)
            }
        }

    }

    fun toHomeScreen() {
        viewModelScope.launch {
            _navigationSharedFlow.emit(WriteNavEvent.NavigateToHome) //navigate
        }
    }

    fun playClickSound(context:Context){
        if(soundState.value){
            AudioHelper.playClick(context)
        }
    }

}