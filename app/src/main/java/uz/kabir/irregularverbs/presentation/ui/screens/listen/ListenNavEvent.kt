package uz.kabir.irregularverbs.presentation.ui.screens.listen

sealed class ListenNavEvent {
    data class NavigateToListenResult(val groupId: Int) : ListenNavEvent()
    object NavigateToHome : ListenNavEvent()
}