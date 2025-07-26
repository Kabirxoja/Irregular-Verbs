package uz.kabir.irregularverbs.presentation.ui.screens.option

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.text.AnnotatedString
import androidx.core.content.ContextCompat.startActivity
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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uz.kabir.irregularverbs.domain.model.IrregularVerbTranslated
import uz.kabir.irregularverbs.domain.model.UserProgress
import uz.kabir.irregularverbs.domain.usecase.GetProgressUseCase
import uz.kabir.irregularverbs.domain.usecase.GetSoundStateUseCase
import uz.kabir.irregularverbs.domain.usecase.GetVerbsByGroupIdUseCase
import uz.kabir.irregularverbs.domain.usecase.UpdateProgressUseCase
import uz.kabir.irregularverbs.presentation.ui.theme.DarkGreen
import uz.kabir.irregularverbs.presentation.ui.theme.DarkRed
import uz.kabir.irregularverbs.presentation.ui.utils.toHighlightedColorText
import javax.inject.Inject
import kotlin.random.Random
import androidx.core.net.toUri
import kotlinx.coroutines.channels.Channel

@HiltViewModel
class OptionViewModel @Inject constructor(
    private val getVerbsByGroupIdUseCase: GetVerbsByGroupIdUseCase,
    private val updateProgressUseCase: UpdateProgressUseCase,
    private val getProgressUseCase: GetProgressUseCase,
    private val getSoundStateUseCase: GetSoundStateUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _verbs = MutableStateFlow<List<IrregularVerbTranslated>>(emptyList())
    val verbs: StateFlow<List<IrregularVerbTranslated>> = _verbs

    private val _isChecked = MutableStateFlow(false)
    val isChecked: StateFlow<Boolean> = _isChecked

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    private val _currentQuestion = MutableStateFlow<OptionItem?>(null)
    val currentQuestion: StateFlow<OptionItem?> = _currentQuestion

    private val _currentExample = MutableStateFlow<Pair<AnnotatedString, String>?>(null)
    val currentExample: StateFlow<Pair<AnnotatedString, String>?> = _currentExample

    private val _selectedAnswer = MutableStateFlow<String?>(null)
    val selectedAnswer: StateFlow<String?> = _selectedAnswer

    private val _correctCount = MutableStateFlow(0)
    val correctCount: StateFlow<Int> = _correctCount

    private val _isBottomSheetVisible = MutableStateFlow(false)
    val isBottomSheetVisible: StateFlow<Boolean> = _isBottomSheetVisible

    private val _currentProgress = MutableStateFlow(0f)
    val currentProgress: StateFlow<Float> = _currentProgress

    private val _questions = MutableStateFlow<List<OptionItem>>(emptyList())
    val questions: StateFlow<List<OptionItem>> = _questions

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished

    private val _getUserProgress = MutableStateFlow<UserProgress>(UserProgress(0, 0, false, false, false))
    val getUserProgress: StateFlow<UserProgress> = _getUserProgress

    private val _navigationSharedFlow = MutableSharedFlow<OptionNavEvent>(extraBufferCapacity = 1)
    val navigationSharedFlow = _navigationSharedFlow.asSharedFlow()

    private val _groupId = mutableIntStateOf(savedStateHandle["groupId"] ?: 0)
    val groupId: Int get() = _groupId.intValue

    val soundState: StateFlow<Boolean> = getSoundStateUseCase.invoke().stateIn(
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

    private val _sendReport = MutableSharedFlow<Unit>()
    val sendReport = _sendReport.asSharedFlow()

    fun sendReport(){
        viewModelScope.launch {
            _sendReport.emit(Unit)
        }
    }

    private val _timerStateFlow = MutableStateFlow(0)
    val timerStateFlow: StateFlow<Int> = _timerStateFlow
    private var timerJob: Job? = null

    fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _timerStateFlow.value++
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }

    fun getVerbsByGroupId(groupId: Int) {
        viewModelScope.launch {
            val data = getVerbsByGroupIdUseCase(groupId)
            _verbs.value = data
            generateQuestion(0)
            Log.d("VIEWMODEL", "Verbs loaded: ${data.size}")
        }
    }

    private fun generateQuestion(index: Int) {
        val verb = _verbs.value.getOrNull(index) ?: return

        val isHideV2 = Random.Default.nextBoolean()

        val correctAnswer: String
        val options: List<String>
        val visiblePart: String

        if (isHideV2) {
            // PAST SIMPLE HIDE, PAST PARTICIPLE SHOW
            correctAnswer = verb.pastSimple
            options = listOf(verb.pastSimple, verb.pastSimpleOption1, verb.pastSimpleOption2)
            visiblePart = verb.pastParticiple
        } else {
            // PAST PARTICIPLE HIDE, PAST SIMPLE SHOW
            correctAnswer = verb.pastParticiple
            options = listOf(verb.pastParticiple, verb.pastParticipleOption1, verb.pastParticipleOption2)
            visiblePart = verb.pastSimple
        }

        val question = OptionItem(
            baseForm = verb.baseForm,
            visiblePart = visiblePart,
            options = options.shuffled(),
            correctAnswer = correctAnswer,
            isVerb2Hidden = isHideV2,
            translation = verb.translation
        )

        _currentQuestion.value = question

        val updatedList = _questions.value.toMutableList()
        if (index < updatedList.size) {
            updatedList[index] = question
        } else {
            updatedList.add(question)
        }
        _questions.value = updatedList

        _currentProgress.value = (index + 1).toFloat() / _verbs.value.size
    }

    fun selectAnswer(answer: String) {
        _selectedAnswer.value = answer
    }

    fun checkAnswer() {
        val selected = _selectedAnswer.value ?: return
        val current = _currentQuestion.value ?: return
        val isCorrect = selected == current.correctAnswer

        if (isCorrect) {
            _correctCount.value++
        }

        val updatedQuestion = current.copy(selectedAnswer = selected)
        _currentQuestion.value = updatedQuestion
        _isChecked.value = true
        _isBottomSheetVisible.value = true

        val index = _currentIndex.value
        val updatedList = _questions.value.toMutableList()

        if (index < updatedList.size) {
            updatedList[index] = updatedQuestion
        } else {
            updatedList.add(updatedQuestion)
        }

        _questions.value = updatedList


        val color = if (isCorrect) DarkGreen else DarkRed

        val verb = _verbs.value.getOrNull(index) ?: return
        val example = if (current.isVerb2Hidden) verb.verb2 else verb.verb3
        val translation =
            if (current.isVerb2Hidden) verb.verb2Translation else verb.verb3Translation

        _currentExample.value = Pair(example.toHighlightedColorText(color), translation)
    }

    fun continueToNextQuestion() {
        _selectedAnswer.value = null
        _isChecked.value = false
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
                _navigationSharedFlow.emit(OptionNavEvent.NavigateToOptionResult(_groupId.intValue))
            }
        }
    }

    fun saveUserProgress(groupId: Int, correctCount: Int, previousProgress: UserProgress) {
        val isOptionStar = correctCount == _verbs.value.size
        val isWriteStar = previousProgress.writeTestStar
        val isListenStar = previousProgress.listenTestStar

        viewModelScope.launch {
            val progressState = if (isListenStar && isWriteStar && isOptionStar) {
                1
            } else {
                previousProgress.testState
            }

            Log.d("TESTT", "write=$isWriteStar option=$isOptionStar listen=$isListenStar")
            Log.d("TESTT", "previous testState: ${previousProgress.testState}")

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

            if (isListenStar && isWriteStar && isOptionStar && nextGroupId <= totalProgressGroups) {
                val nextProgress = allProgress.find { it.groupId == nextGroupId }
                updateProgressUseCase(nextProgress?.copy(testState = 0) ?: return@launch)
            }

        }
    }

    fun toHomeScreen() {
        viewModelScope.launch {
            _navigationSharedFlow.emit(OptionNavEvent.NavigateToHome) //navigate
        }
    }


    fun reportIntent(context: Context){
        Log.d("opktyj", "previous testState: ${_questions.value}")
        Log.d("opktyj", "previous testState: ${_currentQuestion.value}")


        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf("kabirtechapps@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Bug Report from Irregular Verbs MultiLang")
            putExtra(Intent.EXTRA_TEXT, "Please describe your problem in detail...")
        }
        startActivity(context, intent, null)
    }
}