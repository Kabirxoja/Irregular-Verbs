package uz.kabir.irregularverbs.presentation.ui.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.kabir.irregularverbs.data.remote.network.NetworkMonitor
import uz.kabir.irregularverbs.domain.model.UserProgress
import uz.kabir.irregularverbs.domain.usecase.GetProgressUseCase
import uz.kabir.irregularverbs.domain.usecase.GetSoundStateUseCase
import uz.kabir.irregularverbs.presentation.ui.utils.AudioHelper
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProgressUseCase: GetProgressUseCase,
    private val networkMonitor: NetworkMonitor,
    getSoundStateUseCase: GetSoundStateUseCase,
) : ViewModel() {

    private val _selectedItem = MutableStateFlow<UserProgress?>(null)
    val selectedItem: StateFlow<UserProgress?> = _selectedItem

    private val _navigationChannel = Channel<HomeNavEvent>(Channel.BUFFERED)
    val navigationChannel = _navigationChannel.receiveAsFlow()

    val testProgress = getProgressUseCase.getProgressFlow()
        .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5_000), emptyList())

    val soundState: StateFlow<Boolean> = getSoundStateUseCase.invoke().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )


    fun selectItem(item: UserProgress?) {
        _selectedItem.value = item
    }

    fun clearSelectedItem() {
        _selectedItem.value = null
    }

    fun toListenScreen(groupId: Int) {
        viewModelScope.launch {
            _navigationChannel.send(HomeNavEvent.NavigateToListen(groupId))
        }
    }

    fun toWriteScreen(groupId: Int) {
        viewModelScope.launch {
            _navigationChannel.send(HomeNavEvent.NavigateToWrite(groupId))
        }
    }

    fun toOptionScreen(groupId: Int) {
        viewModelScope.launch {
            _navigationChannel.send(HomeNavEvent.NavigateToOption(groupId))
        }
    }

    fun playClickSound(context: Context) {
        if (soundState.value) {
            AudioHelper.playClick(context)
        }

    }


}