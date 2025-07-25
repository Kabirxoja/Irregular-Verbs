package uz.kabir.irregularverbs.presentation.ui.screens.listenresult

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import uz.kabir.irregularverbs.presentation.ui.screens.listen.ListenViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uz.kabir.irregularverbs.R
import uz.kabir.irregularverbs.presentation.navigation.Screens
import uz.kabir.irregularverbs.presentation.ui.screens.optionresult.MainButtonView
import uz.kabir.irregularverbs.presentation.ui.screens.optionresult.ResultItem
import uz.kabir.irregularverbs.presentation.ui.theme.Black
import uz.kabir.irregularverbs.presentation.ui.theme.Blue
import uz.kabir.irregularverbs.presentation.ui.theme.CustomTheme
import uz.kabir.irregularverbs.presentation.ui.theme.Green
import uz.kabir.irregularverbs.presentation.ui.theme.LightGray
import uz.kabir.irregularverbs.presentation.ui.theme.Orange
import uz.kabir.irregularverbs.presentation.ui.theme.Red
import uz.kabir.irregularverbs.presentation.ui.screens.listen.ListenItem
import uz.kabir.irregularverbs.presentation.ui.screens.listen.ListenNavEvent
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListenResultFragment(
    navController: NavHostController,
    listenViewModel: ListenViewModel
) {

    LaunchedEffect(Unit) {
        listenViewModel.navigationSharedFlow.collect { event ->
            if (event is ListenNavEvent.NavigateToHome) {
                navController.navigate(Screens.Home.route) {
                    popUpTo(Screens.Home.route) { inclusive = true }
                }
            }
        }
    }

    val result by listenViewModel.results.collectAsState()
    val timerState by listenViewModel.timerStateFlow.collectAsState()
    val correctAnswerCount = result.count { it.isCorrect }
    val context = LocalContext.current


    if (result.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }


    BackHandler {
        listenViewModel.toHomeScreen()
    }


    val userResult = (correctAnswerCount.toFloat() / result.size) * 100
    val formattedResult = String.format(Locale.US, "%.1f", userResult)

    val timeResult = String.format(Locale.US, "%02d:%02d", timerState / 60, timerState % 60)
    val averagePerTest = timerState / result.size

    Log.d("OptionResultFragment", "timerState: $timerState")
    Log.d("OptionResultFragment", "verbs.size: ${result.size}")
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


    val resultList: List<ListenItem> = result.mapIndexed { index, verb ->
        ListenItem(
            verb1 = verb.verb1,
            verb2 = verb.verb2,
            verb3 = verb.verb3,
            isCorrect = verb.isCorrect,
            selected = verb.selected,
            example = verb.example,
            translationExample = verb.translationExample,
            isVerb2Hidden = verb.isVerb2Hidden
        )
    }

    Log.d("OptionResultFragment", "list: $resultList")


    BackHandler {
        listenViewModel.toHomeScreen()
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

                ResultScreenListen(
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
                        listenViewModel.playClickSound(context)
                    },
                    text = stringResource(R.string.view_result),
                    buttonColor = LightGray
                )

                MainButtonView(
                    onClick = {
                        listenViewModel.toHomeScreen()
                        listenViewModel.playClickSound(context)
                    },
                    text = stringResource(R.string.return_home),
                    buttonColor = Green
                )

                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheet = false },
                        sheetState = bottomSheetState,
                        containerColor = CustomTheme.colors.backgroundColor,
                        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
                    ) {
                        ListenResultBottomSheet(results = resultList)
                    }
                }
            }
        }
    }
}


@Composable
fun ResultScreenListen(
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
fun ListenResultBottomSheet(results: List<ListenItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CustomTheme.colors.backgroundColor)
        ) {
            TableListen("№", weight = 2f, Black)
            TableListen("Base form", weight = 4f, Black)
            TableListen("Past simple", weight = 5f, Black)
            TableListen("Past participle", weight = 5f, Black)
            TableListen("Your response", weight = 5f, Black)
        }

        Divider(color = LightGray, thickness = 2.dp)

        results.forEachIndexed { index, item ->
            Row(modifier = Modifier.fillMaxWidth()) {
                TableListen((index + 1).toString(), weight = 2f, Black)
                TableListen(item.verb1, weight = 4f, Blue)
                TableListen(item.verb2, weight = 5f, if (item.isVerb2Hidden) Orange else Blue)
                TableListen(item.verb3, weight = 5f, if (item.isVerb2Hidden) Blue else Orange)
                TableListen(item.selected.second, weight = 5f, if (item.isCorrect) Green else Red)
            }
            Divider(color = Color.LightGray, thickness = 1.dp)
        }
    }
}

@Composable
fun RowScope.TableListen(text: String, weight: Float, color: Color) {
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