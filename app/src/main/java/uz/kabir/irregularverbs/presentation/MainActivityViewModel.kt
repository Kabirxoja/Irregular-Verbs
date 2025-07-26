package uz.kabir.irregularverbs.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.kabir.irregularverbs.domain.usecase.FetchVerbsUseCase
import uz.kabir.irregularverbs.domain.usecase.GetThemeUseCase
import uz.kabir.irregularverbs.presentation.ui.state.ThemeMode
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val fetchVerbsUseCase: FetchVerbsUseCase,
    getThemeUseCase: GetThemeUseCase
) : ViewModel() {

    val theme: StateFlow<ThemeMode> = getThemeUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            ThemeMode.SYSTEM
        )

    fun syncProgress() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchVerbsUseCase()
        }
    }
}
