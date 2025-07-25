package uz.kabir.irregularverbs.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import uz.kabir.irregularverbs.domain.usecase.FetchVerbsUseCase
import uz.kabir.irregularverbs.domain.usecase.GetThemeUseCase
import uz.kabir.irregularverbs.presentation.ui.state.ThemeMode
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val fetchVerbsUseCase: FetchVerbsUseCase,
    getThemeUseCase: GetThemeUseCase
) : ViewModel() {

    suspend fun syncProgress() {
        fetchVerbsUseCase()
    }

    val theme: StateFlow<ThemeMode> = getThemeUseCase().stateIn(viewModelScope, SharingStarted.Companion.Eagerly, ThemeMode.SYSTEM)

}