package uz.kabir.irregularverbs.presentation.ui.screens.search

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import uz.kabir.irregularverbs.R
import uz.kabir.irregularverbs.presentation.ui.theme.LightGray
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import uz.kabir.irregularverbs.domain.model.IrregularVerbTranslated
import uz.kabir.irregularverbs.presentation.ui.state.AppLanguage
import uz.kabir.irregularverbs.presentation.ui.utils.toHighlightedColorText
import uz.kabir.irregularverbs.presentation.ui.theme.Black
import uz.kabir.irregularverbs.presentation.ui.theme.CustomTheme
import uz.kabir.irregularverbs.presentation.ui.theme.Green
import uz.kabir.irregularverbs.presentation.ui.screens.search.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFragment(
    navHostController: NavHostController,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        searchViewModel.getLanguage()
        searchViewModel.fetchData()
    }

    val searchQuery by searchViewModel.searchQuery.collectAsState()
    val searchResults by searchViewModel.searchResult.collectAsState()
    val selectedItem by searchViewModel.selectedItem.collectAsState()
    val getLanguage by searchViewModel.language.collectAsState()

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val soundOn by searchViewModel.soundState.collectAsState()

    val showBottomSheet = selectedItem != null
    if (showBottomSheet && selectedItem != null) {
        ModalBottomSheet(
            onDismissRequest = { searchViewModel.clearSelectedItem() },
            sheetState = bottomSheetState,
            containerColor = CustomTheme.colors.backgroundColor,
            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
        ) {
            BottomSheetContent(item = selectedItem!!) // itemni beryaamiz
        }
    }
    Surface(modifier = Modifier.fillMaxSize(), color = CustomTheme.colors.backgroundColor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        ) {

            SearchTextField(
                query = searchQuery,
                onQueryChanged = searchViewModel::setSearchWord,
                searchViewModel = searchViewModel,
                context = context
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(searchResults) { index, item ->
                    SearchItem(item = item, getLanguage = getLanguage, onClick = {
                        searchViewModel.selectItem(item) // bu bosilsa srazi viewmodelda o'zgaradi item
                        coroutineScope.launch { bottomSheetState.show() } // ochiladi
                        //Click sound
                        searchViewModel.playClickSound(context)
                    })
                }
            }
        }
    }
}

@Composable
fun BottomSheetContent(item: IrregularVerbTranslated) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Text(
            "Details",
            style = CustomTheme.typography.largeText,
            color = CustomTheme.colors.mainBlue
        )
        Spacer(modifier = Modifier.height(12.dp))
        ExampleItemText(item.verb1)
        Spacer(modifier = Modifier.height(6.dp))
        ExampleItemText(item.verb2)
        Spacer(modifier = Modifier.height(6.dp))
        ExampleItemText(item.verb3)
    }
}

@Composable
fun ExampleItemText(text: String) {
    Text(
        text = buildAnnotatedString { append(text.toHighlightedColorText(Green)) },
        style = CustomTheme.typography.mediumText,
        color = Black,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SearchItem(item: IrregularVerbTranslated, getLanguage: AppLanguage, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CustomTheme.colors.whiteToGray)
            .padding(start = 12.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                VerbItem(verb = item.baseForm, modifier = Modifier.weight(1f))
                VerbItem(verb = item.pastSimple, modifier = Modifier.weight(1f))
                VerbItem(verb = item.pastParticiple, modifier = Modifier.weight(1f))
            }
            Log.d("SHUUU", "item: ${item.translation}")

            if (getLanguage == AppLanguage.UZBEK || getLanguage == AppLanguage.RUSSIAN) {
                Text(
                    text = item.translation,
                    style = CustomTheme.typography.smallText,
                    color = CustomTheme.colors.mainGray
                )
            }


        }

        IconButton(
            onClick = onClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_right_arrow),
                contentDescription = "Details",
                tint = Color.Unspecified
            )
        }
    }
}


@Composable
fun VerbItem(verb: String, modifier: Modifier = Modifier) {
    Text(
        text = verb,
        modifier = modifier,
        style = CustomTheme.typography.smallText,
        color = CustomTheme.colors.textBlackAndWhite
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTextField(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "Search...",
    searchViewModel: SearchViewModel,
    context: Context
) {
    val inputTextStyle = CustomTheme.typography.mediumText.copy(
        color = CustomTheme.colors.textBlackAndWhite,
    )

    TextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, LightGray, RoundedCornerShape(16.dp)),
        placeholder = {
            Text(
                text = hint,
                style = inputTextStyle.copy(color = Color.LightGray.copy(alpha = 0.4f)),
                modifier = Modifier.wrapContentHeight(Alignment.CenterVertically),
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = {
                    onQueryChanged("")
                    //Click sound
                    searchViewModel.playClickSound(context)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cancel),
                        contentDescription = "Clear search",
                        modifier = Modifier
                            .size(24.dp)
                            .padding(4.dp),
                        tint = CustomTheme.colors.textBlackAndWhite
                    )
                }
            }
        },
        textStyle = inputTextStyle,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = CustomTheme.colors.whiteToGray,
            unfocusedContainerColor = CustomTheme.colors.whiteToGray,
            disabledContainerColor = CustomTheme.colors.whiteToGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            cursorColor = CustomTheme.colors.textBlackAndWhite,
            focusedTextColor = CustomTheme.colors.textBlackAndWhite,
            unfocusedTextColor = CustomTheme.colors.textBlackAndWhite,
            disabledTextColor = CustomTheme.colors.textBlackAndWhite,
            errorTextColor = CustomTheme.colors.textBlackAndWhite
        )
    )
}
