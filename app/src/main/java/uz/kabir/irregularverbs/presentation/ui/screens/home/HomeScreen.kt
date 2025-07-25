package uz.kabir.irregularverbs.presentation.ui.screens.home

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import uz.kabir.irregularverbs.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import uz.kabir.irregularverbs.domain.model.UserProgress
import uz.kabir.irregularverbs.presentation.navigation.Screens
import uz.kabir.irregularverbs.presentation.ui.theme.Blue
import uz.kabir.irregularverbs.presentation.ui.theme.CustomTheme
import uz.kabir.irregularverbs.presentation.ui.theme.LightGray
import uz.kabir.irregularverbs.presentation.ui.theme.Orange
import uz.kabir.irregularverbs.presentation.ui.theme.White
import uz.kabir.irregularverbs.presentation.ui.theme.Yellow


@Composable
fun HomeFragment(
    navHostController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        homeViewModel.navigationChannel.collect { event ->
            when (event) {
                is HomeNavEvent.NavigateToOption -> {
                    navHostController.navigate(Screens.Option.passGroupId(event.groupId))
                }

                is HomeNavEvent.NavigateToListen -> {
                    navHostController.navigate(Screens.Listen.passGroupId(event.groupId))
                }

                is HomeNavEvent.NavigateToWrite -> {
                    navHostController.navigate(Screens.Write.passGroupId(event.groupId))
                }
            }
        }
    }

    val context = LocalContext.current
    val testProgress by homeViewModel.testProgress.collectAsState()
    val soundState by homeViewModel.soundState.collectAsState()
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 580


    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CustomTheme.colors.backgroundColor
    ) {
        MyGridLayout(testProgress, isTablet, homeViewModel, context)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyGridLayout(
    items: List<UserProgress?>,
    isTablet: Boolean,
    homeViewModel: HomeViewModel,
    context: Context
) {

    val coroutineScope = rememberCoroutineScope()
    val gridCell = if (isTablet) 4 else 3

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }



    LazyVerticalGrid(
        columns = GridCells.Fixed(gridCell),
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)

    ) {
        itemsIndexed(items) { index, item ->
            GridItem(item, coroutineScope, onClick = {
                homeViewModel.playClickSound(context)

                if (item?.testState != -1) {
                    showBottomSheet = true
                    homeViewModel.selectItem(item)
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.locked),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }


    if (showBottomSheet && homeViewModel.selectedItem.collectAsState().value?.testState != -1) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                homeViewModel.clearSelectedItem()   //tozalash
            },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp),
            containerColor = CustomTheme.colors.backgroundColor
        ) {
            homeViewModel.selectedItem.collectAsState().value?.let { selectedItem->
                BottomSheetContent(
                    sheetState = sheetState,
                    coroutineScope = coroutineScope,
                    onClose = { showBottomSheet = false },
                    homeViewModel,
                    selectedItem,
                    context
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContent(
    sheetState: SheetState,
    coroutineScope: CoroutineScope,
    onClose: () -> Unit,
    homeViewModel: HomeViewModel,
    selectedItemBottomSheet: UserProgress,
    context:Context
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp)
            .background(color = CustomTheme.colors.backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "${stringResource(R.string.test)}  №${selectedItemBottomSheet?.groupId}",
            color = CustomTheme.colors.textBlackAndWhite,
            textAlign = TextAlign.Center,
            style = CustomTheme.typography.largeText
        )
        Text(
            stringResource(R.string.type_choice),
            color = CustomTheme.colors.textBlackAndWhite,
            textAlign = TextAlign.Center,
            style = CustomTheme.typography.smallText
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Test Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 8.dp, end = 4.dp, bottom = 16.dp, top = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CustomTheme.colors.whiteToGray)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_test),
                        contentDescription = "Test",
                        modifier = Modifier.size(62.dp)
                    )
                    Text(
                        stringResource(R.string.test),
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = CustomTheme.typography.smallText,
                        color = CustomTheme.colors.textBlackAndWhite

                    )
                    BottomSheetButton(
                        stringResource(R.string.start).uppercase(),
                        onClick = {
                            coroutineScope.launch {
                                sheetState.hide()
                                onClose()
                            }
                            homeViewModel.toOptionScreen(groupId = selectedItemBottomSheet.groupId)
                            homeViewModel.playClickSound(context)
                        },
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .height(48.dp)
                            .fillMaxWidth()

                    )
                }
            }

            // Audio Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 4.dp, end = 4.dp, bottom = 16.dp, top = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CustomTheme.colors.whiteToGray)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_audio),
                        contentDescription = "Audio",
                        modifier = Modifier.size(62.dp)
                    )
                    Text(
                        stringResource(R.string.listening),
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = CustomTheme.typography.smallText,
                        color = CustomTheme.colors.textBlackAndWhite
                    )
                    BottomSheetButton(
                        stringResource(R.string.start).uppercase(),
                        onClick = {
                            coroutineScope.launch {
                                sheetState.hide()
                                onClose()
                            }
                            homeViewModel.toListenScreen(groupId = selectedItemBottomSheet.groupId)
                            homeViewModel.playClickSound(context)
                        },
                        modifier = Modifier
                            .padding(4.dp)
                            .height(48.dp)
                            .fillMaxWidth()
                    )
                }
            }

            // Write Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 4.dp, end = 8.dp, bottom = 16.dp, top = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CustomTheme.colors.whiteToGray)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_write),
                        contentDescription = "Write",
                        modifier = Modifier.size(62.dp)
                    )
                    Text(
                        stringResource(R.string.writing),
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = CustomTheme.typography.smallText,
                        color = CustomTheme.colors.textBlackAndWhite

                    )
                    BottomSheetButton(
                        stringResource(R.string.start).uppercase(),
                        onClick = {
                            coroutineScope.launch {
                                sheetState.hide()
                                onClose()
                            }
                            homeViewModel.toWriteScreen(groupId = selectedItemBottomSheet.groupId)
                            homeViewModel.playClickSound(context)
                        },
                        modifier = Modifier
                            .padding(4.dp)
                            .height(48.dp)
                            .fillMaxWidth(),

                        )
                }
            }
        }
    }
}


@Composable
fun GridItem(item: UserProgress?, coroutineScope: CoroutineScope, onClick: () -> Unit) {
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }

    val backgroundColor = when (item?.testState) {
        1 -> Orange
        -1 -> LightGray
        else -> Blue
    }

    Box(
        modifier = Modifier
            .graphicsLayer(scaleX.value, scaleY.value)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(8.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                coroutineScope.launch {
                    scaleX.animateTo(0.96f, tween(50))
                    scaleY.animateTo(0.96f, tween(50))
                    scaleX.animateTo(1f, tween(100))
                    scaleY.animateTo(1f, tween(100))
                    onClick()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                item?.groupId.toString(),
                style = CustomTheme.typography.veryLargeText,
                color = CustomTheme.colors.textWhite,
            )
            StarRow(item)
        }
    }
}

@Composable
fun StarRow(item: UserProgress?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        StarIconGrid(if (item?.optionTestStar == true) Yellow else White, (-8).dp)
        Spacer(modifier = Modifier.width(8.dp))
        StarIconGrid(if (item?.listenTestStar == true) Yellow else White, 0.dp)
        Spacer(modifier = Modifier.width(8.dp))
        StarIconGrid(if (item?.writeTestStar == true) Yellow else White, (-8).dp)
    }
}

@Composable
fun BottomSheetButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    val coroutineScope = rememberCoroutineScope()
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }

    Box(
        modifier = modifier
            .graphicsLayer(
                scaleX = scaleX.value,
                scaleY = scaleY.value
            )
            .background(
                color = CustomTheme.colors.mainGreen,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                }
            ) {
                coroutineScope.launch {
                    scaleX.animateTo(0.95f, tween(50))
                    scaleY.animateTo(0.95f, tween(50))
                    scaleX.animateTo(1f, tween(100))
                    scaleY.animateTo(1f, tween(100))
                    onClick()
                }
            }
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = CustomTheme.colors.textWhite,
            style = CustomTheme.typography.smallText,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun StarIconGrid(color: Color, yOffset: Dp) {
    Icon(
        painter = painterResource(id = R.drawable.ic_star),
        contentDescription = "Star",
        tint = color,
        modifier = Modifier
            .size(24.dp)
            .offset(y = yOffset)
    )
}
