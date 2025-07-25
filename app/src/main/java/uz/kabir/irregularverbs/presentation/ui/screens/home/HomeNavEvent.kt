package uz.kabir.irregularverbs.presentation.ui.screens.home

sealed class HomeNavEvent {
    data class NavigateToOption(val groupId: Int) : HomeNavEvent()
    data class NavigateToListen(val groupId: Int) : HomeNavEvent()
    data class NavigateToWrite(val groupId: Int) : HomeNavEvent()
}