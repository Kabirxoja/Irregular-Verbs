package uz.kabir.irregularverbs.presentation.ui.screens.write

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import uz.kabir.irregularverbs.R
import uz.kabir.irregularverbs.presentation.navigation.Screens
import uz.kabir.irregularverbs.presentation.ui.screens.option.ProgressBarOptionScreen
import uz.kabir.irregularverbs.presentation.ui.screens.optionresult.MainButtonView
import uz.kabir.irregularverbs.presentation.ui.theme.Black
import uz.kabir.irregularverbs.presentation.ui.theme.Blue
import uz.kabir.irregularverbs.presentation.ui.theme.CustomTheme
import uz.kabir.irregularverbs.presentation.ui.theme.Green
import uz.kabir.irregularverbs.presentation.ui.theme.LightGray
import uz.kabir.irregularverbs.presentation.ui.theme.Red
import uz.kabir.irregularverbs.presentation.ui.utils.ReportManager
import uz.kabir.irregularverbs.presentation.ui.utils.SoundManager
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteFragment(
    navController: NavHostController,
    writeViewmodel: WriteViewModel
) {
    val verbs by writeViewmodel.verbs.collectAsState()
    val currentIndex by writeViewmodel.currentIndex.collectAsState()
    val currentQuestion by writeViewmodel.currentQuestion.collectAsState()
    val isBottomSheetVisible by writeViewmodel.isBottomSheetVisible.collectAsState()
    val correctCount by writeViewmodel.correctCount.collectAsState()
    val isFinished by writeViewmodel.isFinished.collectAsState()
    val result by writeViewmodel.result.collectAsState()
    val definitionEnglish by writeViewmodel.definitionEnglish.collectAsState()
    val progress by writeViewmodel.currentProgress.collectAsState()
    val timeSeconds by writeViewmodel.timerStateFlow.collectAsState()
    val groupId = writeViewmodel.groupId


    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }
    val soundState by writeViewmodel.soundState.collectAsState()

    val reportManager = remember { ReportManager(context) }

    LaunchedEffect(Unit) {
        writeViewmodel.playClick.collect {
            soundManager.playClickSound()
        }
    }

    LaunchedEffect(Unit) {
        writeViewmodel.getVerbsByGroupId()
        writeViewmodel.navigationSharedFlow.collect { event ->
            if (event is WriteNavEvent.NavigateToWriteResult) {
                navController.navigate(Screens.WriteResult.passGroupId(event.groupId))
            }
        }
    }

    LaunchedEffect(Unit) {
        writeViewmodel.sendReport.collect {
            reportManager.sendBugReport(
                testNumber = groupId,
                testVerb = currentQuestion?.verb1 ?: ""
            )
        }
    }

    DisposableEffect(Unit) {
        writeViewmodel.startTimer()
        onDispose() {
            writeViewmodel.stopTimer()
        }
    }


    val hideVerb2 = currentQuestion?.isVerb2Hidden ?: false
    val verb1 = currentQuestion?.verb1
    val verb2 = currentQuestion?.verb2
    val verb3 = currentQuestion?.verb3

    var inputQuery by remember { mutableStateOf("") }
    Surface(modifier = Modifier.fillMaxSize(), color = CustomTheme.colors.backgroundColor) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            TopBarWrite(
                onBackClick = {
                    navController.popBackStack()
                    if (soundState) {
                        writeViewmodel.playSound()
                    }

                },
                progress = progress,
                timeSeconds = timeSeconds,
            )

            Spacer(modifier = Modifier.weight(1f))



            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = "V1", style = CustomTheme.typography.largeText,
                    color = CustomTheme.colors.mainGray,
                )
                Text(
                    text = "V2", style = CustomTheme.typography.largeText,
                    color = CustomTheme.colors.mainGray,
                )
                Text(
                    text = "V3", style = CustomTheme.typography.largeText,
                    color = CustomTheme.colors.mainGray,
                )
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Blue)
                        .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = verb1.toString(),
                        textAlign = TextAlign.Center,
                        style = CustomTheme.typography.mediumText,
                        color = CustomTheme.colors.textWhite
                    )
                }

                if (hideVerb2) {
                    TextVerbField(
                        value = inputQuery,
                        onValueChange = { inputQuery = it },
                        hint = "Enter...",
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1f)
                    )
                } else {
                    EnterVerb(
                        verb2.toString(), modifier = Modifier
                            .height(56.dp)
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    )
                }

                if (!hideVerb2) {
                    TextVerbField(
                        value = inputQuery,
                        onValueChange = { inputQuery = it },
                        hint = "Enter...",
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1f)
                    )
                } else {
                    EnterVerb(
                        verb3.toString(), modifier = Modifier
                            .height(56.dp)
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            currentQuestion?.let {
                Text(
                    text = it.translation,
                    textAlign = TextAlign.Center,
                    style = CustomTheme.typography.largeText,
                    color = CustomTheme.colors.mainGray
                )
            }


            Spacer(modifier = Modifier.weight(1f))

            CheckButtonWrite(
                enabled = inputQuery != "",
                onCheckClick = {
                    writeViewmodel.checkAnswer(inputQuery)
                    if (soundState) {
                        writeViewmodel.playSound()
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(56.dp)
            )

            if (isBottomSheetVisible) {
                currentQuestion?.isCorrect?.let {
                    ResultBottomSheetWrite(
                        correct = it,
                        onDismiss = {
                            inputQuery = ""
                            writeViewmodel.continueNextQuestion()
                            if (soundState) {
                                writeViewmodel.playSound()
                            }

                        },
                        definitionEnglish = definitionEnglish,
                        definitionTranslation = currentQuestion?.translationExample,
                        onReportClick = {
                            writeViewmodel.sendReport()
                        }
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultBottomSheetWrite(
    correct: Boolean,
    onDismiss: () -> Unit,
    onReportClick: () -> Unit,
    definitionEnglish: AnnotatedString,
    definitionTranslation: String?
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
                    contentDescription = "icon",
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
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onReportClick()
                        },
                    tint = Color.Unspecified,

                    )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = buildAnnotatedString {
                    append("${stringResource(R.string.correct_answer)}: ")
                    append(definitionEnglish)
                },
                color = color,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                style = CustomTheme.typography.mediumText
            )

            if (definitionTranslation != "") {
                Text(
                    text = "${stringResource(R.string.translation)}: $definitionTranslation",
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
fun EnterVerb(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(Blue),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = CustomTheme.typography.mediumText,
            color = CustomTheme.colors.textWhite
        )
    }
}


@Composable
fun TextVerbField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier
) {
    val inputTextStyle = CustomTheme.typography.mediumText.copy(
        color = CustomTheme.colors.textBlackAndWhite,
    )

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = CustomTheme.colors.whiteToGray,
                RoundedCornerShape(12.dp)
            )
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
        textStyle = inputTextStyle,
        singleLine = true,
        maxLines = 1,
        cursorBrush = SolidColor(CustomTheme.colors.textBlackAndWhite),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = hint,
                        style = inputTextStyle,
                        color = Color.LightGray.copy(alpha = 0.4f),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .wrapContentHeight(Alignment.CenterVertically),
                    )
                }
                innerTextField()
            }
        }
    )
}


@Composable
fun CheckButtonWrite(enabled: Boolean, onCheckClick: () -> Unit, modifier: Modifier) {
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


@Composable
fun TopBarWrite(
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