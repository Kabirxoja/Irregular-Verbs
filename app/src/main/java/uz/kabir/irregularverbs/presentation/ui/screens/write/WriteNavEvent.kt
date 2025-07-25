package uz.kabir.irregularverbs.presentation.ui.screens.write

sealed class WriteNavEvent {
    data class NavigateToWriteResult(val groupId: Int) : WriteNavEvent()
    object NavigateToHome : WriteNavEvent()
}