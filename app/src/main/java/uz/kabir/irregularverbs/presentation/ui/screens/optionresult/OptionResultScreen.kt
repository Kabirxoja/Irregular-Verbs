package uz.kabir.irregularverbs.presentation.ui.screens.optionresult

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import uz.kabir.irregularverbs.R
import uz.kabir.irregularverbs.presentation.ui.theme.Blue
import uz.kabir.irregularverbs.presentation.ui.theme.CustomTheme
import uz.kabir.irregularverbs.presentation.ui.theme.Green
import uz.kabir.irregularverbs.presentation.ui.theme.LightGray
import uz.kabir.irregularverbs.presentation.ui.theme.White
import uz.kabir.irregularverbs.presentation.ui.screens.option.OptionViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import uz.kabir.irregularverbs.presentation.navigation.Screens
import uz.kabir.irregularverbs.presentation.ui.screens.option.OptionNavEvent
import uz.kabir.irregularverbs.presentation.ui.theme.Black
import uz.kabir.irregularverbs.presentation.ui.theme.Orange
import uz.kabir.irregularverbs.presentation.ui.theme.Red
import java.util.Locale
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionResultFragment(
    optionViewModel: OptionViewModel,
    navController: NavHostController
) {

    LaunchedEffect(Unit) {
        optionViewModel.navigationSharedFlow.collect { event ->
            if (event is OptionNavEvent.NavigateToHome) {
                navController.navigate(Screens.Home.route) {
                    popUpTo(Screens.Home.route) { inclusive = true }
                }
            }
        }

    }
    val verbs by optionViewModel.verbs.collectAsState()
    val questions by optionViewModel.questions.collectAsState()
    val correctCount by optionViewModel.correctCount.collectAsState()
    val timerState by optionViewModel.timerStateFlow.collectAsState()
    val context = LocalContext.current

    if (verbs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val userResult = (correctCount.toFloat() / verbs.size) * 100
    val formattedResult = String.format(Locale.US, "%.1f", userResult)

    val timeResult = String.format(Locale.US, "%02d:%02d", timerState / 60, timerState % 60)
    val averagePerTest = timerState / verbs.size

    Log.d("OptionResultFragment", "timerState: $timerState")
    Log.d("OptionResultFragment", "verbs.size: ${verbs.size}")
    Log.d("OptionResultFragment", "verbs.size: $averagePerTest")

    val randomPraise = Random.nextInt(1, 4)
    val randomScore = Random.nextInt(1, 3)
    val randomTime = Random.nextInt(1, 3)

    val praiseText = when {
        userResult >= 0.0f && userResult < 50.0f -> {
            when (randomPraise) {
                1 -> stringResource(R.string.result_praise_low_1)
                2 -> stringResource(R.string.result_praise_low_2)
                3 -> stringResource(R.string.result_praise_low_3)
                else -> ""
            }
        }

        userResult >= 50.0f && userResult <= 75.0f -> {
            when (randomPraise) {
                1 -> stringResource(R.string.result_praise_medium_1)
                2 -> stringResource(R.string.result_praise_medium_2)
                3 -> stringResource(R.string.result_praise_medium_3)
                else -> ""
            }
        }

        else -> {
            when (randomPraise) {
                1 -> stringResource(R.string.result_praise_high_1)
                2 -> stringResource(R.string.result_praise_high_2)
                3 -> stringResource(R.string.result_praise_high_3)
                else -> ""
            }
        }
    }

    val scoreText = when {
        userResult >= 0.0f && userResult < 50.0f -> {
            when (randomScore) {
                1 -> stringResource(R.string.result_score_low_1)
                2 -> stringResource(R.string.result_score_low_2)
                else -> ""
            }
        }

        userResult >= 50.0f && userResult <= 75.0f -> {
            when (randomScore) {
                1 -> stringResource(R.string.result_score_medium_1)
                2 -> stringResource(R.string.result_score_medium_2)
                else -> ""
            }
        }

        else -> {
            when (randomScore) {
                1 -> stringResource(R.string.result_score_high_1)
                2 -> stringResource(R.string.result_score_high_2)
                else -> ""
            }
        }

    }

    val timeText = when {
        averagePerTest < 5 -> {
            when (randomTime) {
                1 -> stringResource(R.string.result_time_fast_1)
                2 -> stringResource(R.string.result_time_fast_2)
                else -> ""
            }
        }

        averagePerTest <= 10 -> {
            when (randomTime) {
                1 -> stringResource(R.string.result_time_average_1)
                2 -> stringResource(R.string.result_time_average_2)
                else -> ""
            }
        }

        else -> {
            when (randomTime) {
                1 -> stringResource(R.string.result_time_slow_1)
                2 -> stringResource(R.string.result_time_slow_2)
                else -> ""
            }
        }
    }


    val resultList: List<ResultItem> = verbs.mapIndexed { index, verb ->
        val question = questions.getOrNull(index)

        ResultItem(
            verb1 = verb.baseForm,
            verb2 = verb.pastSimple,
            verb3 = verb.pastParticiple,
            isCorrect = question?.correctAnswer == question?.selectedAnswer,
            userAnswer = question?.selectedAnswer ?: "",
            correctAnswer = question?.correctAnswer ?: "",
            isVerb2Hidden = question?.isVerb2Hidden ?: false
        )
    }

    Log.d("OptionResultFragment", "list: $resultList")


    BackHandler {
        optionViewModel.toHomeScreen()
    }
    Surface(modifier = Modifier.fillMaxSize(), color = CustomTheme.colors.backgroundColor) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 126.dp),
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = praiseText,
                        style = CustomTheme.typography.veryLargeText,
                        color = Green,
                        modifier = Modifier
                            .weight(2f)
                            .padding(end = 16.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_finish_1),
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(83f / 160f)
                    )
                }

                Text(
                    text = stringResource(R.string.your_result),
                    color = LightGray,
                    style = CustomTheme.typography.largeText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )

                ResultScreen(
                    userResult = formattedResult,
                    timeResult = timeResult,
                    timePraise = timeText,
                    scorePraise = scoreText
                )
            }

            var showBottomSheet by remember { mutableStateOf(false) }
            val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

            Column(
                modifier = Modifier.align(Alignment.BottomCenter),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MainButtonView(
                    onClick = {
                        showBottomSheet = true
                        optionViewModel.playClickSound(context)
                    },
                    text = "View my results",
                    buttonColor = LightGray
                )

                MainButtonView(
                    onClick = {
                        optionViewModel.toHomeScreen()
                        optionViewModel.playClickSound(context)

                    },
                    text = "Return Home Screen",
                    buttonColor = Green
                )

                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheet = false },
                        sheetState = bottomSheetState,
                        containerColor = CustomTheme.colors.backgroundColor,
                        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
                    ) {
                        OptionResultBottomSheet(results = resultList)
                    }
                }
            }
        }
    }
}

data class ResultItem(
    val verb1: String,
    val verb2: String,
    val verb3: String,
    val userAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean,
    val isVerb2Hidden: Boolean
)

@Composable
fun OptionResultBottomSheet(results: List<ResultItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CustomTheme.colors.backgroundColor)
        ) {
            TableCell("№", weight = 2f, Black)
            TableCell("Base form", weight = 4f, Black)
            TableCell("Past simple", weight = 5f, Black)
            TableCell("Past participle", weight = 5f, Black)
            TableCell("Your response", weight = 5f, Black)
        }

        Divider(color = LightGray, thickness = 2.dp)

        results.forEachIndexed { index, item ->
            Row(modifier = Modifier.fillMaxWidth()) {
                TableCell((index + 1).toString(), weight = 2f, Black)
                TableCell(item.verb1, weight = 4f, Blue)
                TableCell(item.verb2, weight = 5f, if (item.isVerb2Hidden) Orange else Blue)
                TableCell(item.verb3, weight = 5f, if (item.isVerb2Hidden) Blue else Orange)
                TableCell(item.userAnswer, weight = 5f, if (item.isCorrect) Green else Red)
            }
            Divider(color = Color.LightGray, thickness = 1.dp)
        }
    }
}

@Composable
fun RowScope.TableCell(text: String, weight: Float, color: Color) {
    Text(
        text = text,
        modifier = Modifier
            .weight(weight)
            .padding(8.dp),
        textAlign = TextAlign.Center,
        style = CustomTheme.typography.smallText,
        color = color
    )
}

@Composable
fun ResultScreen(
    userResult: String,
    timeResult: String,
    timePraise: String,
    scorePraise: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            ResultItem(
                painter = painterResource(id = R.drawable.ic_target),
                textResult = "$userResult%",
                textPraise = scorePraise.uppercase(),
                color = Blue
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            ResultItem(
                painter = painterResource(id = R.drawable.ic_time),
                textResult = timeResult,
                textPraise = timePraise.uppercase(),
                color = Orange
            )
        }
    }
}

@Composable
fun ResultItem(painter: Painter, textResult: String, textPraise: String, color: Color) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color = color)
            .padding(top = 12.dp, bottom = 6.dp, start = 6.dp, end = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painter,
                contentDescription = "",
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = textResult,
                style = CustomTheme.typography.veryLargeText,
                color = White,
                modifier = Modifier,
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = textPraise,
            style = CustomTheme.typography.mediumText,
            textAlign = TextAlign.Center,
            color = color,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(color = White)
                .padding(6.dp)
        )
    }

}


@Composable
fun MainButtonView(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Davom Etish",
    buttonColor: Color = Green
) {
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }

    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {
            coroutineScope.launch {
                scaleX.animateTo(0.98f, tween(50))
                scaleY.animateTo(0.98f, tween(50))

                scaleX.animateTo(1f, tween(100))
                scaleY.animateTo(1f, tween(100))

                onClick()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scaleX.value, scaleY.value)
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),

        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = text.uppercase(),
            style = CustomTheme.typography.mediumText,
            color = CustomTheme.colors.textWhite
        )
    }
}


