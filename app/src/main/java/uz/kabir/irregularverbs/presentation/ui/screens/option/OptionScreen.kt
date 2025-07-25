package uz.kabir.irregularverbs.presentation.ui.screens.option

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import uz.kabir.irregularverbs.R
import uz.kabir.irregularverbs.presentation.ui.theme.Black
import uz.kabir.irregularverbs.presentation.ui.theme.CustomTheme
import uz.kabir.irregularverbs.presentation.ui.theme.Green
import uz.kabir.irregularverbs.presentation.ui.theme.LightGray
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.launch
import uz.kabir.irregularverbs.presentation.navigation.Screens
import uz.kabir.irregularverbs.presentation.ui.screens.optionresult.MainButtonView
import uz.kabir.irregularverbs.presentation.ui.theme.Blue
import uz.kabir.irregularverbs.presentation.ui.theme.Orange
import uz.kabir.irregularverbs.presentation.ui.theme.Red
import uz.kabir.irregularverbs.presentation.ui.utils.SoundManager
import java.util.Locale

@Composable
fun OptionFragment(
    navController: NavHostController,
    optionViewModel: OptionViewModel
) {
    val verbs by optionViewModel.verbs.collectAsState()
    val selected by optionViewModel.selectedAnswer.collectAsState()
    val showBottomSheet by optionViewModel.isBottomSheetVisible.collectAsState()
    val question by optionViewModel.currentQuestion.collectAsState()
    val definition by optionViewModel.currentExample.collectAsState()
    val currentIndex by optionViewModel.currentIndex.collectAsState()
    val correctCount by optionViewModel.correctCount.collectAsState()
    val finalList by optionViewModel.questions.collectAsState()
    val timeSeconds by optionViewModel.timerStateFlow.collectAsState()
    val progress by optionViewModel.currentProgress.collectAsState()
    val groupId = optionViewModel.groupId
    val soundState by optionViewModel.soundState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        optionViewModel.navigationSharedFlow.collect { event ->
            if (event is OptionNavEvent.NavigateToOptionResult) {
                navController.navigate(Screens.OptionResult.passGroupId(event.groupId))
            }
        }

    }

    Log.d("groupIdOUT", "groupId = $groupId")

    optionViewModel.startTimer()

    LaunchedEffect(key1 = groupId) {
        groupId.let {
            optionViewModel.getVerbsByGroupId(it)
            Log.d("COMPOSABLE", "Calling getVerbsByGroupId")
        }
    }
    DisposableEffect(Unit) {
        optionViewModel.startTimer()
        onDispose {
            optionViewModel.stopTimer()
        }
    }

    Log.d("OptionFragment", "groupId: $groupId")
    Log.d("OptionFragmentVERBS", "verbs: ${verbs.size}")
    Log.d("OptionFragment", "currentIndex: $currentIndex")
    Log.d("OptionFragment", "correctCount: $correctCount")

    Log.d("OptionFragmentRRR", "verbs: $finalList")

    Surface(modifier = Modifier.fillMaxSize(), color = CustomTheme.colors.backgroundColor) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                TopBarOption(
                    onBackClick = {
                        navController.popBackStack()
                        optionViewModel.playClickSound(context)
                    },
                    progress = progress,
                    timeSeconds = timeSeconds
                )


                if (question == null) return@Box

                val wordList = if (question!!.isVerb2Hidden) {
                    listOf(
                        question!!.baseForm,
                        "?",
                        question!!.visiblePart
                    )

                } else {

                    listOf(
                        question!!.baseForm,
                        question!!.visiblePart,
                        "?"
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                question?.let {
                    Text(
                        text = it.translation,
                        textAlign = TextAlign.Center,
                        style = CustomTheme.typography.largeText,
                        color = CustomTheme.colors.mainGray,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .fillMaxWidth(),
                    )
                }

                MainContent(
                    words = wordList,
                    options = question?.options ?: emptyList(),
                    selected = selected,
                    onOptionSelect = {
                        optionViewModel.selectAnswer(it)
                        optionViewModel.playClickSound(context)
                    },
                    modifier = Modifier.wrapContentHeight(),
                )


                Spacer(modifier = Modifier.weight(1f))

                CheckButtonOption(
                    enabled = selected != null,
                    onCheckClick = {
                        optionViewModel.checkAnswer()
                        optionViewModel.playClickSound(context)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .height(56.dp)
                )
            }

            if (showBottomSheet) {
                ResultBottomSheetOption(
                    correct = selected == question?.correctAnswer,
                    onDismiss = {
                        optionViewModel.continueToNextQuestion()
                        optionViewModel.playClickSound(context)
                    },
                    definition = definition,
                    onReportClick = {
                        optionViewModel.reportIntent(context)
                    }
                )
            }
        }
    }
}

@Composable
fun TopBarOption(
    onBackClick: () -> Unit,
    progress: Float,
    timeSeconds: Int
) {
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    val min = timeSeconds / 60
    val sec = timeSeconds % 60

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_cancel),
            contentDescription = "Back",
            tint = Black,
            modifier = Modifier
                .size(24.dp)
                .scale(scaleX.value, scaleY.value)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    coroutineScope.launch {
                        scaleX.animateTo(0.9f, tween(50))
                        scaleY.animateTo(0.9f, tween(50))
                        scaleX.animateTo(1f, tween(100))
                        scaleY.animateTo(1f, tween(100))
                    }
                    onBackClick()
                }
        )

        Spacer(modifier = Modifier.width(12.dp))

        ProgressBarOptionScreen(
            progress = progress,
            modifier = Modifier
                .weight(1f),
            backgroundColor = CustomTheme.colors.whiteToGray,
            progressColor = Green,
            height = 16.dp,
            cornerRadius = 12.dp
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            String.format(Locale.US, "%02d:%02d", min, sec),
            style = CustomTheme.typography.largeText,
            color = Black
        )
    }
}

@Composable
fun ProgressBarOptionScreen(
    progress: Float,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    progressColor: Color,
    height: Dp,
    cornerRadius: Dp
) {
    Canvas(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawRoundRect(
            color = backgroundColor,
            size = Size(canvasWidth, canvasHeight),
            cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
        )

        drawRoundRect(
            color = progressColor,
            size = Size(canvasWidth * progress.coerceIn(0f, 1f), canvasHeight),
            cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
        )
    }
}


@Composable
fun MainContent(
    words: List<String?>,
    options: List<String>,
    selected: String?,
    onOptionSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QuestionList(words)

        Spacer(modifier = Modifier.height(36.dp))

        OptionList(
            options = options,
            selected = selected,
            onSelect = onOptionSelect
        )
    }
}

@Composable
fun QuestionList(words: List<String?>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        words.forEach {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Blue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = it?.uppercase() ?: "",
                    style = CustomTheme.typography.mediumText,
                    color = CustomTheme.colors.textWhite
                )
            }
        }
    }
}

@Composable
fun OptionList(
    options: List<String>,
    selected: String?,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            AnimatedOptionItem(
                text = option,
                isSelected = option == selected,
                onClick = { onSelect(option) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
        }
    }
}

@Composable
fun AnimatedOptionItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Orange else CustomTheme.colors.mainGray,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = modifier
            .height(56.dp)
            .scale(scaleX.value, scaleY.value)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                coroutineScope.launch {
                    scaleX.animateTo(0.98f, tween(50))
                    scaleY.animateTo(0.98f, tween(50))
                    scaleX.animateTo(1f, tween(100))
                    scaleY.animateTo(1f, tween(100))
                }
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            style = CustomTheme.typography.mediumText,
            color = CustomTheme.colors.textWhite
        )
    }
}

@Composable
fun CheckButtonOption(
    enabled: Boolean,
    onCheckClick: () -> Unit,
    modifier: Modifier
) {
    val buttonCorner = if (enabled) null else BorderStroke(2.dp, Black)
    val buttonColor = if (enabled) Green else LightGray
    val buttonTextColor = if (enabled) CustomTheme.colors.textWhite else Black

    Button(
        onClick = onCheckClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = buttonCorner,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Red
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = (stringResource(R.string.check_button)).uppercase(),
            style = CustomTheme.typography.mediumText,
            color = buttonTextColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultBottomSheetOption(
    correct: Boolean,
    onDismiss: () -> Unit,
    onReportClick: () -> Unit,
    definition: Pair<AnnotatedString, String>?,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val color = if (correct) Green else Red
    val text = if (correct) "Correct" else "Incorrect"
    val icon = if (correct) R.drawable.ic_correct else R.drawable.ic_incorrect

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CustomTheme.colors.backgroundColor,
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "State",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
                Text(
                    text = text,
                    color = color,
                    modifier = Modifier.padding(start = 8.dp),
                    style = CustomTheme.typography.largeText
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.ic_report),
                    contentDescription = "Report",
                    modifier = Modifier.size(24.dp).clickable{
                        onReportClick()
                    },
                    tint = Color.Unspecified,

                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = buildAnnotatedString {
                    append("${stringResource(R.string.correct_answer)}: ")
                    append(definition?.first)
                },
                color = color,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                style = CustomTheme.typography.mediumText
            )

            if (definition?.second != "") {
                Text(
                    text = "${stringResource(R.string.translation)}: ${definition?.second}",
                    color = color,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    style = CustomTheme.typography.mediumText
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            MainButtonView(
                onClick = { onDismiss() },
                text = stringResource(R.string.continue_button),
                buttonColor = color,
                modifier = Modifier.padding(bottom = 14.dp)
            )
        }
    }
}

@Composable
fun rememberSoundManager(): SoundManager {
    val context = LocalContext.current
    return remember(context) { SoundManager(context) }
}



