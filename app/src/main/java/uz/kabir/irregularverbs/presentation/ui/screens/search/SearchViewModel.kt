package uz.kabir.irregularverbs.presentation.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.kabir.irregularverbs.domain.model.IrregularVerbTranslated
import uz.kabir.irregularverbs.domain.usecase.GetLanguageUseCase
import uz.kabir.irregularverbs.domain.usecase.GetSoundStateUseCase
import uz.kabir.irregularverbs.domain.usecase.SearchUseCase
import uz.kabir.irregularverbs.presentation.ui.state.AppLanguage
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val getLanguageUseCase: GetLanguageUseCase,
    getSoundStateUseCase: GetSoundStateUseCase,
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResult = MutableStateFlow<List<IrregularVerbTranslated>>(emptyList())
    val searchResult: StateFlow<List<IrregularVerbTranslated>> = _searchResult

    private val _selectedItem = MutableStateFlow<IrregularVerbTranslated?>(null)
    val selectedItem: StateFlow<IrregularVerbTranslated?> = _selectedItem

    private val _language = MutableStateFlow<AppLanguage>(AppLanguage.AUTO)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

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

    fun selectItem(item: IrregularVerbTranslated) {
        _selectedItem.value = item
    }

    fun clearSelectedItem() {
        _selectedItem.value = null
    }

    fun getLanguage() {
        viewModelScope.launch {
            _language.value = getLanguageUseCase()
        }
    }


    fun setSearchWord(searchedWord: String) {
        viewModelScope.launch {

            _searchQuery.value = searchedWord

            if (searchedWord.isNotBlank()) {
                val result = searchUseCase(searchedWord)
                Log.d("QIDIRUV", "Raw DB result: $result")

                _searchResult.value = searchUseCase(searchedWord)
                Log.d("QIZIRISH", "BOR")


            } else {
                _searchResult.value = emptyList()

                Log.d("QIZIRISH", "YO'q")

            }

            val results = searchUseCase(searchedWord)
            Log.d("SearchDebug", "UI list result: ${results.size}")
            _searchResult.value = results

        }
    }

    fun fetchData() {
        viewModelScope.launch {
            _searchResult.value = searchUseCase("")
        }
    }


}