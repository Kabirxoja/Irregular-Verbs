package uz.kabir.irregularverbs.presentation.ui.screens.listen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import uz.kabir.irregularverbs.R
import uz.kabir.irregularverbs.presentation.ui.theme.LightGray
import uz.kabir.irregularverbs.presentation.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import uz.kabir.irregularverbs.presentation.ui.theme.Black
import uz.kabir.irregularverbs.presentation.ui.theme.Blue
import uz.kabir.irregularverbs.presentation.ui.theme.CustomTheme
import uz.kabir.irregularverbs.presentation.ui.theme.Green
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.flow.collectLatest
import uz.kabir.irregularverbs.presentation.navigation.Screens
import uz.kabir.irregularverbs.presentation.ui.screens.optionresult.MainButtonView
import uz.kabir.irregularverbs.presentation.ui.theme.Red
import uz.kabir.irregularverbs.presentation.ui.utils.ReportManager
import uz.kabir.irregularverbs.presentation.ui.utils.SoundManager

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ListenFragment(
    navController: NavHostController,
    listenViewModel: ListenViewModel
) {
    val getList by listenViewModel.visibleItems.collectAsState()
    val showBottomSheet by listenViewModel.isBottomSheetVisible.collectAsState()
    val isCurrentQuestion by listenViewModel.lastMatchedItem.collectAsState()
    val groupId = listenViewModel.groupId
    val selectedV1 by listenViewModel.selectedV1.collectAsState()
    val selectedV2V3 by listenViewModel.selectedV2V3.collectAsState()
    val matchedPairs by listenViewModel.matched.collectAsState()
    val timeSeconds by listenViewModel.timerStateFlow.collectAsState()
    val definition by listenViewModel.definitionEnglish.collectAsState()
    val verb1 by listenViewModel.verb1List.collectAsState()
    val verb2Or3 by listenViewModel.verb2Or3List.collectAsState()

    val context = LocalContext.current
    val soundManager = SoundManager(context)
    val soundState by listenViewModel.soundState.collectAsState()

    val reportManager = remember { ReportManager(context) }


    LaunchedEffect(Unit) {
        listenViewModel.initTTS()
        listenViewModel.launchList()

        listenViewModel.navigationSharedFlow.collectLatest { event ->
            if (event is ListenNavEvent.NavigateToListenResult) {
                navController.navigate(Screens.ListenResult.passGroupId(event.groupId))
            }
        }
    }

    LaunchedEffect(Unit) {
        listenViewModel.playClick.collect {
            soundManager.playClickSound()
        }
    }

    LaunchedEffect(Unit) {
        listenViewModel.sendReport.collect {
            reportManager.sendBugReport(
                testNumber = groupId,
                testVerb = isCurrentQuestion?.verb1 ?: ""
            )
        }
    }


    DisposableEffect(Unit) {
        listenViewModel.startTimer()
        onDispose {
            listenViewModel.stopTimer()
        }
    }


    Log.d("LISTENFRAGMENT", "matchedItems: ${matchedPairs}")
    Log.d("LISTENFRAGMENT", "results: ${getList}")
    Log.d("LISTENFRAGMENT", "isCurrentQuestion: ${isCurrentQuestion}")


    Surface(modifier = Modifier.fillMaxSize(), color = CustomTheme.colors.backgroundColor) {

        Column(modifier = Modifier.fillMaxSize()) {

            TopBarListen(
                onBackClick = {
                    navController.popBackStack()
                    if (soundState) {
                        listenViewModel.playSound()
                    }
                },
                timeSeconds = timeSeconds
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier.wrapContentHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            AudioSide(
                                list = verb1,
                                selectedV1 = selectedV1,
                                matched = matchedPairs,
                                onClick = {
                                    listenViewModel.selectV1(it)
                                    listenViewModel.speak(it)
                                }
                            )
                        }
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            TextSide(
                                list = verb2Or3,
                                selectedV2V3 = selectedV2V3,
                                matched = matchedPairs,
                                onClick = {
                                    listenViewModel.selectV2V3(it)
                                    listenViewModel.speak(it)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            CheckButtonListen(
                enabled = selectedV1 != "" && selectedV2V3 != "",
                onCheckClick = {
                    listenViewModel.checkAnswer()
                    if (soundState) {
                        listenViewModel.playSound()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                    .height(56.dp)
            )

        }
        if (showBottomSheet) {
            isCurrentQuestion?.let {
                ListenBottomSheet(
                    listenItem = it,
                    definition = definition,
                    onDismiss = {
                        listenViewModel.continueToNextQuestion()
                        if (soundState) {
                            listenViewModel.playSound()
                        }
                    },
                    onReportClick = {
                        listenViewModel.sendReport()
                    }
                )
            }

        }
    }
}

@Composable
fun CheckButtonListen(modifier: Modifier, onCheckClick: () -> Unit, enabled: Boolean) {
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
            disabledContentColor = Color.Transparent,
            contentColor = Color.Transparent,
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
fun ListenBottomSheet(
    listenItem: ListenItem,
    definition: AnnotatedString,
    onDismiss: () -> Unit,
    onReportClick: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CustomTheme.colors.backgroundColor,
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
    ) {
        val correct = listenItem.isCorrect
        val color = if (correct) Green else Red
        val text = if (correct) "Correct" else "Incorrect"
        val icon = if (correct) R.drawable.ic_correct else R.drawable.ic_incorrect

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
                    append(definition)
                },
                color = color,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                style = CustomTheme.typography.mediumText
            )

            if (listenItem.translationExample != "") {
                Text(
                    text = "${stringResource(R.string.translation)}: ${listenItem.translationExample}",
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
fun TopBarListen(
    onBackClick: () -> Unit,
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
            .padding(top = 32.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
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

        Text(
            String.format(Locale.US, "%02d:%02d", min, sec),
            style = CustomTheme.typography.largeText,
            color = Black
        )
    }
}


@Composable
fun AudioSide(
    list: List<String>,
    selectedV1: String,
    matched: Set<Pair<String, String>>,
    onClick: (String) -> Unit
) {
    Column {
        Text(
            text = "V1",
            style = CustomTheme.typography.largeText,
            color = CustomTheme.colors.mainGray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            list.forEach { item ->
                val isMatched = matched.any { it.first == item }
                val isSelected = selectedV1 == item
                val scaleX = remember { Animatable(1f) }
                val scaleY = remember { Animatable(1f) }
                val coroutineScope = rememberCoroutineScope()
                SpeakerItem(
                    item = item,
                    scaleX = scaleX,
                    scaleY = scaleY,
                    coroutineScope = coroutineScope,
                    onClick = {
                        onClick(item)
                    },
                    backgroundColor = when {
                        isMatched -> Black
                        isSelected -> Blue
                        else -> CustomTheme.colors.mainGray
                    }
                )
            }

        }
    }
}


@Composable
fun TextSide(
    list: List<String>,
    selectedV2V3: String,
    matched: Set<Pair<String, String>>,
    onClick: (String) -> Unit
) {
    Column {
        Text(
            text = "V2 / V3",
            style = CustomTheme.typography.largeText,
            color = CustomTheme.colors.mainGray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            list.forEach { item ->
                val option = item
                val isMatched = matched.any { it.second == option }
                val isSelected = selectedV2V3 == option
                val scaleX = remember { Animatable(1f) }
                val scaleY = remember { Animatable(1f) }
                val coroutineScope = rememberCoroutineScope()

                TextItem(
                    item = option,
                    scaleX = scaleX,
                    scaleY = scaleY,
                    coroutineScope = coroutineScope,
                    onClick = {
                        onClick(option)
                    },
                    backgroundColor = when {
                        isMatched -> Black
                        isSelected -> Blue
                        else -> CustomTheme.colors.mainGray
                    }
                )
            }
        }
    }
}


@Composable
fun SpeakerItem(
    item: String,
    scaleX: Animatable<Float, AnimationVector1D>,
    scaleY: Animatable<Float, AnimationVector1D>,
    coroutineScope: CoroutineScope,
    onClick: () -> Unit,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .scale(scaleX.value, scaleY.value)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },

                ) {
                println("UUUUU $item")
                coroutineScope.launch {
                    scaleX.animateTo(0.96f, tween(50))
                    scaleY.animateTo(0.96f, tween(50))
                    scaleX.animateTo(1f, tween(100))
                    scaleY.animateTo(1f, tween(100))
                }
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_speaker_on),
            contentDescription = "speaker",
            tint = White
        )
    }
}


@Composable
fun TextItem(
    item: String,
    scaleX: Animatable<Float, AnimationVector1D>,
    scaleY: Animatable<Float, AnimationVector1D>,
    coroutineScope: CoroutineScope,
    onClick: () -> Unit,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .scale(scaleX.value, scaleY.value)
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                println("TTTT $item")
                coroutineScope.launch {
                    scaleX.animateTo(0.96f, tween(50))
                    scaleY.animateTo(0.96f, tween(50))
                    scaleX.animateTo(1f, tween(100))
                    scaleY.animateTo(1f, tween(100))
                }
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_speaker_on),
            contentDescription = "speaker",
            tint = White
        )
    }
}
