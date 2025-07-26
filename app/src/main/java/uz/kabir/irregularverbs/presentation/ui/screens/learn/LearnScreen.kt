package uz.kabir.irregularverbs.presentation.ui.screens.learn

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import uz.kabir.irregularverbs.R
import uz.kabir.irregularverbs.presentation.ui.theme.CustomTheme
import uz.kabir.irregularverbs.domain.model.IrregularVerbTranslated
import uz.kabir.irregularverbs.presentation.ui.state.AppLanguage
import uz.kabir.irregularverbs.presentation.ui.utils.SoundManager
import uz.kabir.irregularverbs.presentation.ui.utils.toHighlightedColorText


@Composable
fun LearnFragment(
    navHostController: NavHostController,
    learnViewModel: LearnViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }
    val soundState by learnViewModel.soundState.collectAsState()
    val verbs by learnViewModel.verbs.collectAsState()
    val getLanguage by learnViewModel.language.collectAsState()

    LaunchedEffect(Unit) {
        learnViewModel.getLanguage()
        learnViewModel.getVerbsByLevel("beginner")
        learnViewModel.initTTS()
        learnViewModel.playClick.collect {
            soundManager.playClickSound()
        }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CustomTheme.colors.backgroundColor
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LevelTopBar(onLevelSelected = { level ->
                learnViewModel.getVerbsByLevel(level)
                if (soundState){
                    learnViewModel.playSound()
                }
            })

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(verbs.size) { index ->
                    MyListItem(item = verbs[index], getLanguage = getLanguage, learnViewModel = learnViewModel)
                }
            }
        }
    }
}

@Composable
fun MyListItem(
    item: IrregularVerbTranslated,
    getLanguage: AppLanguage,
    learnViewModel: LearnViewModel
) {
    Log.d("GGUUU", "MyListItem: $item")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CustomTheme.colors.mainBlue)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedBox(
                text = item.baseForm,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(end = 4.dp),
                onClick = {
                    Log.d("shumni", "MyListItem: ${item.baseForm}")
                    learnViewModel.speak(item.baseForm)
                }
            )

            AnimatedBox(
                text = item.pastSimple,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 4.dp), onClick = {
                    Log.d("shumni", "MyListItem: ${item.pastSimple}")
                    learnViewModel.speak(item.pastSimple)
                }
            )

            AnimatedBox(
                text = item.pastParticiple,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 4.dp),
                onClick = {
                    Log.d("shumni", "MyListItem: ${item.pastParticiple}")
                    learnViewModel.speak(item.pastParticiple)
                }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))


        if (getLanguage == AppLanguage.UZBEK || getLanguage == AppLanguage.RUSSIAN) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CustomTheme.colors.whiteToBlack)
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.translation,
                    textAlign = TextAlign.Center,
                    style = CustomTheme.typography.mediumText,
                    color = CustomTheme.colors.mainGray
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            HighlightedText(item.verb1)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            HighlightedText(item.verb2)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            HighlightedText(item.verb3)
        }
    }
}

@Composable
fun AnimatedBox(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }

    Box(modifier = modifier.graphicsLayer(scaleX.value, scaleY.value)) { // 👈 modifier shu yerga keldi
        // Main animated box
        Box(
            modifier = Modifier
                .fillMaxSize() // 👈 to'liq joyni egallashi uchun
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CustomTheme.colors.whiteToBlack)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
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
            Text(
                text = text,
                style = CustomTheme.typography.smallText,
                color = CustomTheme.colors.textBlackAndWhite,
                textAlign = TextAlign.Center,
            )
        }

        // Speaker icon
        Box(
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.TopEnd)
                .offset(x = 4.dp, y = (-4).dp)
                .background(CustomTheme.colors.mainOrange, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_speaker_on),
                contentDescription = null,
                tint = CustomTheme.colors.whiteToGray,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}




@Composable
fun LevelTopBar(onLevelSelected: (String) -> Unit) {
    var selectedLevel by remember { mutableStateOf("beginner") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CustomTheme.colors.mainBlue)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        LevelContainer(
            text = stringResource(R.string.beginner),
            isSelected = selectedLevel == "beginner",
            onClick = {
                selectedLevel = "beginner"
                onLevelSelected("beginner")
            })

        LevelContainer(
            text = stringResource(R.string.intermediate),
            isSelected = selectedLevel == "intermediate",
            onClick = {
                selectedLevel = "intermediate"
                onLevelSelected("intermediate")
            })

        LevelContainer(
            text = stringResource(R.string.advanced),
            isSelected = selectedLevel == "advanced",
            onClick = {
                selectedLevel = "advanced"
                onLevelSelected("advanced")
            })
    }
}

@Composable
fun LevelContainer(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor =
        if (isSelected) CustomTheme.colors.backgroundColor else CustomTheme.colors.mainBlue
    val textColor = if (isSelected) CustomTheme.colors.mainBlue else CustomTheme.colors.textWhite

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
    ) {
        Text(
            text = text,
            color = textColor,
            style = CustomTheme.typography.smallText,
            modifier = Modifier
        )
    }
}


@Composable
fun HighlightedText(text: String) {
    Text(
        text = buildAnnotatedString {
            append(text.toHighlightedColorText(CustomTheme.colors.mainYellow))
        },
        style = CustomTheme.typography.smallText,
        color = CustomTheme.colors.textWhite,
        modifier = Modifier.fillMaxWidth()
    )
}
