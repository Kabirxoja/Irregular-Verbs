package uz.kabir.irregularverbs.presentation.ui.screens.setting

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import uz.kabir.irregularverbs.R
import kotlinx.coroutines.launch
import uz.kabir.irregularverbs.presentation.ui.state.AppLanguage
import uz.kabir.irregularverbs.presentation.ui.state.LanguageState
import uz.kabir.irregularverbs.presentation.ui.state.TextMode
import uz.kabir.irregularverbs.presentation.ui.state.TextState
import uz.kabir.irregularverbs.presentation.ui.state.ThemeMode
import uz.kabir.irregularverbs.presentation.ui.theme.Cream
import uz.kabir.irregularverbs.presentation.ui.theme.CustomTheme
import uz.kabir.irregularverbs.domain.model.Profile
import androidx.core.net.toUri
import uz.kabir.irregularverbs.presentation.ui.theme.Green


@Composable
fun SettingsFragment(
    navHostController: NavHostController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val userProgress by settingsViewModel.overallProgressPercentage.collectAsState()
    val userProfile by settingsViewModel.profile.collectAsState()
    val soundState by settingsViewModel.soundState.collectAsState()

    val context = LocalContext.current


    Log.d("userProfile", "userGender: ${userProfile?.userGender}")
    Log.d("userProfile", "userName: ${userProfile?.userName}")


    var showBottomSheetProfile by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Surface(modifier = Modifier.fillMaxSize(), color = CustomTheme.colors.backgroundColor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileHeader(
                profileGender = userProfile?.userGender ?: "",
                profileName = userProfile?.userName ?: "",
                onProfileSettingsClick = {
                    showBottomSheetProfile = true
                    settingsViewModel.playClickSound(context)

                },
                onSoundClick = { isEnabled ->
                    settingsViewModel.toggleSound(isEnabled)
                    settingsViewModel.playClickSound(context)
                },
                soundState = soundState,
            )

            Spacer(modifier = Modifier.height(16.dp))

            ResultsCard(progress = userProgress)

            Spacer(modifier = Modifier.height(18.dp))

            SettingsList(settingsViewModel)

            if (showBottomSheetProfile) {
                BottomSheetProfile(
                    onDismiss = { showBottomSheetProfile = false }, settingsViewModel, context
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetProfile(onDismiss: () -> Unit, settingsViewModel: SettingsViewModel, context: Context) {
    var selectedAvatar: String? by remember { mutableStateOf(null) }
    var inputQuery by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp),
        containerColor = CustomTheme.colors.backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.change_profile),
                style = CustomTheme.typography.largeText,
                color = CustomTheme.colors.textBlackAndWhite
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileItem(
                    avatarResId = R.drawable.ic_man,
                    isSelected = selectedAvatar == "male",
                    onSelectionChanged = { selected ->
                        selectedAvatar = if (selected) "male" else null
                        //Click sound
                        settingsViewModel.playClickSound(context)
                    }
                )
                ProfileItem(
                    avatarResId = R.drawable.ic_woman,
                    isSelected = selectedAvatar == "female",
                    onSelectionChanged = { selected ->
                        selectedAvatar = if (selected) "female" else null
                        //Click sound
                        settingsViewModel.playClickSound(context)
                    }
                )
            }

            TextInputField(
                value = inputQuery,
                onValueChange = { inputQuery = it },
                hint = stringResource(R.string.edit_enter_name)
            )

            MainButton(
                onClick = {
                    settingsViewModel.setProfileInfo(
                        Profile(
                            userGender = selectedAvatar ?: "",
                            userName = inputQuery
                        )
                    )
                    onDismiss()
                    //Click sound
                    settingsViewModel.playClickSound(context)
                },
                text = stringResource(R.string.save).uppercase(),
                buttonColor = Green
            )
        }
    }
}

@Composable
fun MainButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "",
    buttonColor: Color = CustomTheme.colors.mainGreen
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
            .scale(scaleX.value, scaleY.value),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = text,
            style = CustomTheme.typography.mediumText,
            color = CustomTheme.colors.textWhite
        )
    }
}

@Composable
fun SettingItem(icon: Int, text: String, onClick: () -> Unit) {

    val coroutineScope = rememberCoroutineScope()
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX.value, scaleY.value)
            .padding(horizontal = 16.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = CustomTheme.colors.mainGreen)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                coroutineScope.launch {
                    scaleX.animateTo(0.98f, tween(50))
                    scaleY.animateTo(0.98f, tween(50))
                    scaleX.animateTo(1f, tween(100))
                    scaleY.animateTo(1f, tween(100))
                }
                onClick()

            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "$text icon",
            modifier = Modifier
                .size(48.dp)
                .padding(6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color = White)
        )
        Text(
            text = text,
            color = CustomTheme.colors.textWhite,
            style = CustomTheme.typography.mediumText
        )
    }
}

@Composable
fun TextInputField(
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
            .border(1.dp, LightGray, RoundedCornerShape(12.dp)),
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
                        color = LightGray.copy(alpha = 0.4f),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .wrapContentHeight(Alignment.CenterVertically),
                    )
                }
                R.raw.click_sound
                innerTextField()
            }
        }
    )
}

@Composable
fun ProfileItem(
    avatarResId: Int,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }

    Box(
        modifier = modifier
            .size(100.dp)
            .graphicsLayer(scaleX.value, scaleY.value)
            .background(CustomTheme.colors.whiteToBlack, CircleShape)
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
                onSelectionChanged(!isSelected)
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = avatarResId),
            contentDescription = null,
            modifier = Modifier
                .size(82.dp)
                .clip(CircleShape)
        )

        if (isSelected) {
            Icon(
                painter = painterResource(id = R.drawable.ic_checked),
                contentDescription = "Selected",
                tint = Green,
                modifier = Modifier
                    .size(38.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 10.dp, y = 8.dp)
                    .background(CustomTheme.colors.whiteToBlack, CircleShape)
                    .border(4.dp, CustomTheme.colors.whiteToGray, CircleShape)
                    .padding(8.dp)
            )
        }
    }
}


@Composable
fun ProfileHeader(
    profileGender: String,
    profileName: String,
    onProfileSettingsClick: () -> Unit,
    onSoundClick: (Boolean) -> Unit,
    soundState: Boolean,
    modifier: Modifier = Modifier,
) {

    val profileImage: Painter = if (profileGender == "male") {
        painterResource(id = R.drawable.ic_man)
    } else {
        painterResource(id = R.drawable.ic_woman)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = CustomTheme.colors.backgroundColor)
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                drawLine(
                    color = LightGray,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
            },
        verticalAlignment = Alignment.Bottom
    ) {
        Image(
            painter = profileImage,
            contentDescription = "User profile picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(start = 16.dp, bottom = 1.dp, top = 12.dp)
                .size(136.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 16.dp)
                    .align(Alignment.CenterEnd)
            ) {


                ImageProfileButton(
                    iconResId = R.drawable.ic_profile,
                    onClick = onProfileSettingsClick,
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                SoundButton(
                    onSoundClick = onSoundClick,
                    soundState = soundState,
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))


            }


            Text(
                text = profileName,
                color = CustomTheme.colors.textBlackAndWhite,
                style = CustomTheme.typography.largeText,
                modifier = Modifier
                    .padding(start = 12.dp, bottom = 6.dp)
                    .align(Alignment.BottomStart)
            )
        }
    }
}

@Composable
fun ImageProfileButton(
    iconResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }

    Image(
        painter = painterResource(id = iconResId),
        contentDescription = "Profile settings button",
        modifier = modifier
            .graphicsLayer(scaleX.value, scaleY.value)
            .padding(2.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                coroutineScope.launch {
                    scaleX.animateTo(0.96f, tween(50))
                    scaleY.animateTo(0.96f, tween(50))
                    scaleX.animateTo(1f, tween(100))
                    scaleY.animateTo(1f, tween(100))
                }
                onClick()
            }
    )
}

@Composable
fun SoundButton(
    onSoundClick: (Boolean) -> Unit,
    soundState: Boolean = false,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }

    val iconResId = if (soundState) {
        R.drawable.ic_speaker_on
    } else {
        R.drawable.ic_speaker_off
    }


    Image(
        painter = painterResource(id = iconResId),
        contentDescription = "Profile settings button",
        modifier = modifier
            .graphicsLayer(scaleX.value, scaleY.value)
            .padding(4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                coroutineScope.launch {
                    scaleX.animateTo(0.96f, tween(50))
                    scaleY.animateTo(0.96f, tween(50))
                    scaleX.animateTo(1f, tween(100))
                    scaleY.animateTo(1f, tween(100))
                }
                onSoundClick(!soundState)
            }
    )
}

@Composable
fun ResultsCard(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(112.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = CustomTheme.colors.mainBlue),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        var painter: Painter? = null
        var userLevel: String? = ""
        if (progress >= 0.0f && progress < 0.4f) {
            painter = painterResource(R.drawable.ic_reward_bronze)
            userLevel = stringResource(R.string.beginner)
        } else if (progress >= 0.4f && progress < 0.7f) {
            painter = painterResource(R.drawable.ic_reward_silver)
            userLevel = stringResource(R.string.intermediate)
        } else if (progress >= 0.7f && progress <= 1.0f) {
            painter = painterResource(R.drawable.ic_reward_gold)
            userLevel = stringResource(R.string.advanced)
        }


        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.your_result),
                color = CustomTheme.colors.textWhite,
                style = CustomTheme.typography.largeText,
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomLinearProgressBar(
                progress = progress,
                height = 16.dp,
                cornerRadius = 8.dp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.your_level) + userLevel,
                color = CustomTheme.colors.textWhite,
                style = CustomTheme.typography.smallText,
            )
        }

        painter?.let {
            Image(
                painter = it,
                contentDescription = "Reward icon",
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 6.dp, bottom = 6.dp)
            )
        }
    }
}

@Composable
fun SettingsList(settingsViewModel: SettingsViewModel) {
    var showBottomSheetLanguage by remember { mutableStateOf(false) }
    var showBottomSheetTheme by remember { mutableStateOf(false) }
    var showBottomSheetTextSize by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val appPackageName = "uz.kabir.irregularverbs"
    val activity = context as Activity

    Column(modifier = Modifier.fillMaxHeight()) {
        Text(
            text = stringResource(R.string.settings),
            color = CustomTheme.colors.mainGray,
            style = CustomTheme.typography.largeText,
            modifier = Modifier.padding(bottom = 4.dp, start = 16.dp)
        )

        SettingItem(R.drawable.ic_language, stringResource(R.string.translation).uppercase()) {
            //Translation
            showBottomSheetLanguage = true
            //Click sound
            settingsViewModel.playClickSound(context)
        }
        Spacer(modifier = Modifier.height(16.dp))
        SettingItem(R.drawable.ic_theme, stringResource(R.string.setting_theme).uppercase()) {
            //Theme
            showBottomSheetTheme = true
            //Click sound
            settingsViewModel.playClickSound(context)

        }
        Spacer(modifier = Modifier.height(16.dp))
        SettingItem(
            R.drawable.ic_text_size,
            stringResource(R.string.setting_text_size).uppercase()
        ) {
            //TExt size
            showBottomSheetTextSize = true
            //Click sound
            settingsViewModel.playClickSound(context)
        }
        Spacer(modifier = Modifier.height(16.dp))
        SettingItem(R.drawable.ic_share, stringResource(R.string.setting_share).uppercase()) {
            //Share
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "https://play.google.com/store/apps/details?id=$appPackageName"
                )
            }
            val chooser = Intent.createChooser(shareIntent, "Share via")
            context.startActivity(chooser)

            //Click sound
            settingsViewModel.playClickSound(context)
        }
        Spacer(modifier = Modifier.height(16.dp))
        SettingItem(R.drawable.ic_rate, stringResource(R.string.setting_rate).uppercase()) {
            //Rate
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        "market://details?id=$appPackageName".toUri()
                    )
                )
            } catch (e: ActivityNotFoundException) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        "https://play.google.com/store/apps/details?id=$appPackageName".toUri()
                    )
                )
            }
            //Click sound
            settingsViewModel.playClickSound(context)
        }
        Spacer(modifier = Modifier.height(16.dp))
        SettingItem(R.drawable.ic_exit, stringResource(R.string.setting_exit).uppercase()) {
            //Exit
            activity.finish()
            //Click sound
            settingsViewModel.playClickSound(context)
        }
    }

    //Bottom sheet Language
    if (showBottomSheetLanguage) {
        val currentLanguage by settingsViewModel.languageCode.collectAsState()
        val languageOptions = listOf(
            FlagOption(
                text = stringResource(R.string.uzbek),
                flagResId = R.drawable.ic_uzbek,
                isSelected = currentLanguage == AppLanguage.UZBEK,
                onClick = {
                    settingsViewModel.saveLanguage(AppLanguage.UZBEK)
                    LanguageState.currentLanguage = AppLanguage.UZBEK
                    showBottomSheetLanguage = false
                    //Click sound
                    settingsViewModel.playClickSound(context)
                }
            ),
            FlagOption(
                text = stringResource(R.string.russian),
                flagResId = R.drawable.ic_russian,
                isSelected = currentLanguage == AppLanguage.RUSSIAN,
                onClick = {
                    settingsViewModel.saveLanguage(AppLanguage.RUSSIAN)
                    LanguageState.currentLanguage = AppLanguage.RUSSIAN
                    showBottomSheetLanguage = false
                    //Click sound
                    settingsViewModel.playClickSound(context)
                }
            ),

            /* if I add this language, I can will unlock */
//            FlagOption(
//                text = stringResource(R.string.karakalpak),
//                flagResId = R.drawable.ic_karakalpak,
//                isSelected = currentLanguage == AppLanguage.ENGLISH,
//                onClick = {
//                    settingsViewModel.saveLanguage(AppLanguage.ENGLISH)
//                    LanguageState.currentLanguage = AppLanguage.ENGLISH
//                    showBottomSheetLanguage = false
//                }
//            )


        )
        BaseSettingsBottomSheet(
            onDismiss = { showBottomSheetLanguage = false },
            title = stringResource(R.string.setting_language),
            options = languageOptions
        )
    }
    //Bottom sheet Theme
    if (showBottomSheetTheme) {
        val currentTheme by settingsViewModel.themeMode.collectAsState()
        val themeOptions = listOf(
            IconOption(
                text = stringResource(R.string.light),
                iconResId = R.drawable.ic_light_mode,
                isSelected = currentTheme == ThemeMode.LIGHT,
                onClick = {
                    settingsViewModel.setThemeMode(ThemeMode.LIGHT)
                    showBottomSheetTheme = false
                    //Click sound
                    settingsViewModel.playClickSound(context)
                }
            ),
            IconOption(
                text = stringResource(R.string.dark),
                iconResId = R.drawable.ic_dark_mode,
                isSelected = currentTheme == ThemeMode.DARK,
                onClick = {
                    settingsViewModel.setThemeMode(ThemeMode.DARK)
                    showBottomSheetTheme = false
                    //Click sound
                    settingsViewModel.playClickSound(context)
                }
            ),
            IconOption(
                text = stringResource(R.string.system),
                iconResId = R.drawable.ic_auto_mode,
                isSelected = currentTheme == ThemeMode.SYSTEM,
                onClick = {
                    settingsViewModel.setThemeMode(ThemeMode.SYSTEM)
                    showBottomSheetTheme = false
                    //Click sound
                    settingsViewModel.playClickSound(context)
                }
            )
        )
        BaseSettingsBottomSheet(
            onDismiss = { showBottomSheetTheme = false },
            title = stringResource(R.string.change_theme),
            options = themeOptions
        )
    }

    //Bottom sheet Text size
    if (showBottomSheetTextSize) {
        val currentTextSize by settingsViewModel.textSizeMode.collectAsState()
        val textSizeOptions = listOf(
            IconOption(
                text = stringResource(R.string.small),
                iconResId = R.drawable.ic_text_size,
                isSelected = currentTextSize == TextMode.SMALL,
                onClick = {
                    settingsViewModel.setTextSizeMode(TextMode.SMALL)
                    TextState.currentTextSize = TextMode.SMALL
                    showBottomSheetTextSize = false
                    //Click sound
                    settingsViewModel.playClickSound(context)
                }
            ),
            IconOption(
                text = stringResource(R.string.medium),
                iconResId = R.drawable.ic_text_size,
                isSelected = currentTextSize == TextMode.MEDIUM,
                onClick = {
                    settingsViewModel.setTextSizeMode(TextMode.MEDIUM)
                    TextState.currentTextSize = TextMode.MEDIUM
                    showBottomSheetTextSize = false
                    //Click sound
                    settingsViewModel.playClickSound(context)
                }
            ),
            IconOption(
                text = stringResource(R.string.big),
                iconResId = R.drawable.ic_text_size,
                isSelected = currentTextSize == TextMode.BIG,
                onClick = {
                    settingsViewModel.setTextSizeMode(TextMode.BIG)
                    TextState.currentTextSize = TextMode.BIG
                    showBottomSheetTextSize = false
                    //Click sound
                    settingsViewModel.playClickSound(context)
                }
            )
        )
        BaseSettingsBottomSheet(
            onDismiss = { showBottomSheetTextSize = false },
            title = stringResource(R.string.change_text_size),
            options = textSizeOptions
        )
    }
}


@Composable
fun ThemeOption(
    text: String,
    iconPainter: Painter,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer(scaleX.value, scaleY.value)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = CustomTheme.colors.backgroundColor)
            .padding(horizontal = 12.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                coroutineScope.launch {
                    scaleX.animateTo(0.98f, tween(50))
                    scaleY.animateTo(0.98f, tween(50))
                    scaleX.animateTo(1f, tween(100))
                    scaleY.animateTo(1f, tween(100))
                    onClick()
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = iconPainter,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            color = CustomTheme.colors.textBlackAndWhite,
            style = CustomTheme.typography.mediumText,
            modifier = Modifier.weight(1f)
        )

        if (isSelected) {
            Icon(
                painter = painterResource(id = R.drawable.ic_checked),
                contentDescription = "selected",
                tint = CustomTheme.colors.mainGreen,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun LanguageOption(
    text: String,
    flagPainter: Painter,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer(scaleX.value, scaleY.value)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = CustomTheme.colors.backgroundColor)
            .padding(horizontal = 12.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                coroutineScope.launch {
                    scaleX.animateTo(0.98f, tween(50))
                    scaleY.animateTo(0.98f, tween(50))
                    scaleX.animateTo(1f, tween(100))
                    scaleY.animateTo(1f, tween(100))
                }
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = flagPainter,
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            style = CustomTheme.typography.mediumText,
            color = CustomTheme.colors.textBlackAndWhite,
            modifier = Modifier.weight(1f)
        )

        if (isSelected) {
            Icon(
                painter = painterResource(id = R.drawable.ic_checked),
                contentDescription = "selected",
                tint = CustomTheme.colors.mainGreen,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun CustomLinearProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    backgroundColor: Color = White,
    progressColor: Color = Yellow,
    height: Dp = 12.dp,
    cornerRadius: Dp = 12.dp
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


// Generic Composable for all settings bottom sheets
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseSettingsBottomSheet(
    onDismiss: () -> Unit,
    title: String,
    options: List<SettingsOption>,
    modifier: Modifier = Modifier,
    containerColor: Color = CustomTheme.colors.mainBlue
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = containerColor,
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 0.dp, bottom = 8.dp)
                .background(color = containerColor),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = CustomTheme.colors.textWhite,
                style = CustomTheme.typography.largeText,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            options.forEachIndexed { index, option ->
                when (option) {
                    is IconOption -> ThemeOption(
                        text = option.text,
                        iconPainter = painterResource(id = option.iconResId),
                        isSelected = option.isSelected,
                        onClick = option.onClick
                    )

                    is FlagOption -> LanguageOption(
                        text = option.text,
                        flagPainter = painterResource(id = option.flagResId),
                        isSelected = option.isSelected,
                        onClick = option.onClick
                    )
                }
                if (index < options.lastIndex) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


sealed interface SettingsOption {
    val text: String
    val isSelected: Boolean
    val onClick: () -> Unit
}

data class IconOption(
    override val text: String,
    val iconResId: Int,
    override val isSelected: Boolean,
    override val onClick: () -> Unit
) : SettingsOption

data class FlagOption(
    override val text: String,
    val flagResId: Int,
    override val isSelected: Boolean,
    override val onClick: () -> Unit
) : SettingsOption